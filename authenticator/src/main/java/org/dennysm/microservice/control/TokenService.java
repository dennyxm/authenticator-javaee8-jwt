package org.dennysm.microservice.control;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Singleton;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Singleton
public class TokenService {
	
	private Logger logger = LoggerFactory.getLogger(TokenService.class);
	
	@Inject
	@ConfigProperty(name = "application.token-secret", defaultValue = "secret")
	private String tokenSecret;
	
	@Inject
	@ConfigProperty(name = "application.token-duration-in-minutes", defaultValue = "10")
	private String tokenDuration;
	
	// Generate
	public String generateToken(
				final String encryptedUserID,
				final String fullname,
				long timestamp 
			) {
		String token="";
		try {
			LocalDateTime dateTime = LocalDateTime.now().plus(Duration.of( Integer.parseInt(tokenDuration) , ChronoUnit.MINUTES));
		    Date expiresAt = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
			
		    Algorithm algorithm = Algorithm.HMAC256(tokenSecret+encryptedUserID+timestamp);
		     token = JWT.create()
		    	.withClaim("timestamp", timestamp)	 
		    	.withClaim("fullname", fullname)
		    	.withClaim("userID", encryptedUserID)
		        .withIssuer(TokenService.class.getName())
		        .withExpiresAt(expiresAt)
		        .sign(algorithm);
		} catch (JWTCreationException exception){
		    //Invalid Signing configuration / Couldn't convert Claims.
		}
		return token;
	}
	
	// validate
	public boolean isValid( 
			final String encryptedUserID, // disupply oleh user
			final String fullname, // ini disupply oleh validator service
			final String token // disupply user
			) {
		/*
		 * valid itu 
		 * 1. tida expire 
		 * 2. isi userid & fullname nya cocok
		 * 3. tida ada tanda2 manipulasi alias signature nya valid
		 */
		DecodedJWT jwt;
		try {
			jwt = JWT.decode(token);
			
			String claimUserID = jwt.getClaim("userID").asString();
			if(!encryptedUserID.equals(claimUserID)) {
				logger.warn("incoming userid not equal with userid in token");
				return false;
			}
			
			String claimFullname = jwt.getClaim("fullname").asString();
			if(!fullname.contentEquals(claimFullname)) {
				logger.warn("incoming fullname not equal with fullname in token");
				return false;
			}
			
			Date expiration = jwt.getExpiresAt();
			Date current = Calendar.getInstance().getTime();
			if( current.after(expiration) ) {
				logger.warn("incoming token is already expired");
				return false;
			} 
		} catch ( Exception e) {
			e.printStackTrace();
			logger.warn("unable to decode incoming token");
			return false;
		}
		
		try {
			long claimTimestamp = jwt.getClaim("timestamp").asLong();
			
			DecodedJWT jwt2 = JWT.require(Algorithm.HMAC256(tokenSecret+encryptedUserID+claimTimestamp))
	                .build()
	                .verify(token); 
			
			if(!jwt2.getSignature().contentEquals(jwt.getSignature())) {
				logger.warn("incorrect signature");
				return false;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn("unable to validate token signature");
			return false;
		}
		
		
		
		return true;
	}
}
