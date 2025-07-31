package game;

public class MNKRhombusBoard extends MNKAbstractBoard {
    public MNKRhombusBoard(int n, int k, Move special) {
        super(2 * n - 1, 2 * n - 1, k, special);

        cells = new Cell[this.m][this.n];

        int centerX = this.m / 2;
        int centerY = this.n / 2;

        for (int r = 0; r < this.m; r++) {
            for (int c = 0; c < this.n; c++) {
                if (Math.abs(centerX - r) + Math.abs(centerY - c) >= n) {
                    cells[r][c] = Cell.B;
                } else {
                    cells[r][c] = Cell.E;
                    freePlaceCount++;
                }
            }
        }

        position = new MNKPosition(this.m, this.n, cells, turn);
    }
}
