package solver;

import java.util.*;

public class Search {
    
    private int rows;
    private int cols;

    private final int cost = 1;

    private char[][] map;

    private ArrayList<Coordinate> goals = new ArrayList<>();

    private String[] directions = {"u", "d", "l", "r"};

    /**
     * Instantiates a new Search object
     * @param width         width of the map
     * @param height        height of the map
     * @param mapData       static map objects
     */
    public Search (int width, int height, char[][] mapData) { 
        // retrieves every single goal and adds it to the goal list 
        for (int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                if (mapData[i][j] == '.') {
                    Coordinate goal = new Coordinate(i, j);
                    goals.add(goal);
                }
            }
         }

         this.rows = height;
         this.cols = width;

         this.map = mapData;
    }

    /**
     * Checks if the given state is a goal
     * @param state         a given state
     * @return              true if the state is a goal, false otherwise
     */
    private boolean isGoal(char[][] state) {
        for (Coordinate x : getBoxCoord(state)) {
            if (!(map[x.getX()][x.getY()] == '.'))
                return false;
        }

        return true;
    }

    /**
     * Gets the player's coordinates
     * @param state         a given item state
     * @return              Coordinate object containing the player's coords
     */
    private Coordinate getPlayerCoord (char[][] state) {
        Coordinate pl = null;
        
        // searches the state and returns the player's location
        for (int i = 0; i < rows; i++) {
          for(int j = 0; j < cols; j++) {
              if (state[i][j] == '@') {
                pl = new Coordinate(i, j);
                return pl;
              }
          }
        }
        return pl;
    } 

    /**
     * Returns an array of all boxes
     * @param state         a given item state
     * @return              an ArrayList of all boxes' coordinates
     */
    private ArrayList<Coordinate> getBoxCoord (char[][] state) {
        ArrayList<Coordinate> boxCoord = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                if (state[i][j] == '$') {
                    Coordinate box = new Coordinate(i, j);
                    boxCoord.add(box);
                }
            }
        }

        return boxCoord;
    }

    /**
     * Calculates the heuristic of a state through Manhattan Distance calculations to the nearest goal
     * @param state         a given item state
     * @return              heuristics of this state
     */
    private int getHeuristic(char[][] state) {
        int heu = 0;
        for (Coordinate x : getBoxCoord(state)) {
            int h = 0;
            for (Coordinate y : goals) {
                int a = y.manhattanDist(x.getX(), x.getY());
                if (a < h)
                    h = a;
            }
            heu += h;
        }

        return heu;
    }

    /**
     * Determines what kind of move to make and how
     * @param direction     direction to move; one-character string "u", "d", "l", or "r"
     * @param plRow         player's current row
     * @param plCol         player's current column
     * @param curState      current item state
     * @return              new item state; null if move is invalid
     */
    private char[][] executeMove(String direction, int plRow, int plCol, char[][] curState) {

        int ptRow = -1; // destination row of a player
        int ptCol = -1; // destination col of a player
        int btRow = -1; // destination row of a box being pushed
        int btCol = -1; // destination col of a box being pushed

        // determine the destination
        if (direction == "u") {
          ptRow = plRow - 1;
          ptCol = plCol;
          btRow = plRow - 2;
          btCol = plCol;
        } else if (direction == "d") {
          ptRow = plRow + 1;
          ptCol = plCol;
          btRow = plRow + 2;
          btCol = plCol;
        } else if (direction == "l") {
          ptRow = plRow;
          ptCol = plCol - 1;
          btRow = plRow;
          btCol = plCol - 2;
        } else if (direction == "r") {
          ptRow = plRow;
          ptCol = plCol + 1;
          btRow = plRow;
          btCol = plCol + 2;
        }
        return handleMovement(ptRow, ptCol, btRow, btCol, plRow, plCol, curState);
    }

    /**
     * Returns a deep copy of the current item state
     * @param orig      item state to copy
     * @return          copied item state
     */
    private char[][] newCopy(char[][] orig) {
        char[][] copy = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (orig[i][j] != '\0') {
                    copy[i][j] = orig[i][j];
                }
            }
        }
        return copy;
    }

    /**
     * Simulates movement and returns the next item state
     * @param ptRow         player's destination row
     * @param ptCol         player's destination col
     * @param btRow         box's destination row
     * @param btCol         box's destination col
     * @param plRow         player's starting row
     * @param plCol         player's starting col
     * @param items         array of all items in the map
     * @return              new item state; null if move is invalid
     */
    private char[][] handleMovement(int ptRow, int ptCol, int btRow, int btCol, int plRow, int plCol, char[][] items) {
        
        // create a deep copy of all items
        char[][] newState = newCopy(items);

        // deny movement if player is moving out of bounds
        if (ptRow < 0 || ptRow >= rows || ptCol < 0 || ptCol >= cols) {
          return null;
        }
        // deny movement if player is moving towards a wall
        if (map[ptRow][ptCol] == '#') {
          return null;
        }
        // handle non-crate movement
        if (items[ptRow][ptCol] != '$') {
          newState[plRow][plCol] = ' ';
          newState[ptRow][ptCol] = '@';
          return newState;
        // handle pushing crate movement
        } else if (items[ptRow][ptCol] == '$') {
            if (btRow < 0 || btRow >= rows || btCol < 0 || btCol >= cols) {
                return null;
            }
            if (map[btRow][btCol] == '#' || items[btRow][btCol] == '$') {
              return null;
            }

            newState[btRow][btCol] = '$';
            newState[plRow][plCol] = ' ';
            newState[ptRow][ptCol] = '@';
        }
        return newState;
    }

    /**
     * Gets all possible branches coming fom the current state
     * @param parent        state to check
     * @return              List of all states resulting from the current state
     */
    private List<State> getBranches (State parent) {
        List<State> branches = new ArrayList<>();
        Coordinate pl = getPlayerCoord(parent.getState());

        // tries to move per direction
        for (String x : directions) {
            char[][] state = executeMove(x, pl.getX(), pl.getY(), parent.getState());
            // only continue for valid moves
            if (state != null) {
                State branchState = new State(state, parent, getHeuristic(state), x);
                branchState.setEdgeCost(parent.getEdgeCost() + cost);
                branches.add(branchState);
            }   
        }

        return branches;
    }

    /**
     * Gets a string concatenation of a path traversed
     * @param path          path to traverse
     * @return              string representation of the path
     */
    private String moveString (List<State> path) {
        String moves = "";
        for (State x : path) {
            moves = moves.concat(x.getMove());
        }
        return moves;
    }

    /**
     * Runs an A* search on an initial state, returning a solution to the given Sokoban puzzle.
     * @param initialState  initial state of all items (player and boxes)
     * @return              string representation of the solution
     */
    public String aStarSearch (char[][] initialState) {

        // creates a priority queue
        PriorityQueue<State> expand = new PriorityQueue<>(Comparator.comparingInt(state -> state.getHeuristic() + state.getEdgeCost()));
        Set<char[][]> visited = new HashSet<>();

        State initial = new State(initialState, null, getHeuristic(initialState), "");
        initial.setEdgeCost(0);
        expand.add(initial);

        String moveString = "";
        
        // loop while no solution is found and there are states to check
        while (!(expand.isEmpty())) {
            State frontier = expand.poll();

            // if the current state is a goal state
            if (isGoal(frontier.getState())) {
                // trace back a path to the start
                List<State> path = new ArrayList<State>();
                State current = frontier;
                while(current != null) {
                    path.add(current);
                    current = current.getParent();
                }
                Collections.reverse(path);
                moveString = moveString(path);
                break;
            }   

            // move to visited
            visited.add(frontier.getState());
            List<State> branches = getBranches(frontier);
            
            // do calculations for each branch coming off of this branch
            for (State b : branches) {
                int currentCost = frontier.getEdgeCost() + cost;

                // if state was already visited at some point and this is a better move, update it
                if (visited.contains(b.getState()) && currentCost < b.getEdgeCost()) {
                    b.setEdgeCost(currentCost);
                    b.setParent(frontier);

                    if (expand.contains(b)) {
                        expand.remove(b);
                    }
                    expand.add(b);
                }
                // else, add to queue
                else if (!(visited.contains(b.getState()))) {
                    if (!expand.contains(b)) {
                        expand.add(b);
                    }
                }
            }
        }


        return moveString;
    }

    /**
     * Prints the state for debug
     * @param state         state to print
     */
    public void printSt(char[][] state) {
        
        if (state != null) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0 ; j < cols; j++) {
                    System.out.print(state[i][j]);
                }
                System.out.print("\n");
            }
        }
    }
}
