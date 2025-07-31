package game;

import java.io.PrintStream;
import java.util.Scanner;

public class HumanPlayer implements Player {
    private final PrintStream out;
    private final Scanner in;

    private final Move specialMoveForDraw;

    private HumanPlayer(final PrintStream out, final Scanner in, final Move special) {
        this.out = out;
        this.in = in;
        this.specialMoveForDraw = special;
    }

    public HumanPlayer(final Move special) {
        this(System.out, new Scanner(System.in), special);
    }

    @Override
    public Move move(final Position position, final Cell cell) {
        while (true) {
            out.println("Position");
            out.println(position);
            out.println(cell + "'s move");
            out.println("Enter row and column");

            int r;
            int c;

            boolean wasDrawOffer = false;

            while (true) {
                try {
                    String nextLine = in.nextLine();
                    Scanner inLine = new Scanner(nextLine);

                    String maybeDraw = inLine.next();

                    if (maybeDraw.equals("draw?")) {
                        if (wasDrawOffer) {
                            return new Move(-1, -1, cell);
                        }

                        wasDrawOffer = true;

                        System.out.println("Player with cell " + cell + " offers a draw. Do you agree?");
                        System.out.println("Choose: yes/no");

                        String answer = in.nextLine();

                        if (answer.equals("yes")) {
                            return specialMoveForDraw;
                        } else {
                            System.out.println("Draw offer rejected. Input move");
                        }

                        continue;
                    }

                    if (maybeDraw.equals("loose")) {
                        return new Move(-1, -1, cell);
                    }

                    r = Integer.parseInt(maybeDraw);
                    c = inLine.nextInt();
                    break;
                } catch (Exception e) {
                    System.out.println("Incorrect input, try again");
                }
            }

            Move move = new Move(r, c, cell);
            if (position.isValid(move)) {
                return move;
            }

            out.println("Move " + move + " is invalid");
        }
    }
}
