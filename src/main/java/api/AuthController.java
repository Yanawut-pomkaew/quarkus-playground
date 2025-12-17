package api;

import dto.UserBean;
import io.jsonwebtoken.JwtException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service.AuthService;
import service.BusinessService;
import service.TokenService;

import java.util.Map;
import java.util.UUID;

@Path("/auth/token")
@ApplicationScoped
public class AuthController {

    @Inject
    TokenService tokenService;

    @Inject
    BusinessService service;

    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(UserBean bean) {

        Map<String, String> tokens = authService.login(bean);

        if(tokens == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "346346333333333333333333"))
                    .build();
        }

        return Response.ok(tokens).build();
    }

    @POST
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(UserBean bean) {
        try {
            UUID id = service.createUser(bean);
            return Response.status(Response.Status.CREATED).entity(id).build();
        } catch (PersistenceException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Username already exists").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Cannot create user").build();
        }
    }

    @POST
    @Path("/refresh")
    public Response refreshToken(@HeaderParam("Authorization") String refreshTokenHeader) {
        if (refreshTokenHeader == null || !refreshTokenHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Missing refresh token").build();
        }

        String refreshToken = refreshTokenHeader.substring("Bearer ".length());

        try {
            String newAccessToken = tokenService.refreshAccessToken(refreshToken);
            return Response.ok().entity(newAccessToken).build();
        } catch (JwtException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid refresh token").build();
        }
    }

}
