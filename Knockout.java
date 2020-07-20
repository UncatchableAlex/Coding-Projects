import java.util.*;

/**
 * Knockout is a made up game from division one problem K of the 2018 Intercollegiate Programming Contest. You can see
 * the problem-set here: http://www.acmicpc-pacnw.org/ProblemSet/2018/div1.pdf
 *
 * This Knockout class is specially designed to simulate a game of "Knockout". Crate an instance of this class and use
 * it's singular public method "getExpectedValue()" to input the game-state and the most recent roll. This class is
 * designed to help a Knockout player make the most educated removal selections depending on what variation of the game
 * is being played. NOTE: Although I was inspired by the original problem from ICPC, I did not follow the input/output
 * directions exactly.
 *
 * @author Alex Meislich
 * @date 2020-07-19
 */
class Knockout {
    private final double[] probKey = {0, 0, 1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1};
    private final int permutations = 36; 

    public static void main(String[] args) {
        LinkedList<Integer> currState = new LinkedList<>(Arrays.asList(1,2,3,4,9));
        Knockout myGame = new Knockout();
        long start = System.currentTimeMillis();
        System.out.println(myGame.getExpectedValue(currState, 4) + "\n" + (System.currentTimeMillis() - start));
    }

    public Result getExpectedValue(List<Integer> currState, int roll){
        return getExpectedValueHelper(currState, roll, false);
    }


    private Result getExpectedValueHelper(List<Integer> currState, int lastRolled, boolean gameOver) {
        Result expectedValues = new Result(0,0);
        if(gameOver){
            StringBuilder str = new StringBuilder("0");
            for(int x : currState){
                str.append(x);
            }
            double val = Integer.parseInt(str.toString());
            expectedValues.minEV = val;
            expectedValues.maxEV = val;
            return expectedValues;
        }

        int start = lastRolled != -1 ? lastRolled : 2;
        int end = lastRolled != -1 ? lastRolled : 12;
        for (int i = start; i <= end; i++) {
            Result rollExtremes = new Result (Integer.MAX_VALUE, Integer.MIN_VALUE);
            List<List<Integer>> removalOptions = getRemovalOptions(currState, i, 0, 0);
            for(List<Integer> rOption : removalOptions){
                List<Integer> nextState = new LinkedList<>(currState);
                nextState.removeAll(rOption);
                Result res  = getExpectedValueHelper(nextState, -1, rOption.size() == 0);
                if(res.minEV < rollExtremes.minEV){
                    rollExtremes.minEV = res.minEV;
                    rollExtremes.minRemovals = rOption;
                }
                if(res.maxEV > rollExtremes.maxEV){
                    rollExtremes.maxEV = res.maxEV;
                    rollExtremes.maxRemovals = rOption;
                }
            }
            expectedValues.minEV += lastRolled == -1 ?
                    rollExtremes.minEV * (this.probKey[i] / this.permutations): rollExtremes.minEV;

            expectedValues.maxEV += lastRolled == -1 ?
                   rollExtremes.maxEV * (this.probKey[i] / this.permutations): rollExtremes.maxEV;

            expectedValues.maxRemovals = rollExtremes.maxRemovals;
            expectedValues.minRemovals = rollExtremes.minRemovals;
        }

        return expectedValues;
    }

    private List<List<Integer>> getRemovalOptions(List<Integer> currState, int roll, int idx, int depth) {
        if (roll == 0) {
            return new ArrayList<>(Collections.singletonList(new LinkedList<>()));
        }
        List<List<Integer>> rList = new ArrayList<>();
        int i = idx;
        while (i < currState.size() && currState.get(i) <= roll) {
            int val = currState.get(i);
            List<List<Integer>> successor = getRemovalOptions(currState, roll - val, i + 1, depth + 1);
            for (List<Integer> seq : successor) {
                seq.add(val);
                rList.add(seq);
            }
            i++;
        }
        if(depth == 0 && rList.size() == 0){
            rList.add(new LinkedList<>());
        }
        return rList;
    }
    private class Result {
        private double minEV, maxEV;
        private List<Integer> minRemovals, maxRemovals;
        private Result(double minEV, double maxEV){
            this.minEV = minEV;
            this.maxEV = maxEV;
        }
        @Override
        public String toString() {
            return "minimize removal: " + this.minRemovals + "\nminimizing expected value: " + this.minEV + "\n\n" +
                    "maximize Removal: " + this.maxRemovals + "\nmaximizing expected value: " + this.maxEV;
        }
    }
}
