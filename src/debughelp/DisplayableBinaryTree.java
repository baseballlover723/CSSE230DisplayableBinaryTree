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
public class DisplayableBinaryTree extends JComponent {
	public static Node NULL_NODE = null;
	// do you have parent nodes?
	public static boolean hasParents = true;

	// a stormy gray background to be easy on the eyes at night, and set a stormy mood.
	private static final Color BACKGROUND_COLOR = Color.DARK_GRAY;
	// a light blue color, keeping in line with the stormy color scheme
	private static final Color FOWARD_ARROW_COLOR = new Color(0x3399FF);
	private static final Color PARENT_ARROW_COLOR = new Color(0x77619A);
	private static final String FONT_NAME = "Comic Sans MS"; // comics sans for the win
	// private static final String FONT_NAME = "ESSTIXFifteen"; // change if you don't want to make it look cool
	// private static final String FONT_NAME = "ESSTIXThirteen"; // change if you don't want to make it look cool
	// private static final String FONT_NAME = "Jokerman"; // change if you don't want to make it look cool

	private int width;
	private int height;
	private EditTree tree;
	private JFrame frame;
	private double xDistance;
	private double circleRadius;
	private double yDistance;
	private double nodeX;
	private double nodeY;
	private double angle;
	private boolean goingCrazy;

