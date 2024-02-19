public class Sewing {
 
    public static int minMovesEqualizeDresses(int[] sewingMachines) {
        int totalDresses = 0;
        int n = sewingMachines.length;
 
        for (int dresses : sewingMachines) {
            totalDresses += dresses;
        }
 
        int targetDresses = totalDresses / n;
 
        if (totalDresses % n != 0) {
            return -1;
        }
 
        int moves = 0;
        int surplus = 0;
 
        for (int dresses : sewingMachines) {
            surplus += dresses - targetDresses;
            moves += Math.abs(surplus);
        }
 
        return moves / 2;
    }
 
    public static void main(String[] args) {
        int[] sewingMachines = {1, 0, 5};
        int minMoves = minMovesEqualizeDresses(sewingMachines);
        System.out.println("Minimum number of moves required: " + minMoves); 
    }
}