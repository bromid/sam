package se.atg.cmdb.helpers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class TreeHelper {

	public static <T> T createTree(String rootNodeId, Map<String, T> nodes, Function<T,List<String>> getSubNodes, BiConsumer<T,T> addSubNode) {

		final Map<String, NodeState> loopCheck = nodes
			.keySet()
			.stream()
			.collect(Collectors.toMap(Function.identity(), NodeState::create));
		return recursiveCreateTree(nodes.get(rootNodeId), nodes, loopCheck.get(rootNodeId), loopCheck, getSubNodes, addSubNode);
	}

	private static <T> T recursiveCreateTree(T rootNode, Map<String, T> nodes, NodeState nodeState, Map<String, NodeState> loopCheck, Function<T,List<String>> getSubNodes, BiConsumer<T,T> addSubNode) {

		if (nodeState.onStack) return null;
		if (nodeState.visit()) return rootNode;

		final List<String> subNodeIds = getSubNodes.apply(rootNode);
		subNodeIds.stream()
			.map(t->recursiveCreateTree(nodes.get(t), nodes, loopCheck.get(t), loopCheck, getSubNodes, addSubNode))
			.filter(Objects::nonNull)
			.forEach(t->addSubNode.accept(rootNode, t));
	
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
}
