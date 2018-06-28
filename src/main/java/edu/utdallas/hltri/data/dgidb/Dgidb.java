package edu.utdallas.hltri.data.dgidb;

import com.google.common.collect.ImmutableSetMultimap;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import edu.utdallas.hltri.data.dgidb.params.SourceTrustLevel;
import edu.utdallas.hltri.logging.Logger;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by trg19 on 7/20/2017.
 */
@SuppressWarnings("WeakerAccess")
public class Dgidb {
  private final static Logger log = Logger.get(Dgidb.class);
  private final DgidbService restService;

  public Dgidb(final DgidbSettings settings) {
    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
        // Rewrite DGIdb's response header's to cache response for 7 days
        .addNetworkInterceptor(chain -> {
          // Add caching directives
          final CacheControl cacheControl = new CacheControl.Builder()
              // Use cached value for up to 7 days
              .maxAge(7, TimeUnit.DAYS)
              .build();

          final Response response = chain.proceed(chain.request());
          return response.newBuilder()
              .header("Cache-Control", cacheControl.toString())
              .build();
        })
        // Rewrite Retrofit's request to allow cached values for up to 1 year
        .addInterceptor(chain -> {
          final CacheControl cacheControl= new CacheControl.Builder()
              .maxStale(365, TimeUnit.DAYS)
              .build();

          final Request request = chain.request().newBuilder()
              .cacheControl(cacheControl)
              .build();

          log.debug("Request: {}", request.toString());

          final Response response = chain.proceed(request);
          log.debug("Response: {}", response.toString());
          return response.newBuilder().build();
        })
        // Specify cache location and max size
        .cache(new Cache(settings.cachePath.toFile(), settings.maxCacheSize))
        .build();

    final Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(settings.apiUrl)
        .client(okHttpClient)
        .addConverterFactory(JacksonConverterFactory.create())
        .build();

    restService = retrofit.create(DgidbService.class);
  }

  Dgidb(DgidbService service) {
    this.restService = service;
  }

  public @Nonnull DgidbInteractionResponse getInteractionResponse(InteractionsRequest request) {
    final Call<DgidbInteractionResponse> interactions =
        restService.getInteractions(request.buildQueryMap());
    try {
      final retrofit2.Response<DgidbInteractionResponse> httpResponse = interactions.execute();
      final DgidbInteractionResponse response = httpResponse.body();
      if (null == response) {
        log.error("NULL response {} for {}", httpResponse, request);
        throw new RuntimeException("encountered NULL response");
      } else {
        log.debug("Request {} had Response {}", request, response);
        return response;
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public @Nonnull List<MatchedTerm> getInteractions(InteractionsRequest request) {
    return getInteractionResponse(request).getMatchedTerms();
  }

  public @Nonnull ImmutableSetMultimap<String, String> getDrugsForGenes(Iterable<String> genes) {
    final InteractionsRequest interactionsRequest =
        InteractionsRequest.forGenes(genes)
        .setSourceTrustLevels(SourceTrustLevel.EXPERT_CURATED);

    final ImmutableSetMultimap.Builder<String, String> mmapBuilder = new ImmutableSetMultimap.Builder<>();
    final List<MatchedTerm> interactions = getInteractions(interactionsRequest);
    for (final MatchedTerm gene : interactions) {
      for (final Interaction interaction : gene.getInteractions()) {
        mmapBuilder.put(interaction.getDrugName(), interaction.getSource());
      }
    }

    final ImmutableSetMultimap<String, String> mmap = mmapBuilder.build();
    log.debug("Found {} drugs for {} genes", mmap, genes);
    if (mmap.isEmpty()) {
      log.warn("Found no drugs for genes " + genes.toString() + " response was " + interactions);
    }
    return mmap;
  }
}
