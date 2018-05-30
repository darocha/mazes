package de.amr.demos.maze.swingapp.model;

import static de.amr.demos.maze.swingapp.model.AlgorithmTag.*;
import static de.amr.demos.maze.swingapp.model.AlgorithmTag.MST;
import static de.amr.demos.maze.swingapp.model.AlgorithmTag.Slow;
import static de.amr.demos.maze.swingapp.model.AlgorithmTag.SmallGrid;
import static de.amr.demos.maze.swingapp.model.AlgorithmTag.Traversal;
import static de.amr.demos.maze.swingapp.model.AlgorithmTag.UST;

import java.awt.Color;
import java.util.Arrays;
import java.util.Optional;

import de.amr.easy.graph.api.TraversalState;
import de.amr.easy.grid.api.GridPosition;
import de.amr.easy.grid.impl.ObservableGrid;
import de.amr.easy.grid.ui.swing.SwingBFSAnimation;
import de.amr.easy.grid.ui.swing.SwingDFSAnimation;
import de.amr.easy.maze.alg.BinaryTree;
import de.amr.easy.maze.alg.BinaryTreeRandom;
import de.amr.easy.maze.alg.Eller;
import de.amr.easy.maze.alg.EllerInsideOut;
import de.amr.easy.maze.alg.GrowingTree;
import de.amr.easy.maze.alg.HuntAndKill;
import de.amr.easy.maze.alg.HuntAndKillRandom;
import de.amr.easy.maze.alg.RecursiveDivision;
import de.amr.easy.maze.alg.Sidewinder;
import de.amr.easy.maze.alg.mst.BoruvkaMST;
import de.amr.easy.maze.alg.mst.KruskalMST;
import de.amr.easy.maze.alg.mst.PrimMST;
import de.amr.easy.maze.alg.mst.ReverseDeleteBFSMST;
import de.amr.easy.maze.alg.mst.ReverseDeleteDFSMST;
import de.amr.easy.maze.alg.traversal.IterativeDFS;
import de.amr.easy.maze.alg.traversal.RandomBFS;
import de.amr.easy.maze.alg.traversal.RecursiveDFS;
import de.amr.easy.maze.alg.ust.AldousBroderUST;
import de.amr.easy.maze.alg.ust.AldousBroderWilsonUST;
import de.amr.easy.maze.alg.ust.WilsonUSTCollapsingCircle;
import de.amr.easy.maze.alg.ust.WilsonUSTCollapsingRectangle;
import de.amr.easy.maze.alg.ust.WilsonUSTCollapsingWalls;
import de.amr.easy.maze.alg.ust.WilsonUSTExpandingCircle;
import de.amr.easy.maze.alg.ust.WilsonUSTExpandingCircles;
import de.amr.easy.maze.alg.ust.WilsonUSTExpandingRectangle;
import de.amr.easy.maze.alg.ust.WilsonUSTExpandingSpiral;
import de.amr.easy.maze.alg.ust.WilsonUSTHilbertCurve;
import de.amr.easy.maze.alg.ust.WilsonUSTLeftToRightSweep;
import de.amr.easy.maze.alg.ust.WilsonUSTMooreCurve;
import de.amr.easy.maze.alg.ust.WilsonUSTNestedRectangles;
import de.amr.easy.maze.alg.ust.WilsonUSTPeanoCurve;
import de.amr.easy.maze.alg.ust.WilsonUSTRandomCell;
import de.amr.easy.maze.alg.ust.WilsonUSTRecursiveCrosses;
import de.amr.easy.maze.alg.ust.WilsonUSTRightToLeftSweep;
import de.amr.easy.maze.alg.ust.WilsonUSTRowsTopDown;

/**
 * Data model of the maze demo application.
 * 
 * @author Armin Reichert
 */
public class MazeDemoModel {

