# ABRMAR043
# 15th August 2025 
# Parellelized makefile 

JAVAC=javac
JAVA=java

CLASSES = DungeonHunterParallel.java DungeonMapParallel.java HuntParallel.java

# Default arguments (update these if needed)
ARGS ?= 10 0.25 99 # Replace 'default_arguments' with your specific default arguments, if any
#dungeon size, searches, seed

all:
	$(JAVAC) $(CLASSES)

run:
	$(JAVA) DungeonHunterParallel $(ARGS) 

clean:
	rm -f *.class