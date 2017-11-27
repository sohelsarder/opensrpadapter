package opensrpadapter;

import org.springframework.context.annotation.Configuration;

import junit.framework.TestCase;

@Configuration

public class AppTest extends TestCase {
	
	public void testApp() {
		/*		SessionFactory sessionFactory = HibernateUtility.getSessionFactory();
				Session session = sessionFactory.openSession();
				session.beginTransaction();
		
				RequestLog form = new RequestLog();
				form.setEntity_id("entity_id");
				session.save(form);
		
				session.getTransaction().commit();
				session.close();*/
		/*		String s1 = "test1:test2";
				String[] s2 = s1.split(":");
				System.out.println(s2[1]);*/
		
		/*	HTTPAgent httpagent = new HTTPAgent();
			httpagent.fetch("http://192.168.23.13:9876/adapter?request_id=124&entity_id=e123&status=S");*/
		
	}
	
}
