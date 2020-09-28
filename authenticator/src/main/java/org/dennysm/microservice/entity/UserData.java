package org.dennysm.microservice.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
public class UserData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userdata_generator")
	@SequenceGenerator(name="userdata_generator", sequenceName = "userdata_seq")
	private long userID;
	
	private String fullName;
	private String userName;
	private String password;
	private int status;
	
	public UserData() {}

	public UserData(long userID, String fullName, String userName, String password, int status) {
		this.userID = userID;
		this.fullName = fullName;
		this.userName = userName;
		this.password = password;
		this.status = status;
	}

	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
