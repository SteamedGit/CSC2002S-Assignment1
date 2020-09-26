JAVAC=/usr/bin/javac
.SUFFIXES: .java .class
SRCDIR=src
BINDIR=bin
DOCDIR=doc

$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<

CLASSES= ParallelBasin.class ParallelExtraction.class FindBasin.class 
CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)
default: $(CLASS_FILES)
clean:
	rm $(BINDIR)/*.class

run:
	java -cp bin FindBasin "data/large_in.txt" "test.txt" "sFind" "pExtract"
runTests:
	java -cp bin FindBasin "pt" "data/large_in.txt" "3500" > "large_PARALLEL_3500.txt"
	java -cp bin FindBasin "pt" "data/large_in.txt" "4000" > "large_PARALLEL_4000.txt"
	java -cp bin FindBasin "pt" "data/large_in.txt" "4500" > "large_PARALLEL_4500.txt"
	java -cp bin FindBasin "pt" "data/large_in.txt" "5000" > "large_PARALLEL_5000.txt"
	java -cp bin FindBasin "pt" "data/large_in.txt" "5500" > "large_PARALLEL_5500.txt"
	java -cp bin FindBasin "pt" "data/large_in.txt" "6000" > "large_PARALLEL_6000.txt"
	java -cp bin FindBasin "pt" "data/large_in.txt" "6500" > "large_PARALLEL_6500.txt"
	java -cp bin FindBasin "pt" "data/large_in.txt" "7000" > "large_PARALLEL_7000.txt"
	java -cp bin FindBasin "pt" "data/large_in.txt" "7500" > "large_PARALLEL_7500.txt"
	java -cp bin FindBasin "pt" "data/large_in.txt" "8000" > "large_PARALLEL_8000.txt"
	java -cp bin FindBasin "pt" "data/large_in.txt" "8500" > "large_PARALLEL_8500.txt"
	java -cp bin FindBasin "pt" "data/large_in.txt" "9000" > "large_PARALLEL_9000.txt"
	

run44000:
	java -cp bin FindBasin "pt" "data/med_in.txt" "44000"
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
run80000:
	java -cp bin FindBasin "pt" "data/large_in.txt" "80000" 
runSeq:
	java -cp bin FindBasin "st" "data/large_in.txt" "50"
docs:
	javadoc -d $(DOCDIR) src/*.java

