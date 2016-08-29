package se.atg.cmdb.helpers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class TreeHelper {

  public static <T> T createTree(
    String rootNodeId,
    Map<String, T> nodes,
    Function<T, List<String>> getSubNodes,
    BiConsumer<T, T> addSubNode,
    Function<String,T> handleNull
  ) {
    final Map<String, NodeState> loopCheck = nodes
      .keySet()
      .stream()
      .collect(Collectors.toMap(Function.identity(), NodeState::create));
    final State<T> state = new State<T>() {{
      thisNodes = nodes;
      thisLoopCheck = loopCheck;
      thisGetSubNodes = getSubNodes;
      thisAddSubNode = addSubNode;
      thisHandleNull = handleNull;
    }};
    return recursiveCreateTree(rootNodeId, state.getNode(rootNodeId), state.getLoopCheck(rootNodeId), state);
  }

  private static <T> T recursiveCreateTree(String id, T rootNode, NodeState nodeState, State<T> state) {

    if (nodeState == null) {
      return state.handleNull(id);
    }
    if (nodeState.onStack) {
      return null;
    }
    if (nodeState.visit()) {
      return rootNode;
    }

    final List<String> subNodeIds = state.getSubNodes(rootNode);
    subNodeIds.stream()
      .map(t -> recursiveCreateTree(t, state.getNode(t), state.getLoopCheck(t), state))
      .filter(Objects::nonNull)
      .forEach(t -> state.addSubNode(rootNode, t));

    nodeState.leave();
    return rootNode;
  }

  static class NodeState {

    boolean onStack = false;
    boolean visited = false;

    public boolean visit() {
      if (visited) {
        return true;
      }
      visited = true;
      onStack = true;
      return false;
    }

    public void leave() {
      onStack = false;
    }

    static <T> NodeState create(T node) {
      return new NodeState();
    }
  }

  static class State<T> {

    Map<String, T> thisNodes;
    Map<String, NodeState> thisLoopCheck;
    Function<T, List<String>> thisGetSubNodes;
    BiConsumer<T, T> thisAddSubNode;
    Function<String,T> thisHandleNull;

    List<String> getSubNodes(T rootNode) {
      return thisGetSubNodes.apply(rootNode);
    }

    public void addSubNode(T rootNode, T node) {
      thisAddSubNode.accept(rootNode, node);
    }

    public NodeState getLoopCheck(String nodeId) {
      return thisLoopCheck.get(nodeId);
    }

    public T getNode(String nodeId) {
      return thisNodes.get(nodeId);
    }

    public T handleNull(String nodeId) {
      return thisHandleNull.apply(nodeId);
    }
  }
}
