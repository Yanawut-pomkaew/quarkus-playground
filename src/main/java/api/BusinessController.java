package api;

import filter.RequireToken;
import filter.UserContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import service.BusinessService;

@Path("/api")
@RequireToken
@ApplicationScoped
public class BusinessController {

    @Inject
    BusinessService service;


    @Inject
    UserContext userContext;


    @POST
    @Path("/getInfo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getInfo() {
        String role = userContext.getRole();
        String userName = userContext.getUserName();

        return service.getInfo(userName , role);
    }



}
