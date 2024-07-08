/*
Name: Sasha M.
Purpose: position class for x and y variables to be used in main file
Date modified: 6/13/2024
*/


public class Position {
    private int x; //initializing x and y variables
    private int y;

    //setting x and y variables
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    //methods to get and set x and y variables
    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
}//end of position class
