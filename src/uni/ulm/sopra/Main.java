package uni.ulm.sopra;

import uni.ulm.sopra.gomoku.Field;
import uni.ulm.sopra.gomoku.GameLogic;

import java.util.Scanner;

public class Main {
    public static GameLogic game;
    public static Scanner scanner;
    public static boolean running;

    public static void main(String[] args) {
	    game = new GameLogic();
	    scanner = new Scanner(System.in);
	    running = true;
	    
        game.addPropertyChangeListener(evt -> {
            if(evt.getPropertyName().equals("Board")){
                System.out.println("Current Game: ");
                System.out.println(game.getStringRepresentation());
                System.out.println("\n\n");
            }
        });
        //playRandomGame(game);
        
        while(running){
            switch (game.getGameState()){
                case Opener -> opener();
                case Swap2Question -> swap2Question();
                case AwnserA -> answerA();
                case AwnserB -> answerB();
                case AwnserC -> answerC();
                case ColorPick -> colorPick();
                case TurnPlayer1 -> turnPlayer1();
                case TurnPlayer2 -> turnPlayer2();
                case ShowWinner -> showWinner();
            }
        }

    }

    private static void showWinner() {
        System.out.println(game.getWinner() + " has won the game! (Press enter to exit)");
        scanner.nextLine();
        running = false;
    }

    private static void turnPlayer2() {
        System.out.println(player2Name() + " , place a stone on the board");
        if(game.getPlayer2Color().equals(GameLogic.BLACK)){
            placeBlackStone();
        }else{
            placeWhiteStone();
        }
    }

    private static void turnPlayer1() {
        System.out.println(player1Name() + ", place a stone on the board");
        if(game.getPlayer1Color().equals(GameLogic.BLACK)){
            placeBlackStone();
        }else{
            placeWhiteStone();
        }
    }

    private static void colorPick() {
        System.out.println("""
                (ColorPick) Player1, you get the option to choose you stone color:
                a) Black
                b) White""");
        System.out.print("-> ");
        String answer = scanner.nextLine().strip();
        game.makeDecision(answer);
    }

    private static void answerC() {
        System.out.println("Player2, place a white and black stone, " +
                "player1 will now get the decision of her/his playing color");
        placeWhiteStone();
        placeBlackStone();
    }

    private static void answerB() {
        System.out.println("Swapping Stones");
    }

    private static void answerA() {
        System.out.println("Player2, place white Stone ");
        placeWhiteStone();
    }

    private static void opener(){
        System.out.println(game.getStringRepresentation());
        System.out.println("Player 1, place a white and 2 black stones");
        placeBlackStone();
        placeBlackStone();
        placeWhiteStone();
    }

    private static void swap2Question(){
        System.out.println("""
                (Swap2Question) Player 2 , choose one of the following options:\s
                a) Chose white as your color, and place another white stone.
                b) Chose black as your color, your term is finished.
                c) Let Player 1 decide the color, and place another black and white stone.
                """);
        System.out.print("-> ");
        String answer = scanner.nextLine().strip();
        game.makeDecision(answer);
    }

    private static String player1Name(){
        return "Player 1 (" + game.getPlayer1Color() + ")";
    }

    private static String player2Name(){
        return "Player 2 (" + game.getPlayer2Color() + ")";
    }

    private static void playRandomGame(GameLogic game){
        game.putStone(Field.Black, 1, 1);
        game.putStone(Field.Black, 1, 2);
        game.putStone(Field.White, 5, 5);
        //game.putStone(FieldState.WHITE, 6, 6);
        game.makeDecision("a");
        game.putStone(Field.White, 5, 6);
        //System.out.println(game.getStringRepresentation());

        Field c = Field.White;
        while(!game.getGameState().equals(GameLogic.GameState.ShowWinner)){
            int x = 0;
            int y = 0;
            c = (c.equals(Field.White) ? Field.Black : Field.White);
            do{
                x = (int)(Math.random() * 15);
                y = (int)(Math.random() * 15);
            }while (game.putStone(c, x, y));
        }

        //System.out.println(game.getStringRepresentation());
        System.out.println(game.getCurrentPlayer());
    }

    private static void placeBlackStone(){
        placeStone(Field.Black);
    }

    private static void placeWhiteStone(){
        placeStone(Field.White);
    }

    private static void placeStone(Field c){
        boolean valid = false;
        while (!valid){
            try{
                System.out.print("Place a " + c + " Stone (<x>, <y>): ");
                String line = scanner.nextLine();
                String[] cords = line.split(",");
                int x = Integer.parseInt(cords[0]);
                int y = Integer.parseInt(cords[1]);
                valid = game.putStone(c, x, y);
                if(!valid) System.out.println("A stone is already occupying this place!");
            }catch(NumberFormatException e){
                System.out.println("Wrong Format! (Example input: 1,1)");
            }
        }
    }
}
