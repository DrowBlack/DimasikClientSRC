package lombok.eclipse.agent;

public class PatchDiagnostics {
    public static boolean setSourceRangeCheck(Object astNode, int startPosition, int length) {
        if (startPosition >= 0 && length < 0) {
            String nodeTxt = astNode == null ? "(NULL NODE)" : astNode.getClass() + ": " + astNode.toString();
            throw new IllegalArgumentException("startPos = " + startPosition + " and length is " + length + ".\n" + "This breaks the rule that lengths are not allowed to be negative. Affected Node:\n" + nodeTxt);
        }
        if (startPosition < 0 && length != 0) {
            String nodeTxt = astNode == null ? "(NULL NODE)" : astNode.getClass() + ": " + astNode.toString();
            throw new IllegalArgumentException("startPos = " + startPosition + " and length is " + length + ".\n" + "This breaks the rule that length must be 0 if startPosition is negative. Affected Node:\n" + nodeTxt);
        }
        return false;
    }
}
