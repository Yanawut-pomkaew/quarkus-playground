package filter;

import io.jsonwebtoken.*;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.lang.reflect.Method;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtFilter implements ContainerRequestFilter {

    @ConfigProperty(name = "jwt.secret")
    String secret;

    @Context
    ResourceInfo resourceInfo;

    @Inject
    UserContext userContext;

    @Override
    public void filter(ContainerRequestContext ctx) {

        Method method = resourceInfo.getResourceMethod();

        if (method.getAnnotation(RequireToken.class) == null && method.getDeclaringClass().getAnnotation(RequireToken.class) == null) {
            return;
        }

        String auth = ctx.getHeaderString("Authorization");

        if (auth == null || !auth.startsWith("Bearer ")) {
            abort(ctx, "Missing token");
            return;
        }

        String token = auth.substring("Bearer ".length());

        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secret.getBytes())
                    .build()
                    .parseClaimsJws(token);

            // ดึงค่า user id และ role ใส่ context เผื่อ service ใช้
            String userId = claims.getBody().getSubject();
            String role = claims.getBody().get("role", String.class);
            String userName = claims.getBody().get("name", String.class);

            RequireRole requireRole = method.getAnnotation(RequireRole.class);
            if(requireRole == null) {
                requireRole = method.getDeclaringClass().getAnnotation(RequireRole.class);
            }

            if(requireRole != null) {
                String requiredRole = requireRole.value();
                if(!role.equals(requiredRole)) {
                    abort(ctx, "Permission denied.");
                    return;
                }
            }

            userContext.setRole(role);
            userContext.setUserId(userId);
            userContext.setUserName(userName);


        } catch (ExpiredJwtException e) {
            abort(ctx, "Token expired");
        } catch (JwtException e) {
            abort(ctx, "Invalid token");
        }
    }

    private void abort(ContainerRequestContext ctx, String msg) {
        ctx.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity(msg)
                        .build()
        );
    }
}
