package expression;

public interface BinaryMethods {
    int getPriority();
    String getOperationRender();
    boolean needBracketsAfter();
    boolean needBracketsBefore();
}
