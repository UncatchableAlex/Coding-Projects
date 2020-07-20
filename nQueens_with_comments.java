import java.util.*;

/**
 * The NQueensSolver is designed to provide all possible ways that n queens can be fit on a chessboard
 * sized nxn such that none of the queens attack each other. Users can interact with the class through
 * its one public method (called solveNQueens(int n)) once an instance of NQueensSolver has been created.
 *
 * @author Alex Meislich
 * @date 27 May 2020
 */
class NQueensSolver {
    //store the value n which determines how many queens are are trying to place on the n by n board:
    private int n;

    //Make a blank list called rList holding all of our found solutions. The list will be updated as new solution are
    //found. Every sublist within rList list will hold a different solution where every String stored
    //within each sublist will represent a row on the board of the solution represented by that sublist.
    // Each such string will be n characters long and will consist of '.'s and 'Q's to represent the location
    // of a blank square or the location of a queen.
    private List<List<String>> rList = new ArrayList<>();


    /**
     * solveNQueens is the method which users can call to find all solutions, store them in rList, and
     * return the completed rList.
     * @param n the size of the board (n x n) and the number of queens that should be placed on the board.
     * @return the list of solutions
     */
    public List<List<String>> solveNQueens(int n) {
        //set n:
        this.n = n;
        //make a new ArrayList called cols which will represent all of the columns
        //on the board which don't yet have queens:
        ArrayList<Integer> cols = new ArrayList<>();

        //represent all the diagonals on the board which don't yet have queens:
        HashSet<Integer> diags = new HashSet<>();

        //fill cols with all the board's columns (0-n) and fill diags with all diagonals:
        setLists(n, cols, diags);

        //fill rList with all solutions:
        fillReturnList(cols, diags, 0, null);

        //return the completed list of solutions:
        return rList;
    }

    /**
     * addEntry is a helper method which adds a new solution to rList
     * @param co is the last Coordinate object found in the solution. This Coordinate will
     *           always signify the location of a queen on the last row of the board.
     */
    private void addEntry(Coordinate co) {
        //make a new Coordinate which will hold the location of the next queen to be placed
        //while creating a new String to represent the row of the board that that queen is on.
        Coordinate c = co;

        //make a new List which will hold all the rows (Strings) of the entry to be added to rList:
        List<String> newList = new LinkedList<>();
        //for some reason, I've found that an ArrayList here is faster, but I don't understand why, so I'm going to
        //use a LinkedList anyway (we're going to be doing a lot of adds to the head of the list, so it makes sense)

        //for every row that this entry will have:
        for(int i = 0; i < this.n; i++){

            //make a new empty String to store all of the squares in that row:
            String str = "";

            //for each square in that row:
            for(int j = 0; j < this.n; j++) {
                //if it is NOT the location of the queen represented by the Coordinate c, then add a "." to str
                //to signify no queen:
                if (j != c.col) {
                    str += ".  ";

                    //otherwise, add a "Q" to represent a queen:
                } else {
                    str += "Q  ";
                }
            }
            //add the row to the list.
            newList.add(0,str);
            if(i != this.n - 1) {
                //update c to show the Coordinate for the next row:
                c = c.prev;
            }
        }
        //add the entry to rList:
        this.rList.add(newList);
    }

    /**
     * This method fills rList with all the solutions to size n. It takes a int called row which signifies the row it
     * will try to place the next queen on. Keep in mind that every queen must occupy its own row, column, and both
     * diagonals. It can't share any of these things with any other queen. It will loop through all of the available
     * squares in the given row to find a square where the column and both of the diagonals are unoccupied by queens.
     * It will then recurse by incrementing the row, and setting the Coordinate lastPlaced  argument to the available
     * square which it has just found. When its row equals n, then it calls addEntry with the last found coordinate 
     * because it has, at that point, successfully placed n queens on the board and therefore found a solution.
     *
     * @param cols the list of columns unoccupied by queens
     * @param diags the list of diagonals unoccupied by queens
     * @param row the row it will try to place the next queen on.
     * @param lastPlaced the lat created Coordinate signifying where the queen was placed on the previous row.
     */
    private void fillReturnList(ArrayList<Integer> cols, HashSet<Integer> diags, int row, Coordinate lastPlaced) {
        //if it has placed n queens so far (the first call for this method starts on row 0. There is no row n),
        //add an entry for the solution which has been found.
        if(row == this.n){
            addEntry(lastPlaced);
            return;
        }
        //declare a new Coordinate for the next place where a queen is going to be placed (on this row):
        Coordinate newQueenLoc;

        //the column for the square to be evaluated.
        int col;

        //the diagonals of the square to be evaluated:
        int diag1;
        int diag2;

        //for each column in the row / for each square in the row:
        for (int j = 0; j < cols.size(); j++) {
            //grab the next available column from the unoccupied columns list:
            col = cols.get(j);

            //set the diagonals for square we're evaluating
            diag1 = col + row + 1;
            diag2 = -1 * (this.n - row + col);

            //if the diagonals are empty, they will exist in our empty diagonals data structure. If they are there,
            //then the square is a valid place to put the next queen:
            if (diags.contains(diag1) && diags.contains(diag2)) {
                //make a new Coordinate where we will be placing the next queen:
                newQueenLoc = new Coordinate(col, row, lastPlaced);

                //the column and diagonals that this square lies on are now occupied by a queen and should be
                //removed from the unoccupied lists:
                cols.remove(j);
                diags.remove(diag1);
                diags.remove(diag2);

                //try to find a spot for the next queen:
                fillReturnList(cols,diags,row + 1, newQueenLoc);

                //Backtrack. Remove that last queen, add the column and diagonals back to the unoccupied lists.
                //ie reset for trying to place the queen on the next square over.
                cols.add(j, col);
                diags.add(diag1);
                diags.add(diag2);
            }
        }
    }

    /**
     * setLists put all the columns and diagonal which exist on a size n board into their respective data structures.
     * @param n the size of the board.
     * @param cols the ArrayList being used to store the unoccupied cols.
     * @param diags the HashSet being used to store the unoccupied diagonals.
     */
    private void setLists(int n, ArrayList<Integer> cols, HashSet<Integer> diags) {
        //add all the columns to cols:
        for (int i = 0; i < n; i++) {
            cols.add(i);
        }

        //add all the diagonals to diags:
        for (int i = 1; i <= ((n * 2) - 1); i++) {
            diags.add(i);
            diags.add(-1 * i);
        }
    }


    /**
     * The subclass Coordinate is used to keep track of the column, row of a newly placed queen as well as the
     * Coordinate of the last placed queen.
     */
    private class Coordinate {
        private int col;
        private int row;
        private Coordinate prev;
        private Coordinate(int col, int row, Coordinate prev) {
            this.col = col;
            this.row = row;
            this.prev = prev;
        }
        private Coordinate(int col, int row){
            this(col,row,null);
        }
    }
}
