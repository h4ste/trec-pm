package edu.utdallas.hltri.data.dgidb;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.mock.BehaviorDelegate;

public class MockDgidbService implements DgidbService {
  private final BehaviorDelegate<DgidbService> delegate;

  MockDgidbService(BehaviorDelegate<DgidbService> delegate) {
    this.delegate = delegate;
  }

  @Override
  public Call<DgidbInteractionResponse> getInteractions(Map<String, String> params) {
    return delegate.returningResponse(getMockInteractionResponse()).getInteractions(params);
  }

  @Override
  public Call<List<String>> getGenesInCategory(String category) {
    return delegate.returningResponse(getMockGenesInCategoryResponse()).getGenesInCategory(category);
  }

  static String[] getMockGenesInCategoryResponse() {
    return new String[] { "SNRK","BCR","FKBP1A","TAB2","DGKE","PHKA1","PRKCH" };
  }

  static DgidbInteractionResponse getMockInteractionResponse() {
    return new DgidbInteractionResponse(
        Collections.singletonList(
            new MatchedTerm("CDK4",
                "CDK4",
                "cyclin dependent kinase 4",
                Arrays.asList("G PROTEIN COUPLED RECEPTOR", "TRANSPORTER"),
                Arrays.asList(
                    new Interaction("c0872763-cc8e-46e1-b570-9473f626b799",
                        "antagonist", "GLYCOPYRROLATE", "TTD"),
                    new Interaction("57929626-e18e-4fc2-950c-d2390dde59ce",
                        "n/a", "GLYCOPYRROLATE", "TEND")
                )
            )
        ),
        Arrays.asList(
            new UnmatchedTerm("MM1",
                Arrays.asList(
                    "PFDN5",
                    "PLXNB2"
                )
            ),
            new UnmatchedTerm("FAKE", Collections.emptyList())
        )
    );
  }
}
