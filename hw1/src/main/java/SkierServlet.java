import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;


@WebServlet(name = "SkierServlet", value = "/SkierServlet")
public class SkierServlet extends HttpServlet {

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

    if (!isUrlValid(urlParts)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write("{\n" +
          "  \"message\": \"invalid get url\"\n" +
          "}");
    } else {
      res.setStatus(HttpServletResponse.SC_OK);
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

      } else if(urlPath.length == 6) {
        return "days".equals(urlPath[2])
            && isNum(urlPath[3])
            && "skiers".equals(urlPath[4])
            && isNum(urlPath[5]);
      }
    }
    return false;
  }

  private boolean isNum(String input) {
    return input != null && input.matches("[0-9]+");
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
    // (and maybe also some value if input is valid)
    if(!isValidPostUrl(urlParts)) {
      res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      res.getWriter().write("{\n" +
          "  \"message\": \"invalid post url\"\n" +
          "}");
    } else {
      res.setStatus(HttpServletResponse.SC_OK);
      // do any sophisticated processing with urlParts which contains all the url params
      // TODO: process url params in `urlParts`
      res.getWriter().write("{\n" +
          "  \"message\": \"success\"\n" +
          "}");
    }
  }

  private boolean isValidPostUrl(String[] urlPath){
    return urlPath != null
        && urlPath.length == 2
        && "liftrides".equals(urlPath[1]);
  }
}
