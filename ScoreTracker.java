import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreTracker {
    private List<Double> scores;

    public ScoreTracker() {
        scores = new ArrayList<>();
    }

    public void addScore(double score) {
        scores.add(score);
    }

    public double getMedianScore() {
        List<Double> sortedScores = new ArrayList<>(scores);
        Collections.sort(sortedScores);
        int size = sortedScores.size();
        if (size == 0) {
            return 0;
        } else if (size % 2 == 0) {
            int mid = size / 2;
            return (sortedScores.get(mid - 1) + sortedScores.get(mid)) / 2.0;
        } else {
            return sortedScores.get(size / 2);
        }
    }

    public static void main(String[] args) {
        ScoreTracker scoreTracker = new ScoreTracker();
        scoreTracker.addScore(85.5); 
        scoreTracker.addScore(92.3); 
        scoreTracker.addScore(77.8); 
        scoreTracker.addScore(90.1); 
        double median1 = scoreTracker.getMedianScore(); 
        System.out.println("Median 1: " + median1);

        scoreTracker.addScore(81.2); 
        scoreTracker.addScore(88.7);
        double median2 = scoreTracker.getMedianScore(); 
        System.out.println("Median 2: " + median2);
    }
}