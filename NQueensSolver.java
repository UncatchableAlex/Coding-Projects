import java.util.*;

/**
 * The NQueensSolver is designed to provide all possible ways that n queens can be fit on a chessboard
 * sized nxn such that none of the queens attack each other. Users can interact with the class through
 * its one public method (called solveNQueens(int n) once an instance of NQueensSolver has been created.
 *
 * @author Alex Meislich
 * @date 27 May 2020
 */
class NQueensSolver {
    private int n;
    private List<List<String>> rList = new ArrayList<>();

    public List<List<String>> solveNQueens(int n) {
        this.n = n;
        ArrayList<Integer> cols = new ArrayList<>();
        HashSet<Integer> diags = new HashSet<>();
        setLists(n, cols, diags);
        fillReturnList(cols, diags, 0, null);
        return rList;
    }

   
    private void addEntry(Coordinate co) {
        Coordinate c = co;
        List<String> newList = new LinkedList<>();
        for(int i = 0; i < this.n; i++){
            String str = "";
            for(int j = 0; j < this.n; j++) {
                if (j != c.col) {
                    str += ".  ";
                } else {
                    str += "Q  ";
                }
            }
            newList.add(0,str);
            if(i != this.n - 1) {
                c = c.prev;
            }
        }
        this.rList.add(newList);
    }

    
    private void fillReturnList(ArrayList<Integer> cols, HashSet<Integer> diags, int row, Coordinate lastPlaced) {
        if(row == this.n){
            addEntry(lastPlaced);
            return;
        }
        Coordinate newQueenLoc;
        int col;
        int diag1;
        int diag2;
        
        for (int j = 0; j < cols.size(); j++) {
            col = cols.get(j);
            diag1 = col + row + 1;
            diag2 = -1 * (this.n - row + col);
            if (diags.contains(diag1) && diags.contains(diag2)) {
                newQueenLoc = new Coordinate(col, row, lastPlaced);
                cols.remove(j);
                diags.remove(diag1);
                diags.remove(diag2);
                fillReturnList(cols,diags,row + 1, newQueenLoc);
                cols.add(j, col);
                diags.add(diag1);
                diags.add(diag2);
            }
        }
    }

    private void setLists(int n, ArrayList<Integer> cols, HashSet<Integer> diags) {
        for (int i = 0; i < n; i++) {
            cols.add(i);
        }
        for (int i = 1; i <= ((n * 2) - 1); i++) {
            diags.add(i);
            diags.add(-1 * i);
        }
    }


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
