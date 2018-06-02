package de.amr.easy.graph.traversal;

import static de.amr.easy.graph.api.TraversalState.UNVISITED;
import static de.amr.easy.graph.api.TraversalState.VISITED;

import java.util.Comparator;

import de.amr.easy.data.Stack;
import de.amr.easy.graph.api.Graph;
import de.amr.easy.graph.api.ObservableGraphTraversal;
import de.amr.easy.graph.api.TraversalState;

/**
 * A heuristic depth-first-search where the children of the current vertex are visited in the order
 * given by a vertex valuation, for example the Euclidian distance from a given target.
 * <p>
 * Taken from: Patrick Henry Winston, Artificial Intelligence 2nd ed., Addison-Wesley, 1984
 * 
 * @author Armin Reichert
 */
public class HillClimbing extends AbstractGraphTraversal implements ObservableGraphTraversal {

	private final Stack<Integer> stack;

	/**
	 * A vertex comparison which determines the order how children of a vertex are traversed.
	 */
	public Comparator<Integer> vertexValuation = Integer::compare;

	public HillClimbing(Graph<?> graph) {
		super(graph);
		this.stack = new Stack<>();
	}

	@Override
	protected void clear() {
		super.clear();
		stack.clear();
	}

	@Override
	public boolean inQ(int vertex) {
		return stack.contains(vertex);
	}

	@Override
	public void traverseGraph(int source, int target) {
		visit(-1, source);
		while (!stack.isEmpty()) {
			int current = stack.pop();
			if (current == target) {
				break;
			}
			/*@formatter:off*/
			graph.adjVertices(current)
				.filter(child -> getState(child) == UNVISITED)
				.boxed()
				.sorted(vertexValuation.reversed())
				.forEach(child -> visit(current, child));
			/*@formatter:on*/
		}
		while (!stack.isEmpty()) {
			stack.pop();
		}
	}

	private void visit(int parent, int child) {
		TraversalState oldState = getState(child);
		setState(child, VISITED);
		setParent(child, parent);
		stack.push(child);
		vertexTouched(child, oldState, getState(child));
		if (parent != -1) {
			edgeTouched(parent, child);
		}
	}
}