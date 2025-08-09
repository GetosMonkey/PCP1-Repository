# ABRMAR043
# 9th August 2025 
# Parellelized makefile 


JAVAC=javac
JAVA=java
SRC=ParallelSolution
CLASSES = $(SRC)/DungeonMapParallel.java $(SRC)/HuntParallel.java $(SRC)/DungeonHunterParallel.java
# Default arguments (update these if needed)
ARGS ?= 20 0.2 0  # Replace 'default_arguments' with your specific default arguments, if any


all:
	$(JAVAC) $(CLASSES)

run:
	$(JAVA) -cp $(SRC) DungeonHunterParallel $(ARGS) 

clean:
	rm -f $(SRC)/*.class