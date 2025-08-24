
JAVAC=javac
JAVA=java
SRC=SoloLevelling
CLASSES = $(SRC)/DungeonMap.java $(SRC)/Hunt.java $(SRC)/DungeonHunter.java
# Default arguments (update these if needed)
ARGS ?= 10 0.25 99 # Replace 'default_arguments' with your specific default arguments, if any: size, searches, seed


all:
	$(JAVAC) $(CLASSES)

run:
	$(JAVA) -cp $(SRC) DungeonHunter $(ARGS) 

clean:
	rm -f $(SRC)/*.class
