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

import org.dennysm.microservice.control.LoginService;

@Path("login")
public class LoginResource {
	
	@Inject
	LoginService ls;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(JsonObject js) {
		try {
			final String username = js.getString("username");
			final String password = js.getString("password");
			final long timestamp = Long.parseLong(js.getString("timestamp"));
			final String signature = js.getString("signature");
			
			LoginResponse result = ls.doLogin(username, password, timestamp, signature);
			Status status = ( result.getStatusCode().equals("000")?Response.Status.OK:Response.Status.UNAUTHORIZED);
			return Response
					.status(status)
					.entity(result)
					.build();
		} catch (Exception e) {
			return Response
					.status(Response.Status.BAD_REQUEST)
					.entity(new LoginResponse("999", "Invalid Request"))
					.build();
		}
		
	}
}
