package org.dennysm.microservice.boundary;

public class AuthValidatorResponse extends AbstractResponse {
	
	private String token;
	
	public AuthValidatorResponse() {
		// TODO Auto-generated constructor stub
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	
}
