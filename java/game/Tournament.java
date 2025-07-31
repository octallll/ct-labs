package game;

import java.util.*;

public class Tournament {
    private final int playersCount;
    private final int m;
    private final int n;
    private final int k;
    private final Move special;
    private final Players[] types;
    private final BoardType boardType;
    private final Random random;

    Tournament(int playersCount, int m, int n, int k, Move special, Players[] types, BoardType boardType) {
        this.playersCount = playersCount;
        this.m = m;
        this.n = n;
        this.k = k;
        this.special = special;
        this.types = types;
        this.boardType = boardType;
        random = new Random();
    }

    public void playTournament() {
        List<Integer> upperBracket = new ArrayList<>();
        List<Integer> downBracket = new ArrayList<>();

        initialPlayers(upperBracket, downBracket);

        List<Integer> playersOutOrder = new ArrayList<>();

        while (upperBracket.size() != 1 || downBracket.size() != 1) {
            ArrayList<Integer> toDownBracket = new ArrayList<>();

            if (upperBracket.size() > 1) {
                upperBracket = new ArrayList<>(playUpperBracket(upperBracket, toDownBracket));
            }

            if (!toDownBracket.isEmpty()) {
                if (downBracket.isEmpty()) {
                    downBracket = new ArrayList<>(toDownBracket);
                } else {
                    List<Integer> downAndUpper = getDownAndUpper(downBracket, toDownBracket);

                    downBracket = new ArrayList<>(downAndUpper);
                }
            }

            if (downBracket.size() > 1) {
                downBracket = new ArrayList<>(playDownBracket(downBracket, playersOutOrder));
            }
        }

        int[] matchResult = playFinal(upperBracket, downBracket);

        int winner = matchResult[0];
        int looser = matchResult[1];

        printResults(winner, looser, playersOutOrder);
    }

    private void initialPlayers(List<Integer> upperBracket, List<Integer> downBracket) {
        if (playersCount > 2) {
            for (int player = 1; player <= playersCount; player++) {
                upperBracket.add(player);
            }
        } else {
            upperBracket.add(1);
            downBracket.add(2);
        }
    }

    private int[] playFinal(List<Integer> upperBracket, List<Integer> downBracket) {
        System.out.println("Final!");

        int firstPlayer = upperBracket.getFirst();
        int secondPlayer = downBracket.getFirst();

        System.out.println("Player " + firstPlayer + " vs player " + secondPlayer);

        return playMatch(firstPlayer, secondPlayer, m, n, k, special, boardType, types[firstPlayer - 1], types[secondPlayer - 1]);
    }

    private void printResults(int winner, int looser, List<Integer> playersOutOrder) {
        System.out.println("The winner is player " + winner);
        System.out.println("Runner up is player " + looser);

        int currentPlace = 2;

        for (int outPlayerIndex = playersOutOrder.size() - 1, placeIndex = 0; outPlayerIndex >= 0; outPlayerIndex--, placeIndex++) {
            if (playersOutOrder.get(outPlayerIndex) == 0) {
                currentPlace++;
                continue;
            }

            System.out.println("Player " + playersOutOrder.get(outPlayerIndex) + " in " + currentPlace + " place");
        }
    }

    private List<Integer> playDownBracket(List<Integer> downBracket, List<Integer> playersOutOrder) {
        List<Integer> nextDownBracket = new ArrayList<>();

        if (downBracket.size() % 2 == 1) {
            nextDownBracket.add(downBracket.getLast());
        }

        for (int playerIndex = 0; playerIndex + 1 < downBracket.size(); playerIndex += 2) {
            int firstPlayer = downBracket.get(playerIndex);
            int secondPlayer = downBracket.get(playerIndex + 1);

            if (firstPlayer == 0 || secondPlayer == 0) {
                if (firstPlayer == 0) {
                    nextDownBracket.add(secondPlayer);
                }

                if (secondPlayer == 0) {
                    nextDownBracket.add(firstPlayer);
                }

                continue;
            }

            if (random.nextInt() % 2 == 0) {
                int temp = firstPlayer;
                firstPlayer = secondPlayer;
                secondPlayer = temp;
            }

            System.out.println("Down bracket match: " + firstPlayer + " vs " + secondPlayer);
            System.out.println("Player " + firstPlayer + " will start with cell X");

            int[] matchResult = playMatch(firstPlayer, secondPlayer, m, n, k, special, boardType, types[firstPlayer - 1], types[secondPlayer - 1]);

            int winner = matchResult[0];
            int looser = matchResult[1];

            System.out.println("Player " + winner + " win player " + looser + " is out");

            nextDownBracket.add(winner);
            playersOutOrder.add(looser);
        }

        playersOutOrder.add(0);

        return nextDownBracket;
    }

