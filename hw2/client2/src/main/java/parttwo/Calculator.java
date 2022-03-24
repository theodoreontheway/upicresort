package parttwo;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/*
A calculator to read from a csv file and
calculate a mean from a column
 */
public class Calculator {
  private final String fileName;
  private int count;
  public Calculator(String fileName) {
    this.fileName = fileName;
    this.count = 0;
  }

  public double calculateAverage() throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(this.fileName));
    String line;
    int sum = 0;
    try {
      br.readLine();
      while ((line = br.readLine()) != null) {

        // use comma as separator
        String[] data = line.split(",");
        // Omit values with abnormal formats
        int latency = parseWithDefault(data[2].replace("\"", ""));
        sum += latency;
      }
    }catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return (double) sum / this.count;
  }

  private int parseWithDefault(String s) {
    this.count++;
    try {
      return Integer.parseInt(s);
    }
    catch (NumberFormatException e) {
      this.count--;
      return 0;
    }
  }
}
