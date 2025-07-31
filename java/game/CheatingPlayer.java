package game;

public class CheatingPlayer implements Player {
    int m;
    int n;

    CheatingPlayer(int m, int n) {
        this.m = m;
        this.n = n;
    }

    @Override
    public Move move(final Position position, final Cell cell) {
        Board board = (Board) position;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                board.makeMove(new Move(i, j, cell));
            }
        }

        return new Move(0, 0, cell);
    }
}
