import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
            //Originally there was more predicates defined in the tile class, but after
            // some experimentation I found out that this method covers all of them anyway.
            BiPredicate<Tile, HashSet<Integer>> hiddenTuple = Tile::containsSome;

            if (tupleCheck(set, hiddenTuple)) {
                reduceTupleSet(set, hiddenTuple);
            }
        }
    }

    //Returns true iff there's as many instances of a tuple within our tile array as there are
    // elements within that tuple. e.g. {{2,1}, {3,1}, {3,2,1}} returns true on {2,1} because there
    // are exactly 2 instances of sets containing {2,1}, but false on {1}, because there are three
    // instances of sets containing {1}
    private boolean tupleCheck(HashSet<Integer> set,
                               BiPredicate<Tile,HashSet<Integer>> predicate) {
        //Used as a counter to see how many instances there are relative to the set size.
        int currentAmount = set.size();
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
            }
        }
    }

    //Gets all valid unique subsets of a given hashSets and puts them in an given arraylist of
    // hashSets. It does this by adding the initial set to the arraylist, generating every unique
    // set which can be made by removing a single element of the input set, then, if the set isn't
    // empty or already contained within the arraylist, it's added to it.
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

}
