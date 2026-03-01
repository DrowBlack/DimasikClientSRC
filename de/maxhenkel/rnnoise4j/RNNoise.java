package de.maxhenkel.rnnoise4j;

import de.maxhenkel.rnnoise4j.LibraryLoader;
import de.maxhenkel.rnnoise4j.UnknownPlatformException;
import java.io.IOException;

class RNNoise {
    private static boolean loaded;
    private static Exception error;

    RNNoise() {
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
            LibraryLoader.load("rnnoise4j");
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
