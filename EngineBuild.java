import java.util.PriorityQueue;

public class EngineBuild {


    public static int minBuildTime(int[] engines, int splitTime) {
        PriorityQueue<Integer> engineQueue = new PriorityQueue<>();

        for (int engine : engines) {
            engineQueue.offer(engine);
        }

        while (engineQueue.size() > 1) {
            int fastestEngine = engineQueue.poll();
            int secondFastestEngine = engineQueue.poll();

            engineQueue.offer(secondFastestEngine + splitTime);
        }


        return engineQueue.poll();
    }

    public static void main(String[] args) {
        int[] engines = {1, 2, 3};
        int splitTime = 1;

        int totalTime = minBuildTime(engines, splitTime);

        System.out.println("Minimum time to build all engines: " + totalTime);
    }
}