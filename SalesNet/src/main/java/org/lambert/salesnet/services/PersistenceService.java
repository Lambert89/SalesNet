package org.lambert.salesnet.services;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.lambert.salesnet.beans.DocumentTracker;
import org.springframework.beans.factory.InitializingBean;

/**
 * <p>
 * <b> Persistence service. </b>
 * </p>
 */
public class PersistenceService implements InitializingBean {

	/**
	 * entity manager factory.
	 */
	private final EntityManagerFactory emf;

	public PersistenceService() {
		this.emf = Persistence.createEntityManagerFactory(System
				.getProperty("user.dir") + "/db/salesNet.odb");
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

	public void updateObjects(final UpdateOperation updateOperation)
			throws Exception {
		final EntityManager em = this.emf.createEntityManager();
		em.getTransaction().begin();
		updateOperation.update();
		em.getTransaction().commit();
		em.close();
	}

	/**
	 * 
	 * <p>
	 * <b> queryObjects. </b>
	 * </p>
	 * 
	 * @param objectClass
	 * @return
	 */
	public <T> List<T> queryObjects(final Class<T> objectClass) {
		final EntityManager em = this.emf.createEntityManager();
		final String simpleName = objectClass.getSimpleName();
		TypedQuery<T> query = em.createQuery("SELECT obj FROM " + simpleName
				+ " obj", objectClass);
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
	public <T> void deleteObjects(final Class<T> objectClass,
			final long[] primaryKeys) {
		final EntityManager em = this.emf.createEntityManager();
		em.getTransaction().begin();
		for (final long primaryKey : primaryKeys) {
			final Object obj = em.find(objectClass, primaryKey);
			em.remove(obj);
		}
		em.getTransaction().commit();
		em.close();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.findObject(DocumentTracker.class, 1L) == null) {
			this.saveObjects(new Object[] { new DocumentTracker() });
		}
	}

	public static interface UpdateOperation {
		public void update() throws Exception;
	}
}
