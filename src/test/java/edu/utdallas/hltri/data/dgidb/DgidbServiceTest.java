package edu.utdallas.hltri.data.dgidb;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by trg19 on 7/20/2017.
 */
public class DgidbServiceTest {
  private static final String GENE_INTERACTIONS_SAMPLE_PATH =
      "gene-interactions-sample.json";


  /**
    * For reasons no one understands, Java can't create a Path to a file inside
    * the classpath resources, so first we "copy" it to a virtual in-memory filesystem
    */
  private static final FileSystem jimfs = Jimfs.newFileSystem(Configuration.unix());

  @BeforeClass
  public static void initAll() throws IOException {
    Files.copy(DgidbServiceTest.class.getResourceAsStream(GENE_INTERACTIONS_SAMPLE_PATH),
        jimfs.getPath(GENE_INTERACTIONS_SAMPLE_PATH));
  }

  @Test
  public void testInteractions() throws IOException {
    try(final MockWebServer mockWebServer = new MockWebServer()) {

      mockWebServer.enqueue(new MockResponse()
          .setBody(new String(Files.readAllBytes(jimfs.getPath(GENE_INTERACTIONS_SAMPLE_PATH)))));

      final Retrofit retrofit = new Retrofit.Builder()
          .baseUrl(mockWebServer.url("").toString())
          .addConverterFactory(JacksonConverterFactory.create())
          .build();

      final DgidbService service = retrofit.create(DgidbService.class);

      final Call<DgidbInteractionResponse> response =
          service.getInteractions(InteractionsRequest.forGenes("CDK4", "CDK5")
              .buildQueryMap());

      assertNotNull(response);

      final DgidbInteractionResponse actualResponse = response.execute().body();
      final DgidbInteractionResponse expectedResponse =
          MockDgidbService.getMockInteractionResponse();

      assertEquals(expectedResponse, actualResponse);
    }
  }

  @Test
  public void testGeneCategories() throws IOException {
    try (final MockWebServer mockWebServer = new MockWebServer()) {

      mockWebServer.enqueue(new MockResponse()
          .setBody("[\"SNRK\",\"BCR\",\"FKBP1A\",\"TAB2\",\"DGKE\",\"PHKA1\",\"PRKCH\"]"));

      final Retrofit retrofit = new Retrofit.Builder()
          .baseUrl(mockWebServer.url("").toString())
          .addConverterFactory(JacksonConverterFactory.create())
          .build();

      final DgidbService service = retrofit.create(DgidbService.class);

      final Call<List<String>> response = service.getGenesInCategory("FAKE");
      assertNotNull(response);

      final List<String> responseBody = response.execute().body();
      assertNotNull(responseBody);

      final String[] actualResponse = responseBody.toArray(new String[7]);
      final String[] expectedResponse = MockDgidbService.getMockGenesInCategoryResponse();

      assertArrayEquals(expectedResponse, actualResponse);
    }
  }
}