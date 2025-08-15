
import java.util.concurrent.RecursiveAction;

public class HuntParallel extends RecursiveAction {

	private int id;						//  identifier for this hunt
	private int posRow, posCol;		// Position in the dungeonMap
	private int steps; 				//number of steps to end of the search
	private boolean stopped;	// Did the search hit a previously searched location?
	private DungeonMapParallel dungeon;

	//______________

	private int startSearch; 
	private int endSearch;  
	private int localMax = Integer.MIN_VALUE; 
	private HuntParallel[] searches; 

	private static final int THRESHOLD = 100;

	//______________
	
	public enum Direction {
	    STAY,
	    LEFT,
	    RIGHT,
	    UP,
	    DOWN,
	    UP_LEFT,
	    UP_RIGHT,
	    DOWN_LEFT,
	    DOWN_RIGHT
	}

	public HuntParallel(int id, int pos_row, int pos_col, DungeonMapParallel dungeon) {
		this.id = id;
		this.posRow = pos_row; //randomly allocated
		this.posCol = pos_col; //randomly allocated
		this.dungeon = dungeon;
		this.stopped = false;
	}

	//_____________
	public HuntParallel( HuntParallel[] searches, int startSearch, int endSearch){
		this.searches = searches;
        this.startSearch = startSearch;
        this.endSearch = endSearch;
	}
	//hello world 
	//_____________

	//________________________________________________________
	
	@Override
	protected void compute(){	

		if(searches == null){
			this.localMax = findManaPeak(); 
			return;
		}

		if (endSearch - startSearch <= THRESHOLD){
			// Base case
			for (int i = startSearch; i < endSearch; i++){
				searches[i].localMax = searches[i].findManaPeak(); 
			}	
		} else {
			int mid = (startSearch + endSearch)/2;
			
			HuntParallel l = new HuntParallel(searches, startSearch, mid); 
			HuntParallel r = new HuntParallel(searches, mid, endSearch); 
			l.fork(); 			// left in parallel
			r.compute(); 		// compute right
			l.join(); 			// wait for left to complete
		}
	}

	//________________________________________________________
	/**
     * Find the local maximum mana from an initial starting point
     * 
     * @return the highest power/mana located
     */
	public int findManaPeak() {
		int power=Integer.MIN_VALUE;
		Direction next = Direction.STAY;
		
		while(!dungeon.visited(posRow, posCol)) { // stop when hit existing path
			power=dungeon.getManaLevel(posRow, posCol);
			dungeon.setVisited(posRow, posCol, id);
			steps++;
			next = dungeon.getNextStepDirection(posRow, posCol);
			if(DungeonHunterParallel.DEBUG) System.out.println("Shadow "+getID()+" moving  "+next);
			switch(next) {
				case STAY: return power; //found local valley
				case LEFT:
					posRow--;
					break;
				case RIGHT:
					posRow=posRow+1;
					break;
				case UP:
					posCol=posCol-1;
					break;
				case DOWN:
					posCol=posCol+1;
					break;
				case UP_LEFT:
					posCol=posCol-1;
					posRow--;
					break;
				case UP_RIGHT:
					posCol=posCol-1;
					posRow=posRow+1;
					break;
				case DOWN_LEFT:
					posRow=posRow+1;
					posRow--;
					break;
				case DOWN_RIGHT:
					posCol=posCol+1;
					posRow=posRow+1;
				}
		}
		stopped=true;
		return power;
	}

	public int getID() { return id; }

	public int getPosRow() { return posRow;}

	public int getPosCol() { return posCol;}

	public int getSteps() { return steps;}
	
	public boolean isStopped() {return stopped;}

	//____________
	public int getLocalMax(){ return localMax;}
	//____________

}


