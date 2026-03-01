package net.minecraft.util;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.SharedConstants;

public class FileUtil {
    private static final Pattern DUPLICATE_NAME_COUNT_PATTERN = Pattern.compile("(<name>.*) \\((<count>\\d*)\\)", 66);
    private static final Pattern RESERVED_FILENAMES_PATTERN = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", 2);

    public static String findAvailableName(Path dirPath, String fileName, String fileFormat) throws IOException {
        for (char c0 : SharedConstants.ILLEGAL_FILE_CHARACTERS) {
            fileName = ((String)fileName).replace(c0, '_');
        }
        if (RESERVED_FILENAMES_PATTERN.matcher((CharSequence)(fileName = ((String)fileName).replaceAll("[./\"]", "_"))).matches()) {
            fileName = "_" + (String)fileName + "_";
        }
        Matcher matcher = DUPLICATE_NAME_COUNT_PATTERN.matcher((CharSequence)fileName);
        int j = 0;
        if (matcher.matches()) {
            fileName = matcher.group("name");
            j = Integer.parseInt(matcher.group("count"));
        }
        if (((String)fileName).length() > 255 - fileFormat.length()) {
            fileName = ((String)fileName).substring(0, 255 - fileFormat.length());
        }
        while (true) {
            Object s = fileName;
            if (j != 0) {
                String s1 = " (" + j + ")";
                int i = 255 - s1.length();
                if (((String)fileName).length() > i) {
                    s = ((String)fileName).substring(0, i);
                }
                s = (String)s + s1;
            }
            s = (String)s + fileFormat;
            Path path = dirPath.resolve((String)s);
            try {
                Path path1 = Files.createDirectory(path, new FileAttribute[0]);
                Files.deleteIfExists(path1);
                return dirPath.relativize(path1).toString();
            }
            catch (FileAlreadyExistsException filealreadyexistsexception) {
                ++j;
                continue;
            }
            break;
        }
    }

    public static boolean isNormalized(Path pathIn) {
        Path path = pathIn.normalize();
        return path.equals(pathIn);
    }

    public static boolean containsReservedName(Path pathIn) {
        for (Path path : pathIn) {
            if (!RESERVED_FILENAMES_PATTERN.matcher(path.toString()).matches()) continue;
            return false;
        }
        return true;
    }

    public static Path resolveResourcePath(Path dirPath, String locationPath, String fileFormat) {
        String s = locationPath + fileFormat;
        Path path = Paths.get(s, new String[0]);
        if (path.endsWith(fileFormat)) {
            throw new InvalidPathException(s, "empty resource name");
        }
        return dirPath.resolve(path);
    }
}
