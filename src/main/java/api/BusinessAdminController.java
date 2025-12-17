package api;


import filter.RequireRole;
import filter.RequireToken;
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


@Path("/api/adm")
@RequireToken
@RequireRole("ADMIN")
@ApplicationScoped
public class BusinessAdminController {

    @Inject
    BusinessService service;


    @POST
    @Path("/getAllUsersInfo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String getAllUsersInfo(@Context ContainerRequestContext ctx) {

        return service.getAllUsersInfo();
    }
}
