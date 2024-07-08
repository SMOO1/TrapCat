/*
Name: Sasha M.
Purpose: initialising the game board, and methods associated with the game board and file handling
Date modified: 6/13/2024
*/

import java.util.ArrayList;
import java.util.List;
import java.io.*; 
public class GameBoard {//gameboard class
    
    private int size; //initilising values to hold 
    private int[][] board;
    private List<Position> borders;

    public GameBoard(int size) {
        //making 2d array to hold the grids of the board
        this.size = size;
        this.board = new int[size][size];
        this.borders = new ArrayList<>();
    }

    public void updateBoard() {
        // Clear the board by iterating through the list of the grid
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = 0;
            }
        }
        // Update borders
        for (Position border : borders) {
            board[border.getX()][border.getY()] = 1; // 1 represents a border
        }
    }
    //method to check the amount of borders placed
    public int checkBorderCount(){
        return borders.size(); 
    }
    
    public boolean checkLose(Position catPosition) {
        // the player loses if the cat touches the edge
        int x = catPosition.getX();
        int y = catPosition.getY();
        return x == 0 || x == size - 1 || y == 0 || y == size - 1;
    }

    public int[][] getBoard() { //function to return the board array
        return board;
    }

    public void addBorder(int x, int y) { // function to add borders by the user
        borders.add(new Position(x, y));
    }
    
    //funcition to write highscore onto the file
    public void writeHighScore(int score) {
        try {//checking if file exists, and printing the highscore onto 
            File file = new File("highScore.txt");
            //getting highscore from the file, and printing it out
            int highScore = readHighScore();
            if (highScore == -1 || score < highScore) { //checking if curernt score is a high score (is valid), then printing it onto the file
                PrintWriter writer = new PrintWriter(file); //printing score onto high score file
                writer.println(score);
                writer.close();
                System.out.println("New high score written: " + score); //printing high score onto console
            }
        } catch (IOException e) { //catching file errors
            e.printStackTrace();
        }
    }
    //function to read the high score file
    public int readHighScore() {
        try {//reading the line of the file
            BufferedReader bufferedReader = new BufferedReader(new FileReader("highScore.txt"));
            String line = bufferedReader.readLine();
            bufferedReader.close();
            //checkingf if the file is not empty, and returning an integer highscore value
            if (line != null && !line.isEmpty()) {
                return Integer.parseInt(line);
            }
        } catch (IOException e) { //catching file errors
            e.printStackTrace();
        }
        return -1; //return -1 if no valid high score exists
    }
    
}//end of gameboard class
