package ca.weblite.objc;

public interface Recipient {
    public long methodSignatureForSelector(long var1);

    public void forwardInvocation(long var1);

    public boolean respondsToSelector(long var1);
}
