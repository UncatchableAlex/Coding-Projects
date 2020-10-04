import java.util.ArrayList;
import java.util.Arrays;
/**
 * The question is this: Given a set of 7 or fewer integers and another integer designated as a target
 * value, is it possible to form an expression equal to the target value using only basic mathematical
 * symbols (+, - , *, /) and the integers in the set? The rules are: all input integers need to be positive,
 * subtraction can only yield a positive difference, division is only allowed when the quotient is a
 * positive integer, numbers from the set can only be used once, and not all numbers from the set must be used.
 *
 * The program should output a mathematical expression equivalent to the target value
 * if such an expression exists. If no such expression exists, an expression should be outputted equivalent
 * to a value as close to the target value as possible (see EX 3).
 *
 * The way that I chose to approach this problem was with a depth-first brute-force search
 * of every possible way the symbols could be combined with each unique pair of integers
 * (and subsequent results from previous Expressions) from the set. 7 numbers and 4 symbols seemed
 * like a small enough initial problem size to warrant the use of such a demanding exponential-time
 * algorithm. I'm not entirely convinced that there is a faster way to solve this...
 *
 *
 * EX 1:
 * int[] set = {1,2,3,4,5,6};
 * int result = 20;
 *
 * OUTPUT:
 * "POSSIBLE"
 * 4*5 = 20
 *
 * EX 2:
 {37, 43, 61, 79, 119, 127, 197}  result: 47000
 POSSIBLE
 ((43 + 37) - (197 - 127)) * ((79 * 61) - 119) = 47000
 *
 *
 * EX 3:
 {37, 43, 61, 79, 119, 127, 197}  result: 470000
 IMPOSSIBLE
 (((43 * 37) - (79 + 61)) * (197 + 127)) - 119 = 470005
 *
 *
 * @author Alex Meislich
 * @version 1.0
 * @date 20 March 2020
 */
class ExpressionBuilder {
    //this will hold the Expression evaluated to have the result closest to the target value
    private Expression closest;

    /**
     * This is the main method. This is where the code starts. The code gets evaluated in this order.
     * @param args
     */
    public static void main(String[] args) {
        //transfer the array of starting integers into an ArrayList.
        //Integer[] arr = {105,67,92,36,56,28,15};
        //int target = 455552;
        Integer[] arr = {1000,2,1000,1000,1000};
        int target = 2;
        ArrayList<Integer> arrList = new ArrayList<>(Arrays.asList(arr));
        ExpressionBuilder eb = new ExpressionBuilder();
        //find the answer.
        Expression bestAnswer = eb.compute(arrList, target);
        //give a printout based on if bestAnswer evaluates to the target value or if it misses.
        if (bestAnswer != null) {
            System.out.println(eb.arrayToString(arr) + "  result: " + target + "\n" + "POSSIBLE \n" + bestAnswer);
        } else {
            System.out.println(eb.arrayToString(arr) + "  result: " + target + "\n" + "IMPOSSIBLE \n" + eb.closest);
        }
    }

    public Expression compute(ArrayList<Integer> list, int target){
        return this.computeHelper(list, target, null);
    }

    /**
     * ComputeHelper(list, prevExp) takes in a list of usable integers and a previously created expression to combine all
     * possible pairings from the list using all possible operations. New expressions are created from these pairings
     * with the prevExp parameter cited as the previous expression for the new expressions. The updated list and
     * new expressions are used as parameters for compute's recursive calls. When a newly created expression evaluates
     * to the target value, that expression in returned. If no such Expression is found, the method returns null.
     * @param list The list of usable integers to be used to make expressions.
     * @param prevExp The previous expression created.
     * @return The expression which evaluates to the value or null if no such expression exists.
     */
    private Expression computeHelper(ArrayList<Integer> list, int target, Expression prevExp) {
        //the following nested for loop gives all possible pairings between elements in the list.
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < i; j++) {
                //these are the elements pairings.
                int first = list.get(i);
                int second = list.get(j);
                //clone the list before combining them in different ways.
                ArrayList<Integer> newList = new ArrayList<>(list);
                newList.remove(i);
                newList.remove(j);
                //make all the possible Expressions.
                Expression plusExp = new Expression(first, "+", second, prevExp, newList, target, this.closest);
                Expression timesExp = new Expression(first, "*", second, prevExp, newList, target, this.closest);
                Expression minusExp = new Expression(first, "-", second, prevExp, newList, target, this.closest);
                Expression divExp = null;
                //division is a little more complicated.
                //we need to make sure that the quotient is an integer and that the denominator isn't zero.
                if (second != 0 && first % second == 0) {
                    divExp = new Expression(first, "/", second, prevExp, newList, target, this.closest);
                }
                else if (first != 0 && second % first == 0) {
                    divExp = new Expression(second, "/", first, prevExp, newList, target, this.closest);
                }
                //if we have reached the target, return the Expression. (base case)
                if (plusExp.outcome == target) {
                    return plusExp;
                }
                if (timesExp.outcome == target) {
                    return timesExp;
                }
                if (minusExp.outcome == target) {
                    return minusExp;
                }
                if ((((double) first) / second) == target || (((double) second) / first) == target) {
                    return divExp;
                }
                //do recursive calls on all new Expressions and their lists. (recursive case)
                //return any expression that results in a base case
                Expression plusPath = computeHelper(plusExp.list, target, plusExp);
                if(plusPath != null){
                    return plusPath;
                }
                Expression timesPath = computeHelper(timesExp.list, target, timesExp);
                if(timesPath != null){
                    return timesPath;
                }
                Expression minusPath = computeHelper(minusExp.list, target, minusExp);
                if(minusPath != null){
                    return minusPath;
                }
                Expression divPath = null;
                if(divExp != null) {
                    divPath = computeHelper(divExp.list, target, divExp);
                }
                if(divPath != null){
                    return divPath;
                }
            }
        }
        return null; //if we were unable to find any suitable Expression, return null.
    }

    /**
     * Turns a 1d int array into a String
     * @param arr the array to be converted
     * @return the array in String form
     */
    private String arrayToString(Integer[] arr){
        String str = "{";
        for(int x : arr){
            str += (x + ", ");
        }
        str = str.substring(0, str.length() - 2);
        str += "}";
        return str;
    }
}

