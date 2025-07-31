package game;

import java.util.Arrays;

public class MNKSquareBoard extends MNKAbstractBoard{
    public MNKSquareBoard(int m, int n, int k, Move special) {
        super(m, n, k, special);

        this.freePlaceCount = n * m;

        this.cells = new Cell[m][n];
        for (Cell[] row : cells) {
            Arrays.fill(row, Cell.E);
        }

        position = new MNKPosition(m, n, cells, turn);
    }
}
