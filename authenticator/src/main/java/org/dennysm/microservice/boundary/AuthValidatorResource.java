package org.dennysm.microservice.boundary;
 

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.dennysm.microservice.control.AuthValidatorService;

@Path("validate")
public class AuthValidatorResource {
	
	@Inject
	AuthValidatorService auth;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response validate (JsonObject js) {
		final String encryptedUserID = js.getString("userID");
		final String token = js.getString("token"); 
		
		AuthValidatorResponse result = auth.doValidate(encryptedUserID, token);
		Status status = ( result.getStatusCode().equals("000")?Response.Status.OK:Response.Status.UNAUTHORIZED);
		return Response
				.status(status)
				.entity(result)
				.build();
	}
}
