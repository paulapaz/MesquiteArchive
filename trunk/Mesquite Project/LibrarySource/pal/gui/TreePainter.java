// TreePainter.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)

package pal.gui;

import pal.tree.*;
import pal.io.*;
import pal.misc.*;

import headless.awt.*;

/**
 * A class that can paint a tree into a Graphics object.  
 *
 * @version $Id: TreePainter.java,v 1.10 2001/07/13 14:39:13 korbinian Exp $
 *
 * @author Alexei Drummond
 */
abstract public class TreePainter implements Painter {

	public static final Color BACKGROUND = Color.white;
	public static final Color FOREGROUND = Color.black;
	public static final Color NORMAL_LABEL_COLOR = Color.green.darker();

	public PositionedNode treeNode;

	public String title_;

	boolean showTitle_;

	/** The tree being painted */
	private Tree tree;

	/** The time order character data used for determining symbols. */
	private TimeOrderCharacterData tocd = null;

	double width, height;

	double maxLeafTime = 0.0;
	double sizeOfScale = 0.0;

	/** Width of pen used to paint lines */
	private int penWidth = 2;

	/** determines whether colors are used to distinguish branch depth */
	private boolean usingColor = true;

	/** determines whether node heights are displayed on the tree */
	private boolean showingNodeHeights = false;

	/** determines whether internal nodes are labelled */
	protected boolean showingInternalLabels = true;

	/** determines whether symbols are used instead of names */
	private boolean usingSymbols = false;

	public TreePainter(Tree toDisplay, String title, boolean showTitle) {

		this.title_ = title;
		this.showTitle_ = showTitle;
		this.tree = toDisplay;

		if (toDisplay instanceof DatedTipsClockTree) {
			tocd = ((DatedTipsClockTree)toDisplay).getTimeOrderCharacterData();
		}
	}

	/** Rotates the tree by leaf count, creates a positioned node version of the
			trees root and calculates postions and width and height information.
			@param highlightNode
	*/
	protected void standardTreePrep(Node highlightNode) {
		TreeUtils.rotateByLeafCount(tree);

		if(highlightNode!=null) {
			treeNode = new PositionedNode(tree.getRoot(),highlightNode);
		} else {
			treeNode = new PositionedNode(tree.getRoot());
		}
		treeNode.calculatePositions();

		width = NodeUtils.getLeafCount(treeNode);
		height = treeNode.getNodeHeight();

		maxLeafTime = 0.0;
		maxLeafTime = getMaxLeafTime(treeNode);
		maxLeafTime *= 1.5;

		sizeOfScale = getSizeOfScale( height / 5.0);
	}

	public final void setPenWidth(int p) {
		penWidth = p;
	}

	public final int getPenWidth() {
		return penWidth;
	}

	public final void setTree(Tree tree) {
		this.tree = tree;
		standardTreePrep(null);
	}

  
	public final void setTree(Tree tree, Node highlightNode) {
		this.tree = tree;
		standardTreePrep(highlightNode);
	}

	public final void setUsingColor(boolean use) {
		usingColor = use;
	}

	public final boolean isUsingColor() {
		return usingColor;
	}

	public final void setShowingNodeHeights(boolean s) {
		showingNodeHeights = s;
	}

	public final boolean isShowingNodeHeights() {
		return showingNodeHeights;
	}

	public final boolean isShowingInternalLabels() {
		return showingInternalLabels;
	}

	public final TimeOrderCharacterData getTimeOrderCharacterData() {
		return this.tocd;
	}

	public final void setTimeOrderCharacterData(TimeOrderCharacterData tocd) {
		this.tocd = tocd;
		usingSymbols = true;
	}

	public final boolean isUsingSymbols() {
		return usingSymbols;
	}

	/**
	 * Sets whether the tree is painted with symbols. This can
	 * only be set to true of a TimeOrderCharacterData has been set.
	 */
	public final void setUsingSymbols(boolean use) {
		usingSymbols = use;
		if (tocd == null) usingSymbols = false;
	}



	protected final double getSizeOfScale(double target) {

		double sos = 0.1;
		boolean accept = false;
		boolean divideByTwo = true;

		while (!accept) {
			if ((sos / target) >= 5.0) {
				sos /= (divideByTwo ? 2.0 : 5.0);
				divideByTwo = !divideByTwo;
			} else if ((sos / target) < 0.2) {
				sos *= (divideByTwo ? 5.0 : 2.0);
				divideByTwo = !divideByTwo;
			} else accept = true;
		}

		return sos;
	}


	protected static final double getMaxLeafTime(Node node) {

		if (!node.isLeaf()) {
		double max = getMaxLeafTime(node.getChild(0));
			double posmax = 0.0;
			for (int i = 1; i < node.getChildCount(); i++) {
				posmax = getMaxLeafTime(node.getChild(i));
				if (posmax > max) max = posmax;
			}
			return max;
		} else {
			return node.getNodeHeight();
		}
	}

	public final static void drawSymbol(Graphics g, int x, int y, int width, int index) {

		int halfWidth = width / 2;

		switch (index % 6) {
			case 0: g.fillRect(x, y, width, width); break;
			case 1: g.drawRect(x, y, width, width); break;
			case 2: g.fillOval(x, y, width, width); break;
			case 3: g.drawOval(x, y, width, width); break;
			case 4: // draw triangle
				g.drawLine(x, y + width, x + halfWidth, y);
				g.drawLine(x + halfWidth, y, x + width, y + width);
				g.drawLine(x, y + width, x + width, y + width);
				break;
			case 5: // draw X
				g.drawLine(x, y, x + width, y + width);
				g.drawLine(x, y + width, x + width, y);
				break;
		}
	}

	public final boolean isShowTitle() {
		return showTitle_;
	}

	public final void setTitle(String title) {
		this.title_ = title;
		showTitle_ = true;
	}

	public final String getTitle() {
		return title_;
	}

	public final void doTitle(Graphics g, int x, int y) {
		if(showTitle_) {
			g.drawString(title_, x,y);
		}
	}

	protected final void doScale(Graphics g, double xScale, int x, int y) {
		g.setColor(FOREGROUND);
		g.drawLine(x,
				 y,
				 x+ (int)Math.round(sizeOfScale * xScale),
				 y);

		g.drawString(sizeOfScale + ((tree.getUnits() == Tree.GENERATIONS) ?
			" generations" : " substitutions"),
				 x,y-12);
	}

}

