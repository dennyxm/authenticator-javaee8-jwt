package org.dennysm.authenticator.control;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.persistence.NoResultException;

import org.dennysm.microservice.boundary.LoginResponse;
import org.dennysm.microservice.control.LoginService; 
import org.dennysm.microservice.entity.UserData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks; 
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class LoginServiceTest extends AbstractTester {
	
	
	@InjectMocks
	LoginService ls;
	
	UserData invalidUserPwd = new UserData(1L, "bolob", "bolobolo", "ObviouslyWrongPassword", 1);
	
	@Override
	@Before
	public void init() {
		// set semua nilai yg ada di config microprofile saat unit test dengan reflection  
		// kenapa pake getSuperclass , ya karena dibaca dari Object
					
		try {
			f1 = ls.getClass().getSuperclass().getDeclaredField("encryptionKey");
			f1.setAccessible(true);
			f1.set(ls, salt); 
			
			f2 = tokenSvc.getClass().getSuperclass().getDeclaredField("tokenSecret");
			f2.setAccessible(true);
			f2.set(tokenSvc, "secret");
			
			f3 = tokenSvc.getClass().getSuperclass().getDeclaredField("tokenDuration");
			f3.setAccessible(true);
			f3.set(tokenSvc, "10");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	@Test
	public void validLogin() {
		
		final String username="bolobolo"; 
		final long timestamp = System.currentTimeMillis();
		final String plainSignature = username+password+Long.toString(timestamp)+salt;
		
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(plainSignature.getBytes(StandardCharsets.UTF_8));
			final String finalSignature = Base64.getEncoder().encodeToString(hash);
			
			when(store.getUserDataByUsername(username)).thenReturn(
					activeUser
					);
			
			LoginResponse resp = ls.doLogin(username, password, timestamp, finalSignature);
			
			verify(store, times(1)).getUserDataByUsername(username); 
			
			assertEquals("000", resp.getStatusCode());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("pokoknya fail");
		}  
		
	}
//	
	@SuppressWarnings("unchecked")
	@Test
	public void invalidUsername() {
		final String username=Mockito.anyString();
		
		final long timestamp = System.currentTimeMillis();
		final String plainSignature = username+password+Long.toString(timestamp)+salt;
		
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(plainSignature.getBytes(StandardCharsets.UTF_8));
			final String finalSignature = Base64.getEncoder().encodeToString(hash);
			
			when(store.getUserDataByUsername(username)).thenThrow( NoResultException.class );
			
			LoginResponse resp = ls.doLogin(username, password, timestamp, finalSignature);
			
			verify(store, times(1)).getUserDataByUsername(username);
			
			assertEquals("104", resp.getStatusCode());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("pokoknya fail");
		}
	}
//	
	@Test
	public void invalidSignature() {
		final String username="bolobolo22";
		
		final long timestamp = System.currentTimeMillis();
		final String plainSignature = username+password+"messwithsig"+Long.toString(timestamp)+salt;
		
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(plainSignature.getBytes(StandardCharsets.UTF_8));
			final String finalSignature = Base64.getEncoder().encodeToString(hash);
			
			when(store.getUserDataByUsername(username)).thenReturn(
					activeUser
					); 
			
			LoginResponse resp = ls.doLogin(username, password, timestamp, finalSignature);
			
			assertEquals("101", resp.getStatusCode());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("pokoknya fail");
		}
	}
//	
//	@Test
	public void invalidStatus() {
		final String username="bolobolo";
		final String password="tDHeJowOzbqjgiZ2RAubAtL+GsyWdvkiFy+3TYFW824=";
		final long timestamp = System.currentTimeMillis();
		final String plainSignature = username+password+Long.toString(timestamp)+salt;
		
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(plainSignature.getBytes(StandardCharsets.UTF_8));
			final String finalSignature = Base64.getEncoder().encodeToString(hash);
			
			when(store.getUserDataByUsername(username)).thenReturn(
					inactiveUser
					); 
			
			LoginResponse resp = ls.doLogin(username, password, timestamp, finalSignature);
			
			verify(store, times(1)).getUserDataByUsername(username); 
			
			assertEquals("102", resp.getStatusCode());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("pokoknya fail");
		}
	}
//	
	@Test
	public void invalidPassword() {
		final String username="bolobolo";
		final String password="tDHeJowOzbqjgiZ2RAubAtL+GsyWdvkiFy+3TYFW824=";
		final long timestamp = System.currentTimeMillis();
		final String plainSignature = username+password+Long.toString(timestamp)+salt;
		
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(plainSignature.getBytes(StandardCharsets.UTF_8));
			final String finalSignature = Base64.getEncoder().encodeToString(hash);
			
			when(store.getUserDataByUsername(username)).thenReturn(
					invalidUserPwd
					); 
			
			LoginResponse resp = ls.doLogin(username, password, timestamp, finalSignature);
			
			verify(store, times(1)).getUserDataByUsername(username); 
			
			assertEquals("103", resp.getStatusCode());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("pokoknya fail");
		}
	}
}
