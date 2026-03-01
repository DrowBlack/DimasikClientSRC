package org.codehaus.plexus.util.dag;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codehaus.plexus.util.dag.DAG;
import org.codehaus.plexus.util.dag.Vertex;

public class TopologicalSorter {
    private static final Integer NOT_VISITED = 0;
    private static final Integer VISITING = 1;
    private static final Integer VISITED = 2;

    public static List<String> sort(DAG graph) {
        return TopologicalSorter.dfs(graph);
    }

    public static List<String> sort(Vertex vertex) {
        LinkedList<String> retValue = new LinkedList<String>();
        TopologicalSorter.dfsVisit(vertex, new HashMap<Vertex, Integer>(), retValue);
        return retValue;
    }

    private static List<String> dfs(DAG graph) {
        LinkedList<String> retValue = new LinkedList<String>();
        HashMap<Vertex, Integer> vertexStateMap = new HashMap<Vertex, Integer>();
        for (Vertex vertex : graph.getVertices()) {
            if (!TopologicalSorter.isNotVisited(vertex, vertexStateMap)) continue;
            TopologicalSorter.dfsVisit(vertex, vertexStateMap, retValue);
        }
        return retValue;
    }

    private static boolean isNotVisited(Vertex vertex, Map<Vertex, Integer> vertexStateMap) {
        Integer state = vertexStateMap.get(vertex);
        return state == null || NOT_VISITED.equals(state);
    }

    private static void dfsVisit(Vertex vertex, Map<Vertex, Integer> vertexStateMap, List<String> list) {
        vertexStateMap.put(vertex, VISITING);
        for (Vertex v : vertex.getChildren()) {
            if (!TopologicalSorter.isNotVisited(v, vertexStateMap)) continue;
            TopologicalSorter.dfsVisit(v, vertexStateMap, list);
        }
        vertexStateMap.put(vertex, VISITED);
        list.add(vertex.getLabel());
    }
}
