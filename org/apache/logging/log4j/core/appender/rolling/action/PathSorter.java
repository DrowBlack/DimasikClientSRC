package org.apache.logging.log4j.core.appender.rolling.action;

import java.util.Comparator;
import org.apache.logging.log4j.core.appender.rolling.action.PathWithAttributes;

public interface PathSorter
extends Comparator<PathWithAttributes> {
}
