 To simply run the program use:
 java -cp bin FindBasin <data file path> <output file path> <find algorithm> <type of extraction>
 
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