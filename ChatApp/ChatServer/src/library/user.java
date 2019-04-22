package library;

/*
 * This class deals with the data of the user and the
 * chat history of the user. The 
 */

public class user {
	// variables for user
	protected String username;
	protected String password;

	// constructors
	public user() {
		this(null, null);
	}

	public user(String usrnm, String psswrd) {
		username = usrnm;
		password = psswrd;
	}

	// getters/setters
	public String getUser() {
		return username;
	}

	public void setUser(String usrnm) {
		username = usrnm;
	}

	public String getPass() {
		return password;
	}

	public void setPass(String psswrd) {
		password = psswrd;
	}

	// compare functions

	public int compareUser(String usrnm) {
		if (usrnm.compareTo(username) == 0)
			return 0;
		return (usrnm.compareTo(username) < 0) ? -1 : 1;
	}

	public int matchPass(String psswrd) {
		return (psswrd.compareTo(password) == 0) ? 1 : 0;
	}

	// displays username
	public void displayUser() {
		System.out.println(username);
	}
}
