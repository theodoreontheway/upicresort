package part1.caller;

import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.ProgressRequestBody;
import io.swagger.client.ProgressResponseBody;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.SkierVertical;

public class SkiersGetVerticalApi {

  public ApiResponse<SkierVertical> callSkiersGetVerticalApi(String resortId, String dayID, String skierID) throws ApiException {
    //  -/skiers/{resortID}/days/{dayID}/skiers/{skierID}
    SkiersApi skiersApi = new SkiersApi();
    return skiersApi.getSkierDayVerticalWithHttpInfo(resortId,dayID,skierID);
  }
}