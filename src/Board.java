import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class Board {

    //allPossibilities only for ease of implementation when printing/returning
    private ArrayList<Tile> allPossibilities = new ArrayList<>();
    private ArrayList<ArrayList<Group>> RCBGroups = new ArrayList<>();

    Board(int[][] input) {
        //It's presumed the board will be 4x4, 9x9, 16x16, etc;
        //A lot of the initialisation stages depend the length of a single side.
        int boardSize = input.length;
        double sqrtInput = Math.sqrt(boardSize);

        //Code will only work on sets that have a whole number root.
        if (sqrtInput != Math.floor(sqrtInput) || boardSize != input[0].length) {
            System.out.println("Invalid Input");
            return;
        }

        Tile.changeMaxPossibilities(boardSize);

        initRowsColumnsBoxes(boardSize);
        fillBoard(boardSize, sqrtInput, input);

    }

    private void initRowsColumnsBoxes(int boardSize) {
        ArrayList<Group> Rows = new ArrayList<>();
        ArrayList<Group> Columns = new ArrayList<>();
        ArrayList<Group> Boxes = new ArrayList<>();

        RCBGroups.add(Rows);
        RCBGroups.add(Columns);
        RCBGroups.add(Boxes);

        for (int i = 0; i < boardSize; i++) {
            Rows.add(new Group());
            Columns.add(new Group());
            Boxes.add(new Group());
        }
    }

    private void fillBoard(int boardSize, double sqrtInput, int[][] input) {
        ArrayList<Group> Rows, Columns, Boxes;
        Rows = RCBGroups.get(0);
        Columns = RCBGroups.get(1);
        Boxes = RCBGroups.get(2);

        //Fills each Group with the relevant input index, rows are filled using i,
        //columns are filled using j and the sub-boxes are filled using k.
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                //To calculate the box index, you floor the current row index to the
                //closest power of the square root, then add the division of the column
                //to the square root. e.g. in a 9x9 board, (i = 5) => 3, (j = 5) => 1,
                //(3 + 1) = 4; index[5][5] => box 4 in a 9x9 board, zero indexed.
                //This technique also works for higher board sizes.
                int k = (int) Math.round(
                        (Math.floor(i / sqrtInput) * sqrtInput) + Math.floor(j / sqrtInput));
                Tile newTile;
                if (input[i][j] != 0) {
                    newTile = new Tile(input[i][j]);
                } else {
                    newTile = new Tile();
                }
                allPossibilities.add(newTile);
                Rows.get(i).addTile(newTile);
                Columns.get(j).addTile(newTile);
                Boxes.get(k).addTile(newTile);
            }
        }
    }

    private boolean notSolved() {
        for(Tile tile : allPossibilities) {
            if (!tile.solved()) {
                return true;
            }
        }
        return false;
    }

    public void solve() {
        do {
            printState();
            boolean unchanged = true;
            for (ArrayList<Group> groups : RCBGroups) {
                for (Group g : groups) {
                    int currentState = g.state();
                    if (!g.solved()) {
                        g.narrowDown();
                        if (currentState != g.state()) {
                            unchanged = false;
                        }
                    }
                }
            }
            if (unchanged) {
                System.out.println("Unchanged");
                boardLevelReduction();
            }
        } while (notSolved());
    }

    private void boardLevelReduction() {
        int subBoxes = (int) Math.round(Math.sqrt(Math.sqrt(allPossibilities.size())));
        for (ArrayList<Group> RCB : RCBGroups) {
            lineReduction(RCB, subBoxes);
        }
    }

    private void lineReduction(ArrayList<Group> groups, int subBoxes) {
        for (int i = 0; i < subBoxes; i++) {
            for (int j = 0; j < subBoxes; j++) {
                for(Integer variable : Tile.ALL_POSSIBILITIES) {
                    int self = (i * subBoxes + j);
                    int subSet = groups.get(self).uniqueSubset(variable);
                    if(subSet != 0) {
                        lineReduce(groups, i, subSet, self, subBoxes,
                                new HashSet<>(Collections.singletonList(variable)));
                    }
                }
            }
        }
    }

    public void lineReduce(ArrayList<Group> groups, int rowBox, int subSet, int self,
                           int subBoxes, HashSet<Integer> variable) {
        int maxOfSet = ((rowBox + 1) * subBoxes);
        for(int i = rowBox*subBoxes; i < maxOfSet; i++) {
            if (i != self) {
                groups.get(i).removeFromSubset(variable, subSet);
            }
        }
    }

    public void printState() {
        printState(false);
    }

    public void printState(boolean debugMode) {
        //Maybe boardSize should be a class variable given it's ubiquity.
        int boardSize = (int) Math.sqrt(allPossibilities.size());
        int indent = (int) Math.sqrt(boardSize);
        //Initially 5 because outside of debug mode it'll never exceed it.
        StringBuilder tileString = new StringBuilder(5);

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Tile tile = allPossibilities.get((i*boardSize) + j);

                if (tile.solved()) {
                    tileString.append(' ').append(tile.value()).append(' ');
                } else {
                    tileString.append('[');
                    if (debugMode) {
                        for (Integer a : tile.getPossibilties()) {
                            tileString.append(a).append(',');
                        }
                    } else {
                        tileString.append(tile.size());
                    }
                    tileString.append(']');
                }

                tileString.append(',');
                if ((j + 1) % indent == 0) {
                    tileString.append(' ');
                }
                System.out.print(tileString.toString());

                //I'm assuming with branch prediction this is a lot more efficient than calling
                //.length() over and over again, but I'm not sure.
                if (debugMode) {
                    tileString.delete(0, tileString.length());
                } else {
                    tileString.delete(0, 5);
                }
            }
            System.out.println();
            if((i + 1) % indent == 0){
                System.out.println();
            }
        }
        System.out.println();
    }

}
