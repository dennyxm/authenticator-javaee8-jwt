package org.dennysm.microservice.control;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.ejb.Stateless;
import javax.persistence.NoResultException; 
import org.dennysm.microservice.entity.UserData; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.dennysm.microservice.boundary.LoginResponse;
import org.dennysm.microservice.crypto.EncryptorAesGcmPassword;

@Stateless
public class LoginService extends AbstractControlService {
	 
    private Logger logger = LoggerFactory.getLogger(LoginService.class);
	
	public LoginResponse doLogin(
				String username,  String password, 
				long timestamp, String signature 
			) {
		
		/*
		 * 1. validasi signature
		 * 2. ambil berdasar username, jika ada, validasi passwordnya
		 */
		MessageDigest digest;
		try {
			final String signatureBuild = username+password+Long.toString(timestamp)+salt;
			
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(signatureBuild.getBytes(StandardCharsets.UTF_8));
			final String finalSignature = Base64.getEncoder().encodeToString(hash);
			
			logger.info("server sig["+finalSignature+"]vs user sig ["+signature+"]");
			
			if(!finalSignature.equals(signature)) {
				LoginResponse failed = new LoginResponse();
				failed.setStatusCode("101");
				failed.setStatusMessage("Invalid Signature");
				return failed;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		UserData user;
		try {
			user = (UserData)em.createQuery("SELECT u FROM UserData u WHERE u.userName=:username")
					 .setParameter("username", username)
					 .getSingleResult(); 
			
			if(user.getStatus()!=1) {
				LoginResponse failed = new LoginResponse();
				failed.setStatusCode("102");
				failed.setStatusMessage("Inactive User");
				return failed;
			}
			
			// yg dikirimkan user adalah  SHA256( salt+password)
			final String finalPassword = username+password+salt; // ini dibentuk saat regis

			try {
				digest = MessageDigest.getInstance("SHA-256");
				byte[] hash = digest.digest(finalPassword.getBytes(StandardCharsets.UTF_8));
				final String finalHashedPassword = Base64.getEncoder().encodeToString(hash);
				logger.info(" expected password "+finalHashedPassword+" vs "+user.getPassword());
				
				if(user.getPassword().equals(finalHashedPassword)) {
					
					final long respTimestamp = System.currentTimeMillis();
					LoginResponse success = new LoginResponse();
					success.setStatusCode("000");
					success.setStatusMessage("Success");
					// enkrip aes user id nya
					logger.info("ENCRYPTION KEY "+encryptionKey);
					final String encryptedUserID = EncryptorAesGcmPassword.encrypt(Long.toString(user.getUserID()).getBytes(), encryptionKey) ;
					success.setUserID(encryptedUserID);
					
					success.setFullname(user.getFullName());
					success.setToken(ts.generateToken(encryptedUserID, user.getFullName(), respTimestamp));
					success.setTimestamp(respTimestamp);
					return success;
				}else {
					// gagal
					LoginResponse failed = new LoginResponse();
					failed.setStatusCode("103");
					failed.setStatusMessage("Invalid Username or Password");
					return failed;
				}
				
			} catch (NoSuchAlgorithmException e) { 
				e.printStackTrace();
			} catch (Exception e) { 
				e.printStackTrace();
			}
		} catch (NoResultException e) {
			LoginResponse failed = new LoginResponse();
			failed.setStatusCode("104");
			failed.setStatusMessage("Invalid Username or Password");
			return failed;
		} 
		
		return new LoginResponse();
	}
	
	
}
