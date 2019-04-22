package library;

import java.io.*;
import java.util.*;

@SuppressWarnings("unused")
public class library {
	private static final String userfile = "users.txt";
	private FileReader fr = null;
	private BufferedReader br = null;
	private FileWriter fw = null;
	private uNode root;
	private BufferedWriter bw = null;

	public static void main(String[] args) {
		library test = new library();
		test.displayAll();
		int b = test.balance();
		switch (b) {
		case 1:
			System.out.println("Balanced");
			break;
		case 0:
			System.out.println("Not Balanced");
		}
	}

	// constructor
	public library() {
		root = null;
		build();
	}

	// build function
	private int build() {
		try {
			fr = new FileReader(new File(userfile));
			br = new BufferedReader(fr);
			String line = null;
			String[] splitter = null;
			while ((line = br.readLine()) != null) {
				splitter = line.split(";");
				uNode temp = new uNode(splitter[0], splitter[1]);
				insert(temp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return 1;
	}

	// write to file function
	public int writeToFile() {
		try {
			fw = new FileWriter(userfile);
			bw = new BufferedWriter(fw);
			writeToFile(root);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return 1;
	}

	private int writeToFile(uNode root) {
		String usrnm, psswrd, line = null;
		if (root == null)
			return 0;
		usrnm = root.getUser();
		psswrd = root.getPass();
		line = usrnm + ";" + psswrd;
		try {
			bw.write(line);
			bw.newLine();
			writeToFile(root.goLeft());
			writeToFile(root.goRight());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
		return 1;
	}

	// append to file function
	public int appendFile(String usrnm, String psswrd) {
		try {
			fw = new FileWriter(userfile, true);
			bw = new BufferedWriter(fw);
			String line = usrnm + ";" + psswrd + "\n";
			bw.write(line);
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return 1;
	}

	// insert function (sorted by username)
	public int insert(uNode newNode) {
		int cmp, hgt, lhgt, rhgt = 0;
		if (root == null)
			root = new uNode(newNode);
		else {
			String user = newNode.getUser();
			cmp = root.compareUser(user);
			root.insert(newNode);
			lhgt = height(root.goLeft()) + 1;
			rhgt = height(root.goRight()) + 1;
			if (cmp == -1) {
				cmp = root.goLeft().compareUser(user);
				hgt = lhgt - rhgt;
				if (hgt == 2) {
					if (cmp == -1)
						root = root.lRotate(root);
					else
						root = root.dlRotate(root);
				}
			} else {
				cmp = root.goRight().compareUser(user);
				hgt = rhgt - lhgt;
				if (hgt == 2) {
					if (cmp == -1)
						root = root.drRotate(root);
					else
						root = root.rRotate(root);
				}
			}
		}
		return 1;
	}

	// check function (for registering users)
	public int checkUser(String usrnm) {
		return checkUser(usrnm, root);
	}

	private int checkUser(String usrnm, uNode root) {
		if (root == null)
			return 0;
		int cmp = root.compareUser(usrnm);
		switch (cmp) {
		case 0:
			return 1;
		case -1:
			return checkUser(usrnm, root.goLeft());
		case 1:
			return checkUser(usrnm, root.goRight());
		default:
			return 0;
		}
	}

	// check function (for logging in)
	public int check(String usrnm, String psswrd) {
		return check(usrnm, psswrd, root);
	}

	private int check(String usrnm, String psswrd, uNode root) {
		if (root == null)
			return 0;
		int cmp = root.compareUser(usrnm);
		if (cmp == 0) {
			int mtch = root.matchPass(psswrd);
			return (mtch == 1) ? 1 : 0;
		}
		return (cmp < 0) ? check(usrnm, psswrd, root.goLeft()) : check(usrnm, psswrd, root.goRight());
	}

	// display functions
	public int displayUser(String usrnm) {
		return displayUser(usrnm, root);
	}

	private int displayUser(String usrnm, uNode root) {
		if (root == null)
			return 0;
		int cmp = root.compareUser(usrnm);
		switch (cmp) {
		case 0:
			root.displayUser();
			return 1;
		case -1:
			return displayUser(usrnm, root.goLeft());
		case 1:
			return displayUser(usrnm, root.goRight());
		default:
			return 0;
		}
	}


	// displayAll functions
	public int displayAll() {
		return displayAll(root);
	}

	private int displayAll(uNode root) {
		if (root == null)
			return 0;
		displayAll(root.goLeft());
		root.displayUser();
		displayAll(root.goRight());
		return 1;
	}

	// retrieve function
	public uNode retrieve(String usrnm) {
		return retrieve(usrnm, root);
	}

	private uNode retrieve(String usrnm, uNode root) {
		if (root == null)
			return null;
		int cmp = root.compareUser(usrnm);
		switch (cmp) {
		case 0:
			uNode temp = new uNode(root);
			return temp;
		case -1:
			return retrieve(usrnm, root.goLeft());
		case 1:
			return retrieve(usrnm, root.goRight());
		default:
			return null;
		}

	}

	// retrieve all function
	public uNode retrieveAll() {
		return root;
	}

	// removeAll function
	public int removeAll() {
		if (root != null) {
			root.removeAll();
			root = null;
		}
		return 1;
	}

	// balance function (for AL tree balance testing)
	public int balance() {
		int lhgt, rhgt, x, y = 0;
		if (root == null)
			return -1;
		lhgt = height(root.goLeft()) + 1;
		rhgt = height(root.goRight()) + 1;
		x = lhgt - rhgt;
		y = rhgt - lhgt;
		return (x == 0 || x == 1 || y == 0 || y == 1) ? 1 : 0;
	}

	// height functions
	public int height() {
		return height(root);
	}

	private int height(uNode root) {
		return (root == null) ? 0 : Math.max(height(root.goLeft()), height(root.goRight())) + 1;
	}
}
