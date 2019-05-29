# Sudoku-Solver
A simple sudoku solver.
A rewritten version of an old project.

I question a lot about my data structures and processing information on this project, but I really like the simplicity.

Basically you enter an unfinished grid which has a whole number square root; 4x4, 9x9, 16x16, etc.
Upon doing that your input is assorted into three lists, the boxes, the rows and the columns.
After inputing all the tiles and processing their possibilities (you use zero to represent unsolved tiles) 
basically the main logic for the program is the setReduction() method in the "Group" class. 

The internal process is rather simple, you generate every sublist of every possibility belonging to unsolved tiles; 
for example if a list like this was in a group : {0,2,4,6,0,3,7,5,0} it'll generate every sublist of {1,8,9};
this'll result in this collection of sets : [{1,8,9},{1,8},{1,9},{8,9},{1},{8},{9}], these sets are then checked against the 
tiles in a group. If the amount of instances of a set is the same as the amount of elements in that set, then all tiles 
including those possibilities have their possibilites reduced to just those sets.

So given a single tile with the possibility {9} in a group, it'll have all possibilities but 9 removed.

And that seems to be enough to solve a majority of sudoku puzzles, but when it gets to the really hard ones, the puzzles with
2-3 possibilities in every slot and you can't be sure if any are the right one it kinda breaks down.
It would be pretty simple to simply brute force the rest of the results, like most sudoku solvers do at that stage, 
but I'd prefer to leave it as a logical challenge for later on.
