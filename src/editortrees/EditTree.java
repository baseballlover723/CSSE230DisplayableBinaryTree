package editortrees;

import debughelp.AbstractDisplayableBinaryTree;

// A height-balanced binary tree with rank that could be the basis for a text editor.

public class EditTree extends AbstractDisplayableBinaryTree{

	public Node root;
	public int rotCount = 0;
	final static Node NULL_NODE = new Node();
	public boolean rootChanged;
	public AbstractDisplayableBinaryTree display;

	/**
	 * Construct an empty tree
	 */
	public EditTree() {
		this.root = NULL_NODE;

	}

	/**
	 * Construct a single-node tree whose element is c
	 * 
	 * @param c
	 */
	public EditTree(char c) {
		this.root = new Node(c);
	}

	/**
	 * Create an EditTree whose toString is s. This can be done in O(N) time,
	 * where N is the length of the tree (repeatedly calling insert() would be
	 * O(N log N), so you need to find a more efficient way to do this.
	 * 
	 * @param s
	 */
	public EditTree(String s) {

	}

	/**
	 * Make this tree be a copy of e, with all new nodes, but the same shape and
	 * contents.
	 * 
	 * @param e
	 */
	public EditTree(EditTree e) {
		this.root = NULL_NODE;
		this.root = this.root.copy(e.root);
	}

	/**
	 * 
	 * @return the height of this tree
	 */
	public int height() {
		return this.root.height();
	}

	/**
	 * 
	 * returns the total number of rotations done in this tree since it was
	 * created. A double rotation counts as two.
	 *
	 * @return number of rotations since tree was created.
	 */
	public int totalRotationCount() {
		return this.rotCount; // replace by a real calculation.
	}

