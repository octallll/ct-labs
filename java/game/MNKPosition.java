package game;

import java.util.Map;

public class MNKPosition implements Position {
    private static final Map<Cell, Character> SYMBOLS = Map.of(
            Cell.X, 'X',
            Cell.O, 'O',
            Cell.E, '.',
            Cell.B, ' '
    );

    private final int m;
    private final int n;
    private final Cell[][] cells;
    private final Cell turn;

    MNKPosition(int m, int n, Cell[][] cells, Cell turn) {
        this.m = m;
        this.n = n;
        this.cells = cells;
        this.turn = turn;
    }

    @Override
    public boolean isValid(final Move move) {
        return 0 <= move.getRow() && move.getRow() < m
                && 0 <= move.getColumn() && move.getColumn() < n
                && cells[move.getRow()][move.getColumn()] == Cell.E
                && turn == move.getValue()
                && cells[move.getRow()][move.getColumn()] != Cell.B;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("  ");

        for (int c = 0; c < n; c++) {
            sb.append(c).append(" ");
        }

        for (int r = 0; r < m; r++) {
            sb.append("\n").append("\n");
            sb.append(r);

            for (int c = 0; c < n; c++) {
                sb.append(" ").append(SYMBOLS.get(cells[r][c]));
            }
        }
        return sb.toString();
    }
}
