/*
Name: Sasha M.
Purpose: class to handle the movement of the ai cat based on what is on the game board
Date modified: 6/13/2024
*/

import java.io.*;   //importing necessary libraries
import java.util.*;

//ai class to handle the AI's position and movement logic
public class AI {
    private Position position; // Current position of the AI on the game board
    private static final int[] DIRECTION_X = {-1, 1, 0, 0}; // X-axis movement directions (left, right, none, none)
    private static final int[] DIRECTION_Y = {0, 0, -1, 1}; // Y-axis movement directions (none, none, up, down)

    // Constructor to initialize the AI's starting position
    public AI(int xStart, int yStart) {
        this.position = new Position(xStart, yStart);
    }

    // Method to get the current position of the AI
    public Position getPosition() {
        return position;
    }

    // Method to move the AI to the next position based on the game board (user placed borders as obstacles)
    public boolean move(GameBoard gameBoard) {
        Position nextMove = findPath(gameBoard);
        //if there is a possible move
        if (nextMove != null) {
            position = nextMove; //set next move
            return true; // AI moved successfully
        }
        return false; // AI is trapped
    }//end of move method


    //A* pathfinding algorithm
    //find the best path to escape the game board
    private Position findPath(GameBoard gameBoard) {
        int[][] board = gameBoard.getBoard(); // Get the game board's grid
        int size = board.length; // Size of the board

        // PriorityQueue to manage nodes to explore, sorted by their 'f' value (g + h) (total estimated cost of a path going through the current node to the goal node)
        //lambda function to specify the comparator to sort the nodes, ensuring that the nodes with the lowest f value have the highest priority
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(node -> node.f)); //priority queue to hold the nodes that still need to be explored
        boolean[][] closedList = new boolean[size][size]; // Boolean array to keep track of explored nodes (2d)

        // Start node with initial position, no parent, g-cost of 0, and calculated h-cost
        Node startNode = new Node(position.getX(), position.getY(), null, 0, estimateDistance(position.getX(), position.getY(), size));
        openList.add(startNode);

        // Main loop to explore nodes
        while (!openList.isEmpty()) {
            Node currentNode = openList.poll(); // Get the node with the lowest f-cost, returning element at front of priority container
            closedList[currentNode.x][currentNode.y] = true; // mark node as explored

            // If the current node is at the edge of the board, trace the path back to the start
            if (isAtEdge(currentNode, size)) {
                return tracePath(currentNode);
            }

            // Explore neighboring nodes in four possible directions
            for (int i = 0; i < 4; i++) {
                int newX = currentNode.x + DIRECTION_X[i];
                int newY = currentNode.y + DIRECTION_Y[i];

                // Check if move is valid
                if (isValidMove(newX, newY, size, board, closedList)) {
                    int g = currentNode.g + 1; // Increase g-cost (movement cost), cost of moving from the start node to a given node along the path.
                    int h = estimateDistance(newX, newY, size); // Calculate cost to get to edge, estimate of the cost to move from the current node to the goal node
                    Node neighborNode = new Node(newX, newY, currentNode, g, g + h); //g+h is the total estimated cost of a path going through the current node to the goal

                    // add the neighbor node to the open list (nodes that need to be explored further) 
                    openList.add(neighborNode);
                }
            }//end of for loop
        }//end of while
        return null; // Return null if no path is found
    }//end of findpath function

    // Method to check if ai move is valid
    private boolean isValidMove(int x, int y, int size, int[][] board, boolean[][] closedList) {
        return x >= 0 && x < size && y >= 0 && y < size && board[x][y] == 0 && !closedList[x][y]; //checking if the nodes that are being checked are within game board, marked 0 (to show thet there is no border), and are not already explored (held in the closed list)
    }//end of isvalidMove

    // Method to check if a node is at the edge of the board
    private boolean isAtEdge(Node node, int size) { //if the node's x or y coordinate is at the boundary of the board, meaning an edge
        return node.x == 0 || node.x == size - 1 || node.y == 0 || node.y == size - 1;
    }//end of isAtEdge

    // method to trace the path from the goal node back to the start node
    private Position tracePath(Node node) {
        Node current = node;
        
        //Iterating through the parent nodes to trace back the path, stopping at the second last node to determine the next move from the start
        while (current.parent != null && current.parent.parent != null) {
            current = current.parent;
        }
        return new Position(current.x, current.y); //returns the position that the ai should move to next
    }//end of tracepath method

    //function to estimate the distance to the closest edge
    private int estimateDistance(int x, int y, int size) { //minimum distance to any edge of the board by considering the distances to the left, right, top, and bottom edges, and return the smallest value
        return Math.min(Math.min(x, size-1-x), Math.min(y, size-1-y));
    }//end of estimateDistance method

    // Inner class to represent a node in the pathfinding algorithm
    private static class Node {
        private final int x, y, g, f; // Coordinates, g-cost (cost from start to node, known val), and f-cost (total estimated cost of the path through this node)
        private final Node parent; // Parent node to trace the path

        public Node(int x, int y, Node parent, int g, int f) {//constructor to initialize the variables
            this.x = x;
            this.y = y;
            this.parent = parent;
            this.g = g;
            this.f = f;
        } // end of Node
    }//end of class Node
}//end of class AI
