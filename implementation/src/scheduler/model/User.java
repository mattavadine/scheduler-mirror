package scheduler.model;

import scheduler.model.db.DatabaseException;
import scheduler.model.db.IDBUser;

public class User extends ModelObject {
	private final Model model;
	
	final IDBUser underlyingUser;
	
	User(Model model, IDBUser underlyingUser) {
		this.model = model;
		this.underlyingUser = underlyingUser;

		if (!underlyingUser.isTransient())
			assert(!model.userCache.inCache(underlyingUser)); // make sure its not in the cache yet (how could it be, we're not even done with the constructor)
	}

	// PERSISTENCE FUNCTIONS

	public User insert() throws DatabaseException {
		preInsertOrUpdateSanityCheck();
		model.userCache.insert(this);
		return this;
	}

	public void update() throws DatabaseException {
		preInsertOrUpdateSanityCheck();
		model.userCache.update(this);
	}
	
	public void delete() throws DatabaseException {
		model.userCache.delete(this);
	}


	// ENTITY ATTRIBUTES

	public String getUsername() { return underlyingUser.getUsername(); }
	public void setUsername(String username) { underlyingUser.setUsername(username); }
	
	public boolean isAdmin() { return underlyingUser.isAdmin(); }
	public void setAdmin(boolean isAdmin) { underlyingUser.setAdmin(isAdmin); }

	public Integer getID() { return underlyingUser.getID(); }

	@Override
	public void preInsertOrUpdateSanityCheck() {
		assert getUsername() != null;
	}
}
