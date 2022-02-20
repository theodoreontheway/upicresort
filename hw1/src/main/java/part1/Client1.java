package part1;

import io.swagger.client.model.SkierVertical;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import io.swagger.client.ApiException;
import io.swagger.client.api.SkiersApi;
import java.util.concurrent.CountDownLatch;

public class Client1 {

  final static private int NUMTHREADS = 1000;
  private int count = 0;

  synchronized public void inc() {
    count++;
  }

  public int getVal() {
    return this.count;
  }

  public static void main(String[] args) throws InterruptedException {
    final Client1 counter = new Client1();
    CountDownLatch completed = new CountDownLatch(NUMTHREADS);
    SkiersApi apiInstance = new SkiersApi();
    Integer skierID = 56; // Integer | ID the skier to retrieve data for
    List<String> resort = Arrays.asList("resort_example"); // List<String> | resort to filter by
    List<String> season = Arrays.asList("season_example"); // List<String> | season to filter by, optional
    try {
      SkierVertical result = apiInstance.getSkierResortTotals(skierID, resort, season);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling SkiersApi#getSkierResortTotals");
      e.printStackTrace();
    }
    for (int i = 0; i < NUMTHREADS; i++) {
      // lambda runnable creation - interface only has a single method so lambda works fine
      Runnable thread =  () -> { counter.inc(); completed.countDown();
      };
      new Thread(thread).start();
    }

    completed.await();
    System.out.println("Value should be equal to " + NUMTHREADS + " It is: " + counter.getVal());
  }


}