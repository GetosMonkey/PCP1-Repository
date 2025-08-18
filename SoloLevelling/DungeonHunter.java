
/* Solo-levelling Hunt for Dungeon Master
 * Reference sequential version 
 * Michelle Kuttel 2025, University of Cape Town
 * author of original Java code adapted with assistance from chatGPT for reframing 
 * and complex power - "mana" - function.
 * Inspired by  "Hill Climbing with Montecarlo"
 * EduHPC'22 Peachy Assignment developed by Arturo Gonzalez Escribano  (Universidad de Valladolid 2021/2022)
 */
/**
 * DungeonHunter.java
 *
 * Main driver for the Dungeon Hunter assignment.
 * This program initializes the dungeon map and performs a series of searches
 * to locate the global maximum.
 *
 * Usage:
 *   java DungeonHunter <gridSize> <numSearches> <randomSeed>
 *
 */

import java.util.Random; //for the random search locations

class DungeonHunter{
	static final boolean DEBUG=false;

	//timers for how long it all takes
	static long startTime = 0;
	static long endTime = 0;
	private static void tick() {startTime = System.currentTimeMillis(); }
	private static void tock(){endTime=System.currentTimeMillis(); }

    public static void main(String[] args)  {
    	
    	double xmin, xmax, ymin, ymax; //dungeon limits - dungeons are square
    	DungeonMap dungeon;  //object to store the dungeon as a grid
    	
     	int numSearches=10, gateSize= 10;		
    	Hunt [] searches;		// Array of searches
  
    	Random rand = new Random();  //the random number generator
      	int randomSeed=0;  //set seed to have predictability for testing
        
    	if (args.length!=3) {
    		System.out.println("Incorrect number of command line arguments provided.");
    		System.exit(0);
    	}
    	
    	
    	/* Read argument values */
      	try {
    	gateSize=Integer.parseInt( args[0] );
    	 if (gateSize <= 0) {
             throw new IllegalArgumentException("Grid size must be greater than 0.");
         }
    	
    	numSearches = (int) (Double.parseDouble(args[1])*(gateSize*2)*(gateSize*2)*DungeonMap.RESOLUTION);
    	
    	randomSeed=Integer.parseInt( args[2] );
        if (randomSeed < 0) {
                throw new IllegalArgumentException("Random seed must be non-negative.");
            }
        else if(randomSeed>0)  rand = new Random(randomSeed);  // BUG FIX
        } catch (NumberFormatException e) {
            System.err.println("Error: All arguments must be numeric.");
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
 
      	
    	xmin =-gateSize;
    	xmax = gateSize;
    	ymin = -gateSize;
    	ymax = gateSize;
    	dungeon = new DungeonMap(xmin,xmax,ymin,ymax,randomSeed); // Initialize dungeon
    	
    	int dungeonRows=dungeon.getRows();
    	int dungeonColumns=dungeon.getColumns();
     	searches= new Hunt [numSearches];
     	


    	for (int i=0;i<numSearches;i++)  //intialize searches at random locations in dungeon
    		searches[i]=new Hunt(i+1, rand.nextInt(dungeonRows),
    				rand.nextInt(dungeonColumns),dungeon);
    	
    	//do all the searches 	
    	int max =Integer.MIN_VALUE;
    	int localMax=Integer.MIN_VALUE;
       	int finder =-1;
    	tick();  //start timer
     	for  (int i=0;i<numSearches;i++) {
    		localMax=searches[i].findManaPeak();
    		if(localMax>max) {
    			max=localMax;
    			finder=i; //keep track of who found it
    		}
    		if(DEBUG) System.out.println("Shadow "+searches[i].getID()+" finished at  "+localMax + " in " +searches[i].getSteps());
    	}
   		tock(); //end timer
   		
		System.out.printf("\t dungeon size: %d,\n", gateSize);
		System.out.printf("\t rows: %d, columns: %d\n", dungeonRows, dungeonColumns);
		System.out.printf("\t x: [%f, %f], y: [%f, %f]\n", xmin, xmax, ymin, ymax );
		System.out.printf("\t Number searches: %d\n", numSearches );

		/*  Total computation time */
		System.out.printf("\n\t time: %d ms\n",endTime - startTime );
		int tmp=dungeon.getGridPointsEvaluated();
		System.out.printf("\tnumber dungeon grid points evaluated: %d  (%2.0f%s)\n",tmp,(tmp*1.0/(dungeonRows*dungeonColumns*1.0))*100.0, "%");

		/* Results*/
		System.out.printf("Dungeon Master (mana %d) found at:  ", max );
		System.out.printf("x=%.1f y=%.1f\n\n",dungeon.getXcoord(searches[finder].getPosRow()), dungeon.getYcoord(searches[finder].getPosCol()) );
		dungeon.visualisePowerMap("visualiseSearch.png", false);
		dungeon.visualisePowerMap("visualiseSearchPath.png", true);
    }
}