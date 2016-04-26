package editortrees;

import java.util.function.IntSupplier;

import debughelp.AbstractDisplayableNode;

public class Node extends AbstractDisplayableNode{

	enum Code {
		SAME, LEFT, RIGHT;
		// Used in the displayer and debug string
		@Override
		public String toString() {
			switch (this) {
			case LEFT:
				return "/";
			case SAME:
				return "=";
			case RIGHT:
				return "\\";
			default:
				throw new IllegalStateException();
			}
		}
	}

	// The fields would normally be private, but for the purposes of this class,
	// we want to be able to test the results of the algorithms in addition to
	// the
	// "publicly visible" effects

	char element;
	Node left, right, parent; // subtrees and parent
	int rank; // inorder position of this node within its own subtree.
	Code balance;
	AbstractDisplayableNode wrapper;

	public Node() {
		this.balance = Code.SAME;
	}

	public Node(char element) {
		this.element = element;
		this.balance = Code.SAME;
		this.left = EditTree.NULL_NODE;
		this.right = EditTree.NULL_NODE;
		this.parent = EditTree.NULL_NODE;
//		this.wrapper = new DisplayableNodeWrapper(this);
	}

	class Rank implements IntSupplier {

		@Override
		public int getAsInt() {
			// TODO Auto-generated method stub.
			return Node.this.left.size();
		}

	}

	// For the following methods, you should fill in the details so that they
	// work correctly
	public int height() {
		if (this == EditTree.NULL_NODE) {
			return -1;
		}else if (this.balance == Code.LEFT){
			return 1+ this.left.height();
		}else if (this.balance == Code.RIGHT){
			return 1 + this.right.height();
		}else{
			return 1+ this.right.height();
		}
		//return Math.max(1 + this.left.height(), 1 + this.right.height());
	}

	/**
	 * Recursively finds the "size" of the tree, ie. the number of nodes in the
	 * tree
	 *
	 * @return size of tree starting from "this"
	 */
	public int size() {
		if (this == EditTree.NULL_NODE) {
			return 0;
		}
		return 1 + this.left.size() + this.right.size();
	}

	/**
	 * Adds a new node to the tree at the far right
	 *
	 * @param c
	 *            element for new Node
	 * @param parent1
	 *            parent of the new Node
	 * @param tree
	 *            editor tree from which this method was initially called from;
	 *            used in other function calls
	 * @return the new Node created
	 */
	public Node add(char c, Node parent1, EditTree tree) {
		//
		if (this == EditTree.NULL_NODE) {
			Node n = new Node(c);
			n.parent = parent1;
			parent1.right = n;
			n.rank = 0;
			n.checkRotate(false, tree);

			return n;
		} else if (this.right == EditTree.NULL_NODE) {
			this.right = this.right.add(c, this, tree);

		} else {
			this.right.add(c, this, tree);
		}
		return this;

	}

	public Node get(int pos) throws IndexOutOfBoundsException {
		if (this == EditTree.NULL_NODE)
			throw new IndexOutOfBoundsException();
		if (this.rank == pos)
			return this;
		else if (this.rank > pos) {
			return this.left.get(pos);
		} else {
			pos = pos - this.rank - 1;
			return this.right.get(pos);
		}
	}

	public void stringify(StringBuilder sb) {
		if (this == EditTree.NULL_NODE) {
			return;
		}

		this.left.stringify(sb);
		sb.append(this.element);
		this.right.stringify(sb);
	}

	public void stringifyDebug(StringBuilder sb) {
		if (this == EditTree.NULL_NODE) {
			return;
		}
		sb.append(this.element);
		sb.append(this.rank);
		sb.append(this.balance);
		sb.append(", ");
		this.left.stringifyDebug(sb);
		this.right.stringifyDebug(sb);

	}

	public void checkRotate(boolean isLeft, EditTree tree) {
		Node p = this.parent;
		Node c = this;
		if (c == EditTree.NULL_NODE) {
			return;
		}
		while (p != EditTree.NULL_NODE) {

			if (p.getBalance() == Code.SAME && !isLeft) {
				p.setBalace(Code.RIGHT);
			} else if (p.getBalance() == Code.SAME && isLeft) {
				p.setBalace(Code.LEFT);
			} else if ((p.getBalance() == Code.LEFT && !isLeft) || (p.getBalance() == Code.RIGHT && isLeft)) {
				p.setBalace(Code.SAME);
				break;
			} else {
				p = p.doRotation(c, tree);
				break;
			}
			c = p;
			p = p.parent;
			if (p.left == c) {
				isLeft = true;
			} else {
				isLeft = false;
			}
		}

	}

