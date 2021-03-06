package scheduler.model.db.sqlite;

import scheduler.model.db.IDBUser;
/**
 * The Class SQLUser implements all methods of the IDBUser class (part of the IDatabase interface).
 * This class represents a CAS user in the SQLite database.
 * @author kayleneS
 *
 */
public class SQLUser extends SQLObject implements IDBUser {
	String username;
	boolean isAdmin;
	
	public SQLUser(Integer id, String username, Boolean isAdmin) {
		super(id);
		this.username = username;
		this.isAdmin = isAdmin;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public boolean isAdmin() {
		return isAdmin;
	}

	@Override
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

}
