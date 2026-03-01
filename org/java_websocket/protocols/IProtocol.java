package org.java_websocket.protocols;

public interface IProtocol {
    public boolean acceptProvidedProtocol(String var1);

    public String getProvidedProtocol();

    public IProtocol copyInstance();

    public String toString();
}
