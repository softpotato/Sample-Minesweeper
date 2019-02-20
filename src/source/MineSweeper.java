package source;

import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

/**
 * This class uses the ||spoiler|| tags of discord and randomly generates a bunch of MineSweeper
 * games based on the input values. The game will always have a middle empty spot so you won't
 * lose right off the bat, and the number of mines should yeah
 * 
 * TODO: add fence posing for the print, because the whole thing is 9 characters per space,
 * that means that there is an extra character at the end of all of it. The max is 2000 characters
 * so that means 2000 / 9 = 222.222 or 222 total spaces.
 * 
 * @author Benjamin Lin
 *
 */
public class MineSweeper {
	private static int[][] field; 	// Is the MineSweeper field - values mean
							// 	-1 means bomb
							// 	0 means nothing nearby
							// 	# above 0 means a bomb is nearby, max is 9
	private static int width;
	private static int height;
	private static double mineDensity;
	
	/**
	 * This method starts the program.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("MINE SWEEPER GENERATOR 1.0 for Discord - By: Benjamin Lin");
		query();
		generate();
		print();
	}
	
	/**
	 * This method deals with the user's map generation with just text inputs.
	 */
	public static void query() {
		Scanner console = new Scanner(System.in);
		
		// TODO: figure out how to do the error statement if you do it more than once, probably
		//			would use a boolean statement
		do {
			width = inputIntCheck(console,"Width of the Game?");
			height = inputIntCheck(console,"Height of the Game?");
		} while (dimensionCheck(width,height));
		
		mineDensity = inputDoubCheck(console,"Mine Density of the Game? (range: 0.0 - 1.0)");
		
		
		field = new int[width][height];
		
		generate();
	}
	
	/**
	 * This method returns the expected area of the minesweeper game, by taking in just a simple
	 * width and height dimension, it multiplies it to get an area and returns the result.
	 * 
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	private static boolean dimensionCheck(int width,int height) {
		 int area = width * height;
		 if (area <= 222 && area > 0)
			 return false;
		 System.out.println("***ERROR - DIMENSION EXCEEDS AN AREA OF 222***");
		 return true;
	}
	
	/**
	 * This method is to deal with the user input of the user so that it will always take in an
	 * integer value and not just a String. It will use the .nextInt() method from the scanner
	 * method. The code for this method is found here:
	 * https://stackoverflow.com/questions/7446379/how-do-i-safely-scan-for-integer-input
	 * 
	 * This is only used for the width and height of the minesweeper game, so there are some
	 * limitations on the minimum width and height stuck in there.
	 * 
	 * @param input
	 * @param query
	 * @return
	 */
	private static int inputIntCheck(Scanner input,String query) {
		int number = -1;
		
		do {
			System.out.print(query);
			
			try {
				number = input.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("***ERROR - Non-Integer character inputed***");
			}
			
			if (number < 5)
				System.out.println("***ERROR - Current input can't be less than 5!");
		} while (number < 5);
		
		return number;
	}
	
	/**
	 * This method mimics the one above it, except it handles doubles. It also adds a cap on the
	 * mine density forcing it to be under 100%. It's technically impossible to get 100%, because
	 * there will always be a mine in the middle. However, it is very unlikely, because of how the
	 * following methods generate the mines. There are collisions in the generation, so it's
	 * actually difficult to generate it so it will have 99% mines.
	 * 
	 * @param input
	 * @param query
	 * @return
	 */
	private static double inputDoubCheck(Scanner input,String query) {
		double number = -1.0;
		
		do {
			System.out.print(query);
			
			try {
				number = input.nextDouble();
			} catch (InputMismatchException e) {
				System.out.println("***ERROR - Non-Integer character inputed***");
			}
			
			if (number <= 0)
				System.out.println("***ERROR - Current input can't be less than 0%!");
			
			if (number > 1)
				System.out.println("***ERROR - Current input can't be more than 100%!");
		} while (number <= 0 || number > 1);
		
		return number;
	}
	
	/**
	 * This method deals with generating the minesweeper field. This method is broken up into
	 * several parts.
	 */
	public static void generate() {
		Map<Integer,HashSet<Integer>> bombList = populateMines();
		populateTable(bombList);
	}
	
	/**
	 * This method counts the number of tiles and returns the integer value of it.
	 * 
	 * @return
	 * 		column of Field * height of Field
	 */
	private static int gameArea() {
		return field[0].length * field.length;
	}
	
	/**
	 * This method fills the field with mines based on the mineDesity, but it also leaves the
	 * center of field clear of any mines for the player.
	 */
	private static Map<Integer,HashSet<Integer>> populateMines() {
		
		// It won't exactly be this amount, because there might be collisions with the mines
		
		// and as the numbers increaes the probability of adding ines decrease and so do
		// collisions
		int numberOfMines = (int) (gameArea() * mineDensity);
		
		System.out.println("Number of Mines: " + numberOfMines);
		
		Map<Integer,HashSet<Integer>> mineList = new HashMap<Integer,HashSet<Integer>>();
		
		// This populates the map with the values
		for (int counter = 0;counter < numberOfMines;counter++) {
			int xValue = (int) (Math.random() * (width));
			
			// TODO: remove this
			//System.out.println("xValue Generate: " + xValue);
			
			HashSet<Integer> yValue = mineList.get(xValue);
			
			if (yValue == null) {
				mineList.put(xValue,new HashSet<Integer>((int) (Math.random() * (height))));
			} else {
				yValue.add((int) (Math.random() * (height)));
			}
		}
		
		// The following gets rid of any mines in the middle 9 squares or a 3x3 area
		HashSet<Integer> lefRow = mineList.get((width / 2) - 1);
		HashSet<Integer> midRow = mineList.get(width / 2);
		HashSet<Integer> rigRow = mineList.get((width / 2) + 1);
		int value1 = (height / 2) - 1;
		int value2 = (height / 2);
		int value3 = (height / 2) + 1;
		
		if (lefRow != null) {
			lefRow.remove(value1);
			lefRow.remove(value2);
			lefRow.remove(value3);
		}
		if (midRow != null) {
			midRow.remove(value1);
			midRow.remove(value2);
			midRow.remove(value3);
		}
		if (rigRow != null) {
			rigRow.remove(value1);
			rigRow.remove(value2);
			rigRow.remove(value3);
		}
		
		return mineList;
	}
	
