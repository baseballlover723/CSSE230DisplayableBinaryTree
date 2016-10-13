package debughelp;
//package debughelp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JComponent;
import javax.swing.JFrame;

import editortrees.EditTree;
import editortrees.Node;

/* dependencies DisplayableTree:
 * 	Node:
 * 			hasLeft
 * 			hasRight
 * 			getLeft
 * 			getRight
 * 			hasParent (if using parents)
 * 			getParent (if using parents)
 * 
 * 	EditTree: 
 * 			constructors need booleans
 * 			displayable in boolean constructors
 * 			O(n) height method that is not dependent on balance codes or rank
 * 			O(n) size method that is not dependent on balance codes or rank
 * 	DisplayableNode:
 * 		node.getRank()
 * 		node.getBalance()
 * 		node.getBalance().toString()
 * 		node.getElement()
 */

/**
 * A wrapper class for binary trees that can display the wrapped tree in a window.
 * 
 * @author Philip Ross, 2014.
 */
abstract public class AbstractDisplayableBinaryTree {
	private static final long serialVersionUID = 6527873423891440301L;
	public static Node NULL_NODE = null;
	// do you have parent nodes?
	public static boolean hasParents = true;


	private DisplayableBinaryTreeComponent component;

	/**
	 * Constructs a new displayable binary tree, set to default to the given window size for display..
	 * 
	 * @param tree
	 * @param windowWidth
	 *            in pixels
	 * @param windowHeight
	 *            in pixels
	 */
	public AbstractDisplayableBinaryTree() {
		this.component = new DisplayableBinaryTreeComponent(this);
	}
	
	abstract public int slowHeight();
	abstract public int slowSize();
	abstract public AbstractDisplayableNode getRoot();

	public void show() {
		this.component.show(true);
	}
	
	public void close() {
		this.component.close();
	}


	/**
	 * returns a string that gives the given time difference in easily read time units
	 * 
	 * @param time
	 * @return
	 */
	public static String getTimeUnits(long time) {
		double newTime = time;
		if (time < 1000) {
			return String.format("%d NanoSeconds", time);
		}
		newTime = time / 1000.0;
		if (newTime < 1000) {
			return String.format("%f MicroSeconds", newTime);
		}
		newTime /= 1000.0;
		if (newTime < 1000) {
			return String.format("%f MiliSeconds", newTime);
		}
		newTime /= 1000.0;
		if (newTime < 300) {
			return String.format("%f Seconds", newTime);
		}
		newTime /= 60.0;
		if (newTime < 180) {
			return String.format("%f Minutes", newTime);
		}
		return String.format("%f Hours", newTime / 60.0);
	}
}
