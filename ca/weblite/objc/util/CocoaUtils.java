package ca.weblite.objc.util;

import ca.weblite.objc.NSObject;
import ca.weblite.objc.RuntimeUtils;
import ca.weblite.objc.annotations.Msg;

public class CocoaUtils {
    public static void dispatch_async(final Runnable r) {
        new NSObject("NSObject"){

            @Msg(selector="run", like="NSObject.finalize")
            public void run() {
                r.run();
            }
        }.send("performSelectorOnMainThread:withObject:waitUntilDone:", RuntimeUtils.sel("run"), null, false);
    }

    public static void dispatch_sync(final Runnable r) {
        new NSObject("NSObject"){

            @Msg(selector="run", like="NSObject.finalize")
            public void run() {
                r.run();
            }
        }.send("performSelectorOnMainThread:withObject:waitUntilDone:", RuntimeUtils.sel("run"), null, true);
    }
}
