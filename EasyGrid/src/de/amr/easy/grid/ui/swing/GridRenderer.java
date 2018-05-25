package de.amr.easy.grid.ui.swing;

import static java.lang.Math.ceil;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;

import de.amr.easy.graph.api.Edge;
import de.amr.easy.grid.api.BareGrid2D;
import de.amr.easy.grid.impl.Top4;
import de.amr.easy.grid.impl.Top8;

/**
 * Renders a grid as "passages" or "cells with walls" depending on the selected passage thickness.
 * 
 * @author Armin Reichert
 */
public abstract class GridRenderer implements GridRenderingModel {

	public void drawGrid(Graphics2D g, BareGrid2D<?> grid) {
		grid.edgeStream().forEach(passage -> drawPassage(g, grid, passage, true));
		grid.vertexStream().filter(cell -> grid.degree(cell) == 0).forEach(cell -> drawCell(g, grid, cell));
	}

	public void drawPassage(Graphics2D g, BareGrid2D<?> grid, Edge passage, boolean visible) {
		final int p = passage.either();
		final int q = passage.other(p);
		final int dir = grid.direction(p, q).getAsInt();
		final int inv = grid.getTopology().inv(dir);
		drawHalfPassage(g, grid, p, dir, visible ? getPassageColor(p, dir) : getGridBgColor());
		drawHalfPassage(g, grid, q, inv, visible ? getPassageColor(q, inv) : getGridBgColor());
	}

	public void drawCell(Graphics2D g, BareGrid2D<?> grid, int cell) {
		grid.getTopology().dirs().filter(dir -> grid.isConnected(cell, dir))
				.forEach(dir -> drawHalfPassage(g, grid, cell, dir, getPassageColor(cell, dir)));
		drawCellContent(g, grid, cell);
	}

	private void drawHalfPassage(Graphics2D g, BareGrid2D<?> grid, int cell, int dir, Color passageColor) {
		final int cellX = grid.col(cell) * getCellSize();
		final int cellY = grid.row(cell) * getCellSize();
		final int centerX = cellX + getCellSize() / 2;
		final int centerY = cellY + getCellSize() / 2;
		final int longside = (getCellSize() + getPassageWidth()) / 2;
		final int shortside = getPassageWidth();
		g.setColor(passageColor);
		if (grid.getTopology() == Top4.get()) {
			switch (dir) {
			case Top4.E:
				g.translate(centerX - shortside / 2, centerY - shortside / 2);
				g.fillRect(0, 0, longside, shortside);
				g.translate(-centerX + shortside / 2, -centerY + shortside / 2);
				break;
			case Top4.S:
				g.translate(centerX - shortside / 2, centerY - shortside / 2);
				g.fillRect(0, 0, shortside, longside);
				g.translate(-centerX + shortside / 2, -centerY + shortside / 2);
				break;
			case Top4.W:
				g.translate(centerX - getCellSize() / 2, centerY - shortside / 2);
				g.fillRect(0, 0, longside, shortside);
				g.translate(-centerX + getCellSize() / 2, -centerY + shortside / 2);
				break;
			case Top4.N:
				g.translate(centerX - shortside / 2, centerY - getCellSize() / 2);
				g.fillRect(0, 0, shortside, longside);
				g.translate(-centerX + shortside / 2, -centerY + getCellSize() / 2);
				break;
			}
		} else if (grid.getTopology() == Top8.get()) {
			int diagonal = (int) round(longside * sqrt(2));
			Stroke stroke = new BasicStroke(shortside / 2);
			g.setStroke(stroke);
			switch (dir) {
			case Top8.E:
				g.translate(centerX - shortside / 2, centerY - shortside / 2);
				g.fillRect(0, 0, longside, shortside);
				g.translate(-centerX + shortside / 2, -centerY + shortside / 2);
				break;
			case Top8.N:
				g.translate(centerX - shortside / 2, centerY - getCellSize() / 2);
				g.fillRect(0, 0, shortside, longside);
				g.translate(-centerX + shortside / 2, -centerY + getCellSize() / 2);
				break;
			case Top8.NE:
				g.translate(centerX, centerY);
				g.drawLine(0, 0, diagonal, -diagonal);
				g.translate(-centerX, -centerY);
				break;
			case Top8.NW:
				g.translate(centerX, centerY);
				g.drawLine(0, 0, -diagonal, -diagonal);
				g.translate(-centerX, -centerY);
				break;
			case Top8.S:
				g.translate(centerX - shortside / 2, centerY - shortside / 2);
				g.fillRect(0, 0, shortside, longside);
				g.translate(-centerX + shortside / 2, -centerY + shortside / 2);
				break;
			case Top8.SE:
				g.translate(centerX, centerY);
				g.drawLine(0, 0, diagonal, diagonal);
				g.translate(-centerX, -centerY);
				break;
			case Top8.SW:
				g.translate(centerX, centerY);
				g.drawLine(0, 0, -diagonal, diagonal);
				g.translate(-centerX, -centerY);
				break;
			case Top8.W:
				g.translate(centerX - getCellSize() / 2, centerY - shortside / 2);
				g.fillRect(0, 0, longside, shortside);
				g.translate(-centerX + getCellSize() / 2, -centerY + shortside / 2);
				break;
			}
		}
		drawCellContent(g, grid, cell);
	}

	private void drawCellContent(Graphics2D g, BareGrid2D<?> grid, int cell) {
		final int cellX = grid.col(cell) * getCellSize();
		final int cellY = grid.row(cell) * getCellSize();
		final int offset = (int) ceil((getCellSize() / 2 - getPassageWidth() / 2));
		g.translate(cellX, cellY);
		g.setColor(getCellBgColor(cell));
		g.fillRect(offset, offset, getPassageWidth(), getPassageWidth());
		drawCellText(g, grid, cell);
		g.translate(-cellX, -cellY);
	}

	private void drawCellText(Graphics2D g, BareGrid2D<?> grid, int cell) {
		int fontSize = getTextFont().getSize();
		if (fontSize < getMinFontSize()) {
			return;
		}
		String text = getText(cell);
		text = (text == null) ? "" : text.trim();
		if (text.length() == 0) {
			return;
		}
		g.setColor(getTextColor());
		g.setFont(getTextFont().deriveFont(Font.PLAIN, fontSize));
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		Rectangle textBox = g.getFontMetrics().getStringBounds(text, g).getBounds();
		g.drawString(text, (getCellSize() - textBox.width) / 2, (getCellSize() + textBox.height / 2) / 2);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}
}