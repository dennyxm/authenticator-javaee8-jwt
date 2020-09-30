package org.dennysm.authenticator.control;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.NoResultException;

import org.dennysm.microservice.boundary.AuthValidatorResponse; 
import org.dennysm.microservice.control.AuthValidatorService;
import org.dennysm.microservice.control.TokenService;
import org.dennysm.microservice.crypto.EncryptorAesGcmPassword;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks; 
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuthValidatorServiceTest extends AbstractTester { 
	
	String encryptedUserID;
	String encryptionKey;
	String expiredToken;
	

	final String fullname="bolob";
	
	final long invalidUserID=-99L;
	final long validUserID=1L;
	
	// it is necessary to simulate real function of this class
	@InjectMocks
	TokenService tknSvc2;
	 
	
	@InjectMocks
	AuthValidatorService authSvc;

	@Override
	@Before
	public void init() {
		System.setProperty("log4j.configurationFile","log4j2-test.properties");
		
		//System.out.println("INITIALIZATION ");
		try {
			f1 = authSvc.getClass().getSuperclass().getDeclaredField("encryptionKey");
			f1.setAccessible(true);
			f1.set(authSvc, salt); 
			
			f2 = tknSvc2.getClass().getDeclaredField("tokenSecret");
			f2.setAccessible(true);
			f2.set(tknSvc2, "secret");
			
			// supaya lebih enak ngetesnya.. inject durasinya jadi cuma 1 menit
			f3 = tknSvc2.getClass().getDeclaredField("tokenDuration");
			f3.setAccessible(true);
			f3.set(tknSvc2, "1");
			
			// generate a valid token for user 
			
		} catch (Exception e) { 
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUserIdTidakTerdaftar() { 
		try {
			encryptedUserID = EncryptorAesGcmPassword.encrypt( Long.toString(invalidUserID).getBytes()  , salt);
		} catch (Exception e) { 
			e.printStackTrace();
		}
		
		// 105
		when(store.getUserDataByUserID(invalidUserID)).thenThrow( NoResultException.class );
		
		AuthValidatorResponse resp = authSvc.doValidate(encryptedUserID, "asalasalansaja");
		
		verify(store, times(1)).getUserDataByUserID(invalidUserID);
		
		assertEquals("105", resp.getStatusCode());
	}
	
	@Test
	public void testUserNonAktif() {
		// 102
		try {
			encryptedUserID = EncryptorAesGcmPassword.encrypt( Long.toString(validUserID).getBytes()  , salt);
		} catch (Exception e) { 
			e.printStackTrace();
		}
		
		when(store.getUserDataByUserID(validUserID)).thenReturn(
				inactiveUser
				); 
		
		AuthValidatorResponse resp = authSvc.doValidate(encryptedUserID, "asalasalansaja");
		
		verify(store, times(1)).getUserDataByUserID(validUserID);
		
		assertEquals("102", resp.getStatusCode());
	}
	
	
	@Test
	public void testTokenTidakValid() {
		try {
			encryptedUserID = EncryptorAesGcmPassword.encrypt( Long.toString(validUserID).getBytes()  , salt);
		} catch (Exception e) { 
			e.printStackTrace();
		}
		final long timestamp = System.currentTimeMillis();
		
		System.out.println("isi token svc "+tokenSvc.hashCode());
		
		String token = tknSvc2.generateToken(encryptedUserID, activeUser.getFullName(), timestamp);
		
		System.out.println("Value of generated token "+token);
		
		when(store.getUserDataByUserID(validUserID)).thenReturn(
				activeUser
				); 
		
		when(tokenSvc.isValid(encryptedUserID, activeUser.getFullName(), token)).thenReturn(false);
		
		AuthValidatorResponse resp = authSvc.doValidate(encryptedUserID, token);
		
		verify(tokenSvc, times(1)).isValid(encryptedUserID, activeUser.getFullName(), token); 
		verify(store, times(1)).getUserDataByUserID(validUserID);
		
		assertEquals("106", resp.getStatusCode());
	}
	
	@Test
	public void testValidUserIDandToken() {
		// 000
		try {
			encryptedUserID = EncryptorAesGcmPassword.encrypt( Long.toString(validUserID).getBytes()  , salt);
		} catch (Exception e) { 
			e.printStackTrace();
		}
		final long timestamp = System.currentTimeMillis();
		
		System.out.println("isi token svc "+tokenSvc.hashCode());
		
		String token = tknSvc2.generateToken(encryptedUserID, activeUser.getFullName(), timestamp);
		
		System.out.println("Value of generated token "+token);
		
		when(store.getUserDataByUserID(validUserID)).thenReturn(
				activeUser
				); 
		
		when(tokenSvc.isValid(encryptedUserID, activeUser.getFullName(), token)).thenReturn(true);
		
		AuthValidatorResponse resp = authSvc.doValidate(encryptedUserID, token);
		
		verify(tokenSvc, times(1)).isValid(encryptedUserID, activeUser.getFullName(), token); 
		verify(store, times(1)).getUserDataByUserID(validUserID);
		
		assertEquals("000", resp.getStatusCode());
	}
}
