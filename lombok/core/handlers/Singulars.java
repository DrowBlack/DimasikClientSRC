package lombok.core.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Singulars {
    private static final List<String> SINGULAR_STORE = new ArrayList<String>();

    static {
        try {
            InputStream in = Singulars.class.getResourceAsStream("singulars.txt");
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String line = br.readLine();
                while (line != null) {
                    if (!(line = line.trim()).startsWith("#") && !line.isEmpty()) {
                        if (line.endsWith(" =")) {
                            SINGULAR_STORE.add(line.substring(0, line.length() - 2));
                            SINGULAR_STORE.add("");
                        } else {
                            int idx = line.indexOf(" = ");
                            SINGULAR_STORE.add(line.substring(0, idx));
                            SINGULAR_STORE.add(line.substring(idx + 3));
                        }
                    }
                    line = br.readLine();
                }
            }
            catch (Throwable throwable) {
                try {
                    in.close();
                }
                catch (Throwable throwable2) {}
                throw throwable;
            }
            try {
                in.close();
            }
            catch (Throwable throwable) {}
        }
        catch (IOException iOException) {
            SINGULAR_STORE.clear();
        }
    }

    public static String autoSingularize(String in) {
        int inLen = in.length();
        int i = 0;
        while (i < SINGULAR_STORE.size()) {
            String lastPart = SINGULAR_STORE.get(i);
            boolean wholeWord = Character.isUpperCase(lastPart.charAt(0));
            int endingOnly = lastPart.charAt(0) == '-' ? 1 : 0;
            int len = lastPart.length();
            if (inLen >= len && in.regionMatches(true, inLen - len + endingOnly, lastPart, endingOnly, len - endingOnly) && (!wholeWord || inLen == len || Character.isUpperCase(in.charAt(inLen - len)))) {
                String replacement = SINGULAR_STORE.get(i + 1);
                if (replacement.equals("!")) {
                    return null;
                }
                boolean capitalizeFirst = !replacement.isEmpty() && Character.isUpperCase(in.charAt(inLen - len + endingOnly));
                String pre = in.substring(0, inLen - len + endingOnly);
                String post = capitalizeFirst ? String.valueOf(Character.toUpperCase(replacement.charAt(0))) + replacement.substring(1) : replacement;
                return String.valueOf(pre) + post;
            }
            i += 2;
        }
        return null;
    }
}
