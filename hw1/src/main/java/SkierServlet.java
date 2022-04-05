import com.google.gson.Gson;
import io.swagger.client.model.LiftRide;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;


@WebServlet(name = "SkierServlet", value = "/SkierServlet")
public class SkierServlet extends HttpServlet {

  private Gson gson = new Gson();
  private Publisher publisher;
  public void init() {
    try {
      publisher = new Publisher();

    } catch (IOException | TimeoutException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    res.setContentType("application/json"); // the difference between application/json and text/plain ?
    String urlPath = req.getPathInfo();

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write("missing parameters");
      return;
    }

    String[] urlParts = urlPath.split("/");
    // and now validate url path and return the response status code
    // (and maybe also some value if input is valid)
    for(int i = 0; i < urlParts.length; i++) {
      if(i == 3 || i == 5 || i == 7) System.out.println(urlParts[i]);
    }
    if (!isUrlValid(urlParts)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      System.out.println(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write("{\n" +
          "  \"message\": \"invalid get url\"\n" +
          "}");
    } else {
      res.setStatus(HttpServletResponse.SC_OK);
      System.out.println(HttpServletResponse.SC_OK);
      // do any sophisticated processing with urlParts which contains all the url params
      // TODO: process url params in `urlParts`
      res.getWriter().write("{\n" +
          "  \"resortID\": \"Mission Ridge\",\n" +
          "  \"totalVert\": 56734\n" +
          "}");
      }
  }

  private boolean isUrlValid(String[] urlPath) {
    if(urlPath != null) {
      if(urlPath.length == 3) {
        return isNum(urlPath[1])
            && "vertical".equals(urlPath[2]);

      } else if(urlPath.length == 8) {
        return "seasons".equals(urlPath[2])
            && isNum(urlPath[3])
            && "days".equals(urlPath[4])
            && isNum(urlPath[5])
            && "skiers".equals(urlPath[6])
            && isNum(urlPath[7]);
      }
    }
    return false;
  }

  private boolean isNum(String input) {
    if (input == null) {
      return false;
    }
    try {
      int d = Integer.parseInt(input);
    } catch (NumberFormatException nfe) {
      return false;
    }
    return true;
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    res.setContentType("application/json");

    String urlPath = req.getPathInfo();
    // check we have a URL!
    if(urlPath == null || urlPath.isEmpty()){
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write("missing parameters");
      return;
    }
    String[] urlParts = urlPath.split("/");


    // and now validate url path and return the response status code
    if(!isValidPostUrl(urlParts)) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      //System.out.println(HttpServletResponse.SC_BAD_REQUEST);
      res.getWriter().write("{\n" +
          "  \"message\": \"invalid post url\"\n" +
          "}");
    } else {
      res.setStatus(HttpServletResponse.SC_CREATED);
      //validated = validateRequestBody(req)
      LiftRide liftRide = gson.fromJson(req.getReader(), LiftRide.class);
      //send liftRide to RabbitMQ/the publisher
      publisher.send(liftRide);

    }
  }

  private boolean isValidPostUrl(String[] urlPath){
    return urlPath != null
        && urlPath.length == 8
        && "seasons".equals(urlPath[2])
        && isNum(urlPath[3])
        && "days".equals(urlPath[4])
        && isNum(urlPath[5])
        && "skiers".equals(urlPath[6])
        && isNum(urlPath[7]);
  }

  private boolean validateRequestBody (HttpServletRequest request) throws IOException {
      String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
      System.out.println(body);
      return true;
  }
}
