package edu.utdallas.hltri.data.dgidb;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

import static org.junit.Assert.assertEquals;

/**
 * Created by trg19 on 7/20/2017.
 */
public class DgidbTest {
  private Dgidb dgidb;

  @Before
  public void setUp() {
    final Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(DgidbSettings.DEFAULT.apiUrl)
        .build();

    final NetworkBehavior behavior = NetworkBehavior.create();
    final MockRetrofit mockRetrofit = new MockRetrofit.Builder(retrofit)
        .networkBehavior(behavior)
        .build();

    BehaviorDelegate<DgidbService> delegate = mockRetrofit.create(DgidbService.class);
    this.dgidb = new Dgidb(new MockDgidbService(delegate));

  }

  @Test
  public void getInteractionResponse() throws Exception {
    final DgidbInteractionResponse actualResponse =
        dgidb.getInteractionResponse(InteractionsRequest.forDrugs("FAKE"));
    final DgidbInteractionResponse expectedResponse = MockDgidbService.getMockInteractionResponse();
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  public void getInteractions() throws Exception {
    final List<MatchedTerm> actualTerms =
        dgidb.getInteractions(InteractionsRequest.forDrugs("FAKE"));
    final List<MatchedTerm> expectedTerms =
        MockDgidbService.getMockInteractionResponse().getMatchedTerms();
    assertEquals(expectedTerms, actualTerms);
  }

  @Test
  public void getDrugsForGenes() throws Exception {
    final SetMultimap<String, String> actualDrugs =
        dgidb.getDrugsForGenes(Collections.singleton("FAKE"));
    final SetMultimap<String, String> expectedDrugs = HashMultimap.create();
    expectedDrugs.putAll("GLYCOPYRROLATE", Arrays.asList("TTD", "TEND"));
    assertEquals(expectedDrugs, actualDrugs);
 }
}