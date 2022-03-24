import com.rabbitmq.client.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.swagger.client.model.LiftRide;
import java.util.concurrent.atomic.AtomicInteger;


public class Recv {
  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("172.31.13.49"); // Private IPv4 172.31.13.49
    AtomicInteger count = new AtomicInteger();
    // A map that logs liftRides
    int NUM_OF_THREADS = 5;
    Map<Integer, List<LiftRide>> map = new HashMap<>();
    factory.setUsername("radmin");
    factory.setPassword("radmin");
    factory.setVirtualHost("/");
    factory.setPort(5672);
    Connection connection = factory.newConnection();
    System.out.println(" ====== Threads waiting for messages ====== ");

    for (int i = 1; i <= NUM_OF_THREADS; i++) {
      String threadName = "Thread " + i;
      RecvRunner runnable = new RecvRunner(connection, threadName, map, count);
      new Thread(runnable).start();
      System.out.println(threadName + " starts...");
    }

  }


}