	public static final AlgorithmInfo[] ALGORITHMS = {
		/*@formatter:off*/
		new AlgorithmInfo(RecursiveDFS.class, "Depth-First-Traversal (recursive, small grids only!)", Traversal, SmallGrid),
		new AlgorithmInfo(IterativeDFS.class, "Depth-First-Traversal (non-recursive)", Traversal),
		new AlgorithmInfo(RandomBFS.class, "Breadth-First-Traversal", Traversal),
		new AlgorithmInfo(KruskalMST.class, "Kruskal MST", MST),
		new AlgorithmInfo(PrimMST.class, "Prim MST", MST),
		new AlgorithmInfo(BoruvkaMST.class, "Boruvka MST", MST),
		new AlgorithmInfo(ReverseDeleteBFSMST.class, "Reverse-Delete MST (BFS, very slow)", MST, SmallGrid),
		new AlgorithmInfo(ReverseDeleteDFSMST.class, "Reverse-Delete MST (DFS, very slow)", MST, SmallGrid),
		new AlgorithmInfo(AldousBroderUST.class, "Aldous-Broder UST (rather slow)", UST, Slow),
		new AlgorithmInfo(AldousBroderWilsonUST.class, "Houston UST (rather slow)", UST, Slow),
		new AlgorithmInfo(WilsonUSTRandomCell.class, "Wilson UST (random)", UST, Slow),
		new AlgorithmInfo(WilsonUSTRowsTopDown.class, "Wilson UST (row-wise top-to-bottom)", UST),
		new AlgorithmInfo(WilsonUSTLeftToRightSweep.class, "Wilson UST (column-wise left to right)", UST),
		new AlgorithmInfo(WilsonUSTRightToLeftSweep.class, "Wilson UST (column-wise right to left)", UST),
		new AlgorithmInfo(WilsonUSTCollapsingWalls.class, "Wilson UST (column-wise collapsing)", UST),
		new AlgorithmInfo(WilsonUSTCollapsingRectangle.class, "Wilson UST (collapsing rectangle)", UST),
		new AlgorithmInfo(WilsonUSTExpandingCircle.class, "Wilson UST (expanding circle)", UST),
		new AlgorithmInfo(WilsonUSTCollapsingCircle.class, "Wilson UST (collapsing circle)", UST),
		new AlgorithmInfo(WilsonUSTExpandingCircles.class, "Wilson UST (expanding circles)", UST),
		new AlgorithmInfo(WilsonUSTExpandingSpiral.class, "Wilson UST (expanding spiral)", UST),
		new AlgorithmInfo(WilsonUSTExpandingRectangle.class, "Wilson UST (expanding rectangle)", UST),
		new AlgorithmInfo(WilsonUSTNestedRectangles.class, "Wilson UST (nested rectangles)", UST),
		new AlgorithmInfo(WilsonUSTRecursiveCrosses.class, "Wilson UST (recursive crosses)", UST),
		new AlgorithmInfo(WilsonUSTHilbertCurve.class, "Wilson UST (Hilbert curve)", UST),
		new AlgorithmInfo(WilsonUSTMooreCurve.class, "Wilson UST (Moore curve)", UST),
		new AlgorithmInfo(WilsonUSTPeanoCurve.class, "Wilson UST (Peano curve)", UST),
		new AlgorithmInfo(BinaryTree.class, "Binary Tree (row-wise, top-to-bottom)"),
		new AlgorithmInfo(BinaryTreeRandom.class, "Binary Tree (random)"), 
		new AlgorithmInfo(Sidewinder.class, "Sidewinder"),
		new AlgorithmInfo(Eller.class, "Eller's Algorithm"), 
		new AlgorithmInfo(EllerInsideOut.class, "Armin's Algorithm"), 
		new AlgorithmInfo(HuntAndKill.class, "Hunt-And-Kill"),
		new AlgorithmInfo(HuntAndKillRandom.class, "Hunt-And-Kill (random)"),
		new AlgorithmInfo(GrowingTree.class, "Growing Tree", Slow),
		new AlgorithmInfo(RecursiveDivision.class, "Recursive Division"),
		/*@formatter:on*/
	};

