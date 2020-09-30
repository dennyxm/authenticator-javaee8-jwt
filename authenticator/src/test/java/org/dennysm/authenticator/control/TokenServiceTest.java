package org.dennysm.authenticator.control;

import static org.junit.Assert.*;

import org.dennysm.microservice.control.TokenService;
import org.dennysm.microservice.crypto.EncryptorAesGcmPassword;
import org.dennysm.microservice.entity.UserData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

public class TokenServiceTest extends AbstractTester {
	
	String encryptedUserID;
	long timestamp;
	
	UserData secondActiveUser = new UserData(2L, "johann", "johanbrew", "kZsWb0J+7Y+Jluen39xW4+Rwu7mvotqKbBRbwlbZ+WU=", 1);
	
	@InjectMocks
	TokenService ts;

	@Test
	public void testGenerateToken() { 
		String token = ts.generateToken(encryptedUserID, activeUser.getFullName(), timestamp);
		assertNotNull(token); 
	}

	@Test
	public void testIsValid() {
		String token = ts.generateToken(encryptedUserID, activeUser.getFullName(), timestamp);
		assertTrue(ts.isValid(encryptedUserID, activeUser.getFullName(), token));
	}
	
	@Test
	public void testInvalidUserID() {
		// userid yg jadi parameter itu beda dengan yg ada didalam token
		try {
			String invalidUserID = EncryptorAesGcmPassword.encrypt( Long.toString(secondActiveUser.getUserID()).getBytes()  , salt); 
			String token = ts.generateToken(encryptedUserID, activeUser.getFullName(), timestamp);
			assertFalse(ts.isValid(invalidUserID, activeUser.getFullName(), token));
		} catch (Exception e) { 
			fail("pokoknya fail");
		} 
		
		
	}
	
	@Test
	public void testInvalidFullname() {
		// user id cocok dgn yg ada di token, tapi fullname nya beda dengan yg ada di token
		try {
			String token = ts.generateToken(encryptedUserID, activeUser.getFullName(), timestamp);
			assertFalse(ts.isValid(encryptedUserID, secondActiveUser.getFullName(), token));
		} catch (Exception e) {
			fail("not yet");
		}
		
	}
	
	@Test
	public void testExpiredToken() {
		// userid cocok 
		try {
			String token = ts.generateToken(encryptedUserID, activeUser.getFullName(), timestamp);
			System.out.println("WAITING 65s");
			Thread.sleep(65*1*1000L);
			assertFalse(ts.isValid(encryptedUserID, activeUser.getFullName(), token));
		} catch (Exception e) {
			fail("expired");
		}
		
	}
	
	
	@Test
	public void testInvalidToken() {
		// valid token punya orang
		try {
			String invalidUserID = EncryptorAesGcmPassword.encrypt( Long.toString(secondActiveUser.getUserID()).getBytes()  , salt); 
			String token = ts.generateToken(encryptedUserID, activeUser.getFullName(), timestamp); 
			assertFalse(ts.isValid(invalidUserID, secondActiveUser.getFullName(), token));
		} catch (Exception e) {
			fail("inv token");
		}
	}
	
	@Test
	public void testInvalidTokenString() {
		// bahkan bukan token jwt
		try {
			String token = "THISISNOTEVENAJSONWEBTOKEN"; 
			assertFalse(ts.isValid(encryptedUserID, activeUser.getFullName(), token));
		} catch (Exception e) {
			fail("inv token");
		}
	}
	
	@Test
	public void testInvalidTokenSignature() {
		/*
		 * bikin 2 token
		 * swap signature nya
		 * validasi
		 */
		try {
			String tokenOne = ts.generateToken(encryptedUserID, activeUser.getFullName(), timestamp);
			System.out.println("TOKEN ONE "+tokenOne);
			String [] arrTokenOne = tokenOne.split("\\."); 
			System.out.println("ARR ONE "+arrTokenOne.length);
			
			String invalidUserID = EncryptorAesGcmPassword.encrypt( Long.toString(secondActiveUser.getUserID()).getBytes()  , salt);  
			String tokenTwo = ts.generateToken(invalidUserID, secondActiveUser.getFullName(), timestamp); 
			System.out.println("TOKEN TWO "+tokenTwo);
			String [] arrTokenTwo = tokenTwo.split("\\.");
			System.out.println("ARR TWO "+arrTokenTwo.length);
			
			String newTokenOne = arrTokenOne[0]+arrTokenOne[1]+arrTokenTwo[2];
			//String newTokenTwo = 
			assertFalse(ts.isValid(encryptedUserID, activeUser.getFullName(), newTokenOne));
		} catch (Exception e) {
			e.printStackTrace();
			fail("not yet");
		} 
	}

	@Override
	@Before
	public void init() {
		try {
			System.setProperty("log4j.configurationFile","log4j2-test.properties");
			
			f2 = ts.getClass().getDeclaredField("tokenSecret");
			f2.setAccessible(true);
			f2.set(ts, "secret");
			
			// supaya lebih enak ngetesnya.. inject durasinya jadi cuma 1 menit
			f3 = ts.getClass().getDeclaredField("tokenDuration");
			f3.setAccessible(true);
			f3.set(ts, "1");
			
			timestamp = System.currentTimeMillis();
			
			encryptedUserID = EncryptorAesGcmPassword.encrypt( Long.toString(1L).getBytes()  , salt); 
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
	}

}
