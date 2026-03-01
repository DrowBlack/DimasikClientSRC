package lombok.patcher;

public class Version {
    private static final String VERSION = "0.50";

    private Version() {
    }

    public static void main(String[] args) {
        System.out.println(VERSION);
    }

    public static String getVersion() {
        return VERSION;
    }
}
