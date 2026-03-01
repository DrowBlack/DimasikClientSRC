import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MappingGenerator {
    public static void main(String[] args) throws IOException {
        if (new File("mappings.txt").createNewFile()) {
            System.out.println("ok");
        }
        FileOutputStream fileOutputStream = new FileOutputStream("mappings.txt");
        fileOutputStream.write("\nwhile\ntrue\nfalse\nthrow\nthrows\nif\nelse\nfor\nswitch\ncase\nbreak\ncontinue\nreturn\ntry\ncatch\nfinally\ndo\nnew\nclass\ninterface\nenum\nextends\nimplements\nimport\npackage\nstatic\nfinal\nvoid\npublic\nprivate\nprotected\nabstract\nsynchronized\nvolatile\ntransient\nnative\nstrictfp\nsuper\nthis\ninstanceof\nassert\ndefault\ngoto\nforEach\nconst\n\n".getBytes());
        for (int i = 0; i < 150000; ++i) {
            fileOutputStream.write(MappingGenerator.generate().getBytes());
        }
        fileOutputStream.close();
    }

    public static String generate() {
        List<String> KEYWORDS = List.of("while", "true", "false", "throw", "throws", "if", "else", "for", "switch", "case", "break", "continue", "return", "try", "catch", "finally", "do", "new", "class", "interface", "enum", "extends", "implements", "import", "package", "static", "final", "void", "public", "private", "protected", "abstract", "synchronized", "volatile", "transient", "native", "strictfp", "super", "this", "instanceof", "assert", "default", "goto", "const");
        StringBuilder finalName = new StringBuilder("");
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 5; ++i) {
            finalName.append("%s%s".formatted(KEYWORDS.get(random.nextInt(KEYWORDS.size())), "_%s".formatted(random.nextInt(1338))));
        }
        return String.valueOf(finalName) + "\n";
    }
}
