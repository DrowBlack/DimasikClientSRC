package de.maxhenkel.lame4j;

import de.maxhenkel.lame4j.LibraryLoader;
import de.maxhenkel.lame4j.UnknownPlatformException;
import java.io.IOException;

class Lame {
    private static boolean loaded;
    private static Exception error;

    Lame() {
    }

    public static void load() throws UnknownPlatformException, IOException {
        if (loaded) {
            if (error != null) {
                if (error instanceof IOException) {
                    throw (IOException)error;
                }
                if (error instanceof UnknownPlatformException) {
                    throw (UnknownPlatformException)error;
                }
                throw new RuntimeException(error);
            }
            return;
        }
        try {
            LibraryLoader.load("lame4j");
            loaded = true;
        }
        catch (UnknownPlatformException | IOException e) {
            error = e;
            throw e;
        }
    }

    public static boolean isLoaded() {
        return loaded && error == null;
    }
}
