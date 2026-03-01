package lombok.installer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class CorruptedIdeLocationException
extends Exception {
    private final String ideType;

    public CorruptedIdeLocationException(String message, String ideType, Throwable cause) {
        super(message, cause);
        this.ideType = ideType;
    }

    public String getIdeType() {
        return this.ideType;
    }

    void showDialog(JFrame appWindow) {
        JOptionPane.showMessageDialog(appWindow, this.getMessage(), "Cannot configure " + this.ideType + " installation", 2);
    }
}
