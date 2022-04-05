import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import io.swagger.client.model.LiftRide;

public class Publisher {
  private ConnectionFactory factory;
  private Connection connection;
  private ObjectPool<Channel> pool;
  private final String hostName = "172.31.13.49"; //Rabbitmq instance private IPv4 172.31.13.49
  private final String QUEUE_NAME = "newQueue";

  public Publisher() throws IOException, TimeoutException {
    this.factory = new ConnectionFactory();
    this.factory.setHost(hostName);
    this.factory.setUsername("radmin");
    this.factory.setPassword("radmin");
    this.factory.setVirtualHost("/"); //what does this mean
    this.factory.setPort(5672);
    this.connection = this.factory.newConnection();
    this.pool = new GenericObjectPool<>(new ChannelFactory(this.connection));
  }

  public void send(LiftRide liftRide) {
    Channel channel = null;
    try {
      channel = pool.borrowObject();
      channel.queueDeclare(QUEUE_NAME, true, false, false, null);
      Gson gson = new Gson();
      channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, gson.toJson(liftRide).getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if(channel != null) {
          pool.returnObject(channel);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}