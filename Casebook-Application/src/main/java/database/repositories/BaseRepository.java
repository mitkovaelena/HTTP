package database.repositories;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

abstract class BaseRepository implements Repository {
    private EntityManagerFactory entityManagerFactory;

    private Map<String, Method> methods;

    protected EntityManager entityManager;

    protected BaseRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
        this.entityManager = this.entityManagerFactory.createEntityManager();
        this.initializeMethods();
    }

    private void initializeMethods() {
        this.methods = new HashMap<>();

        for (Method method : this.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            this.methods.putIfAbsent(method.getName(), method);
        }
    }

    public Object doAction(String action, Object... args) {
        EntityTransaction transaction = null;
        Object result = null;

        try {
            transaction = this.entityManager.getTransaction();
            transaction.begin();

            result = this.methods.get(action).invoke(this, args);

            transaction.commit();
        } catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        return result;
    }

    public void dismiss() {
        this.entityManager.close();
    }
}
