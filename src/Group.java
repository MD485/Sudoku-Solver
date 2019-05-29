import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.BiPredicate;

public class Group {
    private ArrayList<Tile> tiles;
    private HashSet<Integer> solvedTiles;

    public boolean solved() {
        //Designed to allow abstraction into higher board tiers later.
        sane();
        return (solvedTiles.size() == Tile.ALL_POSSIBILITIES.size());
    }

    Group() {
        tiles = new ArrayList<>();
        solvedTiles = new HashSet<>();
    }

    //Checks all the single value tile's values are in the "solvedTiles" HashSet,
    //any new value added to the HashSet is removed from all children.
    //Because all the tiles are altered by three separate instances of this group class
    //this method is used to maintain consistency between those instances.
    private void sane() {
        boolean stateChanged;
        do {
            stateChanged = false;
            for (Tile tile : tiles) {
                if (tile.solved() && !solvedTiles.contains(tile.value())) {
                    solvedTiles.add(tile.value());
                    stateChanged = true;
                }
            }
            for (Tile tile : tiles) {
                if (!tile.solved()) {
                    tile.removePossibilities(solvedTiles);
                }
            }
        } while(stateChanged);
    }

    public void addTile(Tile newTile) {
        tiles.add(newTile);
        sane();
    }

    public void narrowDown() {
        sane();
        setReduction();
    }

    private void setReduction() {
        HashSet<Integer> possibilities = new HashSet<>(Tile.ALL_POSSIBILITIES);
        possibilities.removeAll(solvedTiles);
        ArrayList<HashSet<Integer>> uniqueSets = new ArrayList<>();
        getUniqueSets(possibilities, uniqueSets);

        for(HashSet<Integer> set : uniqueSets) {
            BiPredicate<Tile, HashSet<Integer>> nakedTuple = Tile::contains;
            BiPredicate<Tile, HashSet<Integer>> hiddenTuple = Tile::containsSome;
            BiPredicate<Tile, HashSet<Integer>> hiddenIncompleteTuple = Tile::containsOnly;

            /*if (tupleCheck(set, nakedTuple)) {
                reduceTupleSet(set, nakedTuple);
            } else if (tupleCheck(set, hiddenIncompleteTuple)) {
                reduceTupleSet(set, hiddenIncompleteTuple);
            }  else */ if (tupleCheck(set, hiddenTuple)) {
                reduceTupleSet(set, hiddenTuple);
            } //*/
        }
    }

    //Returns true iff there's as many instances of a tuple within our tile array as there are
    // elements within that tuple. e.g. {{2,1}, {3,1}, {3,2,1}} returns true on {2,1} because there
    // are exactly 2 instances of sets containing {2,1}, but false on {1}, because there are three
    // instances of sets containing {1}
    private boolean tupleCheck(HashSet<Integer> set,
                               BiPredicate<Tile,HashSet<Integer>> predicate) {
        //Used as a counter to see how many instances there are relative to the set size.
        Integer currentAmount = new Integer(set.size());
        for (Tile t : tiles) {
            if (predicate.test(t,set)) {
                currentAmount -= 1;
            }
        }
        return (currentAmount == 0);
    }

    private void reduceTupleSet(HashSet<Integer> set,
                                BiPredicate<Tile,HashSet<Integer>> predicate) {
        for (Tile t : tiles) {
            if (predicate.test(t,set)) {
                t.setPossibilities(set);
            } else {
                t.removePossibilities(set);
            }
        }
    }

    private void getUniqueSets(HashSet<Integer> possibilities,
                               ArrayList<HashSet<Integer>> applyTo) {
        Iterator<Integer> hashIterator = possibilities.iterator();
        applyTo.add(possibilities);

        while (hashIterator.hasNext()) {
             HashSet<Integer> subArray = new HashSet<>(possibilities);
             subArray.remove(hashIterator.next());
             if (!applyTo.contains(subArray) && !subArray.isEmpty()) {
                getUniqueSets(subArray, applyTo);
             }
        }
    }

    public int state() {
        Optional<Integer> state = tiles.stream().map(Tile::size).reduce((X, Y) -> X + Y);
        return state.orElse(0);
    }

    public Integer uniqueSubset(Integer value) {
        int subBoxes = (int) Math.round(Math.sqrt(tiles.size()));
        HashSet<Integer> valContainedInIndex = new HashSet<>();
        for (int i = 0; i < subBoxes; i++) {
            for (int j = 0; j < subBoxes; j++) {
                if(tiles.get(i*subBoxes + j).getPossibilties().contains(value)) {
                    valContainedInIndex.add(i);
                }
            }
        }
        if(valContainedInIndex.size() == 1) {
            return valContainedInIndex.iterator().next();
        } else {
            return 0;
        }
    }

    public void removeFromSubset(HashSet<Integer> value, int subSet) {
        int subBoxes = (int) Math.round(Math.sqrt(tiles.size()));
        for (int i = 0; i < subBoxes; i++) {
            tiles.get(i + subSet*subBoxes).removePossibilities(value);
        }
    }
}
