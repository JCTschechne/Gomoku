package uni.ulm.jct;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class GameLogic {
    public static final String PLAYER1 = "Player1";
    public static final String PLAYER2 = "Player2";

    public static final String BLACK = "Black";
    public static final String WHITE = "White";


    public enum GameState {
        Opener{
            @Override
            public String getPlayer() {
                return PLAYER1;
            }
        },
        Swap2Question{
            @Override
            public String getPlayer() {
                return PLAYER2;
            }
        },
        AwnserA{
            @Override
            public String getPlayer() {
                return PLAYER2;
            }
        },
        AwnserB {
            @Override
            public String getPlayer() {
                return PLAYER2;
            }
        },
        AwnserC {
            @Override
            public String getPlayer() {
                return PLAYER2;
            }
        },
        ColorPick {
            @Override
            public String getPlayer() {
                return PLAYER1;
            }
        },
        TurnPlayer1 {
            @Override
            public String getPlayer() {
                return PLAYER1;
            }
        },
        TurnPlayer2 {
            @Override
            public String getPlayer() {
                return PLAYER2;
            }
        },
        ShowWinner {
            @Override
            public String getPlayer() {
                return prev.getPlayer();
            }
        };

        public abstract String getPlayer();
        public GameState prev = null;
    }


    private PropertyChangeSupport pcs;

    private static final int BOARD_SIZE = 15;
    private FieldState[][] board;
    private GameState myState;
    private String player1Color;
    private String player2Color;
    // 1 => Palyer 1, 2 => Player2
    private String currentPlayer;

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
        this.player1Color = BLACK;
        this.player2Color = WHITE;
        this.setGameState(GameState.Opener);
        this.currentPlayer = PLAYER1;
        this.blackCoutner = 0;
        this.whiteCounter = 0;
    }

    public boolean putStone(FieldState color, int x, int y){
        if (this.board[x][y] != FieldState.EMTPY){
            return false;
        }

        switch(this.getGameState()){
            case Opener:
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
            case AwnserA:
                if(color.equals(FieldState.WHITE)){
                    this.board[x][y] = color;
                    finishTurn();
                }else{
                    return false;
                }
                break;
            case AwnserC:
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
            case TurnPlayer1:
            case TurnPlayer2:
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
            case Opener:
                this.setGameState(GameState.Swap2Question);
                break;
            case AwnserA:
            case AwnserB:
                this.setGameState(GameState.TurnPlayer1);
                break;
            case AwnserC:
                this.setGameState(GameState.ColorPick);
                break;
            case TurnPlayer1:
                if(playerHasWon()){
                    this.setGameState(GameState.ShowWinner);
                }else{
                    this.setGameState(GameState.TurnPlayer2);
                }
                break;
            case TurnPlayer2:
                if(playerHasWon()){
                    this.setGameState(GameState.ShowWinner);
                }else{
                    this.setGameState(GameState.TurnPlayer1);
                }
                break;
            default:
                System.err.println("Unhandelt state change!");
                break;
        }
        this.currentPlayer = (this.currentPlayer.equals(PLAYER1)) ? PLAYER2 : PLAYER1;
        this.blackCoutner = 0;
        this.whiteCounter = 0;
        // System.out.println("Turn finished, " + ((currentPlayer == 1) ? "Player 1" : "Player 2") + " is now playing, mode " + getGameState());


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
        if(this.getGameState().equals(GameState.Swap2Question)){
            if(option.equals("a")){
                this.setGameState(GameState.AwnserA);
            }else if(option.equals("b")){
                this.player1Color = "black";
                this.player2Color = "white";
                this.setGameState(GameState.AwnserB);
            }else if(option.equals("c")){
                this.setGameState(GameState.AwnserC);
            }else{
                System.err.println("Option not avaible");
                return false;
            }
        }else if(this.getGameState().equals(GameState.ColorPick)){
            if(option.equals(WHITE)){
                // dont change color
                this.setGameState(GameState.TurnPlayer1);
            }else if(option.equals(BLACK)){
                // change color
                this.player1Color = "black";
                this.player2Color = "white";
                this.setGameState(GameState.TurnPlayer1);
            }else{
                System.err.println("Option not avaible");
                return false;
            }
        }
        return true;
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


    public GameState getGameState() {
        return myState;
    }

    public void setGameState(GameState gameState) {
        pcs.firePropertyChange("GameState", getGameState(), gameState);
        this.myState.prev = gameState;
        this.myState = gameState;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        pcs.removePropertyChangeListener(pcl);
    }



}