    private List<Integer> playUpperBracket(List<Integer> upperBracket, List<Integer> toDownBracket) {
        Collections.shuffle(upperBracket);

        ArrayList<Integer> nextUpperBracket = new ArrayList<>();

        if (upperBracket.size() % 2 == 1) {
            nextUpperBracket.add(upperBracket.getLast());
        }

        for (int playerIndex = 0; playerIndex + 1 < upperBracket.size(); playerIndex += 2) {
            int firstPlayer = upperBracket.get(playerIndex);
            int secondPlayer = upperBracket.get(playerIndex + 1);

            if (firstPlayer == 0 || secondPlayer == 0) {
                if (firstPlayer == 0) {
                    nextUpperBracket.add(secondPlayer);
                }

                if (secondPlayer == 0) {
                    nextUpperBracket.add(firstPlayer);
                }

                continue;
            }

            if (random.nextInt() % 2 == 0) {
                int temp = firstPlayer;
                firstPlayer = secondPlayer;
                secondPlayer = temp;
            }

            System.out.println("Upper bracket game: " + firstPlayer + " vs " + secondPlayer);
            System.out.println("Player " + firstPlayer + " starts with cell X");

            int[] matchResult = playMatch(firstPlayer, secondPlayer, m, n, k, special, boardType, types[firstPlayer - 1], types[secondPlayer - 1]);

            int winner = matchResult[0];
            int looser = matchResult[1];

            System.out.println("Player " + winner + " win, player " + looser + " go to down bracket");

            nextUpperBracket.add(winner);
            toDownBracket.add(looser);
        }

        return nextUpperBracket;
    }

    private List<Integer> getDownAndUpper(List<Integer> downBracket, List<Integer> toDownBracket) {
        List<Integer> downAndUpper = new ArrayList<>();

        int downIndex = 0, upperIndex = 0;

        while (downIndex < downBracket.size() && upperIndex < toDownBracket.size()) {
            if (downIndex < upperIndex) {
                downAndUpper.add(downBracket.get(downIndex++));
            } else {
                downAndUpper.add(toDownBracket.get(upperIndex++));
            }
        }

        while (downIndex < downBracket.size()) {
            downAndUpper.add(downBracket.get(downIndex++));
        }

        while (upperIndex < toDownBracket.size()) {
            downAndUpper.add(toDownBracket.get(upperIndex++));
        }
        return downAndUpper;
    }

    public static int[] playMatch(int firstPlayer, int secondPlayer, int m, int n, int k, Move special, BoardType boardType, Players player1Type, Players player2Type) {
        int gameResult;

        do {
            gameResult = playGame(m, n, k, special, boardType, player1Type, player2Type);

            if (gameResult == 0) {
                int temp = firstPlayer;
                firstPlayer = secondPlayer;
                secondPlayer = temp;

                System.out.println("Draw, player " + firstPlayer + " and player " + secondPlayer + " will play again");
                System.out.println("Player " + firstPlayer + " will start with cell X");
            }
        } while (gameResult == 0);

        int winner;
        int looser;

        if (gameResult == 1) {
            winner = firstPlayer;
            looser = secondPlayer;
        } else {
            winner = secondPlayer;
            looser = firstPlayer;
        }

        int[] matchResult = new int[2];
        matchResult[0] = winner;
        matchResult[1] = looser;

        return matchResult;
    }

    public static int playGame(int m, int n, int k, Move special, BoardType boardType, Players player1Type, Players player2Type) {
        int result;

        Board board;

        if (boardType == BoardType.Square) {
            board = new MNKSquareBoard(m, n, k, special);
        } else {
            board = new MNKRhombusBoard(n, k, special);
        }

        final Player player1 = getPlayer(player1Type, special, board.getM(), board.getN());
        final Player player2 = getPlayer(player2Type, special, board.getM(), board.getN());

        final Game game = new Game(false, player1, player2);

        result = game.play(board);

        return result;
    }

    public static Player getPlayer(Players type, Move special, int m, int n) {
        final Player player;

        if (type == Players.Human) {
            player = new HumanPlayer(special);
        } else if (type == Players.Sequential) {
            player = new SequentialPlayer(m, n);
        } else  {
            player = new RandomPlayer(m, n);
        }

        return player;
    }
}