	/**
	 * return the string produced by an inorder traversal of this tree
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		this.root.stringify(sb);
		return sb.toString(); // replace by a real calculation.

	}

	/**
	 * This one asks for more info from each node. You can write it like the
	 * arraylist-based toString() method from the BST assignment. However, the
	 * output isn't just the elements, but the elements, ranks, and balance
	 * codes. Former CSSE230 students recommended that this method, while making
	 * it harder to pass tests initially, saves them time later since it catches
	 * weird errors that occur when you don't update ranks and balance codes
	 * correctly. For the tree with node b and children a and c, it should
	 * return the string: [b1=, a0=, c0=] There are many more examples in the
	 * unit tests.
	 * 
	 * @return The string of elements, ranks, and balance codes, given in a
	 *         pre-order traversal of the tree.
	 */
	public String toDebugString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		this.root.stringifyDebug(sb);
		if (sb.length() > 2) {
			sb.delete(sb.length() - 2, sb.length());
		}
		return sb.append("]").toString();
	}

	/**
	 * 
	 * @param pos
	 *            position in the tree
	 * @return the character at that position
	 * @throws IndexOutOfBoundsException
	 */
	public char get(int pos) throws IndexOutOfBoundsException {
		if (pos < 0 || pos > this.root.size()) {
			throw new IndexOutOfBoundsException();
		}

		Node current = this.root.get(pos);
		return current.element;
	}

	/**
	 * 
	 * @param c
	 *            character to add to the end of this tree.
	 */
	public void add(char c) {
		// Notes:
		// 1. Please document chunks of code as you go. Why are you doing what
		// you are doing? Comments written after the code is finalized tend to
		// be useless, since they just say WHAT the code does, line by line,
		// rather than WHY the code was written like that. Six months from now,
		// it's the reasoning behind doing what you did that will be valuable to
		// you!
		// 2. Unit tests are cumulative, and many things are based on add(), so
		// make sure that you get this one correct.
		Node newN = this.root.add(c, NULL_NODE, this);
		if (!this.rootChanged) {
			this.root = newN;
		}
		this.rootChanged = false;

	}

	public void setRoot(Node n) {
		this.root = n;
		this.rootChanged = true;
	}

	/**
	 * 
	 * @param c
	 *            character to add
	 * @param pos
	 *            character added in this inorder position
	 * @throws IndexOutOfBoundsException
	 *             id pos is negative or too large for this tree
	 */
	public void add(char c, int pos) throws IndexOutOfBoundsException {
		// might be able to do this better by placing it in Node class
		if (pos < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (this.root == EditTree.NULL_NODE) {
			if (pos != 0) {
				throw new IndexOutOfBoundsException();
			}
			this.root = new Node(c);
			this.root.parent = EditTree.NULL_NODE;
		} else {
			this.root.add(c, NULL_NODE, this, pos);

		}
	}

	/**
	 * 
	 * @return the number of nodes in this tree
	 */
	public int size() {
		return this.root.size(); // replace by a real calculation.
	}

	/**
	 * 
	 * @param pos
	 *            position of character to delete from this tree
	 * @return the character that is deleted
	 * @throws IndexOutOfBoundsException
	 */
	public char delete(int pos) throws IndexOutOfBoundsException {
		// Implementation requirement:
		// When deleting a node with two children, you normally replace the
		// node to be deleted with either its in-order successor or predecessor.
		// The tests assume assume that you will replace it with the
		// *successor*.
		if (this.root != NULL_NODE && pos >= 0) {
			// will check to see if pos > than number of nodes in tree inside
			// the node class and throw an exception there
			return this.root.delete(pos, this);
		}
		throw new IndexOutOfBoundsException();
	}

	/**
	 * This method operates in O(length*log N), where N is the size of this
	 * tree.
	 * 
	 * @param pos
	 *            location of the beginning of the string to retrieve
	 * @param length
	 *            length of the string to retrieve
	 * @return string of length that starts in position pos
	 * @throws IndexOutOfBoundsException
	 *             unless both pos and pos+length-1 are legitimate indexes
	 *             within this tree.
	 */
	public String get(int pos, int length) throws IndexOutOfBoundsException {
		return "";
	}

	/**
	 * This method is provided for you, and should not need to be changed. If
	 * split() and concatenate() are O(log N) operations as required, delete
	 * should also be O(log N)
	 * 
	 * @param start
	 *            position of beginning of string to delete
	 * 
	 * @param length
	 *            length of string to delete
	 * @return an EditTree containing the deleted string
	 * @throws IndexOutOfBoundsException
	 *             unless both start and start+length-1 are in range for this
	 *             tree.
	 */
	public EditTree delete(int start, int length) throws IndexOutOfBoundsException {
		if (start < 0 || start + length >= this.size())
			throw new IndexOutOfBoundsException(
					(start < 0) ? "negative first argument to delete" : "delete range extends past end of string");
		EditTree t2 = this.split(start);
		EditTree t3 = t2.split(length);
		this.concatenate(t3);
		return t2;
	}

	/**
	 * Append (in time proportional to the log of the size of the larger tree)
	 * the contents of the other tree to this one. Other should be made empty
	 * after this operation.
	 * 
	 * @param other
	 * @throws IllegalArgumentException
	 *             if this == other
	 */
	public void concatenate(EditTree other) throws IllegalArgumentException {

	}

	/**
	 * This operation must be done in time proportional to the height of this
	 * tree.
	 * 
	 * @param pos
	 *            where to split this tree
	 * @return a new tree containing all of the elements of this tree whose
	 *         positions are >= position. Their nodes are removed from this
	 *         tree.
	 * @throws IndexOutOfBoundsException
	 */
	public EditTree split(int pos) throws IndexOutOfBoundsException {
		return null; // replace by a real calculation.
	}

	/**
	 * Don't worry if you can't do this one efficiently.
	 * 
	 * @param s
	 *            the string to look for
	 * @return the position in this tree of the first occurrence of s; -1 if s
	 *         does not occur
	 */
	public int find(String s) {
		return -2;
	}

	/**
	 * 
	 * @param s
	 *            the string to search for
	 * @param pos
	 *            the position in the tree to begin the search
	 * @return the position in this tree of the first occurrence of s that does
	 *         not occur before position pos; -1 if s does not occur
	 */
	public int find(String s, int pos) {
		return -2;
	}

	/**
	 * @return The root of this tree.
	 */
	public Node getRoot() {
		if (this.root == NULL_NODE) {
			return null;
		}
		return this.root;
	}

	public int slowHeight() {
		if (this.root == NULL_NODE) {
			return -1;
		}
		return this.root.slowHeight();
	}

	public int slowSize() {
		return this.root.size();
	}
}
