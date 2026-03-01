package com.mojang.authlib;

import com.mojang.authlib.Environment;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import java.util.Arrays;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnvironmentParser {
    @Nullable
    private static String environmentOverride;
    private static final String PROP_PREFIX = "minecraft.api.";
    private static final Logger LOGGER;
    public static final String PROP_ENV = "minecraft.api.env";
    public static final String PROP_AUTH_HOST = "minecraft.api.auth.host";
    public static final String PROP_ACCOUNT_HOST = "minecraft.api.account.host";
    public static final String PROP_SESSION_HOST = "minecraft.api.session.host";
    public static final String PROP_SERVICES_HOST = "minecraft.api.services.host";

    public static void setEnvironmentOverride(String override) {
        environmentOverride = override;
    }

    public static Optional<Environment> getEnvironmentFromProperties() {
        String envName = environmentOverride != null ? environmentOverride : System.getProperty(PROP_ENV);
        Optional<Environment> env = YggdrasilEnvironment.fromString(envName).map(Environment.class::cast);
        return env.isPresent() ? env : EnvironmentParser.fromHostNames();
    }

    private static Optional<Environment> fromHostNames() {
        String auth = System.getProperty(PROP_AUTH_HOST);
        String account = System.getProperty(PROP_ACCOUNT_HOST);
        String session = System.getProperty(PROP_SESSION_HOST);
        String services = System.getProperty(PROP_SERVICES_HOST);
        if (auth != null && account != null && session != null) {
            return Optional.of(Environment.create(auth, account, session, services, "properties"));
        }
        if (auth != null || account != null || session != null) {
            LOGGER.info("Ignoring hosts properties. All need to be set: " + Arrays.asList(PROP_AUTH_HOST, PROP_ACCOUNT_HOST, PROP_SESSION_HOST));
        }
        return Optional.empty();
    }

    static {
        LOGGER = LogManager.getLogger();
    }
}
