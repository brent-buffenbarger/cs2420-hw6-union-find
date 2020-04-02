import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Hex {

	private static char[][] board = {
		{'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0'},
		{'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0'},
		{'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0'},
		{'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0'},
		{'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0'},
		{'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0'},
		{'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0'},
		{'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0'},
		{'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0'},
		{'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0'},
		{'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0'}
	};
	
	private static int[] leftBorderVals = {1, 12, 23, 34, 45, 56, 67, 78, 89, 100, 111};
	private static int[] rightBorderVals = {11, 22, 33, 44, 55, 66, 77, 88, 99, 110, 121};
	private static int[] topBorderVals = {1, 2, 3 , 4, 5, 6, 7, 8, 9, 10, 11};
	private static int[] bottomBorderVals = {111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121};

	private static final String ANSI_RESET = "\u001B[0m";
	private static final String ANSI_WHITE = "\u001B[37m";
	private static final String ANSI_RED = "\u001B[31m";
	private static final String ANSI_BLUE = "\u001B[34m";
	
	private static boolean isPlayerRed = false;
	private static boolean isGameOver = false;

	private static final int LEFT = 122;
	private static final int RIGHT = 123;
	private static final int TOP = 124;
	private static final int BOTTOM = 125;

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		System.out.println("Would you like to use 'moves.txt' or 'moves2.txt'?");
		System.out.println("Input 1 for moves.txt or 2 for moves2.txt: ");
		int choice = input.nextInt();
		while(choice != 1 && choice != 2 && choice != 3) {
			System.out.println("Input 1 for moves.txt, 2 for moves2.txt, or 3 to exit the program.");
		}

		if(choice == 1)
			playGame("moves.txt");
		else if(choice == 2)
			playGame("moves2.txt");
		else
			System.exit(-1);
	}

	/**
	 *. This is the method that holds the game logic
	*/ 
	private static void playGame(String fileName) {
		DisjSet blue = new DisjSet(126); // create a blue Disjoint Set
		DisjSet red = new DisjSet(126);	 // create a red Disjoint Set
		
		int moves = 0; // Track the total number of moves
		try {
			// read in the given moves file
			File positions = new File("data/" + fileName);
			Scanner input = new Scanner(positions);
			
			// loop to play the game (ends whenever the file is out of moves, or somebody has won)
			while(input.hasNext() && !isGameOver) {
				int positionToMoveTo = input.nextInt(); // grab the next move
				// conditional statement to skip this move if the given position is invalid
				if(positionToMoveTo < 1 || positionToMoveTo > 121) {
					System.out.println("Invalid position provided");
					continue;
				}
				int[] neighbors = {-1, -1, -1, -1, -1, -1};
				int[] rowCol = getRowCol(positionToMoveTo); // grab the row and column for the board array
				int row = rowCol[0];
				int col = rowCol[1];
				if(checkPositionOccupant(positionToMoveTo) == '0') { // Enter here if the position is empty
					updateBoard(positionToMoveTo); // update the board to have the write color in given position
					if(isPlayerRed) { // case for red player	
						if(isValInArray(positionToMoveTo, topBorderVals)) // statement to check if the position is in the top row
							red.union(red.find(TOP), red.find(positionToMoveTo));
						else if(isValInArray(positionToMoveTo, bottomBorderVals)) // statement to check if the position is in the bottom row
							red.union(red.find(BOTTOM), red.find(positionToMoveTo));
						neighbors = getNeighbors(positionToMoveTo);
						for(int n : neighbors) { // find all the neighbors and union the valid ones
							if(n != -1) {
								if(checkPositionOccupant(n) == 'R')
									red.union(red.find(n), red.find(positionToMoveTo));
							}	
						}
					} else { // case for blue player
						if(isValInArray(positionToMoveTo, leftBorderVals)) // statement to check if the position is in the left column
							blue.union(blue.find(LEFT), blue.find(positionToMoveTo));
						else if(isValInArray(positionToMoveTo, rightBorderVals)) // statement to check if the position is in the right column
							blue.union(blue.find(RIGHT), blue.find(positionToMoveTo));
						neighbors = getNeighbors(positionToMoveTo);
						for(int n : neighbors) { // find all the neighbors and union the valid ones
							if(n != -1) {
								if(checkPositionOccupant(n) == 'B')
									blue.union(blue.find(n), blue.find(positionToMoveTo));
							}	
						}
					}
				} else {
					System.out.println("Position " + positionToMoveTo + " is already occupied.");
				}
				moves++; // increment move tracker
				checkWinner(red, blue, moves); // check if there is a winner yet
				isPlayerRed = !isPlayerRed; // switch whose turn it is
			}
		} catch(FileNotFoundException err) {
			System.out.println("Input file not found");
		}
	}

	/**
	 *. Method to return an array of all of the neighbors of a given position
	 */
	private static int[] getNeighbors(int location) {
		int[] neighbors = {-1, -1, -1, -1, -1, -1};
		
		// west (left) neighbor
		if(!isValInArray(location, leftBorderVals))
			neighbors[0] = location - 1;

		// east (right) neighbor
		if(!isValInArray(location, rightBorderVals))
		       neighbors[1] = location + 1;

		// north-west (top-left) neighbor
		if(!isValInArray(location, topBorderVals))
			neighbors[2] = location - 11;

		// north-east (top-right) neighbor
		if(!isValInArray(location, topBorderVals) && !isValInArray(location, rightBorderVals))
			neighbors[3] = location - 10;

		// south-west (bottom-left) neighbor
		if(!isValInArray(location, leftBorderVals) && !isValInArray(location, bottomBorderVals))
			neighbors[4] = location + 10;

		// south-east (bottom-right) neighbor
		if(!isValInArray(location, bottomBorderVals))
			neighbors[5] = location + 11;	
		
		return neighbors;
	}

	/**
	 *. Helper method to see if a value is in an int[] array
	 *  This is being used to check if a value is in a border spot
	 */
	private static boolean isValInArray(int value, int[] array) {
		for(int n : array) {
			if(n == value)
				return true;
		}
		return false;
	}

	/**
	 *. Method to update the board with 'R' for red and 'B' for blue 
	 */ 
	private static void updateBoard(int position) {
		int[] rowCol = getRowCol(position);
		int row = rowCol[0];
		int col = rowCol[1];
		
		// if player is red, then we turn the position to 'R'
		if(isPlayerRed)
			board[row][col] = 'R';
		else // else the player is blue
			board[row][col] = 'B';
	}

	/**
	 *. Method to check the row and column position on the board given a position
	 */
	private static int[] getRowCol(int position) {
		int[] rowCol = {-1, -1};
		int temp = position;
		
		// traverse to the left column of the row
		while(!isValInArray(temp, leftBorderVals)) {
			--temp;
		}

		
		int row = (int) (temp / 11);
		int col = (position % 11) - 1; // mod 11 because 10 is largest column # so this will gives us col #
		
		// Modulas operator makes the last column 0, which turns into -1. We need to reset this here	
		if(col == -1)
			col = 10;

		rowCol[0] = row;
		rowCol[1] = col;
		
		return rowCol;
	}

	/**
	 *. Method to return the occupier of a position ('0', 'R', or 'B')
	 */
       private static char checkPositionOccupant(int position) {
	        int[] rowCol = getRowCol(position);
		return board[rowCol[0]][rowCol[1]];
       }	   

	
       /**
	*. Method to check for a winner and print winning message
        */
	private static void checkWinner(DisjSet red, DisjSet blue, int moves) {
		if((red.find(TOP) == red.find(BOTTOM)) && red.find(TOP) != -1) { // red has won if BOTTOM and TOP have the same root
			System.out.println("--------> Red has won after " + moves + " attempted moves! Here is the final board.");
			isGameOver = true;
			printBoard();
		} else if((blue.find(LEFT) == blue.find(RIGHT)) && blue.find(LEFT) != -1) { // blue has won if LEFT and RIGHT have the same root
			System.out.println("--------> Blue has won after " + moves + " attempted moves! Here is the final board.");	
			isGameOver = true;
			printBoard();
		}
	}
       /**
	 *. Method that is used to print the gameboard
	 */
	private static void printBoard() {
		int spaceCounter = 0; // Use this counter to know how many spaces to put in front of line
		String spaces = "";
		for(char[] i : board) { // iterate through each array (row) in the board
			for(int k = 0; k < spaceCounter; k++) // create the 'spaces' String
				spaces += " ";

			System.out.print(spaces);
			for(char j : i) { // iterate through each column in the row
				if(j == '0')
					System.out.print(ANSI_WHITE + "" +  j + " " + ANSI_RESET);
				else if(j == 'R')
					System.out.print(ANSI_RED + "" + j + " " + ANSI_RESET);
				else
					System.out.print(ANSI_BLUE + "" + j + " " + ANSI_RESET);
			}
			spaceCounter += 1;
			spaces = "";
			System.out.println();
		}
	}
}