	/**
	 * I should have put notes, but I believe this method was used to populate the table of
	 * bombs based on the generated imputs.
	 * 
	 * @param inputBombs
	 */
	public static void populateTable(Map<Integer,HashSet<Integer>> inputBombs) {
		for (Map.Entry<Integer,HashSet<Integer>> bombEntry : inputBombs.entrySet()) {
			int xValue = bombEntry.getKey();
			for (Integer yValue : bombEntry.getValue()) {
				
				// TODO: remove
				//System.out.println("X:" + xValue + " ,Y:" + yValue);
				
				field[xValue][yValue] =  -1;
				
				boolean leftXBord = xValue == 0;
				boolean topYBord = yValue == 0;
				boolean righXBord = xValue == width - 1;
				boolean botYBord = yValue == height - 1;
				
				// Top Left corner of the board, the X and Y values are always the same
				if (leftXBord && topYBord) {
					incrementPoint(1,0);
					incrementPoint(1,1);
					incrementPoint(0,1);
					
				// Bottom left corner of the board, the X value is always the same, but the 
				// y can only be decremented or kept the same.
				} else if (leftXBord && botYBord) {
					incrementPoint(0,yValue - 1);
					incrementPoint(1,yValue - 1);
					incrementPoint(1,yValue);
					
				// Top right corner of the board, and the y value is always the same, the x however
				// has to be decremented only to not go over the edge.
				} else if (righXBord && topYBord) {
					incrementPoint(xValue,1);
					incrementPoint(xValue - 1,0);
					incrementPoint(xValue - 1,1);
					
				// Bottom right and left corner of the board.
				} else if (righXBord && botYBord) {
					incrementPoint(xValue - 1,yValue - 1);
					incrementPoint(xValue - 1,yValue);
					incrementPoint(xValue,yValue - 1);
					
				// Left Side only
				} else if (leftXBord) {
					incrementPoint(xValue,yValue - 1);
					incrementPoint(xValue + 1,yValue - 1);
					incrementPoint(xValue + 1,yValue);
					incrementPoint(xValue + 1,yValue + 1);
					incrementPoint(xValue,yValue + 1);
					
				// Top Side only
				} else if (topYBord) {
					incrementPoint(xValue - 1,yValue);
					incrementPoint(xValue - 1,yValue + 1);
					incrementPoint(xValue,yValue + 1);
					incrementPoint(xValue + 1,yValue + 1);
					incrementPoint(xValue + 1,yValue);
					
				// Right Side only
				} else if (righXBord) {
					incrementPoint(xValue,yValue - 1);
					incrementPoint(xValue - 1,yValue - 1);
					incrementPoint(xValue - 1,yValue);
					incrementPoint(xValue - 1,yValue + 1);
					incrementPoint(xValue,yValue + 1);
					
				// Bottom Side only
				} else if (botYBord) {
					incrementPoint(xValue - 1,yValue - 1);
					incrementPoint(xValue - 1,yValue);
					incrementPoint(xValue,yValue - 1);
					incrementPoint(xValue + 1,yValue - 1);
					incrementPoint(xValue + 1,yValue);
					
				// General Condition ones
				} else {
					incrementPoint(xValue - 1,yValue - 1);
					incrementPoint(xValue - 1,yValue);
					incrementPoint(xValue - 1,yValue + 1);
					incrementPoint(xValue,yValue - 1);
					incrementPoint(xValue,yValue + 1);
					incrementPoint(xValue + 1,yValue - 1);
					incrementPoint(xValue + 1,yValue);
					incrementPoint(xValue + 1,yValue + 1);
				}
				
			}
		}
	}
	
	/**
	 * This method only increments points that don't have a bomb in them yet.
	 * 
	 * @param x
	 * @param y
	 */
	private static void incrementPoint(int x,int y) {
		if (field[x][y] != -1)
			field[x][y]++;
	}
	
	/**
	 * This method prints out the data output into the console.
	 */
	public static void print() {
		int midX = (width / 2);
		int midY = (height / 2);
		//System.out.println("```");
		for (int row = 0;row < field.length;row++) {
			for (int column = 0;column < field[row].length;column++) {
				
				// SIMPLE TROUBLESHOOT PRINT
				if (field[row][column] == -1) {
					System.out.print("`X` ");
				} else {
					System.out.print("`" + field[row][column] + "` ");
				}
				
				// SIMPLE PRINT - BASIC
				/*
				if (field[row][column] == -1) {
					System.out.print("||`X`|| ");
				} else {
					System.out.print("||`" + field[row][column] + "`|| ");
				}
				*/
				
				/*
				 * OLD PRINT - BROKEN WITH OPEN SQUARE IN MIDDLE
				if (field[row][column] == -1) {
					System.out.print("||X|| ");
				} else if (row < midX - 1 || row > midX + 1) {
					System.out.print("|| " + field[row][column] + " || ");
				} else if (column < midY - 1 || column > midY + 1) {
					System.out.print("|| " + field[row][column] + " || ");
				} else {
					System.out.print("" + field[row][column] + " ");
				}
				*/
			}
			System.out.println();
		}
		//System.out.print("```");
	}
	
}