	public Node singleRR(Node b, EditTree tree) {
		tree.rotCount++;
		Node a = this;
		Node apar = a.parent;
		Node bright = b.right;

		if (a.parent == EditTree.NULL_NODE) {
			b.parent = EditTree.NULL_NODE;
			tree.setRoot(b);
		} else {
			b.parent = apar;

			if (a == apar.left) {
				apar.left = b;
			} else {
				apar.right = b;
			}
		}

		a.left = bright;
		a.parent = b;
		b.right = a;
		bright.parent = a;

		a.rank = a.rank - (b.rank + 1);

		int lh = a.left.height();
		int rh = a.right.height();
		b.setBalace(Code.SAME);
		if (lh > rh)
			a.setBalace(Code.LEFT);
		else if (lh == rh)
			a.setBalace(Code.SAME);
		else
			a.setBalace(Code.RIGHT);

		return b;
	}

	public Node singleLR(Node b, EditTree tree) {
		tree.rotCount++;
		Node a = this;
		Node Apar = a.parent;
		Node bleft = b.left;

		if (a.parent == EditTree.NULL_NODE) {
			b.parent = EditTree.NULL_NODE;
			tree.setRoot(b);
		} else {
			b.parent = Apar;
			if (a == a.parent.left) {
				Apar.left = b;
			} else {
				Apar.right = b;
			}
		}

		a.right = bleft;
		a.parent = b;
		b.left = a;
		bleft.parent = a;

		b.rank = a.rank + b.rank + 1;

		int lh = a.left.height();
		int rh = a.right.height();

		b.setBalace(Code.SAME);

		if (lh > rh)
			a.setBalace(Code.LEFT);
		else if (lh == rh)
			a.setBalace(Code.SAME);
		else
			a.setBalace(Code.RIGHT);

		return b;
	}

	public Node doRotation(Node b, EditTree tree) {
		// No need to check if caller is a nullnode, checkRotate screens out
		// that case
		// Single right rotation
		if (this.getBalance() == Code.LEFT && b.getBalance() == Code.LEFT) {
			return this.singleRR(b, tree);
		} // Double right rotation
		else if (this.getBalance() == Code.LEFT && b.getBalance() == Code.RIGHT) {

			Node newB = b.singleLR(b.right, tree);
			Node output = this.singleRR(newB, tree);

			return output;
		} // Double left rotation
		else if (this.getBalance() == Code.RIGHT && b.getBalance() == Code.LEFT) {
			Node newB = b.singleRR(b.left, tree);
			Node output = this.singleLR(newB, tree);

			return output;
		} // Single left rotation
		else {
			return this.singleLR(b, tree);
		}
	}

	private void setBalace(Code newBalance) {
		this.balance = newBalance;
	}

	public Code getBalance() {
		return this.balance;
	}

	public void add(char c, Node parent1, EditTree tree, int pos) {
		if (this.left == EditTree.NULL_NODE && this.right == EditTree.NULL_NODE) {
			if (pos > 1) {
				throw new IndexOutOfBoundsException();
			}
		}

		if (pos <= this.rank) {
			if (this.left == EditTree.NULL_NODE) {
				this.left = new Node(c);
				this.left.parent = this;
				this.rank++;
				this.left.checkRotate(true, tree);

			} else {
				this.rank++;
				this.left.add(c, this, tree, pos);

			}

		} else if (pos > this.rank) {
			if (this.right == EditTree.NULL_NODE) {
				this.right = new Node(c);
				this.right.parent = this;
				this.right.checkRotate(false, tree);
			} else {
				this.right.add(c, this, tree, pos - (this.rank + 1));
			}
		}

	}



	public Node copy(Node oldNode) {
		if (oldNode == EditTree.NULL_NODE) {
			return EditTree.NULL_NODE;
		}
		Node toReturn = new Node(oldNode.element);
		toReturn.rank = oldNode.rank;
		toReturn.balance = oldNode.balance;
		toReturn.left = toReturn.copy(oldNode.left);
		toReturn.left.parent = toReturn;
		toReturn.right = toReturn.copy(oldNode.right);
		toReturn.right.parent = toReturn;
		return toReturn;
	}

