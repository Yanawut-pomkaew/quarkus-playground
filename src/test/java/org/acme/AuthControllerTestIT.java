package org.acme;


import api.AuthController;
import dto.UserBean;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import service.BusinessService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
@TestTransaction

public class AuthControllerTestIT {

    @Inject
    BusinessService service;

    @Inject
    AuthController authController;

    @Inject
    EntityManager em;

    @Test
    void registerSuccess() {

        UserBean user = new UserBean();
        user.setUserName("success");
        user.setPassword("success");


        Response response = authController.register(user);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getEntity());

        Long count = em.createQuery(" select count(u) from User u where userName = :userName ", Long.class)
                .setParameter("userName", "success")
                .getSingleResult();

        assertEquals(1L , count);
    }


}
