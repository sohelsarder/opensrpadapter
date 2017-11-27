/**
 * 
 */
package org.mpower.opensrpadapter;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.mpower.entity.RequestQueue;
import org.mpower.util.HibernateUtility;

/**
 * @author sohel, proshanto
 */
public class DataBaseOperation<T> {
	
	SessionFactory sessionFactory = HibernateUtility.getSessionFactory();
	
	public DataBaseOperation() {
		
	}
	
	public int save(T t) {
		int status = -1;
		Session session = sessionFactory.openSession();
		try {
			session.beginTransaction();
			session.save(t);
			session.getTransaction().commit();
			status = 1;
		}
		catch (HibernateException e) {}
		finally {
			session.close();
		}
		return status;
	}
	
	public List<RequestQueue> getAll() {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from RequestQueue");
		query.setMaxResults(500);
		List<RequestQueue> rquestQueues = (List<RequestQueue>) query.list();
		session.close();
		return rquestQueues;
	}
	
	public int delete(T t) {
		int status = -1;
		Session session = sessionFactory.openSession();
		try {
			session.beginTransaction();
			session.delete(t);
			session.getTransaction().commit();
			status = 1;
		}
		catch (HibernateException e) {}
		finally {
			session.close();
		}
		
		return status;
	}
}