	public char delete(int pos, EditTree tree) {
		
		if (this == EditTree.NULL_NODE) {
			throw new IndexOutOfBoundsException();
		}
		if (this.left == EditTree.NULL_NODE && this.right == EditTree.NULL_NODE) {
			if (pos > 1) {
				throw new IndexOutOfBoundsException();
			}
		}
		if (pos > this.rank) {
			return this.right.delete(pos - (this.rank + 1), tree);
		} else if (pos < this.rank) {
			this.rank--;
			return this.left.delete(pos, tree);
		} else {
			if (this.parent.left == this) {
				if (this.left == EditTree.NULL_NODE && this.right == EditTree.NULL_NODE) {
					this.parent.left = EditTree.NULL_NODE;
					this.parent.right.checkRotate(false, tree);

				} else if (this.left == EditTree.NULL_NODE && this.right != EditTree.NULL_NODE) {
					this.parent.left = this.right;
					this.right.parent = this.parent.left;
					this.parent.left.checkRotate(true, tree);
				} else if (this.left != EditTree.NULL_NODE && this.right == EditTree.NULL_NODE) {
					this.parent.left = this.left;
					this.left.parent = this.parent.left;
					this.parent.left.checkRotate(true, tree);
				}else{
				Node current = this.right;
				if (current.left != EditTree.NULL_NODE) {
					while (current.left != EditTree.NULL_NODE) {
						current = current.left;
					}
				}
				char toReturn = this.element;
				this.element = current.element;
				current.element = toReturn;
				return this.delete(this.rank + 1, tree);
				}
			} else {
				if (this.left == EditTree.NULL_NODE && this.right == EditTree.NULL_NODE) {
					this.parent.right = EditTree.NULL_NODE;
					this.parent.left.checkRotate(true, tree);

				} else if (this.left == EditTree.NULL_NODE && this.right != EditTree.NULL_NODE) {
					this.parent.right = this.right;
					this.right.parent = this.parent.right;
					this.parent.right.checkRotate(false, tree);
				} else if (this.left != EditTree.NULL_NODE && this.right == EditTree.NULL_NODE) {
					this.parent.right = this.left;
					this.left.parent = this.parent.right;
					this.parent.right.checkRotate(false, tree);
				}
				else{
					Node current = this.right;
				if (current.left != EditTree.NULL_NODE){
					while (current.left != EditTree.NULL_NODE) {
						current = current.left;
					}
				}
				char toReturn = this.element;
				this.element = current.element;
				current.element = toReturn;
				return this.delete( this.rank + 1, tree);
			}
			}
			
		}

		return this.element;
		// TODO Auto-generated method stub.
		// If no children, make this a null node

		// If one child, make this data child's data and delete child. Check
		// rotation

		// If two children, make this data this.right.data (or
		// this.parent.right.data, etc) and remove other node. Check rotation

	}
	public int slowHeight() {
		// base case
		if (this == EditTree.NULL_NODE) {
			return 0;
		}
		return 1 + Math.max(this.left.height(), this.right.height());
	}

	public int slowSize() {
		if (this == EditTree.NULL_NODE) {
			return 0;
		}
		return this.left.size() + this.right.size() + 1;
	}

	public boolean hasLeft() {
		return this.left != EditTree.NULL_NODE;
	}

	public Node getLeft() {
		return this.left;
	}

	public boolean hasRight() {
		return this != EditTree.NULL_NODE && this.right != EditTree.NULL_NODE;
	}

	public Node getRight() {
		return this.right;
	}

	public boolean hasParent() {
		return this != EditTree.NULL_NODE && this.parent != EditTree.NULL_NODE;
	}

	public Node getParent() {
		return this.parent;
	}

	public int getRank() {
		return this.rank;
	}

	public char getElement() {
		return this.element;
	}

	@Override
	public String getRankString() {
		return Integer.toString(this.rank);
	}

	@Override
	public String getBalanceString() {
		return this.balance.toString();
	}

	@Override
	public String getElementString() {
		return Character.toString(this.element);
	}
}
