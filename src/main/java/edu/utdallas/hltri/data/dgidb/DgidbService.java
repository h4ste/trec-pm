package edu.utdallas.hltri.data.dgidb;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface DgidbService {

  @GET("/api/v1/interactions.json")
  Call<DgidbInteractionResponse> getInteractions(@QueryMap(encoded=true) Map<String, String> params);

  @GET("/api/v1/genes_in_category.json")
  Call<List<String>> getGenesInCategory(@Query("category") String category);
}
