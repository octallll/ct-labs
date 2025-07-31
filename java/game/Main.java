package game;

import java.util.*;

public class Main {
    public enum GameType {
        Tournament,
        Game
    }

    public static void main(String[] args) {
        Random random = new Random();
        Move special = new Move(random.nextInt(), random.nextInt(), Cell.B);

        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose type of board: square/rhombus");

        BoardType boardType;

        while (true) {
            String type = scanner.nextLine();

            if (type.equals("square")) {
                boardType = BoardType.Square;
                break;
            }

            if (type.equals("rhombus")) {
                boardType = BoardType.Rhombus;
                break;
            }

            System.out.println("Incorrect type, try again");
        }

        System.out.println("Enter m, n, k for game");

        int m;
        int n;
        int k;

        while (true) {
            try {
                String nextLine = scanner.nextLine();
                Scanner inLine = new Scanner(nextLine);
                m = inLine.nextInt();
                n = inLine.nextInt();
                k = inLine.nextInt();

                if (boardType == BoardType.Rhombus && m != n) {
                    System.out.println("Rhombus needs equivalent boards!");
                    continue;
                }

                break;
            } catch (Exception e) {
                System.out.println("Incorrect input, try again");
            }
        }

        System.out.println("One game or tournament?");
        System.out.println("Choose game/tournament");

        GameType gameType;

        while (true) {
            String choice = scanner.nextLine();

            if (choice.equals("game")) {
                gameType = GameType.Game;
                break;
            }

            if (choice.equals("tournament")) {
                gameType = GameType.Tournament;
                break;
            }

            System.out.println("Incorrect type of game, try again");
        }

        if (gameType == GameType.Game) {
            Players[] types = getPlayersType(2, scanner);

            Tournament oneGame = new Tournament(2, m, n, k, special, types, boardType);

            oneGame.playTournament();
        } else {
            System.out.println("Enter count of players for tournament");

            int playersCount;

            while (true) {
                try {
                    String nextLine = scanner.nextLine();
                    Scanner inLine = new Scanner(nextLine);

                    playersCount = inLine.nextInt();
                    break;
                } catch (Exception e) {
                    System.out.println("Incorrect input, try again");
                }
            }

            Players[] types = getPlayersType(playersCount, scanner);

            Tournament tournament = new Tournament(playersCount, m, n, k, special, types, boardType);

            tournament.playTournament();
        }
    }

    public static Players[] getPlayersType(int playersCount, Scanner scanner) {
        System.out.println("Input players types H/R/S");

        Players[] types = new Players[playersCount];

        while (true) {
            try {
                String playersType;
                playersType = scanner.nextLine();

                for (int i = 0; i < playersCount; i++) {
                    if (playersType.charAt(i) == 'H') {
                        types[i] = Players.Human;
                    } else if (playersType.charAt(i) == 'R') {
                        types[i] = Players.Random;
                    } else if (playersType.charAt(i) == 'S') {
                        types[i] = Players.Sequential;
                    } else {
                        throw new IllegalStateException();
                    }
                }

                break;
            } catch (Exception e) {
                System.out.println("Incorrect input, try again");
            }
        }

        return types;
    }
}
