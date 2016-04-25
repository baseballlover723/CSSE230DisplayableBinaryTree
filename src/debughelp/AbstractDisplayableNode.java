package debughelp;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
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
	private Point.Double point;
	private double radius;

	// ******************************************************************************
	
	public AbstractDisplayableNode() {
		this.point = null;
		this.radius = -1;
	}
	
	abstract public boolean hasLeft();
	abstract public boolean hasRight();
	abstract public boolean hasParent();

	abstract public AbstractDisplayableNode getLeft();
	abstract public AbstractDisplayableNode getRight();
	abstract public AbstractDisplayableNode getParent();
	
	abstract public String getRankString();
	abstract public String getBalanceString();
	abstract public String getElementString();
	
	/**
	 * sets this.point to the new point
	 * 
	 * @param x
	 * @param y
	 */
	public void setPoint(double x, double y) {
		this.point = new Point.Double(x, y);
	}

	/**
	 * sets this.point to the new point
	 * 
	 * @param newPoint
	 */
	public void setPoint(Point.Double newPoint) {
		this.point = newPoint;
	}

	/**
	 * @returns this.point
	 */
	public Point.Double getPoint() {
		return this.point;
	}

	/**
	 * sets this.radius
	 * 
	 * @param newRadius
	 */
	public void setCircleRadius(double newRadius) {
		this.radius = newRadius;
	}

	/**
	 * 
	 * @returns this.radius
	 */
	public double getCircleRadius() {
		return this.radius;
	}

	/**
	 * REQUIRES the point to already been set draws the node on the current Graphics2D object
	 * 
	 * @param g2
	 */
	public void displayNode(Graphics2D g2) {
		// sets the circle outline color
		g2.setColor(CIRCLE_COLOR);
		// creates circle taking into account that this.point is the centerPoint
		Ellipse2D circle = new Ellipse2D.Double(this.point.x - this.radius, this.point.y - this.radius,
				this.radius * 2, this.radius * 2);
		g2.draw(circle);
		// sets the text color
		g2.setColor(TEXT_COLOR);

		// finds how much to shift the string to center the letter
		String rank = this.getRankString();
		Rectangle2D bounds = g2.getFontMetrics().getStringBounds(rank, g2);
		int upperLeftX = (int) (this.point.x - bounds.getWidth() / 2);
		int upperLeftY = (int) (this.point.y - 1 * bounds.getHeight() / 3); // don't know why this 1/3 works so good
		g2.drawString(rank, upperLeftX, upperLeftY);
		// System.out.println(this.balance);

		String balance = this.getBalanceString();
		bounds = g2.getFontMetrics().getStringBounds(balance, g2);
		upperLeftX = (int) (this.point.x - bounds.getWidth() / 2);
		upperLeftY = (int) (this.point.y + 1 * bounds.getHeight() / 4); // don't know why this 1/3 works so good
		g2.drawString(balance, upperLeftX, upperLeftY);

		String text = String.valueOf(this.getElementString());
		bounds = g2.getFontMetrics().getStringBounds(text, g2);
		upperLeftX = (int) (this.point.x - bounds.getWidth() / 2);
		upperLeftY = (int) (this.point.y + 5 * bounds.getHeight() / 6); // don't know why this 1/3 works so good
		g2.drawString(text, upperLeftX, upperLeftY);

	}

}
