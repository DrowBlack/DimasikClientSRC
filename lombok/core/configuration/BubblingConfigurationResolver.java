package lombok.core.configuration;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import lombok.ConfigurationKeys;
import lombok.core.configuration.ConfigurationFile;
import lombok.core.configuration.ConfigurationFileToSource;
import lombok.core.configuration.ConfigurationKey;
import lombok.core.configuration.ConfigurationResolver;
import lombok.core.configuration.ConfigurationSource;

public class BubblingConfigurationResolver
implements ConfigurationResolver {
    private final ConfigurationFile start;
    private final ConfigurationFileToSource fileMapper;

    public BubblingConfigurationResolver(ConfigurationFile start, ConfigurationFileToSource fileMapper) {
        this.start = start;
        this.fileMapper = fileMapper;
    }

    @Override
    public <T> T resolve(ConfigurationKey<T> key) {
        boolean isList = key.getType().isList();
        ArrayList<List> listModificationsList = null;
        boolean stopBubbling = false;
        ConfigurationFile currentLevel = this.start;
        HashSet<ConfigurationFile> visited = new HashSet<ConfigurationFile>();
        block0: while (currentLevel != null) {
            ArrayDeque<ConfigurationFile> round = new ArrayDeque<ConfigurationFile>();
            round.push(currentLevel);
            while (!round.isEmpty()) {
                ConfigurationSource source;
                ConfigurationFile currentFile = (ConfigurationFile)round.pop();
                if (currentFile == null || !visited.add(currentFile) || (source = this.fileMapper.parsed(currentFile)) == null) continue;
                for (ConfigurationFile importFile : source.imports()) {
                    round.push(importFile);
                }
                ConfigurationSource.Result stop = source.resolve(ConfigurationKeys.STOP_BUBBLING);
                stopBubbling = stopBubbling || stop != null && Boolean.TRUE.equals(stop.getValue());
                ConfigurationSource.Result result = source.resolve(key);
                if (result == null) continue;
                if (isList) {
                    if (listModificationsList == null) {
                        listModificationsList = new ArrayList<List>();
                    }
                    listModificationsList.add((List)result.getValue());
                }
                if (!result.isAuthoritative()) continue;
                if (isList) break block0;
                return (T)result.getValue();
            }
            if (stopBubbling) break;
            currentLevel = currentLevel.parent();
        }
        if (!isList) {
            return null;
        }
        if (listModificationsList == null) {
            return (T)Collections.emptyList();
        }
        ArrayList<Object> listValues = new ArrayList<Object>();
        Collections.reverse(listModificationsList);
        for (List listModifications : listModificationsList) {
            if (listModifications == null) continue;
            for (ConfigurationSource.ListModification modification : listModifications) {
                listValues.remove(modification.getValue());
                if (!modification.isAdded()) continue;
                listValues.add(modification.getValue());
            }
        }
        return (T)listValues;
    }
}
