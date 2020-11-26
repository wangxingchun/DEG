package com.hpe.scheduler;

import java.util.Date;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hpe.Control;
import com.hpe.tool.PropertyUtil;


@Component
@Async
public class Scheduler {
	// private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyyMMddhhmmss");

	// @Scheduled(cron = "0 0/1 * * * ?")
	@Scheduled(fixedRate = 60000)
	public void statusCheck() {
		if (PropertyUtil.getInstance().getPerformance().equalsIgnoreCase("TRUE")) {
			// statusTask.healthCheck();
			System.out.println();
			System.out.println("#########################################################");
			System.out.println("Time:" + format2.format(new Date()));
			System.out.println("Request Count=" + Control.request_count);
			
			System.out.println("#########################################################");
			// MidServerController.acquireConfiguration
		}

	}
}
