package parttwo;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        // args consists of arguments formatted as below
        // --threadsNum 100 --skierNum 100 --numLift 40 --numRun 10 --ip localhost --port 8080
        InputArguments input = InputArguments.parseArgs(args);
        AtomicInteger numberSuccess = new AtomicInteger(0);
        AtomicInteger numberUnsuccess = new AtomicInteger(0);
        String csv = "data.csv";
        CSVWriter writer = new CSVWriter(new FileWriter(csv));
        long startTime = System.currentTimeMillis();
        // Write headers to csv
        String [] record = new String[4];
        record[0] = "StartTime";
        record[1] = "Type";
        record[2] = "Latency";
        record[3] = "StatusCode";
        writer.writeNext(record);

        // Phase 1
        int totalThreadP1 = input.numThread/4;
        int phaseTwoTrigger = (totalThreadP1 + 9)/5; // round up
        CountDownLatch phaseTwoLatch = new CountDownLatch(phaseTwoTrigger);
        int postNumP1 = (int) (input.numRun*input.PHASE_ONE_POST_NUM_FACTOR*(input.numSkier/totalThreadP1));
        Phase phase1 = new Phase("Phase1", totalThreadP1, input.numSkier, phaseTwoLatch, 1, 90, input, postNumP1, numberSuccess, numberUnsuccess, writer);
        phase1.startPhase();

        // Phase 2
        int totalThreadP2 = input.numThread;
        int phaseThreeTrigger = (totalThreadP2 + 9)/5;
        CountDownLatch phaseThreeLatch = new CountDownLatch(phaseThreeTrigger);
        int postNumP2 = (int) (input.numRun*input.PHASE_TWO_POST_NUM_FACTOR*(input.numSkier/totalThreadP2));
        Phase phase2 = new Phase("Phase2", totalThreadP2, input.numSkier, phaseThreeLatch, 91, 360, input, postNumP2,numberSuccess, numberUnsuccess, writer);
        // Wait for 20% of phase 1 thread to finish, then start phase 2
        phaseTwoLatch.await(); // when countdown to 0
        phase2.startPhase();

        // Phase 3
        int totalThreadP3 = input.numThread/10;
        CountDownLatch endLatch = new CountDownLatch(totalThreadP3);
        int postNumP3 = (int) (input.numRun*input.PHASE_THREE_POST_NUM_FACTOR*(input.numSkier/totalThreadP3));
        Phase phase3 = new Phase("Phase3", totalThreadP3, input.numSkier, endLatch, 361, 420, input, postNumP3, numberSuccess, numberUnsuccess, writer);
        // Wait for 10% of phase 2 thread to finish, then start phase 3
        phaseThreeLatch.await();
        phase3.startPhase();

        phase1.waitForPhaseFinish();
        phase2.waitForPhaseFinish();
        phase3.waitForPhaseFinish();
        // Close the CSVWriter
        writer.close();

        long endTime = System.currentTimeMillis();
        long wallTime = endTime - startTime;

        System.out.println("Successful request count: " + numberSuccess);
        System.out.println("Unsuccessful request count: " + numberUnsuccess);
        System.out.printf("Total wall-time is %d ms for %d threads.%n", wallTime, input.numThread);
        System.out.println("Throughput is " +
            (long) input.MILLISECOND_TO_SECOND *(numberSuccess.get() + numberUnsuccess.get()) / wallTime + "/s");
        DecimalFormat df = new DecimalFormat("0.00");
        System.out.println("Average response time is: " + df.format(new Calculator(csv).calculateAverage()) + "/ms");

    }
}
