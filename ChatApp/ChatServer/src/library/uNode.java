package library;

/*
 * This class is for the node of users, also known as the uNode.
 * There will be two sections, one section deals with the management
 * of the users, which involves the name, username, and password,
 * while the other section will deal with the management of the 
 * chat mssages sent by a user.
 */

public class uNode extends user {
	private uNode left;// left pointer
	private uNode right;// right pointer
	// constructors

	public uNode() {
		this(null, null);
	}

	public uNode(String usrnm, String psswrd) {
		super(usrnm, psswrd);
		left = null;
		right = null;
	}

	public uNode(uNode copy) {
		this(copy.getUser(), copy.getPass());
	}

	// getters/setters for left/right
	public uNode goLeft() {
		return left;
	}

	public void setLeft(uNode connect) {
		left = connect;
	}

	public uNode goRight() {
		return right;
	}

	public void setRight(uNode connect) {
		right = connect;
	}

	// getters/setters for node's data

	public String getUser() {
		return username;
	}

	public String getPass() {
		return password;
	}

	// compare/match functions
	public int compareUser(String usrnm) {
		return super.compareUser(usrnm);
	}

	public int matchPass(String psswrd) {
		return super.matchPass(psswrd);
	}

	// insert functions
	public int insert(uNode temp) {
		int cmp = compareUser(temp.username);
		if (cmp == 0)
			return 1;
		return (cmp == -1) ? insertLeft(temp) : insertRight(temp);
	}

	private int insertLeft(uNode temp) {
		int cmp, hgt, lhgt, rhgt = 0;
		if (left == null)
			left = temp;
		else {
			String user = temp.getUser();
			cmp = left.compareUser(user);
			left.insert(temp);
			lhgt = height(left.left) + 1;
			rhgt = height(left.right) + 1;
			if (cmp == 0)
				return 1;
			else if (cmp == -1) {
				hgt = lhgt - rhgt;
				cmp = left.left.compareUser(user);
				if (hgt == 2)
					left = (cmp == -1) ? lRotate(left) : dlRotate(left);
			} else {
				hgt = rhgt - lhgt;
				cmp = left.right.compareUser(user);
				if (hgt == 2)
					left = (cmp == 1) ? drRotate(left) : rRotate(left);
			}
		}
		return 1;
	}

	private int insertRight(uNode temp) {
		int cmp, hgt, lhgt, rhgt = 0;
		if (right == null)
			right = temp;
		else {
			String user = temp.getUser();
			cmp = right.compareUser(user);
			right.insert(temp);
			lhgt = height(right.left) + 1;
			rhgt = height(right.right) + 1;
			if (cmp == 0)
				return 1;
			else if (cmp == -1) {
				hgt = lhgt - rhgt;
				cmp = right.left.compareUser(user);
				if (hgt == 2)
					right = (cmp == -1) ? lRotate(right) : dlRotate(right);
			} else {
				hgt = rhgt - lhgt;
				cmp = right.right.compareUser(user);
				if (hgt == 2)
					right = (cmp == -1) ? drRotate(right) : rRotate(right);
			}
		}
		return 1;
	}

	// display function(s)
	public void displayUser() {
		super.displayUser();
	}

	// removeAll function
	public int removeAll() {
		if (left != null) {
			left.removeAll();
			left = null;
		}
		if (right != null) {
			right.removeAll();
			right = null;
		}
		return 1;
	}

	// height function
	private int height(uNode root) {
		if (root == null)
			return 0;
		return Math.max(height(root.goLeft()), height(root.goRight())) + 1;
	}

	// rotate functions
	public uNode lRotate(uNode root) {
		uNode temp = root.left;
		root.left = temp.right;
		temp.right = root;
		root = temp;
		return root;
	}

	public uNode dlRotate(uNode root) {
		root.left = rRotate(root.left);
		root = lRotate(root);
		return root;
	}

	public uNode rRotate(uNode root) {
		uNode temp = root.right;
		root.right = temp.left;
		temp.left = root;
		root = temp;
		return root;
	}

	public uNode drRotate(uNode root) {
		root.right = lRotate(root.right);
		root = rRotate(root);
		return root;
	}
}
