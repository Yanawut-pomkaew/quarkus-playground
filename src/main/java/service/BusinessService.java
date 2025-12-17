package service;

import dto.UserBean;
import entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.UUID;

@ApplicationScoped
public class BusinessService {

    @Inject
    EntityManager entityManager;


    public String getInfo(String userName , String role) {


        return userName + " : " + role;
    }

    public String getAllUsersInfo() {
        return "ข้อมูลของทุกคนอยู่ที่นี่";
    }


    @Transactional
    public UUID createUser(UserBean bean) {

        User entity = new User();
        entity.setUserName(bean.getUserName());
        entity.setPassword(AuthService.hashPassword(bean.getPassword()));

        entityManager.persist(entity);

        return entity.getId();
    }
}