	public static Optional<AlgorithmInfo> findAlgorithm(Class<?> generatorClass) {
		return Arrays.stream(ALGORITHMS).filter(alg -> alg.getAlgorithmClass().equals(generatorClass)).findFirst();
	}

	public static final AlgorithmInfo[] PATHFINDER_ALGORITHMS = {
			new AlgorithmInfo(SwingDFSAnimation.class, "Depth-First-Search"),
			new AlgorithmInfo(SwingDFSAnimation.class, "Hill Climbing (Manhattan)", HillClimbingManhattan),
			new AlgorithmInfo(SwingDFSAnimation.class, "Hill Climbing (Euclidian)", HillClimbingEuclidian),
			new AlgorithmInfo(SwingBFSAnimation.class, "Breadth-First-Search"),
	};

	private ObservableGrid<TraversalState, Integer> grid;
	private int[] gridCellSizes;
	private int gridCellSize;
	private int passageWidthPercentage;
	private boolean generationAnimated;
	private boolean hidingControlsWhenRunning;
	private boolean longestPathHighlighted;
	private int delay;
	private GridPosition generationStart;
	private GridPosition pathFinderStart;
	private GridPosition pathFinderTarget;
	private Color unvisitedCellColor;
	private Color visitedCellColor;
	private Color completedCellColor;
	private Color pathColor;

	public int[] getGridCellSizes() {
		return gridCellSizes;
	}

	public void setGridCellSizes(int... gridCellSizes) {
		this.gridCellSizes = gridCellSizes;
	}

	public int getGridCellSize() {
		return gridCellSize;
	}

	public void setGridCellSize(int gridCellSize) {
		this.gridCellSize = gridCellSize;
	}

	public int getPassageWidthPercentage() {
		return passageWidthPercentage;
	}

	public void setPassageWidthPercentage(int percent) {
		this.passageWidthPercentage = percent;
	}

	public boolean isGenerationAnimated() {
		return generationAnimated;
	}

	public void setGenerationAnimated(boolean generationAnimated) {
		this.generationAnimated = generationAnimated;
	}

	public boolean isHidingControlsWhenRunning() {
		return hidingControlsWhenRunning;
	}

	public void setHidingControlsWhenRunning(boolean hidingControlsWhenRunning) {
		this.hidingControlsWhenRunning = hidingControlsWhenRunning;
	}

	public boolean isLongestPathHighlighted() {
		return longestPathHighlighted;
	}

	public void setLongestPathHighlighted(boolean longestPathHighlighted) {
		this.longestPathHighlighted = longestPathHighlighted;
	}

	public ObservableGrid<TraversalState, Integer> getGrid() {
		return grid;
	}

	public void setGrid(ObservableGrid<TraversalState, Integer> grid) {
		this.grid = grid;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public GridPosition getGenerationStart() {
		return generationStart;
	}

	public void setGenerationStart(GridPosition pos) {
		this.generationStart = pos;
	}

	public GridPosition getPathFinderSource() {
		return pathFinderStart;
	}

	public void setPathFinderStart(GridPosition pos) {
		this.pathFinderStart = pos;
	}

	public GridPosition getPathFinderTarget() {
		return pathFinderTarget;
	}

	public void setPathFinderTarget(GridPosition pos) {
		this.pathFinderTarget = pos;
	}

	public Color getUnvisitedCellColor() {
		return unvisitedCellColor;
	}

	public void setUnvisitedCellColor(Color unvisitedCellColor) {
		this.unvisitedCellColor = unvisitedCellColor;
	}

	public Color getVisitedCellColor() {
		return visitedCellColor;
	}

	public void setVisitedCellColor(Color visitedCellColor) {
		this.visitedCellColor = visitedCellColor;
	}

	public Color getCompletedCellColor() {
		return completedCellColor;
	}

	public void setCompletedCellColor(Color completedCellColor) {
		this.completedCellColor = completedCellColor;
	}

	public Color getPathColor() {
		return pathColor;
	}

	public void setPathColor(Color pathColor) {
		this.pathColor = pathColor;
	}
}
