package uni.ulm.jct;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Main {

    public static boolean stop;
    public static GameLogic game;
    public static Scanner scanner;

    public static void main(String[] args) {
	    game = new GameLogic();
	    scanner = new Scanner(System.in);
	    stop = false;

	    game.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                GameLogic.GameState current = (GameLogic.GameState) evt.getNewValue();
                if(evt.getPropertyName().equals("GameState") && evt.getNewValue().equals(GameLogic.GameState.SHOW_WINNER))
                    stop = true;
                switch (current){
                    case OPENER -> opener();

                    case AWNSER_OPENER -> awnserOpener();
                    /*
                    case AWNSER_A -> awnserA();
                    case AWNSER_B -> awnserB();
                    case AWNSER_C -> awnserC();
                    case COLOR_PICK -> pick();
                    case NORMAL -> normal();

                     */

                }
            }
        });
        opener();

	    /*
	    game.putStone(FieldState.BLACK, 1, 1);
        game.putStone(FieldState.BLACK, 1, 2);
        game.putStone(FieldState.WHITE, 5, 5);
        //game.putStone(FieldState.WHITE, 6, 6);
        game.makeDecision("a");
        game.putStone(FieldState.WHITE, 5, 6);

        FieldState c = FieldState.WHITE;
        while(!stop){
            int x = 0;
            int y = 0;
            c = (c.equals(FieldState.WHITE) ? FieldState.BLACK : FieldState.WHITE);
            do{
                x = (int)(Math.random() * 15);
                y = (int)(Math.random() * 15);
            }while (game.putStone(c, x, y));
        }

        System.out.println(game.getStringRepresentation());

	     */

    }

    private static void opener(){
        System.out.println(game.getStringRepresentation());
        System.out.println("Player 1, place two black and a white stone");
        placeBlackStone();
        System.out.println(game.getStringRepresentation());
        placeBlackStone();
        System.out.println(game.getStringRepresentation());
        placeWhiteStone();
        System.out.println(game.getStringRepresentation());
        System.out.println("Term finished");
    }

    private static void awnserOpener(){
        System.out.println("You can choose an option from a, b or c");
        String awnser = scanner.nextLine().strip();
        game.makeDecision(awnser);
    }

    private static void placeBlackStone(){
        boolean valid = false;
        while (!valid){
            System.out.print("Input Cords (<x>, <y>): ");
            String line = scanner.nextLine();
            String[] cords = line.split(",");
            int x = Integer.parseInt(cords[0]);
            int y = Integer.parseInt(cords[1]);
            valid = game.putStone(FieldState.BLACK, x, y);
        }
    }

    private static void placeWhiteStone(){
        boolean valid = false;
        while (!valid){
            System.out.print("Input Cords (<x>, <y>): ");
            String line = scanner.nextLine();
            String[] cords = line.split(",");
            int x = Integer.parseInt(cords[0]);
            int y = Integer.parseInt(cords[1]);
            valid = game.putStone(FieldState.WHITE, x, y);
        }
    }
}