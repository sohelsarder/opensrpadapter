package org.mpower.opensrpadapter;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionFactoryConfig {
	private SessionFactoryConfig(){
	}
	public static SessionFactory getSessionFactory(){
		SessionFactory sessionFactory = new Configuration().configure()
				.buildSessionFactory();
		return sessionFactory;
		
		
	}

}
