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

    private final double[] probKey = {0, 0, 1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1};//probKey[i] = number of permutations of i
    private final int permutations = 36; //number of possible permutations two dice can make (6 * 6 == 36)

    /**
     * The main method can be used to enter queries and interact with the class.
     */
    public static void main(String[] args) {
        LinkedList<Integer> currState = new LinkedList<>(Arrays.asList(1,2,3,4,9));
        Knockout myGame = new Knockout();
        long start = System.currentTimeMillis();
        System.out.println(myGame.getExpectedValue(currState, 4) + "\n" + (System.currentTimeMillis() - start));
    }


    /**
     * Takes as arguments the currentState of the board in list-form and the sum of the two dice.
     *
     * @param currState The numbers remaining on the board.
     * @param roll The sum of the two dice rolled.
     * @return A Result object containing the minimum and maximum expected values of the game (assuming perfect play
     * aimed at either minimizing or maximizing).
     */
    public Result getExpectedValue(List<Integer> currState, int roll){
        return getExpectedValueHelper(currState, roll, false);
    }


    /**
     * Helps the getExpectedValue method. Mainly, however, this helps the user by not making them worry about this
     * confusing extra parameter called "gameOver". The user simply calls getExpectedValue() which has one fewer
     * argument.
     *
     * @param currState The current state of the game in list-form
     * @param lastRolled The sum of the two dice rolled.
     * @param gameOver Describe of the game is over yet. I.E. Were any numbers removed last turn?
     * @return A Result object containing the minimum and maximum expected values of the game (assuming perfect play
     *      * aimed at either minimizing or maximizing).
     */
    private Result getExpectedValueHelper(List<Integer> currState, int lastRolled, boolean gameOver) {
        //  this is the Result which will ultimately be returned. It will be modified according to evaluations will be
        //  compared to it to check for more suitable removal options.
        Result expectedValues = new Result(0,0);

        // base case. If the game is over, then no removals were able to occur in previous roll, so now the game is over.
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

        /*start and end will normally be 2 and 12 respectively and a variable "i" will iterate from start to end
        representing a possible roll from the dice which will need to be evaluated. If it is depth 0 and a lastRollled
         argument has been provided, we will only want to evaluate that roll and no others.*/
        int start = lastRolled != -1 ? lastRolled : 2;
        int end = lastRolled != -1 ? lastRolled : 12;
        for (int i = start; i <= end; i++) {

/*          for each possible roll from 2 to 12, every possible combination of removals will have to be evaluated.
            rollExtremes will have to keep track of which combinations of rolls produce the highest and lowest
            expected values:                                   */
            Result rollExtremes = new Result (Integer.MAX_VALUE, Integer.MIN_VALUE);
            List<List<Integer>> removalOptions = getRemovalOptions(currState, i, 0, 0);

            // for each removal option:
            for(List<Integer> rOption : removalOptions){
                // get the expected value of removing that option:
                List<Integer> nextState = new LinkedList<>(currState);
                nextState.removeAll(rOption);
                Result res  = getExpectedValueHelper(nextState, -1, rOption.size() == 0);

                // if the expected value (if playing to minimize) is lower than the the lowest found expected value
                // for this roll, then update:
                if(res.minEV < rollExtremes.minEV){
                    rollExtremes.minEV = res.minEV;
                    rollExtremes.minRemovals = rOption;
                }

                // if the expected value (if playing to maximize) is lower than the the lowest found expected value
                // for this roll, then update:
                if(res.maxEV > rollExtremes.maxEV){
                    rollExtremes.maxEV = res.maxEV;
                    rollExtremes.maxRemovals = rOption;
                }
            }

            /* If this next part is confusing, then the reader is encouraged to brush up on calculating basic
             expected values. Basically, the expected minimum and maximum expected values of each possible roll are
             aggregated based on the likelihood of that roll occurring. In this case that likelihood can be phrased
             as the number of ways that that number can be rolled on two dice divided by 36 (the total number of
             possible permutations. */
            expectedValues.minEV += lastRolled == -1 ?
                    rollExtremes.minEV * (this.probKey[i] / this.permutations): rollExtremes.minEV;

            expectedValues.maxEV += lastRolled == -1 ?
                   rollExtremes.maxEV * (this.probKey[i] / this.permutations): rollExtremes.maxEV;

            //this next part is only relevant for the first layer of depth where the user has provided a specific roll:
            expectedValues.maxRemovals = rollExtremes.maxRemovals;
            expectedValues.minRemovals = rollExtremes.minRemovals;
        }

        return expectedValues;
    }


    /**
     * Helps getExpectedValueHelper() by telling it all of the combinations of numbers that can be removed from the
     * current game-state such that the sum of the numbers removed equals the sum of the roll. If the problem size was
     * much larger than 9, then, almost certainly, some kind of dynamic programming solution would be needed here.
     * This simpler, brute force, solution is probably just as fast (if not faster) for this input size and will suit
     * this problem nicely.
     *
     *
     * @param currState The current state of the game in list form.
     * @param roll The sum of the two dice rolled.
     * @param idx The index of the last removal evaluated in currState. Used for recursion
     * @param depth Describes how many potential removals have been accumulated so far.
     * @return A list of lists where each sublist contains a combination of number which sum to the roll and can be
     * removed.
     */
    private List<List<Integer>> getRemovalOptions(List<Integer> currState, int roll, int idx, int depth) {
        /*I'm not going to comment this bit because how it works is not all that important to the problem as a whole.
        Feel free to figure it out on your own if you are interested. It isn't especially complicated, but still quite
        fascinating */

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

    /**
     * The Result subclass will hold all the information which an evaluation of a removal from a game-state can give.
     */
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
