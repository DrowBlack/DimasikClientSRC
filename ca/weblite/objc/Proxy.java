package ca.weblite.objc;

import ca.weblite.objc.Client;
import ca.weblite.objc.Message;
import ca.weblite.objc.Peerable;
import ca.weblite.objc.RuntimeUtils;
import com.sun.jna.Pointer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Proxy
implements Peerable {
    private static Map<Pointer, Proxy> proxyCache = new HashMap<Pointer, Proxy>();
    Client client;
    Pointer peer;
    private int retainCount = 0;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Object retain(Object obj) {
        Map<Pointer, Proxy> map = proxyCache;
        synchronized (map) {
            if (Proxy.class.isInstance(obj)) {
                Proxy pobj = (Proxy)obj;
                ++pobj.retainCount;
            }
        }
        return obj;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Object release(Object obj) {
        Map<Pointer, Proxy> map = proxyCache;
        synchronized (map) {
            if (Proxy.class.isInstance(obj)) {
                Proxy pobj = (Proxy)obj;
                --pobj.retainCount;
                if (pobj.retainCount <= 0) {
                    proxyCache.remove(pobj.getPeer());
                }
            }
        }
        return obj;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void drainCache() {
        Map<Pointer, Proxy> map = proxyCache;
        synchronized (map) {
            HashSet<Proxy> remove = new HashSet<Proxy>();
            for (Proxy p : proxyCache.values()) {
                if (p.retainCount != 0) continue;
                remove.add(p);
            }
            for (Proxy p : remove) {
                proxyCache.remove(p.getPeer());
            }
        }
    }

    public Proxy() {
        this(Client.getInstance());
    }

    public Proxy(Client client) {
        this(client, Pointer.NULL);
    }

    public Proxy(Client client, Pointer peer) {
        this.client = client;
        this.peer = peer;
    }

    public Proxy(Pointer peer) {
        this(Client.getInstance(), peer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Proxy load(Pointer peer) {
        Map<Pointer, Proxy> map = proxyCache;
        synchronized (map) {
            Proxy cached = proxyCache.get(peer);
            if (cached == null) {
                cached = new Proxy(peer);
                proxyCache.put(peer, cached);
            }
            Proxy.retain(cached);
            return cached;
        }
    }

    public void dispose(boolean sendDeallocMessage) {
        proxyCache.remove(this.getPeer());
        if (sendDeallocMessage) {
            this.send("dealloc", new Object[0]);
        }
    }

    public Pointer sendPointer(Pointer selector, Object ... args) {
        return (Pointer)this.send(selector, args);
    }

    public Pointer sendPointer(String selector, Object ... args) {
        return this.sendPointer(RuntimeUtils.sel(selector), args);
    }

    public Proxy sendProxy(String selector, Object ... args) {
        return (Proxy)this.send(selector, args);
    }

    public Proxy sendProxy(Pointer selector, Object ... args) {
        return (Proxy)this.send(selector, args);
    }

    public String sendString(Pointer selector, Object ... args) {
        return (String)this.send(selector, args);
    }

    public String sendString(String selector, Object ... args) {
        return this.sendString(RuntimeUtils.sel(selector), args);
    }

    public int sendInt(Pointer selector, Object ... args) {
        Object res = this.send(selector, args);
        if (Boolean.TYPE.isInstance(res) || Boolean.class.isInstance(res)) {
            return (Boolean)res != false ? 1 : 0;
        }
        if (Byte.TYPE.isInstance(res) || Byte.class.isInstance(res)) {
            return new Byte((Byte)res).intValue();
        }
        if (Integer.TYPE.isInstance(res) || Integer.class.isInstance(res)) {
            return (Integer)res;
        }
        if (Long.TYPE.isInstance(res) || Long.class.isInstance(res)) {
            return new Long((Long)res).intValue();
        }
        return (Integer)res;
    }

    public int sendInt(String selector, Object ... args) {
        return this.sendInt(RuntimeUtils.sel(selector), args);
    }

    public double sendDouble(Pointer selector, Object ... args) {
        return (Double)this.send(selector, args);
    }

    public double sendDouble(String selector, Object ... args) {
        return this.sendDouble(RuntimeUtils.sel(selector), args);
    }

    public boolean sendBoolean(Pointer selector, Object ... args) {
        Object res = this.send(selector, args);
        if (Boolean.TYPE.isInstance(res) || Boolean.class.isInstance(res)) {
            return (Boolean)res;
        }
        if (Byte.TYPE.isInstance(res) || Byte.class.isInstance(res)) {
            byte bres = (Byte)res;
            return bres > 0;
        }
        if (Integer.TYPE.isInstance(res) || Integer.class.isInstance(res)) {
            int ires = (Integer)res;
            return ires > 0;
        }
        if (Long.TYPE.isInstance(res) || Long.class.isInstance(res)) {
            long lres = (Long)res;
            return lres > 0L;
        }
        return (Boolean)res;
    }

    public boolean sendBoolean(String selector, Object ... args) {
        return this.sendBoolean(RuntimeUtils.sel(selector), args);
    }

    public Object send(Pointer selector, Object ... args) {
        return this.client.send(this.peer, selector, args);
    }

    public Object send(String selector, Object ... args) {
        return this.client.send(this.peer, selector, args);
    }

    public Object send(Message ... msgs) {
        return this.client.send(msgs);
    }

    public Object sendRaw(Pointer selector, Object ... args) {
        return Client.getRawClient().send((Peerable)this, selector, args);
    }

    public Object sendRaw(String selector, Object ... args) {
        return this.sendRaw(RuntimeUtils.sel(selector), args);
    }

    public Object sendRaw(Message ... msgs) {
        return Client.getRawClient().send(msgs);
    }

    public Proxy chain(Pointer selector, Object ... args) {
        this.send(selector, args);
        return this;
    }

    public Proxy chain(String selector, Object ... args) {
        this.send(selector, args);
        return this;
    }

    public Proxy chain(Message ... msgs) {
        this.send(msgs);
        return this;
    }

    public Client getClient() {
        return this.client;
    }

    public Proxy setClient(Client client) {
        this.client = client;
        return this;
    }

    public Pointer getPeer() {
        return this.peer;
    }

    public void setPeer(Pointer peer) {
        this.peer = peer;
    }

    public String toString() {
        System.out.println("The peer is " + this.getPeer());
        if (this.getPeer() == null) {
            return "null";
        }
        Pointer res = Client.getRawClient().sendPointer(this.getPeer(), "description", new Object[0]);
        Pointer str = Client.getRawClient().sendPointer(res, "UTF8String", new Object[0]);
        return str.getString(0L);
    }

    public boolean equals(Object o) {
        if (!Peerable.class.isInstance(o)) {
            return false;
        }
        Peerable p = (Peerable)o;
        return this.getPeer() == p.getPeer();
    }

    public int hashCode() {
        return this.getPeer().hashCode();
    }

    public void set(String key, Object value) {
        this.send("setValue:forKey:", value, key);
    }

    public Object get(String key) {
        return this.send("valueForKey:", key);
    }

    public int getInt(String key) {
        return this.sendInt("valueForKey:", key);
    }

    public boolean getBoolean(String key) {
        return this.sendBoolean("valueForKey:", key);
    }

    public Proxy getProxy(String key) {
        return this.sendProxy("valueForKey:", key);
    }

    public double getDouble(String key) {
        return this.sendDouble("valueForKey:", key);
    }

    public Pointer getPointer(String key) {
        return this.sendPointer("valueForKey:", key);
    }
}
