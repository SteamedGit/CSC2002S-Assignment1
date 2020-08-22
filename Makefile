JAVAC=/usr/bin/javac
.SUFFIXES: .java .class
SRCDIR=src
BINDIR=bin

$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<

CLASSES= ParallelBasin.class ParallelExtraction.class FindBasin.class 
CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)
default: $(CLASS_FILES)
clean:
	rm $(BINDIR)/*.class

run:
	java -cp bin FindBasin "data/large_in.txt" "test_out.txt"
runTests:
	java -cp bin FindBasin "pt" "data/large_in.txt" "50" > "large_PARALLEL_180000.txt"

run600:
	java -cp bin FindBasin "pt" "data/large_in.txt" "50" > "large_PARALLEL_600.txt"
run700:
	java -cp bin FindBasin "pt" "data/large_in.txt" "50" > "large_PARALLEL_700.txt"
run800:
	java -cp bin FindBasin "pt" "data/large_in.txt" "50" > "large_PARALLEL_800.txt"
run900:
	java -cp bin FindBasin "pt" "data/large_in.txt" "50" > "large_PARALLEL_900.txt"
run1000:
	java -cp bin FindBasin "pt" "data/large_in.txt" "50" > "large_PARALLEL_1000.txt"
run1500:
	java -cp bin FindBasin "pt" "data/large_in.txt" "50" > "large_PARALLEL_1500.txt"
run2000:
	java -cp bin FindBasin "pt" "data/large_in.txt" "50" > "large_PARALLEL_2000.txt"
runSeq:
	java -cp bin FindBasin "st" "data/large_in.txt" "50"

