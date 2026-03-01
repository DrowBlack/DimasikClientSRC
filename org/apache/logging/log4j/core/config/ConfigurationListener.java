package org.apache.logging.log4j.core.config;

import org.apache.logging.log4j.core.config.Reconfigurable;

public interface ConfigurationListener {
    public void onChange(Reconfigurable var1);
}
