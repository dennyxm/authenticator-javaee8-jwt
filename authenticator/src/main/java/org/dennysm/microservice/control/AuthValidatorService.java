package org.dennysm.microservice.control;

import javax.ejb.Stateless;
import javax.persistence.NoResultException; 

import org.dennysm.microservice.boundary.AuthValidatorResponse; 
import org.dennysm.microservice.crypto.EncryptorAesGcmPassword;
import org.dennysm.microservice.entity.UserData; 

@Stateless
public class AuthValidatorService extends AbstractControlService {
	 
	//private Logger logger = LoggerFactory.getLogger(LoginService.class);
	
	public AuthValidatorResponse doValidate(
				String encryptedUserID, String token
			) {
		/*
		 * dekrip userId, parse jadi long, jika bisa.. maka dia itu merupakan angka & bisa dilanjut ke proses query
		 * 
		 */
		UserData user;
		try {
			String decryptedUserID = EncryptorAesGcmPassword.decrypt(encryptedUserID, encryptionKey);
			final long userID = Long.parseLong(decryptedUserID);
			user = userStore.getUserDataByUserID(userID); 
			
			if(user.getStatus()!=1) {
				AuthValidatorResponse failed = new AuthValidatorResponse();
				failed.setStatusCode("102");
				failed.setStatusMessage("Inactive User");
				return failed;
			}
			
			if(ts.isValid(encryptedUserID, user.getFullName(), token)) {
				// generate token baru
				final long respTimestamp = System.currentTimeMillis();
				final String newToken = ts.generateToken(encryptedUserID, user.getFullName(), respTimestamp);
				
				AuthValidatorResponse success = new AuthValidatorResponse();
				success.setStatusCode("000");
				success.setStatusMessage("Success");
				success.setToken(newToken);
				return success;
			}else {
				// karena 1 dan lain hal, tidak valid, paksa logout
				AuthValidatorResponse failed = new AuthValidatorResponse();
				failed.setStatusCode("106");
				failed.setStatusMessage("Invalid Token Credential");
				return failed;
			}
			
		} catch (NoResultException e) {
			AuthValidatorResponse failed = new AuthValidatorResponse();
			failed.setStatusCode("105");
			failed.setStatusMessage("Invalid User Credential");
			return failed;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return new AuthValidatorResponse("999","General Error");
	}
}
