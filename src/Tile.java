import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Tile {
    private HashSet<Integer> possibilities;
    public static final HashSet<Integer> ALL_POSSIBILITIES;

    static {
        ALL_POSSIBILITIES = new HashSet<>(Arrays.asList(1,2,3,4,5,6,7,8,9));
    }

    //The Tile class defaults to a 9x9 board but you can change the amount to 4x4 or 16x16 here
    // before
    static void changeMaxPossibilities(Integer boardSize) {
        ALL_POSSIBILITIES.clear();
        //This would be shorter/clearer as a for, I just wanted to experiment with streams.
        ALL_POSSIBILITIES.addAll(IntStream.rangeClosed(1,boardSize)
                                          .boxed()
                                          .collect(Collectors.toList()));
    }

    public Tile() {
        possibilities = new HashSet<>(ALL_POSSIBILITIES);
    }

    public Tile(int number) {
        possibilities = new HashSet<>(Collections.singletonList(number));
    }

    public Boolean solved() {
        return (possibilities.size() == 1);
    }

    //While it may be faster to simply go possibilities = new HashSet(numbers), (or
    // possibilities.clear(); possibilities.addAll(numbers); ) it allows for more
    //erroneous code paths, we should never be adding to the possibilities of a tile.
    public void setPossibilities(HashSet<Integer> numbers) {
        possibilities.retainAll(numbers);
    }

    public void removePossibilities(HashSet<Integer> numbers) {
        possibilities.removeAll(numbers);
    }

    public Integer value() {
        if (solved()) {
            return possibilities.iterator().next();
        } else {
            return 0;
        }
    }

    public boolean containsSome(HashSet<Integer> inputSet) {
        for (Integer input : inputSet) {
            if (possibilities.contains(input)) {
                return true;
            }
        }
        return false;
    }

    public Integer size() {
        return possibilities.size();
    }

    public HashSet<Integer> getPossibilties() {
        return new HashSet<>(possibilities);
    }
}
