
/**
 * DungeonMap.java
 *
 * Represents the dungeon terrain for the Dungeon Hunter assignment.
 *Methods to compute  power (mana) values in the dungeon grid,
 * to find the neighbouring cell with highest mana value and 
 * to visualise the power of visited cells .
 *
 *
 * Michelle Kuttel
 * 2025
 */

import java.util.Random;

import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

public class DungeonMap {

	public static final int PRECISION = 10000;
	public static final int RESOLUTION = 5;

	private int rows, columns; //dungeonGrid size
	private double xmin, xmax, ymin, ymax; //x and y dungeon limits
	private int [][] manaMap;
	private int [][] visit;
	private int dungeonGridPointsEvaluated;
    private double bossX;
    private double bossY;
    private double decayFactor;  

    //constructor
	public DungeonMap(	double xmin, double xmax, 
			double ymin, double ymax, 
			int seed) {
		super();
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;

		this.rows = (int) Math.round((xmax-xmin)*RESOLUTION); //the grid resolution is fixed
		this.columns =  (int) Math.round((ymax-ymin)*RESOLUTION);//the grid resolution is fixed

		// Randomly place the boss peak
		Random rand;
		if(seed==0) rand= new Random(); //no fixed seed
		else rand= new Random(seed);
        double xRange = xmax - xmin;
        this.bossX = xmin + (xRange) * rand.nextDouble();
        this.bossY = ymin + (ymax - ymin) * rand.nextDouble();
     // Calculate decay factor based on range
        this.decayFactor = 2.0 / (xRange * 0.1);  // adjust scaling factor to control width

		manaMap = new int[rows][columns];
		visit = new int[rows][columns];
		dungeonGridPointsEvaluated=0;

		/* Terrain initialization */
		for(int i=0; i<rows; i++ ) {
			for( int j=0; j<columns; j++ ) {
				manaMap[i][j] = Integer.MIN_VALUE;; //means mana not yet measured
				visit[i][j] = -1; //grid point not yet visited
			}
		}
	}

	// has this site been visited before?
	 boolean visited( int x, int y) {
		 if (visit[x][y]==-1) return false;
		 return true;
}

	 void setVisited( int x, int y, int id) {
		 if (visit[x][y]==-1) //don't reset
			 visit[x][y]= id;
	 }

	 /**
	     * Evaluates mana at a dungeonGrid  coordinate (x, y) in the dungeon,
	     * and writes it to the map.
	     *
	     * @param x_coord The x-coordinate in the dungeon grid.
	     * @param y_coord The y-coordinate in the dungeon grid.
	     * @return A double value representing the mana value at (x, y).
	     */
	int getManaLevel( int x, int y) {
		if (visited(x,y)) return manaMap[x][y];  //don't recalculate 
		if (manaMap[x][y]>Integer.MIN_VALUE) return manaMap[x][y];  //don't recalculate 

		/* Calculate the coordinates of the point in the ranges */
		double x_coord = xmin + ( (xmax - xmin) / rows ) * x;
		double y_coord = ymin + ( (ymax - ymin) / columns ) * y;
		double dx = x_coord - bossX;
		double dy = y_coord - bossY;
		double distanceSquared = dx * dx + dy * dy;
		
		/* The function to compute the mana value value */
		/*DO NOT CHANGE this - unless you are testing, but then put it back!*/
		double mana = (2 * Math.sin(x_coord + 0.1 * Math.sin(y_coord / 5.0) + Math.PI / 2) *
                Math.cos((y_coord + 0.1 * Math.cos(x_coord / 5.0) + Math.PI / 2) / 2.0) +
            0.7 * Math.sin((x_coord * 0.5) + (y_coord * 0.3) + 0.2 * Math.sin(x_coord / 6.0) + Math.PI / 2) +
            0.3 * Math.sin((x_coord * 1.5) - (y_coord * 0.8) + 0.15 * Math.cos(y_coord / 4.0)) +
            -0.2 * Math.log(Math.abs(y_coord - Math.PI * 2) + 0.1) +
            0.5 * Math.sin((x_coord * y_coord) / 4.0 + 0.05 * Math.sin(x_coord)) +
            1.5 * Math.cos((x_coord + y_coord) / 5.0 + 0.1 * Math.sin(y_coord)) +
            3.0 * Math.exp(-0.03 * ((x_coord - bossX - 15) * (x_coord - bossX - 15) +
                                    (y_coord - bossY + 10) * (y_coord - bossY + 10))) +
            8.0 * Math.exp(-0.01 * distanceSquared) +                 
            2.0 / (1.0 + 0.05 * distanceSquared)); 
		
		/* Transform to fixed point precision */
		int fixedPoint = (int)( PRECISION * mana );
		manaMap[x][y]=fixedPoint;
		dungeonGridPointsEvaluated++;//keep count
		return fixedPoint;
	}

