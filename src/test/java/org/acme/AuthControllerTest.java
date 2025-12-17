package org.acme;

import api.AuthController;
import dto.UserBean;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import service.AuthService;
import service.BusinessService;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
public class AuthControllerTest {

    @InjectMock
    AuthService authService;

    @InjectMock
    BusinessService businessServiceMock;


    @Inject
    AuthController authController;


    @Test
    void loginSuccess() {

        UserBean user = new UserBean();
        user.setUserName("555");
        user.setPassword("555");

        Map<String, String> tokens = Map.of(
                "accessToken", "a",
                "refreshToken", "r"
        );

        when(authService.login(any(UserBean.class))).thenReturn(tokens);

        Response response = authController.login(user);

        Map<String, Object> entity = (Map<String, Object>) response.getEntity();

        // Assert Value
        assertEquals(200, response.getStatus());
        assertTrue(entity.containsKey("accessToken"));
        assertTrue(entity.containsKey("refreshToken"));


    }


    @Test
    void loginFail() {

        UserBean user = new UserBean();
        user.setUserName("fail");
        user.setPassword("fail");

        when(authService.login(any(UserBean.class))).thenReturn(null);

        Response response = authController.login(user);

        Map<String, Object> entity = (Map<String, Object>) response.getEntity();

        // Assert Value
        assertEquals(401, response.getStatus());
        assertFalse(entity.containsKey("accessToken"));
        assertFalse(entity.containsKey("refreshToken"));
        assertEquals("Invalid username or password", entity.get("error"));


    }


    @Test
    void registerSuccess() {

        UserBean user = new UserBean();
        user.setUserName("success");
        user.setPassword("success");

        UUID mockId = UUID.randomUUID();

        when(businessServiceMock.createUser(any(UserBean.class))).thenReturn(mockId);

        Response response = authController.register(user);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getEntity());
        assertEquals(mockId, response.getEntity());
    }

    @Test
    void registerConfilct() {

        UserBean user = new UserBean();
        user.setUserName("success");
        user.setPassword("success");


        when(businessServiceMock.createUser(any(UserBean.class))).thenThrow(new PersistenceException());

        Response response = authController.register(user);

        assertEquals(409, response.getStatus());
        assertEquals("Username already exists", response.getEntity());
    }
}
