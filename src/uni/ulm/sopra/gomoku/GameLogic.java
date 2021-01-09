package uni.ulm.sopra.gomoku;

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
        AnswerA {
            @Override
            public String getPlayer() {
                return PLAYER2;
            }
        },
        AnswerB {
            @Override
            public String getPlayer() {
                return PLAYER2;
            }
        },
        AnswerC {
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
                return "Winner";
            }
        };

        public abstract String getPlayer();
    }


    private final PropertyChangeSupport pcs;

    private static final int BOARD_SIZE = 15;
    private final Board board;
    private GameState myState = null;
    private GameState prev = null;
    private String player1Color;
    private String player2Color;

    private int whiteCounter;
    private int blackCounter;


    public GameLogic(){
        this.pcs = new PropertyChangeSupport(this);
        this.board = new Board(BOARD_SIZE);
        this.player1Color = BLACK;
        this.player2Color = WHITE;
        this.setGameState(GameState.Opener);
        this.blackCounter = 0;
        this.whiteCounter = 0;
    }

    public boolean putStone(Field color, int x, int y){
        if (this.board.getField(x,y) != Field.Empty){
            return false;
        }

        switch(this.getGameState()){
            case Opener:
                if(color.equals(Field.White) && this.whiteCounter <= 1 ||
                    color.equals(Field.Black) && this.blackCounter <= 2)
                {
                    setField(color, x, y);  
                    if(color.equals(Field.White)){
                        this.whiteCounter++;
                    }else{
                        this.blackCounter++;
                    }
                    if(this.blackCounter + this.whiteCounter == 3) {
                        finishTurn();
                    }
                }else{
                    return false;
                }
                break;
            case AnswerA:
                if(color.equals(Field.White)){
                    setField(color, x, y);
                    finishTurn();
                }else{
                    return false;
                }
                break;
            case AnswerC:
                if(color.equals(Field.White) && this.whiteCounter <= 1 ||
                        color.equals(Field.Black) && this.blackCounter <= 1)
                {
                    if(color.equals(Field.White)){
                        this.whiteCounter++;
                    }else{
                        this.blackCounter++;
                    }
                    setField(color, x, y);
                    if(this.blackCounter + this.whiteCounter == 2){
                        finishTurn();
                    }
                }else{
                    return false;
                }
                break;
            case TurnPlayer1:
            case TurnPlayer2:
                setField(color, x, y);
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
            case AnswerA:
            case AnswerB:
                this.setGameState(GameState.TurnPlayer1);
                break;
            case AnswerC:
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
                System.err.println("Unhandled state change!");
                break;
        }
        this.blackCounter = 0;
        this.whiteCounter = 0;
    }


    public boolean makeDecision(String option){
        if(this.getGameState().equals(GameState.Swap2Question)){
            switch (option) {
                case "a" -> {
                    this.player1Color = BLACK;
                    this.player2Color = WHITE;
                    this.setGameState(GameState.AnswerA);
                }
                case "b" -> {
                    this.player1Color = WHITE;
                    this.player2Color = BLACK;
                    this.setGameState(GameState.AnswerB);
                    finishTurn();
                }
                case "c" -> this.setGameState(GameState.AnswerC);
                default -> {
                    System.err.println("Option not available");
                    return false;
                }
            }
        }else if(this.getGameState().equals(GameState.ColorPick)){
            if(option.equals("a")){
                this.player1Color = WHITE;
                this.player2Color = BLACK;
                this.setGameState(GameState.TurnPlayer1);
            }else if(option.equals("b")){
                // change color
                this.player1Color = BLACK;
                this.player2Color = WHITE;
                this.setGameState(GameState.TurnPlayer1);
            }else{
                System.err.println("Option not available");
                return false;
            }
        }
        return true;
    }

    private boolean playerHasWon() {
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                // traverse every field
                Field c = board.getField(x, y);
                // if we have a stone, check if its the start of a five row
                if(c.equals(Field.Empty)) continue;
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

    private boolean _fiveInARow(Field color, int x, int y, int dx, int dy, int n){
        if(n == 5) return true;
        x = x+dx;
        y = y+dy;
        n = n+1;
        if(x >= BOARD_SIZE || x < 0 || y >= BOARD_SIZE || y < 0)
            return false;
        if(!board.getField(x, y).equals(color)) return false;
        return _fiveInARow(color, x, y, dx, dy, n);
    }


    public GameState getGameState() {
        return myState;
    }

    public String getCurrentPlayer(){
        return this.getGameState().getPlayer();
    }

    public void setGameState(GameState gameState) {
        this.prev = getGameState();
        this.myState = gameState;
        pcs.firePropertyChange("GameState", getGameState(), gameState);

    }

    private void setField(Field c, int x, int y){
        Field old = this.board.getField(x, y);
        this.board.setField(c, x, y);
        this.pcs.firePropertyChange("Board", c, old);
    }

    public String getPlayer1Color(){
        return player1Color;
    }

    public String getPlayer2Color(){
        return player2Color;
    }

    public String getStringRepresentation(){
        StringBuilder out = new StringBuilder();
        for(int x = 0; x < BOARD_SIZE; x++){
            for (int y = 0; y < BOARD_SIZE; y++) {
                switch (board.getField(x, y)) {
                    case White -> out.append("o");
                    case Black -> out.append("x");
                    case Empty -> out.append(".");
                }
            }
            out.append("\n");
        }
        return out.toString();
    }

    public String getWinner() {
        return prev.getPlayer();
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        pcs.removePropertyChangeListener(pcl);
    }



}
