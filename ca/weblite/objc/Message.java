package ca.weblite.objc;

import com.sun.jna.Pointer;
import java.util.ArrayList;
import java.util.List;

public class Message {
    public static final int STATUS_SKIPPED = 1;
    public static final int STATUS_CANCELLED = 2;
    public static final int STATUS_COMPLETED = 3;
    public static final int STATUS_READY = 0;
    public Pointer receiver;
    public Pointer selector;
    public List args = new ArrayList();
    public Object result;
    public Exception error;
    public int status = 0;
    public boolean coerceInput;
    public boolean coerceOutput;
    public boolean inputWasCoerced;
    public boolean outputWasCoerced;
    public Message next;
    public Message previous;

    public void beforeRequest() {
    }

    public void afterResponse() {
    }
}
