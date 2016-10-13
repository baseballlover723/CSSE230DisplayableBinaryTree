package debughelp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import editortrees.Node;

/*
 * REQUIRES:
 * node.getRank()
 * node.getBalance()
 * node.getBalance().toString()
 * node.getElement()
 */

abstract public class AbstractDisplayableNode {
	// *****************************************************************************
	private static Color CIRCLE_COLOR = Color.WHITE;
	// lightish green to keep in line with our stormy color scheme
	private static Color TEXT_COLOR = new Color(0x66FFB2);
	// a light blue color, keeping in line with the stormy color scheme
	public static final Color FOWARD_ARROW_COLOR = new Color(0x3399FF);
	public static final Color PARENT_ARROW_COLOR = new Color(0x77619A);

	private double x;
	private double y;
	private double radius;

	// ******************************************************************************

	public AbstractDisplayableNode() {
		this.x = -1;
		this.y = -1;
		this.radius = -1;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	abstract public boolean hasLeft();
	abstract public AbstractDisplayableNode getLeft();

	abstract public boolean hasRight();
	abstract public AbstractDisplayableNode getRight();

	abstract public boolean hasParent();
	abstract public AbstractDisplayableNode getParent();

	abstract public String getRankString();
	abstract public String getBalanceString();
	abstract public String getElementString();

	/**
	 * this method paints the nodes and arrows on the given graphics object at the correct position with the given
	 * distances between the nodes at the given radius. coordinates are for the center of the node.
	 * 
	 * @param g2
	 * @param x
	 * @param y
	 * @param deltaX
	 * @param deltaY
	 * @param radius
	 * @return
	 */
	protected double paintHelper(Graphics2D g2, double x, double y, double deltaX, double deltaY, double radius) {
		if (this.hasLeft()) {
			// recurse updating the x position each time you draw a node
			x = this.getLeft().paintHelper(g2, x, y + deltaY, deltaX, deltaY, radius);
		}
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.drawNode(g2);
		if (this.hasLeft()) {
			this.drawFowardArrow(g2, this.getLeft());
		}
		if (this.hasParent()) {
			this.drawParentArrow(g2);
		}

		x += deltaX;
		if (this.hasRight()) {
			// recurse updating the x position each time you draw a node
			x = this.getRight().paintHelper(g2, x, y + deltaY, deltaX, deltaY, radius);
			this.drawFowardArrow(g2, this.getRight());
		}
		return x;
	}

	/**
	 * REQUIRES the point to already been set. draws the node on the current Graphics2D object
	 * 
	 * @param g2
	 */
	public void drawNode(Graphics2D g2) {
		g2.setColor(CIRCLE_COLOR);
		// creates circle taking into account that this.point is the centerPoint
		Ellipse2D circle = new Ellipse2D.Double(this.x - this.radius, this.y - this.radius, this.radius * 2,
				this.radius * 2);
		g2.draw(circle);

		drawRank(g2);
		drawBalance(g2);
		drawElement(g2);
	}

	/**
	 * draws the string form of the nodes rank inside of the node
	 * 
	 * @param g2
	 */
	private void drawRank(Graphics2D g2) {
		g2.setColor(TEXT_COLOR);
		String rank = this.getRankString();
		Rectangle2D bounds = g2.getFontMetrics().getStringBounds(rank, g2);
		// finds how much to shift the string to center the letter
		int upperLeftX = (int) (this.x - bounds.getWidth() / 2);
		int upperLeftY = (int) (this.y - 1 * bounds.getHeight() / 3); // don't know why this 1/3 works so good
		g2.drawString(rank, upperLeftX, upperLeftY);
	}

	/**
	 * draws the string form of the nodes balance inside of the node
	 * 
	 * @param g2
	 */
	private void drawBalance(Graphics2D g2) {
		g2.setColor(TEXT_COLOR);
		String balance = this.getBalanceString();
		Rectangle2D bounds = g2.getFontMetrics().getStringBounds(balance, g2);
		// finds how much to shift the string to center the letter
		int upperLeftX = (int) (this.x - bounds.getWidth() / 2);
		int upperLeftY = (int) (this.y + 1 * bounds.getHeight() / 4); // don't know why this 1/3 works so good
		g2.drawString(balance, upperLeftX, upperLeftY);
	}

	/**
	 * draws the string form of the nodes value inside of the node
	 * 
	 * @param g2
	 */
	private void drawElement(Graphics2D g2) {
		g2.setColor(TEXT_COLOR);
		String text = String.valueOf(this.getElementString());
		Rectangle2D bounds = g2.getFontMetrics().getStringBounds(text, g2);
		// finds how much to shift the string to center the letter
		int upperLeftX = (int) (this.x - bounds.getWidth() / 2);
		int upperLeftY = (int) (this.y + 5 * bounds.getHeight() / 6); // don't know why this 1/3 works so good
		g2.drawString(text, upperLeftX, upperLeftY);
	}

	/**
	 * draws a parent arrow on the given graphics object to this nodes parent
	 * 
	 * @param g2
	 */
	private void drawParentArrow(Graphics2D g2) {
		double sizeMultiplier = 0.75;
		AbstractDisplayableNode parent = this.getParent();
		AffineTransform transform = g2.getTransform(); // save graphics state to restore later
		this.moveGraphicsToEdge(g2, parent);
		double arrowLength = this.distanceTo(parent) - 2 * this.radius; // distance is from edge to edge
		boolean doubleArrow = false;
		if (this == this.getParent().getLeft() || this == this.getParent().getRight()) {
			// if there is a child arrow and a parent arrow on the same line, cut line part in half
			doubleArrow = true;
		}

		this.drawArrow(g2, PARENT_ARROW_COLOR, arrowLength, sizeMultiplier, doubleArrow);

		g2.setTransform(transform); // restores the graphics state
	}

	/**
	 * draws a forward arrow on the given graphics object to the given node
	 * 
	 * @param g2
	 * @param end
	 */
	private void drawFowardArrow(Graphics2D g2, AbstractDisplayableNode end) {
		double sizeMultiplier = 1;
		AffineTransform transform = g2.getTransform(); // save graphics state to restore later
		this.moveGraphicsToEdge(g2, end);
		double arrowLength = this.distanceTo(end) - 2 * this.radius; // distance is from edge to edge
		boolean doubleArrow = false;
		if (this.getLeft() == end || this.getRight() == end) {
			doubleArrow = true;
		}

		this.drawArrow(g2, FOWARD_ARROW_COLOR, arrowLength, sizeMultiplier, doubleArrow);

		g2.setTransform(transform); // restores the graphics state
	}

	/**
	 * moves the graphics object to the edge of the current node in the direction of the destination node
	 * 
	 * @param g2
	 * @param destination
	 */
	private void moveGraphicsToEdge(Graphics2D g2, AbstractDisplayableNode destination) {
		double angle = Math.atan2(destination.getY() - this.y, destination.getX() - this.x);

		// move to the edge of the nodes radius in the direction of next node
		g2.translate(destination.getX(), destination.getY());
		g2.rotate(angle + Math.PI / 2.0);
		g2.translate(0, this.radius);
	}

	/**
	 * draws an arrow on the graphics object with the given length and color and with a given size multiplier. Assumes
	 * the graphics object starts at the edge of the node, pointing towards the destination node. If there is multiple
	 * lines then it will have a half length stem
	 * 
	 * @param g2
	 * @param color
	 * @param length
	 * @param sizeMultiplier
	 * @param doubleLine
	 */
	private void drawArrow(Graphics2D g2, Color color, double length, double sizeMultiplier, boolean doubleLine) {
		g2.setColor(color);
		if (length < 0) {
			// draw the arrow the right way
			g2.rotate(Math.PI);
			length *= -1;
		}
		Line2D.Double line = new Line2D.Double(0, 0, 0, (doubleLine ? length / 2.0 : length));
		g2.draw(line);

		Path2D.Double arrowHead = new Path2D.Double();
		double lengthSqrt = Math.sqrt(length);
		// draws the arrow head, scaling with the sqrt of the length of the arrow
		arrowHead.moveTo(0, 0);
		arrowHead.lineTo(-lengthSqrt * sizeMultiplier, 2 * lengthSqrt * sizeMultiplier);
		arrowHead.lineTo(lengthSqrt * sizeMultiplier, 2 * lengthSqrt * sizeMultiplier);
		arrowHead.closePath();

		g2.fill(arrowHead);
	}

	/**
	 * calculates the distance from the center of this node to the center of the given node
	 * 
	 * @param end
	 * @return
	 */
	private double distanceTo(AbstractDisplayableNode end) {
		double dx = this.x - end.getX();
		double dy = this.y - end.getY();
		return Math.sqrt(dx * dx + dy * dy);
	}
}
