package com.fbp.engine.flow.validation;

import com.fbp.engine.flow.Flow;
import com.fbp.engine.edge.Edge;
import com.fbp.engine.flow.exception.CycleDetectedException;
import com.fbp.engine.flow.exception.EmptyFlowException;
import com.fbp.engine.flow.exception.FlowNotFoundException;
import com.fbp.engine.node.Node;
import com.fbp.engine.node.exception.NodeNotFoundException;
import com.fbp.engine.port.exception.InputPortNotFoundException;
import com.fbp.engine.port.exception.OutputPortNotFoundException;

import java.util.*;

public class FlowValidationSupport {
    private FlowValidationSupport() {
        /* This utility class should not be instantiated */
    }

    public static void validateFlowExists(Flow flow) {
        if (flow == null) {
            throw new FlowNotFoundException();
        }
    }

    public static void validateFlowHasNodes(Map<String, Node> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            throw new EmptyFlowException();
        }
    }

    public static void validateNodeExists(Map<String, Node> nodes, String nodeId) {
        if (nodeId == null || nodeId.isEmpty()
                || !nodes.containsKey(nodeId)) {
            throw new NodeNotFoundException(nodeId);
        }
    }

    public static void validateInputPortExists(Node node, String portName) {
        if (node.getInputPort(portName) == null) {
            throw new InputPortNotFoundException(node.getId(), portName);
        }
    }

    public static void validateOutputPortExists(Node node, String portName) {
        if (node.getOutputPort(portName) == null) {
            throw new OutputPortNotFoundException(node.getId(), portName);
        }
    }

    public static void validateEdge(Edge edge, Map<String, Node> nodes) {
        validateNodeExists(nodes, edge.sourceNodeId());
        validateNodeExists(nodes, edge.targetNodeId());
        validateOutputPortExists(nodes.get(edge.sourceNodeId()), edge.sourcePortName());
        validateInputPortExists(nodes.get(edge.targetNodeId()), edge.targetPortName());
    }

    // Kahn's Algorithm 버전
    public static void validateNoCycles(Map<String, Node> nodes, List<Edge> edges) {
        Map<String, List<String>> nodeGraph = buildNodeGraph(nodes, edges);
        Map<String, Integer> inDegree = new HashMap<>();

        // 1. 모든 노드의 진입 차수 초기화
        for (String nodeId : nodes.keySet()) {
            inDegree.put(nodeId, 0);
            nodeGraph.put(nodeId, new ArrayList<>());
        }

        // 2. 엣지를 순회하며 진입 차수 계산
        for (Edge edge : edges) {
            nodeGraph.get(edge.sourceNodeId()).add(edge.targetNodeId());
            inDegree.put(edge.targetNodeId(), inDegree.get(edge.targetNodeId()) + 1);
        }

        // 3. 진입 차수가 0인 노드를 큐에 추가
        Queue<String> queue = new ArrayDeque<>();
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.offer(entry.getKey());
            }
        }

        // 4. 큐에서 노드를 하나씩 처리하며 진입 차수 업데이트
        int processedCount = 0;
        while (!queue.isEmpty()) {
            String currentNodeId = queue.poll();
            processedCount++;

            for (String nextNodeId : nodeGraph.get(currentNodeId)) {
                inDegree.put(nextNodeId, inDegree.get(nextNodeId) - 1);
                if (inDegree.get(nextNodeId) == 0) {
                    queue.offer(nextNodeId);
                }
            }
        }

        // 5. 처리된 노드 수와 전체 노드 수를 비교하여 순환 참조 여부 판단
        if (processedCount != nodes.size()) {
            throw new CycleDetectedException();
        }
    }

    // DFS 버전
//    public static void validateNoCycles(Map<String, Node> nodes, List<Edge> edges) {
//        Map<String, List<String>> nodeGraph = buildNodeGraph(nodes, edges);
//        Set<String> visiting = new HashSet<>();
//        Set<String> visited = new HashSet<>();
//
//        for (String startNodeId : nodes.keySet()) {
//            if (!visited.contains(startNodeId) && hasCycleFrom(startNodeId, nodeGraph, visiting, visited)) {
//                throw new CycleDetectedException();
//            }
//        }
//    }
//
//    private static boolean hasCycleFrom(
//            String currentNodeId,
//            Map<String, List<String>> nodeGraph,
//            Set<String> visiting,
//            Set<String> visited
//    ) {
//        if (visiting.contains(currentNodeId)) {
//            return true; // 순환 참조 발견
//        }
//        if (visited.contains(currentNodeId)) {
//            return false; // 이미 방문한 노드
//        }
//
//        visiting.add(currentNodeId); // 처음 방문하는 노드
//        for (String nextNodeId : nodeGraph.get(currentNodeId)) {
//            if (hasCycleFrom(nextNodeId, nodeGraph, visiting, visited)) {
//                return true;
//            }
//        }
//        visiting.remove(currentNodeId);
//        visited.add(currentNodeId);
//        return false;
//    }

    private static Map<String, List<String>> buildNodeGraph(Map<String, Node> nodes, List<Edge> edges) {
        // 노드 ID를 키로 하고, 해당 노드에서 나가는 엣지의 대상 노드 ID 목록을 값으로 하는 인접 리스트 생성
        Map<String, List<String>> nodeGraph = new HashMap<>();
        for (Node node : nodes.values()) {
            nodeGraph.put(node.getId(), new ArrayList<>());
        }
        for (Edge edge : edges) {
            nodeGraph.get(edge.sourceNodeId()).add(edge.targetNodeId());
        }
        return nodeGraph;
    }
}
