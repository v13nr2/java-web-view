package nanang.application.id.model;

import java.io.Serializable;

public class remember implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	String email, password;

	public remember(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return this.email;
	}

	public String getPassword() {
		return this.password;
	}

}
