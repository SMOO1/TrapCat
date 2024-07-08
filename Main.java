/*
Name: Sasha M.
Purpose: Main file to execute all code and set up GUI
Date modified: 6/13/2024
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent; //importing necessary libraries
import java.io.*; 
public class Main {
    private static final int GRID_SIZE = 10; // value to make 10x10 grid
    private static final int maxMoves = 17; //max movess possible
    private static GameBoard gameBoard; //initialising variables from the classes
    private static AI ai;
    
    private static int moveCount = 0; //initial variables for number of moves and player score set as 0
    private static int playerScore = 0;
    private static String[] roundResults = new String[maxMoves]; //array to hold move results
    private static int gameNumber = 1; // Variable to track the game number
    
    //main function
    public static void main(String[] args) {
        //instructions for user
        System.out.println("This is Trap the Cat, you must place borders to trap it in order to stop the cat from touching the edge of the screen (escaping), you have a maximum of " + maxMoves + " borders that you may place, Good luck!"); 
        //creating game board and ai from their classes
        gameBoard = new GameBoard(GRID_SIZE);
        ai = new AI(GRID_SIZE / 2, GRID_SIZE / 2); // Start AI at the center of the grid
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }//end of main
    
    //function to set up the GUI
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Trap the Cat"); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);//frame size
        
        //painting the grid
        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                generateGrid(g);
            }
        };//end of JPanel
        //function to return x and y values of the users mouse
        panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                //checking if maximum moves reached
                if (moveCount >= maxMoves) {
                    JOptionPane.showMessageDialog(frame, "Game over! Maximum moves reached.");
                    endGame(frame, "Game over! Maximum moves reached.");
                    return;
                }

                int x = e.getX() / (panel.getWidth() / GRID_SIZE);  // getting x and y coordinates of mouse click
                int y = e.getY() / (panel.getHeight() / GRID_SIZE);
                
                if (x < 0 || x >= GRID_SIZE || y < 0 || y >= GRID_SIZE) {
                    return; // Invalid move, ignore
                }
                
                gameBoard.addBorder(x, y);  // adding borders based on where the user clicks
                gameBoard.updateBoard();  // adding these borders to the game board
                panel.repaint(); // showing the board

                boolean moved = ai.move(gameBoard); // seeing if AI has moved
                gameBoard.updateBoard(); // updating the board
                panel.repaint(); // showing the board
                
                //logging the result
                roundResults[moveCount] = "Move " + (moveCount + 1) + ": Player placed border at (" + x + ", " + y + "). AI moved to (" + ai.getPosition().getX() + ", " + ai.getPosition().getY() + ")";
                moveCount++; //incrementing number of moves

                if (!moved) { // if the AI has not moved (no moves left, null returned)
                    int playerScore = gameBoard.checkBorderCount();  // finding user score 
                    gameBoard.writeHighScore(playerScore);
                    JOptionPane.showMessageDialog(frame, "You win! Your score: " + playerScore); // printing "you win"
                    showHighScore(); // printing score
                    endGame(frame, "You win!: Score: "+playerScore);
                    
                } else if (gameBoard.checkLose(ai.getPosition())) { //if player loses (ai is touching border) print lose message
                    JOptionPane.showMessageDialog(frame, "You lose!");
                    endGame(frame, "You Lose!");
                }
            }
        }); //end of event listener 

        //setting the gameboard visible 
        frame.add(panel);
        frame.setVisible(true);
    }//end of create and show GUI method
    
    //function to create the grid 
    private static void generateGrid(Graphics g) {
        int panelWidth = 600;  //width and length of the game panel
        int panelHeight = 600;
        int cellWidth = panelWidth / GRID_SIZE; //making size of each square 
        int cellHeight = panelHeight / GRID_SIZE;

        // draw grid
        for (int i = 0; i < GRID_SIZE; i++) { //make 10x10 grid
            for (int j = 0; j < GRID_SIZE; j++) {
                g.drawRect(i * cellWidth, j * cellHeight, cellWidth, cellHeight); //rectangles for each grid square
                
                //if the user clicked on the particular sqaure on the grid, make the swquare black to symbolize the border placed
                if (gameBoard.getBoard()[i][j] == 1) {
                    g.setColor(Color.BLACK);
                    g.fillRect(i*cellWidth, j*cellHeight, cellWidth, cellHeight);
                    g.setColor(Color.BLACK); 
                }
            }
        }

        // draw AI cat based on measurements
        Position aiPosition = ai.getPosition();
        g.setColor(Color.RED);
        g.fillOval(aiPosition.getX()*cellWidth, aiPosition.getY() * cellHeight, cellWidth, cellHeight);
        g.setColor(Color.BLACK);
    }
    //function to reset game in order to play again
    private static void resetGame() {
        gameBoard = new GameBoard(GRID_SIZE);  //clearing the board
        ai = new AI(GRID_SIZE / 2, GRID_SIZE / 2); //setting ai position to the middle again
        
        roundResults = new String[maxMoves]; //resetting moves array and variables to start new game
        moveCount = 0;
        playerScore = 0; 
    }
    
    //function to show the high score of the user (lowest number of borders placed)
    private static void showHighScore() {
            int highScore = gameBoard.readHighScore();
            //checking if high score is valid and printing out the result
            if (highScore != -1) {
                System.out.println("Current High Score (Lowest Borders): " + highScore);
            } else {
                System.out.println("No high score recorded yet.");
        }
    }
    
    //function to handle the end of the game
    private static void endGame(JFrame frame, String finalMessage) {
        //ensuring buffered writer is opened properly
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Output.txt", true))) { // Append mode
            //write game number to file
            writer.write("Game " + gameNumber + ":");
            writer.newLine();
            
            //write each result to file by iterating through the roundResults array
            for (String result : roundResults) {
                if (result != null) {
                    writer.write(result);
                    writer.newLine();
                }
            }
            
            //write results to file (win/lose)
            writer.write("Result: " + finalMessage);
            writer.newLine();
            gameNumber++; // increment the game number for next game
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //ask player if they want to play again
        int response = JOptionPane.showConfirmDialog(frame, "Do you want to play again?", "Play Again", JOptionPane.YES_NO_OPTION);
        //if they choose yes, call the reset game method to start again
        if (response == JOptionPane.YES_OPTION) {
            resetGame();
            frame.repaint();
        } else {
        //if they choose to stop, end the program
            System.exit(0);
        }
    }
}
