package org.lambert.salesnet.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

/**
 * <p>
 * <b> Persistence service. </b>
 * </p>
 */
public class PersistenceService {

    /**
     * entity manager factory.
     */
    private final EntityManagerFactory emf;

    public PersistenceService() {
        this.emf = Persistence.createEntityManagerFactory(System.getProperty("user.dir") + "/db/salesNet.odb");
    }

    /**
     * 
     * <p>
     * <b> Save objects. </b>
     * </p>
     * 
     * @param objects
     */
    public void saveObjects(final Object[] objects) {
        final EntityManager em = this.emf.createEntityManager();
        em.getTransaction().begin();
        for (Object obj : objects) {
            em.persist(obj);
        }
        em.getTransaction().commit();
        em.close();
    }

    /**
     * 
     * <p>
     * <b> queryObjects. </b>
     * </p>
     * 
     * @param object
     * @param objectClass
     * @return
     */
    public <T> List<T> queryObjects(final T object, final Class<T> objectClass) {
        final EntityManager em = this.emf.createEntityManager();
        final String simpleName = object.getClass().getSimpleName();
        TypedQuery<T> query = em.createQuery("SELECT obj FROM " + simpleName + " obj", objectClass);
        List<T> results = query.getResultList();
        em.close();
        return results;
    }

    /**
     * 
     * <p>
     * <b> find Object. </b>
     * </p>
     * 
     * @param objectClass
     * @param primaryKey
     * @return
     */
    public <T> T findObject(final Class<T> objectClass, final long primaryKey) {
        final EntityManager em = this.emf.createEntityManager();
        T result = em.find(objectClass, primaryKey);
        em.close();
        return result;
    }

    /**
     * 
     * <p>
     * <b> Delete Objects. </b>
     * </p>
     * 
     * @param objects
     */
    public void deleteObjects(final Object[] objects) {
        final EntityManager em = this.emf.createEntityManager();
        em.getTransaction().begin();
        for (Object obj : objects) {
            em.remove(obj);
        }
        em.getTransaction().commit();
        em.close();
    }
}
