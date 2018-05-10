package de.amr.demos.grid.curves;

import static de.amr.easy.grid.curves.CurveUtils.traverse;
import static de.amr.easy.util.GridUtils.log;

import java.util.stream.IntStream;

import de.amr.easy.grid.curves.MooreLCurve;
import de.amr.easy.grid.impl.Topologies;
import de.amr.easy.grid.ui.swing.SwingBFSAnimation;
import de.amr.easy.grid.ui.swing.SwingGridSampleApp;

/**
 * Creates Moore curves of different sizes and shows an animation of the creation and BFS-traversal
 * of the underlying graph.
 * 
 * @author Armin Reichert
 */
public class MooreCurveApp extends SwingGridSampleApp {

	public static void main(String[] args) {
		launch(new MooreCurveApp());
	}

	public MooreCurveApp() {
		super(512, 512, 512, Topologies.TOP4);
		setAppName("Moore Curve");
	}

	@Override
	public void run() {
		IntStream.of(2, 4, 8, 16, 32, 64, 128, 256).forEach(n -> {
			resizeGrid(canvasSize.width / n);
			int startCol = n / 2, startRow = n - 1;
			int startCell = grid.cell(startCol, startRow);
			MooreLCurve curve = new MooreLCurve(log(2, n));
			traverse(curve, grid, startCell, this::addEdge);
			SwingBFSAnimation bfs = new SwingBFSAnimation(canvas, grid);
			bfs.setDistancesVisible(false);
			bfs.run(startCell);
			sleep(1000);
		});
	}
}