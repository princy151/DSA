import java.util.ArrayList;
import java.util.List;
 
public class SecretSharing {
 

    public static List<Integer> findIndividuals(int n, int[][] intervals, int firstPerson) {

        boolean[] knowsSecret = new boolean[n];
        knowsSecret[firstPerson] = true;
 

        for (int[] interval : intervals) {
            int start = interval[0];
            int end = interval[1];
 
            for (int i = start; i <= end; i++) {
                knowsSecret[i] = true;
            }
        }

        List<Integer> knownIndividuals = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (knowsSecret[i]) {
                knownIndividuals.add(i);
 
            }
        }
 
        return knownIndividuals;
    }
 
    public static void main(String[] args) {
        int n = 5;
        int[][] intervals = {{0, 2}, {1, 3}, {2, 4}};
        int firstPerson = 0;


        List<Integer> result = findIndividuals(n, intervals, firstPerson);
 
        System.out.println("Individuals who will eventually know the secret: " + result);
    }
}