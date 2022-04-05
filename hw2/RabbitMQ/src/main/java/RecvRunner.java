import com.google.gson.Gson;
import com.rabbitmq.client.*;

import io.swagger.client.model.LiftRide;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RecvRunner implements Runnable {
  private Connection connection;
  private final String name;
  private final static String QUEUE_NAME = "newQueue";
  private Map<Integer, List<LiftRide>> map;
  private AtomicInteger count;
  public RecvRunner(Connection connection, String name, Map<Integer, List<LiftRide>> map,
      AtomicInteger count) {
    this.connection = connection;
    this.name = name;
    this.map = map;
    this.count = count;
  }
  
  @Override
  public void run() {
    try {
      Channel channel = connection.createChannel();

      channel.queueDeclare(QUEUE_NAME, true, false, false, null);
      channel.basicQos(1);
      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        Gson gson = new Gson();
        LiftRide liftRide = gson.fromJson(message, LiftRide.class);
        int id = liftRide.getLiftID();
        System.out.println("The LiftRide received is: " + liftRide);
        count.getAndIncrement();

        if(!map.containsKey(id)) {
          map.put(id, new ArrayList<>());
        }
        map.get(id).add(liftRide);
        System.out.println("map size: " + this.map.size() + "; Total # requests handled: " + this.count);
      };
      channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    } catch (IOException e) {
        e.printStackTrace();
      }
    }
}
