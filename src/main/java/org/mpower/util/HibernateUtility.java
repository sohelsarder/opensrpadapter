/**
 * 
 */
package org.mpower.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * @author proshanto
 *
 */
public class HibernateUtility {
	public static SessionFactory factory;	
	 
	    private HibernateUtility() {
	    }	
	 
	    public static synchronized SessionFactory getSessionFactory() {
	 
	        if (factory == null) {
	            factory = new Configuration().configure("hibernate.cfg.xml").
	                    buildSessionFactory();
	        }
	        return factory;
	    }
}
