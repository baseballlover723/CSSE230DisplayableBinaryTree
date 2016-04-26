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
	private static final long serialVersionUID = 6527873423891440301L;
	public static Node NULL_NODE = null;
	// do you have parent nodes?
	public static boolean hasParents = true;

	// a stormy gray background to be easy on the eyes at night, and set a stormy mood.
	public static final Color BACKGROUND_COLOR = Color.DARK_GRAY;
	public static final String FONT_NAME = "Comic Sans MS"; // comics sans for the win
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
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		AbstractDisplayableNode current = this.tree.getRoot();
		// CURRENT.POINT = THE CENTER POINT, NOT THE UPPER LEFT CORNER
		// System.out.println();
		current.paintHelper(g2, this.nodeX, this.nodeY, this.xDistance, this.yDistance, this.circleRadius);
//		current.lineHelper(g2);
//		this.paintHelper(g2, current, this.nodeY);
//		this.lineHelper(g2, current);
		// System.out.println("DONE");
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
