package com.ibm.icu.impl.coll;

import com.ibm.icu.util.ICUCloneNotSupportedException;
import java.util.concurrent.atomic.AtomicInteger;

public class SharedObject
implements Cloneable {
    private AtomicInteger refCount = new AtomicInteger();

    public SharedObject clone() {
        SharedObject c;
        try {
            c = (SharedObject)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new ICUCloneNotSupportedException(e);
        }
        c.refCount = new AtomicInteger();
        return c;
    }

    public final void addRef() {
        this.refCount.incrementAndGet();
    }

    public final void removeRef() {
        this.refCount.decrementAndGet();
    }

    public final int getRefCount() {
        return this.refCount.get();
    }

    public final void deleteIfZeroRefCount() {
    }

    public static final class Reference<T extends SharedObject>
    implements Cloneable {
        private T ref;

        public Reference(T r) {
            this.ref = r;
            if (r != null) {
                ((SharedObject)r).addRef();
            }
        }

        public Reference<T> clone() {
            Reference c;
            try {
                c = (Reference)super.clone();
            }
            catch (CloneNotSupportedException e) {
                throw new ICUCloneNotSupportedException(e);
            }
            if (this.ref != null) {
                ((SharedObject)this.ref).addRef();
            }
            return c;
        }

        public T readOnly() {
            return this.ref;
        }

        public T copyOnWrite() {
            T r = this.ref;
            if (((SharedObject)r).getRefCount() <= 1) {
                return r;
            }
            SharedObject r2 = ((SharedObject)r).clone();
            ((SharedObject)r).removeRef();
            this.ref = r2;
            r2.addRef();
            return (T)r2;
        }

        public void clear() {
            if (this.ref != null) {
                ((SharedObject)this.ref).removeRef();
                this.ref = null;
            }
        }

        protected void finalize() throws Throwable {
            super.finalize();
            this.clear();
        }
    }
}
