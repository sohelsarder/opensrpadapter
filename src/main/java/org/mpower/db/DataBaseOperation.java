/**
 * 
 */
package org.mpower.db;

import java.util.Date;
import java.util.List;



import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.mpower.entity.RequestLog;
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
	
	public List<RequestLog> getRlToNotify() {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from RequestLog where isNotified = 0 ");
		query.setMaxResults(500);
		List<RequestLog> requestLogs = (List<RequestLog>) query.list();
		session.close();
		return requestLogs;
	}
	
	public int updateRequestLog(int id) {
		Date nTime = new Date();
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		Query query = session.createQuery("update RequestLog set isNotified = 1 , notification_time = ? where id = ? ");
		query.setTimestamp(0, nTime);
		query.setParameter(1, id);
		
		int result = query.executeUpdate();
		t.commit();
		session.close();
		return result;	
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
