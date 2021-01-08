package uni.ulm.jct;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class GameLogic {

    public enum GameState {
        OPENER,
        AWNSER_OPENER,
        AWNSER_A,
        AWNSER_B,
        AWNSER_C,
        COLOR_PICK,
        NORMAL,
        SHOW_WINNER
    }

    private PropertyChangeSupport pcs;

    private static final int BOARD_SIZE = 15;
    private FieldState[][] board;
    private GameState gameState;
    private String player1;
    private String player2;
    // 1 => Palyer 1, 2 => Player2
    private int currentPlayer;

    private int whiteCounter;
    private int blackCoutner;


    public GameLogic(){
        this.pcs = new PropertyChangeSupport(this);
        this.board = new FieldState[BOARD_SIZE][BOARD_SIZE];
        for(int x = 0; x < BOARD_SIZE; x++){
            for(int y = 0; y < BOARD_SIZE; y++){
                this.board[x][y] = FieldState.EMTPY;
            }
        }
        this.player1 = "black";
        this.player2 = "white";
        this.setGameState(GameState.OPENER);
        this.currentPlayer = 1;
        this.blackCoutner = 0;
        this.whiteCounter = 0;
    }

    public boolean putStone(FieldState color, int x, int y){
        if (this.board[x][y] != FieldState.EMTPY){
            return false;
        }

        switch(this.getGameState()){
            case OPENER:
                if(color.equals(FieldState.WHITE) && this.whiteCounter <= 1 ||
                    color.equals(FieldState.BLACK) && this.blackCoutner <= 2)
                {
                    this.board[x][y] = color;
                    if(color.equals(FieldState.WHITE)){
                        this.whiteCounter++;
                    }else{
                        this.blackCoutner++;
                    }
                    if(this.blackCoutner + this.whiteCounter == 3) {
                        finishTurn();
                    }
                }else{
                    return false;
                }
                break;
            case AWNSER_A:
                if(color.equals(FieldState.WHITE)){
                    this.board[x][y] = color;
                    finishTurn();
                }else{
                    return false;
                }
                break;
            case AWNSER_C:
                if(color.equals(FieldState.WHITE) && this.whiteCounter <= 1 ||
                        color.equals(FieldState.BLACK) && this.blackCoutner <= 1)
                {
                    if(color.equals(FieldState.WHITE)){
                        this.whiteCounter++;
                    }else{
                        this.blackCoutner++;
                    }
                    this.board[x][y] = color;
                    if(this.blackCoutner + this.whiteCounter == 2){
                        finishTurn();
                    }
                }else{
                    return false;
                }
                break;
            case NORMAL:
                this.board[x][y] = color;
                finishTurn();
                break;
            default:
                System.out.println("Current state is not expecting layStone()");
        }

        return true;
    }

    private void finishTurn(){
        switch (this.getGameState()){
            case OPENER:
                this.setGameState(GameState.AWNSER_OPENER);
                break;
            case AWNSER_A:
            case AWNSER_B:
                this.setGameState(GameState.NORMAL);
                break;
            case AWNSER_C:
                this.setGameState(GameState.COLOR_PICK);
                break;
            case NORMAL:
                if(playerHasWon()){
                    this.setGameState(GameState.SHOW_WINNER);
                    System.out.println("player " + this.currentPlayer + " won!");
                }
                break;
            default:
                System.err.println("Unhandelt state change!");
                break;
        }
        this.currentPlayer = (this.currentPlayer == 1) ? 2 : 1;
        this.blackCoutner = 0;
        this.whiteCounter = 0;
        // System.out.println("Turn finished, " + ((currentPlayer == 1) ? "Player 1" : "Player 2") + " is now playing, mode " + getGameState());


    }

    private boolean playerHasWon() {
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                // traverse every field
                FieldState c = board[x][y];
                // if we have a stone, check if its the start of a five row
                if(c.equals(FieldState.EMTPY)) continue;
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if(dx == 0 && dy == 0) continue;
                        if(_fiveInARow(c, x, y, dx, dy, 1)) return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean _fiveInARow(FieldState color, int x, int y, int dx, int dy, int n){
        if(n == 5) return true;
        x = x+dx;
        y = y+dy;
        n = n+1;
        if(x >= BOARD_SIZE || x < 0 || y >= BOARD_SIZE || y < 0)
            return false;
        if(!board[x][y].equals(color)) return false;
        return _fiveInARow(color, x, y, dx, dy, n);
    }

    public String getStringRepresentation(){
        String out = "";
        for(int x = 0; x < BOARD_SIZE; x++){
            for (int y = 0; y < BOARD_SIZE; y++) {
                switch (board[x][y]){
                    case WHITE:
                        out += "o";
                        break;
                    case BLACK:
                        out += "x";
                        break;
                    case EMTPY:
                        out += ".";
                        break;
                }
            }
            out += "\n";
        }
        return out;
    }

    public boolean makeDecision(String option){
        if(this.getGameState().equals(GameState.AWNSER_OPENER)){
            if(option.equals("a")){
                this.setGameState(GameState.AWNSER_A);
            }else if(option.equals("b")){
                this.player1 = "black";
                this.player2 = "white";
                this.setGameState(GameState.AWNSER_B);
            }else if(option.equals("c")){
                this.setGameState(GameState.AWNSER_C);
            }else{
                System.err.println("Option not avaible");
                return false;
            }
        }else if(this.getGameState().equals(GameState.COLOR_PICK)){
            if(option.equals("a")){
                // dont change color
                this.setGameState(GameState.NORMAL);
            }else if(option.equals("b")){
                // change color
                this.player1 = "black";
                this.player2 = "white";
                this.setGameState(GameState.NORMAL);
            }else{
                System.err.println("Option not avaible");
                return false;
            }
        }
        return true;
    }


    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        pcs.firePropertyChange("GameState", getGameState(), gameState);
        this.gameState = gameState;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        pcs.removePropertyChangeListener(pcl);
    }



}
