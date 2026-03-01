package ca.weblite.objc;

import ca.weblite.objc.Message;
import ca.weblite.objc.Peerable;
import ca.weblite.objc.PeerableRecipient;
import ca.weblite.objc.Proxy;
import ca.weblite.objc.RuntimeUtils;
import com.sun.jna.Pointer;
import java.util.ArrayList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Client {
    private static Client instance;
    private static Client rawClient;
    boolean coerceInputs = true;
    boolean coerceOutputs = true;

    public static Client getInstance() {
        if (instance == null) {
            instance = new Client();
        }
        return instance;
    }

    public static Client getRawClient() {
        if (rawClient == null) {
            rawClient = new Client();
            Client.rawClient.coerceInputs = false;
            Client.rawClient.coerceOutputs = false;
        }
        return rawClient;
    }

    public Client setCoerceInputs(boolean coerceInputs) {
        this.coerceInputs = coerceInputs;
        return this;
    }

    public Client setCoerceOutputs(boolean coerceOutputs) {
        this.coerceOutputs = coerceOutputs;
        return this;
    }

    public boolean getCoerceInputs() {
        return this.coerceInputs;
    }

    public boolean getCoerceOutputs() {
        return this.coerceOutputs;
    }

    public Object send(Pointer receiver, Pointer selector, Object ... args) {
        return RuntimeUtils.msg(this.coerceOutputs, this.coerceInputs, receiver, selector, args);
    }

    public Object send(Pointer receiver, String selector, Object ... args) {
        return this.send(receiver, RuntimeUtils.sel(selector), args);
    }

    public Object send(String receiver, Pointer selector, Object ... args) {
        return this.send(RuntimeUtils.cls(receiver), selector, args);
    }

    public Object send(String receiver, String selector, Object ... args) {
        return this.send(RuntimeUtils.cls(receiver), RuntimeUtils.sel(selector), args);
    }

    public Object send(Peerable proxy, Pointer selector, Object ... args) {
        return this.send(proxy.getPeer(), selector, args);
    }

    public Object send(Peerable proxy, String selector, Object ... args) {
        return this.send(proxy.getPeer(), RuntimeUtils.sel(selector), args);
    }

    public Pointer sendPointer(Pointer receiver, Pointer selector, Object ... args) {
        Object res = this.send(receiver, selector, args);
        if (Pointer.class.isInstance(res)) {
            return (Pointer)res;
        }
        if (Proxy.class.isInstance(res)) {
            return ((Proxy)res).getPeer();
        }
        if (Long.TYPE.isInstance(res) || Long.class.isInstance(res)) {
            return new Pointer((Long)res);
        }
        return (Pointer)res;
    }

    public Pointer sendPointer(Pointer receiver, String selector, Object ... args) {
        return this.sendPointer(receiver, RuntimeUtils.sel(selector), args);
    }

    public Pointer sendPointer(String receiver, Pointer selector, Object ... args) {
        return this.sendPointer(RuntimeUtils.cls(receiver), selector, args);
    }

    public Pointer sendPointer(String receiver, String selector, Object ... args) {
        return this.sendPointer(RuntimeUtils.cls(receiver), RuntimeUtils.sel(selector), args);
    }

    public Proxy sendProxy(Pointer receiver, Pointer selector, Object ... args) {
        return (Proxy)this.send(receiver, selector, args);
    }

    public Proxy sendProxy(String receiver, Pointer selector, Object ... args) {
        return this.sendProxy(RuntimeUtils.cls(receiver), selector, args);
    }

    public Proxy sendProxy(String receiver, String selector, Object ... args) {
        return this.sendProxy(RuntimeUtils.cls(receiver), RuntimeUtils.sel(selector), args);
    }

    public Proxy sendProxy(Pointer receiver, String selector, Object ... args) {
        return this.sendProxy(receiver, RuntimeUtils.sel(selector), args);
    }

    public Proxy chain(String cls, Pointer selector, Object ... args) {
        Pointer res = Client.getRawClient().sendPointer(cls, selector, args);
        return new Proxy(res);
    }

    public Proxy chain(String cls, String selector, Object ... args) {
        return this.chain(cls, RuntimeUtils.sel(selector), args);
    }

    public PeerableRecipient newObject(Class<? extends PeerableRecipient> cls) {
        try {
            PeerableRecipient instance = cls.newInstance();
            if (instance.getPeer() == Pointer.NULL) {
                Pointer peer = new Pointer(RuntimeUtils.createProxy(instance));
                instance.setPeer(peer);
            }
            return instance;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public Object send(Message ... messages) {
        return RuntimeUtils.msg(messages);
    }

    public Message[] buildMessageChain(Object ... parameters) {
        ArrayList<Message> messages = new ArrayList<Message>();
        for (int i = 0; i < parameters.length; ++i) {
            Message buffer = new Message();
            buffer.coerceInput = this.coerceInputs;
            buffer.coerceOutput = this.coerceOutputs;
            buffer.receiver = String.class.isInstance(parameters[i]) ? ("_".equals(parameters[i]) ? Pointer.NULL : RuntimeUtils.cls((String)parameters[i])) : (Peerable.class.isInstance(parameters[i]) ? ((Peerable)parameters[i]).getPeer() : (Pointer)parameters[i]);
            buffer.selector = String.class.isInstance(parameters[++i]) ? RuntimeUtils.sel((String)parameters[i]) : (Peerable.class.isInstance(parameters[i]) ? ((Peerable)parameters[i]).getPeer() : (Pointer)parameters[i]);
            ++i;
            while (i < parameters.length && parameters[i] != null) {
                buffer.args.add(parameters[i++]);
            }
            messages.add(buffer);
        }
        return messages.toArray(new Message[messages.size()]);
    }
}
