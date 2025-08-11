# PCP1-Repository
The search implemented in this assignment is a nice example of a Monte Carlo method. Monte Carlo methods use random selections in calculations to solve numerical or physical problems. Here we use a Monte Carlo algorithm to find the maximum (the highest value) of the “mana” within a dungeon.

## CSC2002F 2025 Parallelism Assignment 1

### 9th August 2025

setting up my git repository and assignment basics: 
    Process:
    1) Ubuntu -> code, open terminal
    2) cd Assignment - PCP1'
    3) git init -> git remote add origin https://github.com/GetosMonkey/PCP1-Repository.git
    4) git pull orgin master
    5) git add . -> git commit -m "some change" -> git push origin master
    6) use password of laptop for passphrase key for abrmar043 (no 07, no capital) 
Deleted the .project file because of issues with not using the Eclipse IDE.
To run the program use: make run -> this runs the program with default values
                        make ARGS= "30 200 999" -> dungeon size 30, 200 searches, seed 999

### 10th August 2025 

I'm assuming that for my project I must use the forkjoin method to split my threads/workers into one per search and that the bulk of the actual search splitting takes place in the HuntParallel program but is called in the DungeonHunterParallel program main method. 

### 11th August 2025 

My first working version of my program I got today, however it seems to be slower: 

#### Serial solution: 

    java -cp SoloLevelling DungeonHunter 20 0.2 0   
         dungeon size: 20,
         rows: 200, columns: 200
         x: [-20.000000, 20.000000], y: [-20.000000, 20.000000]
         Number searches: 1600

         time: 18 ms
        number dungeon grid points evaluated: 47259  (118%)
    Dungeon Master (mana 126638) found at:  x=2.8 y=-6.8

    map saved to visualiseSearch.png
    map saved to visualiseSearchPath.png

#### Parallel solution: 

    java -cp ParallelSolution DungeonHunterParallel 20 0.2 0   
         dungeon size: 20,
         rows: 200, columns: 200
         x: [-20.000000, 20.000000], y: [-20.000000, 20.000000]
         Number searches: 1600

         time: 20 ms
        number dungeon grid points evaluated: 34017  (85%)
    Dungeon Master (mana 122868) found at:  x=-18.6 y=-13.8

    map saved to visualiseSearch.png
    map saved to visualiseSearchPath.png

## Use of Artificial intelligence: 
    1) Learning how to use gitignores so that I don't change the serial solution
    2) Using Claude to help understand the sample code after creating my own summary table of each class
    3) Work around run issues with the project file and file directory structure
    4) Confirmed that ForkJoinPool violates the assignment’s synchronization restrictions and to troubleshoot manual thread management with join()
    5) Helped me with the understanding of the approach : creating an inner Seacrh worker class instead of extending Recucrsiveaction within the Hunt class (which implements the search abilities) in the first place.
    6) Help with understanding the implementation of my paralellism and the structure of the inner classes
    7) Debugging

## Resources used: 
    1) https://youtu.be/r_MbozD32eo  // Multithreading
    2) https://youtu.be/tusUoAfYzAI  // Forkjoin