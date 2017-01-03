package debughelp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JComponent;
import javax.swing.JFrame;

// This class handles all the GUI logic
public class DisplayableBinaryTreeComponent extends JComponent {
	private static final long serialVersionUID = 2737198941294146621L;
	private AbstractDisplayableBinaryTree tree;
	private static final int DEFAULT_WIDTH = 960;
	private static final int DEFAULT_HEIGHT = 1080;
	

	// a stormy gray background to be easy on the eyes at night, and set a stormy mood.
	public static final Color BACKGROUND_COLOR = Color.DARK_GRAY;
	public static final String FONT_NAME = "Comic Sans MS"; // comics sans for the win
	// private static final String FONT_NAME = "ESSTIXFifteen"; // change if you don't want to make it look cool
	// private static final String FONT_NAME = "ESSTIXThirteen"; // change if you don't want to make it look cool
	// private static final String FONT_NAME = "Jokerman"; // change if you don't want to make it look cool


	private int width;
	private int height;
	private JFrame frame;
	private double xDistance;
	private double circleRadius;
	private double yDistance;
	private double nodeX;
	private double nodeY;
	private double angle;
	private boolean goingCrazy;
	private AtomicBoolean shouldRun;

	public DisplayableBinaryTreeComponent(AbstractDisplayableBinaryTree editorTree) {
		this.tree = editorTree;
		// makes the size of the nodes oscillate
		this.goingCrazy = Math.random() < 0.05;
		this.shouldRun = new AtomicBoolean(false);
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
		// System.out.println("DONE");
	}
	
	public void show() {
		if (this.frame != null) {
			this.frame.toFront();
			return;
		}
		this.frame = new JFrame();
		this.frame.setFocusable(true);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setMinimumSize(new Dimension(this.tree.slowSize() * 20 + 18, this.tree.slowHeight() * 20 + 45));
		this.frame.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
//		System.out.println(new Dimension(this.width, this.height));
		// set the background color to a stormy gray
		this.frame.getContentPane().setBackground(BACKGROUND_COLOR);
		// add the tree to the frame
		this.frame.getContentPane().add(this);
		this.frame.setVisible(true);
		this.frame.toFront();
		this.shouldRun.set(true);
		Runnable repainter = new Runnable() {
			@Override
			public void run() {
				try {
					while (shouldRun.get()) {
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

	/**
	 * closes the tree window still keeps all the data and you can still reshow
	 * the tree with the show method
	 */
	public void close() {
		shouldRun.set(false);
		this.frame.dispose();
	}
}
