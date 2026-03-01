package lombok.installer;

import java.util.ArrayList;
import java.util.List;

public class WindowsDriveInfo {
    public List<String> getLogicalDrives() {
        int flags = this.getLogicalDrives0();
        ArrayList<String> letters = new ArrayList<String>();
        int i = 0;
        while (i < 26) {
            if ((flags & 1 << i) != 0) {
                letters.add(Character.toString((char)(65 + i)));
            }
            ++i;
        }
        return letters;
    }

    private native int getLogicalDrives0();

    public boolean isFixedDisk(String letter) {
        if (letter.length() != 1) {
            throw new IllegalArgumentException("Supply 1 letter, not: " + letter);
        }
        char drive = Character.toUpperCase(letter.charAt(0));
        if (drive < 'A' || drive > 'Z') {
            throw new IllegalArgumentException("A drive is indicated by a letter, so A-Z inclusive. Not " + drive);
        }
        return (long)this.getDriveType(String.valueOf(drive) + ":\\") == 3L;
    }

    private native int getDriveType(String var1);

    public static void main(String[] args) {
        System.loadLibrary("WindowsDriveInfo");
        WindowsDriveInfo info = new WindowsDriveInfo();
        for (String letter : info.getLogicalDrives()) {
            System.out.printf("Drive %s: - %s\n", letter, info.isFixedDisk(letter) ? "Fixed Disk" : "Not Fixed Disk");
        }
    }
}
