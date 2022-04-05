package parttwo;

public class InputArguments {
    // --threadsNum 100 --skierNum 100 --numLift 5 --skiDay 1 --resortId silverMt --ip localhost --port 8080

    public static final String THREAD_NUM = "--threadsNum";
    public static final String SKIER_NUM = "--skierNum";
    public static final String LIFT_NUM = "--numLift";
    public static final String RUN_NUM = "--numRun";
    public static final String IP = "--ip";
    public static final String PORT = "--port";

    public int numThread;
    public int numSkier;
    public int numLift;
    public int numRun;
    public String ip;
    public String port;
    public final double PHASE_ONE_POST_NUM_FACTOR;
    public final double PHASE_TWO_POST_NUM_FACTOR;
    public final double PHASE_THREE_POST_NUM_FACTOR;
    public final int MILLISECOND_TO_SECOND;

    public InputArguments(int numThread, int numSkier, int numLift, int numRun, String ip, String port) {
        this.numThread = numThread;
        this.numSkier = numSkier; // max 100K
        this.numLift = numLift;
        this.numRun = numRun; // [10, 20]
        this.ip = ip;
        this.port = port;
        this.PHASE_ONE_POST_NUM_FACTOR = 0.2;
        this.PHASE_TWO_POST_NUM_FACTOR = 0.6;
        this.PHASE_THREE_POST_NUM_FACTOR = 0.1;
        this.MILLISECOND_TO_SECOND = 1000;
    }

    public static InputArguments parseArgs(String[] args) {
        // --threadsNum 100 --skierNum 100 --numLift 40 ----numRun 10 --resortId Heng --ip localhost --port 8080
        int threadNum = 64;
        int skierNum = 20000;
        int numLift = 40;
        int numRun = 10;
        //ec2 instance ip
        final String upicresort = "34.212.176.22";
        final String servlet1 = "54.149.191.206";
        final String servlet2 = "34.216.108.134";
        final String servlet3 = "34.212.169.180";
        final String albDNS = "my-alb-63230616.us-west-2.elb.amazonaws.com";
        String ip = albDNS;
        String port = "8080";

        for (int i = 0; i < args.length; i++) {

            String curArg = args[i];
            String argValue = args[++i];
            if (isSameString(THREAD_NUM, curArg)) {
                threadNum = Integer.parseInt(argValue);
            }
            if (isSameString(SKIER_NUM, curArg)) {
                skierNum = Integer.parseInt(argValue);
            }
            if (isSameString(LIFT_NUM, curArg)) {
                numLift = Integer.parseInt(argValue);
            }
            if (isSameString(RUN_NUM, curArg)) {
                numRun = Integer.parseInt(argValue);
            }
            if (isSameString(IP, curArg)) {
                ip = argValue;
            }
            if (isSameString(PORT, curArg)) {
                port = argValue;
            }
        }
        return new InputArguments(threadNum, skierNum, numLift, numRun, ip, port);
    }


    private static boolean isSameString(String s1, String s2) {
        if (s1 == null) return s2 == null;
        return s1.equals(s2);
    }
}
