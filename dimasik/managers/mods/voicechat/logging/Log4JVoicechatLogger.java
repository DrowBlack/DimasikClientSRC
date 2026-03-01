package dimasik.managers.mods.voicechat.logging;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.logging.LogLevel;
import dimasik.managers.mods.voicechat.logging.VoicechatLogger;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.config.Configurator;

public class Log4JVoicechatLogger
implements VoicechatLogger {
    private final boolean debugMode;
    private final Logger logger;

    public Log4JVoicechatLogger(Logger logger) {
        this.logger = logger;
        this.debugMode = Voicechat.debugMode();
        try {
            if (this.debugMode) {
                this.initDebugLogLevel();
            }
        }
        catch (Throwable t) {
            logger.error("Failed to set log level", t);
        }
    }

    public Log4JVoicechatLogger(String name) {
        this(LogManager.getLogger(name));
    }

    private void initDebugLogLevel() throws Exception {
        if (!(this.logger instanceof org.apache.logging.log4j.core.Logger)) {
            throw new IllegalStateException("Logger is not an instance of org.apache.logging.log4j.core.Logger");
        }
        org.apache.logging.log4j.core.Logger coreLogger = (org.apache.logging.log4j.core.Logger)this.logger;
        Map<String, Appender> appenders = coreLogger.getAppenders();
        coreLogger.setAdditive(false);
        Configurator.setLevel(this.logger.getName(), Level.DEBUG);
        for (Appender appender : appenders.values()) {
            coreLogger.addAppender(appender);
        }
    }

    @Override
    public void log(LogLevel level, String message, Object ... args) {
        if (!this.isEnabled(level)) {
            return;
        }
        this.logger.log(this.fromLogLevel(level), this.modifyMessage(message), args);
    }

    @Override
    public boolean isEnabled(LogLevel level) {
        return this.logger.isEnabled(this.fromLogLevel(level));
    }

    private Level fromLogLevel(LogLevel level) {
        switch (level) {
            case TRACE: {
                return Level.TRACE;
            }
            case DEBUG: {
                return Level.DEBUG;
            }
            case WARN: {
                return Level.WARN;
            }
            case ERROR: {
                return Level.ERROR;
            }
            case FATAL: {
                return Level.FATAL;
            }
        }
        return Level.INFO;
    }

    private String modifyMessage(String message) {
        return String.format("[%s] %s", this.logger.getName(), message);
    }

    public Logger getLogger() {
        return this.logger;
    }
}
