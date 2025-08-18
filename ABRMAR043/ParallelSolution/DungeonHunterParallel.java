
import java.util.Random;
import java.util.concurrent.ForkJoinPool; 

class DungeonHunterParallel{

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
			//_______________________________________________________________________________________________________


			searches= new HuntParallel [numSearches];

			for (int i=0;i<numSearches;i++){ //intialize searches at random locations in dungeon
				
				/* 
				 * searches[i]=new HuntParallel(i+1, rand.nextInt(dungeonRows),
						rand.nextInt(dungeonColumns),dungeon);
				*/
				//____________
					Random localRand = new Random(i + randomSeed);
						searches[i] = new HuntParallel(i+1,
							localRand.nextInt(dungeonRows),
							localRand.nextInt(dungeonColumns),
							dungeon);

				//____________
					}
			
			//do all the searches 
			//_______________________________________________________________________________________________________

			int max =Integer.MIN_VALUE;
			int finder =-1;

			tick();  //start timer

			int threshold = 100; 
			ForkJoinPool pool = new ForkJoinPool(); 
			HuntParallel mainTask = new HuntParallel(searches, 0, numSearches); 
			pool.invoke(mainTask); 
			pool.shutdown();

			for (int i = 0; i < numSearches; i++) {
				int currentMax = searches[i].getLocalMax(); 
				if (currentMax > max) { 
					max = currentMax; 
					finder = i;
				}
			}

			if (DEBUG) {
				System.out.println("Shadow " + searches[finder].getID() +
					" finished at " + max + " in " + searches[finder].getSteps());
			}			

			tock(); //end timer
			
			//_______________________________________________________________________________________________________
			
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