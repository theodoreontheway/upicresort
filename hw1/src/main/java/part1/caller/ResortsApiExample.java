package part1.caller;

import io.swagger.client.*;
import io.swagger.client.api.ResortsApi;
import io.swagger.client.model.*;

import java.util.*;

public class ResortsApiExample {

  public static void main(String[] args) {
    // -/resort/day/top10vert
    ResortsApi apiInstance = new ResortsApi();
    List<String> resort = Arrays.asList("resort_example"); // List<String> | resort to query by
    List<String> dayID = Arrays.asList("dayID_example"); // List<String> | day number in the season
    try {
      TopTen result = apiInstance.getTopTenVert(resort, dayID);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ResortsApi#getTopTenVert");
      e.printStackTrace();
    }
  }
}