	/**
	 * Constructs a new displayable binary tree, set to default to the given window size for display..
	 * 
	 * @param tree
	 * @param windowWidth
	 *            in pixels
	 * @param windowHeight
	 *            in pixels
	 */
	public DisplayableBinaryTree(EditTree tree, int windowWidth, int windowHeight, boolean visable) {
		this.angle = 0;
		this.width = windowWidth;
		this.height = windowHeight;
		this.tree = tree;
		// makes the size of the nodes oscillate
		this.goingCrazy = Math.random() < 0.05;
		this.show(visable);
		Runnable repainter = new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						Thread.sleep(10);
						repaint();
					}
				} catch (InterruptedException exception) {
					// Reports interrupt
				}
			}
		};
		new Thread(repainter).start();
	}

	@Override
	public void show(boolean visable) {
		if (this.frame != null) {
			this.frame.toFront();
			return;
		}
		this.frame = new JFrame();
		this.frame.setFocusable(true);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setMinimumSize(new Dimension(this.tree.slowSize() * 20 + 18, this.tree.slowHeight() * 20 + 45));
		this.frame.setSize(new Dimension(this.width, this.height));
		// set the background color to a stormy gray
		this.frame.getContentPane().setBackground(BACKGROUND_COLOR);
		// add the tree to the frame
		this.frame.getContentPane().add(this);
		this.frame.setVisible(visable);
	}

	public void close() {
		this.frame.dispose();
	}

	/**
	 * Sets the default size for the next window displayed.
	 * 
	 * @param windowWidth
	 *            in pixels
	 * @param windowHeight
	 *            in pixels
	 */
	@Override
	public void setSize(int windowWidth, int windowHeight) {
		this.width = windowWidth;
		this.height = windowHeight;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		// anti aliasing makes everything better
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.width = this.frame.getContentPane().getWidth() - 4;// - 18; // adjust for margins
		this.height = this.frame.getContentPane().getHeight() - 4;// - 45; // adjust for the margins

		int treeHeight = this.tree.slowHeight();
		int treeSize = this.tree.slowSize();
		if (treeSize < 1) {
			return;
		}

		this.xDistance = this.width / ((double) (treeSize)); // make the constant
		this.circleRadius = this.xDistance / 2.0; // sets the circle diameter to the delta x distance
		Dimension minSize = new Dimension((int) (treeSize * 20 + 18), (int) (treeHeight * 30 + 45));
		if (minSize.getHeight() > 1080) {
			minSize.setSize(minSize.getWidth(), 1080);
		}
		if (minSize.getWidth() > 1920) {
			minSize.setSize(1920, minSize.getHeight());
		}
		this.frame.getContentPane().setMinimumSize(minSize);
		this.circleRadius *= 1.25;
		if (this.goingCrazy) {
			this.angle += 0.0001;
			// fun feature to see if students notice that the circles are changing size
			this.circleRadius += 10 * Math.sin(3 * this.angle) + 2 * Math.cos(15 * this.angle);
		}
		this.xDistance = (this.width - this.circleRadius * 2) / ((double) (treeSize - 1));
		// calculates the delta y distance by equally dividing up the height minus the circle diameter
		this.yDistance = (this.height - 2 * circleRadius) / ((double) (treeHeight));

		// start at the upper left corner
		this.nodeX = this.circleRadius + 2;
		this.nodeY = this.circleRadius + 2;

		int size = 0;
		// loops through font sizes, to get the right font size
		while (true) {
			// System.out.println(size);
			FontMetrics metric = g2.getFontMetrics(new Font(FONT_NAME, Font.CENTER_BASELINE, size));
			int height = metric.getHeight();
			int width = metric.getMaxAdvance();
			// times 1.5 works out nice
			double multiplyer = 1.5;
			// if the diagonal is 1.5 times the radius stop making it bigger
			if (Math.sqrt(height * height + width * width) > multiplyer * this.circleRadius) {
				g2.setFont(new Font(FONT_NAME, Font.PLAIN, --size));
				// System.out.println(g2.getFont().getSize());
				break; // done
			}
			size++;
		}
		// RAISE THE BAR VVVVV
		g2.setColor(Color.blue); // blue looks so much better
		g2.fill(new Rectangle2D.Double(this.width - 5, 50, 10, 5));
		g2.fill(new Rectangle2D.Double(this.width - 10, 60, 20, 5));
		g2.fill(new Rectangle2D.Double(this.width - 15, 70, 30, 5));
		g2.fill(new Rectangle2D.Double(this.width - 20, 80, 40, 5));
		g2.fill(new Rectangle2D.Double(this.width - 25, 90, 50, 5));
		// // RAISE THE BAR ^^^^^
		DisplayableNodeWrapper current = this.tree.getRoot().getDisplayableNodePart();
		// CURRENT.POINT = THE CENTER POINT, NOT THE UPPER LEFT CORNER
		// System.out.println();
		this.paintHelper(g2, current, this.nodeY);
		this.lineHelper(g2, current);
		// System.out.println("DONE");
	}

	/**
	 * helper method to paint nodes
	 * 
	 * @param g2
	 * @param current
	 * @param nodeY
	 */
	private void paintHelper(Graphics2D g2, DisplayableNodeWrapper current, double nodeY) {
		// System.out.println(current.getNode().hasLeft());
		if (current.getNode().hasLeft()) {
			this.paintHelper(g2, current.getLeft(), nodeY + this.yDistance); // recurse
		}
		// set up the node
		current.setPoint(this.nodeX, nodeY);
		// System.out.println(current.getNode().getElement());
		// System.out.println("(" + this.nodeX + ", " + nodeY + ")");
		current.setCircleRadius(this.circleRadius);
		current.displayNode(g2); // display the node by passing the graphics2D
		this.nodeX += this.xDistance;
		if (current.getNode().hasRight()) {
			this.paintHelper(g2, current.getRight(), nodeY + this.yDistance); // recurse
		}
	}

	/**
	 * 
	 * @param g2
	 * @param current
	 */
	private void lineHelper(Graphics2D g2, DisplayableNodeWrapper current) {
		if (current == null) {
			return;
		}
		if (hasParents) {
			if (current.getNode().hasParent()) {
				this.drawParentArrow(g2, current);
			}
		}
		// only if has left child
		if (current.getLeft() != null) {
			// draw line arrow
			this.drawFowardArrow(g2, current.getPoint(), current.getLeft().getPoint());
			this.lineHelper(g2, current.getLeft()); // recurse
		}
		// only if has right child
		if (current.getRight() != null) {
			// draw line arrow
			this.drawFowardArrow(g2, current.getPoint(), current.getRight().getPoint());
			this.lineHelper(g2, current.getRight()); // recurse
		}
	}

	/**
	 * makes the frame take an arrow to the knee
	 * 
	 * @param g2
	 *            graphics
	 * @param start
	 *            center point of the parent
	 * @param end
	 *            center point of the child
	 */
	private void drawParentArrow(Graphics2D g2, DisplayableNodeWrapper node) {
		Point2D.Double start = node.getPoint();
		Point2D.Double end = node.getParent().getPoint();
		g2.setColor(PARENT_ARROW_COLOR);
		double SIZE_MULTIPLIER = 1.5;
		AffineTransform transform = g2.getTransform(); // save graphics state to restore later
		double angle = 0;
		try {
			angle = Math.atan2(end.getY() - start.getY(), end.getX() - start.getX());
		} catch (NullPointerException e) {
			// eh, this probability doesn't matter that much
			return;
		}
		g2.translate(end.getX(), end.getY()); // move the center of the child node
		g2.rotate(angle + Math.PI / 2.0); // rotate
		// move the edge of the circle
		g2.translate(0, this.circleRadius);
		double arrowLength = start.distance(end) - 2 * this.circleRadius; // distance is from edge to edge
		double arrowLengthSqrt = Math.sqrt(arrowLength); // scales better with the sqrt
		Node dataNode = node.getNode();
		if (dataNode == dataNode.getParent().getLeft() || dataNode == dataNode.getParent().getRight()) {
			Line2D.Double line = new Line2D.Double(0, 0, 0, arrowLength - arrowLengthSqrt * 2);
			g2.draw(line);
		}

		Path2D.Double arrowHead = new Path2D.Double(); // paths are cool
		// draws the arrow head
		arrowHead.moveTo(0, 0);
		arrowHead.lineTo(-arrowLengthSqrt / SIZE_MULTIPLIER, 2 * arrowLengthSqrt / SIZE_MULTIPLIER);
		arrowHead.lineTo(arrowLengthSqrt / SIZE_MULTIPLIER, 2 * arrowLengthSqrt / SIZE_MULTIPLIER);
		arrowHead.closePath();

		g2.fill(arrowHead);
		g2.setTransform(transform); // restores the graphics state
	}

	/**
	 * makes the frame take an arrow to the knee
	 * 
	 * @param g2
	 *            graphics
	 * @param start
	 *            center point of the parent
	 * @param end
	 *            center point of the child
	 */
	private void drawFowardArrow(Graphics2D g2, Point2D.Double start, Point2D.Double end) {
		g2.setColor(FOWARD_ARROW_COLOR);
		AffineTransform transform = g2.getTransform(); // save graphics state to restore later
		// get the correct rotation angle
		if (end == null || start == null) {
			// System.out.println("NULL ANGLE");
		}
		double angle = 0;
		try {
			angle = Math.atan2(end.getY() - start.getY(), end.getX() - start.getX());
		} catch (NullPointerException e) {
			// silently ignore, cause you know, YOLO
			// but really though, there is a small (1 in ~1000) chance of one of these being null
			// and its better for it to just skip this arrow then actually fix it since it will get redrawn
			return;
		}
		g2.translate(end.getX(), end.getY()); // move the center of the child node
		g2.rotate(angle + Math.PI / 2.0); // rotate
		g2.translate(0, this.circleRadius); // move the edge of the circle
		double arrowLength = start.distance(end) - 2 * this.circleRadius; // distance is from edge to edge
		Line2D.Double line = new Line2D.Double(0, 0, 0, arrowLength);
		g2.draw(line);

		Path2D.Double arrowHead = new Path2D.Double(); // paths are cool
		double arrowLengthSqrt = Math.sqrt(arrowLength); // scales better with the sqrt
		// draws the arrow head
		arrowHead.moveTo(0, 0);
		arrowHead.lineTo(-arrowLengthSqrt, arrowLengthSqrt * 2);
		arrowHead.lineTo(arrowLengthSqrt, arrowLengthSqrt * 2);
		arrowHead.closePath();

		g2.fill(arrowHead);
		g2.setTransform(transform); // restores the graphics state
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
