package org.mpower.opensrpadapter;

import java.util.Date;
import java.util.List;

import org.mpower.db.DataBaseOperation;
import org.mpower.entity.RequestLog;
import org.mpower.form.SubmissionBuilder;
import org.mpower.http.HTTPAgent;
import org.mpower.properties.AdapterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@EnableScheduling
@ComponentScan(basePackageClasses = { SubmissionBuilder.class,
		AdapterProperties.class })
public class Notifier {

	@Autowired
	private AdapterProperties adapterProperties;

	@Autowired
	private SubmissionBuilder submissionBuilder;

	private static final int listenerDelay = 6000;

	public Notifier() {

	}

	private static final Logger logger = LoggerFactory
			.getLogger(Notifier.class);

	@Scheduled(fixedDelay = listenerDelay)
	@Transactional
	public void scheduleFixedDelayTask() {

		boolean isFailure = false;

		int successCount = 0;
		List<RequestLog> requestLogs = new DataBaseOperation<RequestLog>()
				.getRlToNotify();
		logger.debug("total number of requestLog to notify: "
				+ requestLogs.size());

		for (RequestLog requestLog : requestLogs) {
			Date startNotify = new Date();
			HTTPAgent httpagent = new HTTPAgent();
			isFailure = httpagent.fetch(
					adapterProperties.getNotificationURL()
							+ "?request_id="
							+ requestLog.getReqeust_id() 
							+ "&entity_id="
							+ requestLog.getEntity_id() 
							+ "&relational_id="
							+ requestLog.getRelational_id() 
							+ "&status="
							+ requestLog.getStatus()).isFailure();
			if (isFailure != false) {
				logger.error("could not notify requestId:"
						+ requestLog.getReqeust_id());

			} else {

				int i = new DataBaseOperation<RequestLog>()
						.updateRequestLog(requestLog.getId());
				successCount++;
				Date finishNotify = new Date();
				logger.info("requestID : " 
						+ requestLog.getReqeust_id()
						+ " start_notify : " 
						+ startNotify
						+ " finish_notify : " 
						+ finishNotify);
				//check user
			}
		}

		logger.info("Total Notified: " + successCount + " / "
				+ requestLogs.size());
	}

}
