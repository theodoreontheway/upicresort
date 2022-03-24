package parttwo;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class PhaseRunner implements Runnable {

    private String threadId;
    private int startSkierId;
    private int endSkierId;
    private int startTime;
    private int endTime;
    private InputArguments input;
    private int postNum;
    private CountDownLatch nextPhaseCountDownLatch;
    private CountDownLatch curPhaseCountDownLatch;
    private AtomicInteger numberSuccess;
    private AtomicInteger numberUnsuccess;

    private String dayID;
    private Integer resortID;
    private String seasonID;

    private CSVWriter writer;


    public PhaseRunner(String belongPhase, String threadId, int startSkierId, int endSkierId, int startTime, int endTime, InputArguments input, int postNum, CountDownLatch nextPhaseCountDownLatch, CountDownLatch curPhaseCountDownLatch, AtomicInteger numberSuccess, AtomicInteger numberUnSuccess, CSVWriter writer)
        throws IOException {
        this.threadId = threadId;
        this.startSkierId = startSkierId;
        this.endSkierId = endSkierId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.input = input;
        this.postNum = postNum;
        this.nextPhaseCountDownLatch = nextPhaseCountDownLatch;
        this.curPhaseCountDownLatch = curPhaseCountDownLatch;
        this.numberSuccess = numberSuccess;
        this.numberUnsuccess = numberUnSuccess;
        this.dayID = "1";
        this.resortID = 1;
        this.seasonID = "1";
        this.writer = writer;
    }

    @Override
    public void run() {
        //System.out.println(threadId + " started");

        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath("http://" + input.ip + ":" + input.port + "/upicresort_war");
        for (int i = 0; i < postNum; i++) {
            // do post: /skiers/liftrides
            LiftRide liftRide = new LiftRide();
            //liftRide.setDayID(dayID);

            liftRide.setTime(getRandomTime(this.startTime, this.endTime));
            int START_OF_WAIT_TIME = 0;
            int END_OF_WAIT_TIME = 10;
            liftRide.setWaitTime(getRandomTime(START_OF_WAIT_TIME, END_OF_WAIT_TIME));
            liftRide.setLiftID(getRandomLiftID());
            SkiersApi skiersApi = new SkiersApi(apiClient);
            ApiResponse<Void> res;
            try {
                long startTime = System.currentTimeMillis();
                res = skiersApi.writeNewLiftRideWithHttpInfo(liftRide, this.resortID, this.seasonID, this.dayID, getRandomSkierID());
                if(res.getStatusCode() == 201) {
                    numberSuccess.incrementAndGet();
                } else {
                    numberUnsuccess.incrementAndGet();
                }
                long endTime = System.currentTimeMillis();
                long latency = endTime - startTime;
                String [] record = new String[4];
                record[0] = String.valueOf(startTime);
                record[1] = "POST";
                record[2] = String.valueOf(latency);
                record[3] = String.valueOf(res.getStatusCode());
                this.writer.writeNext(record);

            } catch (ApiException e) {
                e.printStackTrace();
            }
        }



        // System.out.println(threadId + " finished");
        nextPhaseCountDownLatch.countDown();
        curPhaseCountDownLatch.countDown();
    }

    private Integer getRandomSkierID(){
        return ThreadLocalRandom.current().nextInt(this.startSkierId, this.endSkierId + 1);
    }
    private Integer getRandomTime(int startTime, int endTime){
        return ThreadLocalRandom.current().nextInt(startTime, endTime + 1);
    }
    private Integer getRandomLiftID(){
        return ThreadLocalRandom.current().nextInt(1, this.input.numLift + 1);
    }
}
