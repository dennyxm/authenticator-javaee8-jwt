package org.dennysm.microservice.boundary;

public class AuthValidatorResponse extends AbstractResponse {
	
	private String token;
	
	public AuthValidatorResponse() {
		// TODO Auto-generated constructor stub
	}
	
	public AuthValidatorResponse(String statusCode, String statusMessage) {
		this.setStatusCode(statusCode);
		this.setStatusMessage(statusMessage);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	
}
