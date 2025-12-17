package filter;

import dto.LoginEvent;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Provider;

import java.time.Instant;

@Provider
@Priority(Priorities.USER)
public class LoginLoggingFilter implements ContainerResponseFilter {

    @Context
    HttpServletRequest request;

    @Inject
    UserContext userContext;

    @Override
    public void filter(ContainerRequestContext reqCtx, ContainerResponseContext resCtx) {
//        if(reqCtx.getUriInfo().getPath().equals("auth/token/login")) {
//            boolean success = resCtx.getStatus() == 200;
//
//            LoginEvent event = new LoginEvent();
//            event.setUserName(userContext.getUserName());
//            event.setSuccess(success);
//            event.setIp(request.getRemoteAddr());
//            event.setTimestamp(Instant.now());
//
//            producer.sendEvent(event);
//        }
    }
}
