package solver;

public class State {
    private final char[][] state;
    private State parent;
    private final int heuristic;
    private final String move;
    private int edgeCost;

    State (char[][] state, State parent, int heuristic, String move){
        this.state = state;
        this.parent = parent;
        this.heuristic = heuristic;
        this.move = move;
    }

    public int getHeuristic() {
        return heuristic;
    }
    
    
    public char[][] getState() {
        return state;
    }
    
    public String getMove() {
        return move;
    }
    
    public State getParent() {
        return parent;
    }

    public void setParent(State parent) {
        this.parent = parent;
    }
    
    public void setEdgeCost(int edgeCost) {
        this.edgeCost = edgeCost;
    }
    
    public int getEdgeCost() {
        return edgeCost;
    }

    @Override
    public boolean equals(Object s) {

        int row = state.length;
        int cols = state[0].length;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < cols; j++) {
                if (state[i][j] != ((State)s).getState()[i][j]) 
                    return false;
            }
        }
       
        return true;
    }

}
