package debughelp;

/**
 * An abstract class for binary trees whose subclasses can be displayed in a flexible, auto-sizing GUI.
 * 
 * @author Philip Ross, 2014.
 */
abstract public class AbstractDisplayableBinaryTree {
	private DisplayableBinaryTreeComponent component;

	/**
	 * Constructs a new displayable binary tree
	 */
	public AbstractDisplayableBinaryTree() {
		this.component = new DisplayableBinaryTreeComponent(this);
	}

	/**
	 * Determines the height of the binary tree in O(n) time. Used to always correctly display the binary tree.
	 * 
	 * @return
	 */
	abstract public int slowHeight();

	/**
	 * Determines the number of nodes in the binary tree in O(n) time. Used to always correctly display the binary tree.
	 * 
	 * @return
	 */
	abstract public int slowSize();

	/**
	 * Returns the root of the binary tree.
	 * @return
	 */
	abstract public AbstractDisplayableNode getRoot();

	/**
	 * Shows the binary tree in an JFrame.
	 */
	public void show() {
		this.component.show();
	}

	/**
	 * closes the JFrame that the binary tree is displayed in.
	 */
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
