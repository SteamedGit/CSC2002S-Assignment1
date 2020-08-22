JAVAC=/usr/bin/javac
.SUFFIXES: .java .class
SRCDIR=src
BINDIR=bin

$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<

CLASSES= ParallelBasin.class FindBasin.class 
CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)
default: $(CLASS_FILES)
clean:
	rm $(BINDIR)/*.class

run:
	java -cp bin FindBasin "large_in.txt" "test_out.txt"
runTests:
	java -cp bin FindBasin "st" "large_in.txt" "50"
	java -cp bin FindBasin "pt" "large_in.txt" "50"

