package dimasik.managers.mods.voicechat.logging;

import dimasik.managers.mods.voicechat.logging.LogLevel;

public interface VoicechatLogger {
    public void log(LogLevel var1, String var2, Object ... var3);

    public boolean isEnabled(LogLevel var1);

    default public void trace(String message, Object ... args) {
        this.log(LogLevel.TRACE, message, args);
    }

    default public void debug(String message, Object ... args) {
        this.log(LogLevel.DEBUG, message, args);
    }

    default public void info(String message, Object ... args) {
        this.log(LogLevel.INFO, message, args);
    }

    default public void warn(String message, Object ... args) {
        this.log(LogLevel.WARN, message, args);
    }

    default public void error(String message, Object ... args) {
        this.log(LogLevel.ERROR, message, args);
    }

    default public void fatal(String message, Object ... args) {
        this.log(LogLevel.FATAL, message, args);
    }
}