/**
 * The Expression class holds two integers, a mathematical symbol, the result of that symbol applied to the
 * integers. It also temporarily holds an up to date list of still unused/newly combined integers from the
 * original set. All Expressions also hold the Expression performed previous to their own. This way, any
 * Expression which evaluates to the target value, can be traced back, step by step, through all the
 * Expressions that facilitated it. The last piece of information that Expressions can hold is two more
 * Expressions in String form (strA and strB). These two variable are necessary for the toString method.
 */
class Expression {
    //the integers to be combined:
    protected int a;
    protected int b;

    //the operation specifying how the integers will be combined:
    protected String sym; //+ or - or * or /

    //the previous Expression:
    protected Expression prev = null;

    //the evaluation of the two integers and the operation
    protected int outcome;

    //the list of usable integers available for further combining.
    protected ArrayList<Integer> list;

    //the subExpression Strings.
    protected String strA;
    protected String strB;

    /**
     * This is the constructor for an Expression object.
     * @param a the first integer
     * @param sym the operation ("+", "-", "*", or "/")
     * @param b the second integer
     * @param prev the previous Expression
     * @param list the list of usable integers to be combined.
     */
    public Expression(int a, String sym, int b, Expression prev, ArrayList<Integer> list, int target, Expression closest) {
        this.a = a;
        this.b = b;
        this.sym = sym;
        this.prev = prev;
        //see if this is the closest we have gotten to the target value.
        try {
            if (Math.abs(this.evaluate() - target) < Math.abs(closest.evaluate() - target)) {
                closest = this;
            }
            //if there isn't a previously closest value, then this expression is closest by default.
        } catch (NullPointerException e) {
            closest = this;
        }
        //clone the list
        this.list = new ArrayList<>(list);
        this.outcome = this.evaluate();
        //add the outcome to the new list
        this.list.add(outcome);
        //for now, the two Strings will hold this.a and this.b in String format.
        this.strA = Integer.toString(this.a);
        this.strB = Integer.toString(this.b);
    }

    /**
     * Solve for the operation applied to the two integers.
     * @return the evaluation of the operation applied to the two integers.
     */
    public int evaluate() {
        int outcome;

        //do an operation based on what the sym variable is:
        switch (sym) {
            case "+":
                outcome = this.a + this.b;
                break;
            case "-":
                if(this.b > this.a){
                    int temp = this.a;
                    this.a = this.b;
                    this.b = temp;
                }
                outcome = Math.abs(this.a - this.b);
                break;
            case "*":
                outcome = this.a * this.b;
                break;
            case "/":
                outcome = this.a / this.b;
                break;
            default:// this case (hopefully) will never happen. If it does, everything breaks.
                throw new RuntimeException("Something went horribly wrong in the 'evaluate' function.");
        }
        return outcome;
    }

    private ArrayList<Expression> getSequence() {
        return getSequenceHelper(this);
    }

    /**
     * Find the sequence of evaluations that facilitated this one.
     * @param exp the current expression
     * @return an ArrayList of Expressions.
     */
    private ArrayList<Expression> getSequenceHelper(Expression exp) {
        try {
            ArrayList<Expression> temp = getSequenceHelper(exp.prev);
            temp.add(exp);// add this expression to the ArrayList and return.
            return temp;
        } catch (NullPointerException e) {//base case. There is no previous expression
            return new ArrayList<>(); // return a blank ArrayList.
        }
    }

    /**
     * prints the whole expression.
     * @return a whole mathematical expression as a String.
     */
    @Override
    public String toString(){
        //get the sequence of previous expressions.
        ArrayList<Expression> expList = this.getSequence();
        Expression curr;
        Expression compareToCurr;
        // for each Expression, match its outcome to the a or b variable in another expression and
        for(int i = 0; i < expList.size() - 1; i++){
            curr = expList.get(i);
            for(int j = i + 1; j < expList.size(); j++){
                compareToCurr = expList.get(j);
                if(curr.outcome == compareToCurr.a){
                    compareToCurr.strA = "(" + curr.strA + " " + curr.sym + " " + curr.strB + ")";
                    break;
                }
                if(curr.outcome == compareToCurr.b){
                    compareToCurr.strB = "(" + curr.strA + " " + curr.sym + " " + curr.strB + ")";
                    break;
                }
            }
        }
        return this.strA + " " + this.sym + " " + this.strB + " = " + this.outcome;
    }
}
