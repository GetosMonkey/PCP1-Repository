// ABRMAR043
// Maryam Abrahams
// Parallelized Dungeon Hunter
// 9 August 2025

import java.util.Random;
import java.util.concurrent.ForkJoinPool; 
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;


public class DungeonHunterParallel {
    
    static final boolean DEBUG=false;

	//timers for how long it all takes
	static long startTime = 0;
	static long endTime = 0;
	private static void tick() {startTime = System.currentTimeMillis(); }
	private static void tock(){endTime=System.currentTimeMillis(); }

    public static void main(String[] args)  {
    	
    	double xmin, xmax, ymin, ymax; //dungeon limits - dungeons are square
    	DungeonMapParallel dungeon;  //object to store the dungeon as a grid
    	
     	int numSearches=10, gateSize= 10;		
    	HuntParallel [] searches;		// Array of searches
  
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
    	
    	numSearches = (int) (Double.parseDouble(args[1])*(gateSize*2)*(gateSize*2)*DungeonMapParallel.RESOLUTION);
    	
    	randomSeed=Integer.parseInt( args[2] );
        if (randomSeed < 0) {
                throw new IllegalArgumentException("Random seed must be non-negative.");
            }
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
    	dungeon = new DungeonMapParallel(xmin,xmax,ymin,ymax,randomSeed); // Initialize dungeon
    	
    	int dungeonRows=dungeon.getRows();
    	int dungeonColumns=dungeon.getColumns();
     	searches= new HuntParallel [numSearches];
    	for (int i=0;i<numSearches;i++)  //intialize searches at random locations in dungeon
    		searches[i]=new HuntParallel(i+1, rand.nextInt(dungeonRows),
    				rand.nextInt(dungeonColumns),dungeon);
    	
		//____________________________________________________________________________________________________________________________________________________________________________________

    	// do all the searches
		// Main part to parallize	

    	int max =Integer.MIN_VALUE;
       	int finder =-1;

    	tick();  //start timer
		
     	ForkJoinPool pool = new ForkJoinPool(); 
		SearchWorker find = new SearchWorker(searches, 0, numSearches); 
		SearchResult result = pool.invoke(find); 
		pool.shutdown(); 

		max = result.maxMana; 
		finder = result.finder; 

		if(DEBUG){
			for (int i =0; i < numSearches; i++){
				System.out.println("Shadow " + searches[i].getID()+ " finished at " + searches[i].getLastResult() + " in " + searches[i].getSteps()); 
			}
		}

   		tock(); //end timer


		  

		//____________________________________________________________________________________________________________________________________________________________________________________

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

	public static class SearchResult{
		final int maxMana; 
		final int finder; 

		public SearchResult(int maxMana, int finder){
			this.maxMana = maxMana; 
			this.finder = finder;
		}
		}

		static class SearchWorker extends RecursiveTask<SearchResult>{

			public HuntParallel[] searches; 
			public int start; 
			public int end; 
			public static final int Threshold = 50; 

			public SearchWorker (){}
			public SearchWorker (HuntParallel[] searches, int start, int end) {
				this.searches = searches; 
				this.start = start; 
				this.end = end; 
			}
			
			public SearchResult compute(){//Compute method for enabling my parallelism in the first place
				
				//Recursive calls and base case
				if(end - start <= Threshold){
					int maxMana = Integer.MIN_VALUE; 
					int finder = -1; 

					for (int i = start; i <end; i++){
						int mana = searches[i].findManaPeak();
						if (mana > maxMana){
							maxMana = mana; 
							finder = i;
						}
					}
					return new SearchResult(maxMana, finder); 
				} else {
					int mid = (start + end) / 2; 

					SearchWorker l = new SearchWorker(searches, start, mid);
					SearchWorker r = new SearchWorker(searches, mid, end);

					l.fork(); 

					SearchResult rresult = r.compute(); 
					SearchResult lresult = l.join(); 

					if (lresult.maxMana > rresult.maxMana || (lresult.maxMana == rresult.maxMana && lresult.finder < rresult.finder))
					{
						return lresult; 
					} else {
						return rresult; 
					}
				}
			}
		}
}
