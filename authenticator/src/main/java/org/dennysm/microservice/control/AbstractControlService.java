package org.dennysm.microservice.control;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.eclipse.microprofile.config.inject.ConfigProperty; 

public abstract class AbstractControlService {
	protected static final String salt="3atSh!t";
	 
    @Inject
    TokenService ts;
	
	@PersistenceContext
	EntityManager em;
	
	@Inject
	@ConfigProperty(name = "application.encryption-key", defaultValue = salt)
	protected String encryptionKey;
}
