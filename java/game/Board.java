package game;

public interface Board {
    Position getPosition();
    Result makeMove(Move move);
    Cell getCell();
    String toString();
    int getM();
    int getN();
}
