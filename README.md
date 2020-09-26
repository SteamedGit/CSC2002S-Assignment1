# CSC2002S-Assignment1

## Overview
This assignment required an investigation into the potential performance benefits of parallelizing 
a problem that was inherently suited to parallelization. The problem was highly suitable because it
involved classifying points in a grid of height values as basins, and this required a comparison of their
height to all their immediate neighbours'. Thus, classification was extremely localized, hence minimal coordination
between threads was required. For the above reasons, the *map pattern* of parallel programming was chosen for this assignment, 
via the Java Fork-Join Framework, which is far more lightweight than traditional Java Threads.

## Classification Process
A point is regarded as a basin if its height is less than all of its neighbours by 0.01m or more.
Points on the edge are not considered because they do not have a full ring of neighbours and thus can never be basins.

### Example: A 4x4 grid

|     | (0)  | (1)  | (2)  | (3)  |
|-----|------|------|------|------|
| (0) | 1.00 | 0.90 | 0.95 | 0.80 |
| (1) | 1.00 | 0.95 | 0.90 | 0.80 |
| (2) | 0.85 | 0.60 | 0.80 | 0.75 |
| (3) | 1.00 | 1.00 | 1.00 | 1.00 |

The only basin in this grid is at (2,1); all of its neighbours {1.00, 0.95, 0.90, 0.85, 0.80, 1.00, 1.00, 1.00} 
have heights that are greater than its by 0.01m or more.

## How to use 

To simply run the program use:</br>
<code> java -cp bin FindBasin <data> <output> <find algorithm> <type of extraction> </code>
 
Find Alorithms:
sFind - Sequential Algorithm
pFind - Parallel Algorithm 

Extraction 
sExtract - Sequential Extraction
pExtract - Parallel Extraction

To benchmark the parallel algorithm:
java -cp bin FindBasin "pt" <data file path> <Sequential cutoff>

To benchmark the sequential program:
java -cp bin FindBasin "st" <data file path>

## Documentation

Further information regarding all the code can be found in the docs folder which contains javadocs for this assignment.

## Known issues

When the basin has a relative height deficit of exactly 0.01, it is not classified as a basin. This is a floating-point precision
issue. This assignment explicitly required the use of floating point numbers and thus this issue it not considered to be significant.
For exact precision, *BigDecimal* is a more appropriate choice.
