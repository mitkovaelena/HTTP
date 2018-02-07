package database.repositories;

import database.models.User;

import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import java.util.List;

public class UserRepository extends BaseRepository {
    public UserRepository(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    private boolean create(String username, String password) {
        this.entityManager.persist(new User(username, password));
        return true;
    }

    private User findById(String userId) {
        User resultingObject = null;

        resultingObject = (User) this.entityManager
                .createNativeQuery("SELECT * FROM users as u WHERE u.id = \'" + userId + "\'", User.class)
                .getSingleResult();

        return resultingObject;
    }

    private User findByEmail(String username) {
        User resultingObject = null;

        resultingObject = (User) this.entityManager
                .createNativeQuery("SELECT * FROM users as u WHERE u.email = \'" + username + "\'", User.class)
                .getSingleResult();

        return resultingObject;
    }

    private User[] findAll() {
        List<User> resultList = null;

        resultList = this.entityManager
                .createNativeQuery("SELECT * FROM users", User.class)
                .getResultList();

        return resultList.toArray(new User[resultList.size()]);
    }
}
