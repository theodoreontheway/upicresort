package parttwo;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Phase {
    private String phaseId;
    private int threadNum;
    private int skierNum;
    private CountDownLatch nextPhaseLatch;
    private CountDownLatch curPhaseLatch;
    private int startTime;
    private int endTime;
    private InputArguments inputArguments;
    private int postNum;
    private int getNum;
    private AtomicInteger numberSuccess;
    private AtomicInteger numberUnsuccess;
    private CSVWriter writer;

    public Phase(String phaseId, int threadNum, int skierNum, CountDownLatch nextPhaseLatch, int startTime, int endTime, InputArguments inputArguments, int postNum, AtomicInteger numberSuccess, AtomicInteger numberUnSuccess, CSVWriter writer) {
        this.phaseId = phaseId;
        this.threadNum = threadNum;
        this.skierNum = skierNum;
        this.nextPhaseLatch = nextPhaseLatch;
        // control threads are completed in each phase
        this.curPhaseLatch = new CountDownLatch(threadNum);
        this.startTime = startTime;
        this.endTime = endTime;
        this.inputArguments = inputArguments;
        this.postNum = postNum;
        this.numberSuccess = numberSuccess;
        this.numberUnsuccess = numberUnSuccess;
        this.writer = writer;
    }

    public void startPhase() throws IOException {
        System.out.println("======== Starting " + phaseId + " ========");
        int skierPerThread = skierNum / threadNum; // Round down
        // initial each thread in a specific phase

        for (int i = 0; i < threadNum; i++) { // ith thread
            int startSkierId = i*skierPerThread + 1;
            int endSkierId = (i == threadNum - 1) ? skierNum : (i + 1) * skierPerThread;
            // runner-thread: control the number of post and get
            Runnable runner = new PhaseRunner(phaseId, "TID-" + i, startSkierId, endSkierId, startTime, endTime, inputArguments, postNum, nextPhaseLatch, curPhaseLatch, numberSuccess, numberUnsuccess, writer);
            new Thread(runner).start();
        }
    }

    public void waitForPhaseFinish() throws InterruptedException {
        curPhaseLatch.await();
    }
}