	//work out where to go next - move in direction of highest mana
	 /**
     * Function to return the neighbouring cell direction with highest mana 
     * @param x_coord The x-coordinate in the dungeon grid.
     * @param y_coord The y-coordinate in the dungeon grid.
     * @return the direction of highest mana.
     */
	Hunt.Direction getNextStepDirection( int x, int y) {
		Hunt.Direction climbDirection = Hunt.Direction.STAY;
	    int localMax = getManaLevel(x, y);

	    // Define directions with (dx, dy)
	    int[][] directions = {
	        {-1,  0}, // LEFT
	        { 1,  0}, // RIGHT
	        { 0, -1}, // UP
	        { 0,  1}, // DOWN
	        {-1, -1}, // UP_LEFT
	        { 1, -1}, // UP_RIGHT
	        {-1,  1}, // DOWN_LEFT
	        { 1,  1}  // DOWN_RIGHT
	    };

	    Hunt.Direction[] directionEnums = {
	        Hunt.Direction.LEFT,
	        Hunt.Direction.RIGHT,
	        Hunt.Direction.UP,
	        Hunt.Direction.DOWN,
	        Hunt.Direction.UP_LEFT,
	        Hunt.Direction.UP_RIGHT,
	        Hunt.Direction.DOWN_LEFT,
	        Hunt.Direction.DOWN_RIGHT
	    };

	    for (int i = 0; i < directions.length; i++) {
	        int newX = x + directions[i][0];
	        int newY = y + directions[i][1];

	        if (newX >= 0 && newX < rows && newY >= 0 && newY < columns) {
	            int power = getManaLevel(newX, newY);
	            if (power > localMax) {
	                localMax = power;
	                climbDirection = directionEnums[i];
	            }
	        }
	    }

	    return climbDirection;
	}

	/**
     * Generates an image from the dungeon grid.
     * Unvisited cells are colored black, while visited cells follow a black→purple→red→white gradient.
     *
     * @param filename The name of the output PNG file.
     */
	public void visualisePowerMap(String filename, boolean path) {
	    int width = manaMap.length;
	    int height = manaMap[0].length;

	    //output image
	    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

	    // Find min and max for normalization (ignore unvisited sites)
	    int min = Integer.MAX_VALUE;
	    int max = Integer.MIN_VALUE;	    
	    
	    for (int x = 0; x < width; x++) {
	        for (int y = 0; y < height; y++) {
	            int value = manaMap[x][y];
	            if (value==Integer.MIN_VALUE)  continue; // ignore unvisited sites
	            if (value < min) min = value;
	            if (value > max) max = value;
	        }
	    }
	    // Prevent division by zero if everything has the same value
	    double range = (max > min) ? (max - min) : 1.0;

	    // Map height values to colors
	    for (int x = 0; x < width; x++) {
	        for (int y = 0; y < height; y++) {
	            Color color;

	            if (path && !visited(x, y)) color = Color.BLACK; //view path only, all not visited black
	            else if (manaMap[x][y]==Integer.MIN_VALUE) color = Color.BLACK; // not evaluated black
	            else {
	                double normalized = (manaMap[x][y] - min) / range; // 0–1
	                color = mapHeightToColor(normalized);
	            }
	            image.setRGB(x, height - 1 - y, color.getRGB());
	        }
	    }
	    try {
	        File output = new File(filename);
	        ImageIO.write(image, "png", output);
	        System.out.println("map saved to " + filename);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	/**
	 * Maps normalized height [0..1] to black → purple → red → white.
	 */
	private Color mapHeightToColor(double normalized) {
	    normalized = Math.max(0, Math.min(1, normalized)); // clamp to [0,1]

	    int r = 0, g = 0, b = 0;

	    if (normalized < 0.33) {
	        // Black -> Purple
	        double t = normalized / 0.33;
	        r = (int) (128 * t); // purple has some red
	        g = 0;
	        b = (int) (128 + 127 * t); // increasing blue
	    } 
	    else if (normalized < 0.66) {
	        // Purple -> Red
	        double t = (normalized - 0.33) / 0.33;
	        r = (int) (128 + 127 * t); // red dominates
	        g = 0;
	        b = (int) (255 - 255 * t); // fade out blue
	    } 
	    else {
	        // Red -> White
	        double t = (normalized - 0.66) / 0.34;
	        r = 255;
	        g = (int) (255 * t);
	        b = (int) (255 * t);
	    }

	    return new Color(r, g, b);
	}

	public int getGridPointsEvaluated() {
		return dungeonGridPointsEvaluated;
	}

	public double getXcoord(int x) {
		return xmin + ( (xmax - xmin) / rows ) * x;
	}
	public double getYcoord(int y) {
		return ymin + ( (ymax - ymin) / columns ) * y;
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}


}
