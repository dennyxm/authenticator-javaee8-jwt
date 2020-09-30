package org.dennysm.microservice.control;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.dennysm.microservice.entity.UserData;

public class UserStore {
	@PersistenceContext
	EntityManager em;
	
	public UserData getUserDataByUsername(String username) {
		return (UserData)em.createQuery("SELECT u FROM UserData u WHERE u.userName=:username")
				 .setParameter("username", username)
				 .getSingleResult(); 
	}
	
	public UserData getUserDataByUserID(long userID) {
		return (UserData)em.createQuery("SELECT u FROM UserData u WHERE u.userID=:userid")
				 .setParameter("userid", userID)
				 .getSingleResult();
	}
}
