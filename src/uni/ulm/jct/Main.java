package uni.ulm.jct;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLOutput;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Main {
    
    public static GameLogic game;
    public static Scanner scanner;
    public static boolean running;

    public static void main(String[] args) {
	    game = new GameLogic();
	    scanner = new Scanner(System.in);
	    running = true;
	    
        game.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(evt.getPropertyName().equals("Board")){
                    System.out.println("Current Game: ");
                    System.out.println(game.getStringRepresentation());
                    System.out.println("\n\n");
                }
            }
        });
        //playRandomGame(game);
        
        while(running){
            switch (game.getGameState()){
                case Opener -> opener();
                case Swap2Question -> swap2Question();
                case AwnserA -> awnserA();
                case AwnserB -> awnserB();
                case AwnserC -> awnserC();
                case ColorPick -> colorPick();
                case TurnPlayer1 -> turnPlayer1();
                case TurnPlayer2 -> turnPlayer2();
                case ShowWinner -> showWinner();
            }
        }

    }

    private static void showWinner() {
        System.out.println("Player " + game.getWinner() + "has won the game! (Press enter to exit)");
        scanner.nextLine();
        running = false;
    }

    private static void turnPlayer2() {
        System.out.println("Player2, place a stone on the board");
        if(game.getPlayer2Color().equals(GameLogic.BLACK)){
            placeBlackStone();
        }else{
            placeWhiteStone();
        }
    }

    private static void turnPlayer1() {
        System.out.println("Player1, place a stone on the board");
        if(game.getPlayer1Color().equals(GameLogic.BLACK)){
            placeBlackStone();
        }else{
            placeWhiteStone();
        }
    }

    private static void colorPick() {
        System.out.println("(ColorPick) Player1, you get the option to choose you stone color:\n" +
                "a) Black\n" +
                "b) White");
        System.out.print("-> ");
        String awnser = scanner.nextLine().strip();
        game.makeDecision(awnser);
    }

    private static void awnserC() {
        System.out.println("Player2, place a white and black stone, " +
                "player1 will now get the decision of her/his playing color");
        placeWhiteStone();
        placeBlackStone();
    }

    private static void awnserB() {
        System.out.println("Swapping Stones");
    }

    private static void awnserA() {
        System.out.println("Player2, place white Stone ");
        placeWhiteStone();
    }

    private static void playRandomGame(GameLogic _game){
        GameLogic game = _game;
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

    private static void opener(){
        System.out.println(game.getStringRepresentation());
        System.out.println("Player 1, place a white and 2 black stones");
        placeBlackStone();
        placeBlackStone();
        placeWhiteStone();
    }

    private static void swap2Question(){
        System.out.println("(Swap2Question) Player 2, choose one of the following options: \n" +
                "a) \n" +
                "b) \n" +
                "c) \n");
        System.out.print("-> ");
        String awnser = scanner.nextLine().strip();
        game.makeDecision(awnser);
    }

    private static void placeBlackStone(){
        boolean valid = false;
        while (!valid){
            try{
                System.out.print("Input Cords (<x>, <y>): ");
                String line = scanner.nextLine();
                String[] cords = line.split(",");
                int x = Integer.parseInt(cords[0]);
                int y = Integer.parseInt(cords[1]);
                valid = game.putStone(Field.Black, x, y);
                if(!valid) System.out.println("A stone is already occupaing this place!");
            }catch(NumberFormatException e){
                System.out.println("Wrong Format! (Example input: 1,1)");
            }
        }
    }

    private static void placeWhiteStone(){
        boolean valid = false;
        while (!valid){
            try{
                System.out.print("Input Cords (<x>, <y>): ");
                String line = scanner.nextLine();
                String[] cords = line.split(",");
                int x = Integer.parseInt(cords[0]);
                int y = Integer.parseInt(cords[1]);
                valid = game.putStone(Field.White, x, y);
                if(!valid) System.out.println("A stone is already occupaing this place!");
            }catch(NumberFormatException e){
                System.out.println("Wrong Format! (Example input: 1,1)");
            }
       }
    }
}
