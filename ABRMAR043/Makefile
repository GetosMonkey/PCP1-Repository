# ABRMAR043
# 15th August 2025 
# Parellelized makefile 

JAVAC=javac
JAVA=java
SRC=ParallelSolution
CLASSES = $(SRC)/DungeonMapParallel.java $(SRC)/HuntParallel.java $(SRC)/DungeonHunterParallel.java
# Default arguments (update these if needed)
ARGS ?= 200 30 99 # Replace 'default_arguments' with your specific default arguments, if any
#dungeon size, searches, seed

all:
	$(JAVAC) $(CLASSES)

run:
	$(JAVA) -cp $(SRC) DungeonHunterParallel $(ARGS) 

clean:
	rm -f $(SRC)/*.class