package com.hpe.scheduler;

import java.util.Date;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hpe.ICCIDController;
import com.hpe.MidServerController;
import com.hpe.PropertyUtil;

@Component
@Async
public class Scheduler {
	// private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyyMMddhhmmss");

	// @Scheduled(cron = "0 0/1 * * * ?")
	@Scheduled(fixedRate = 60000)
	public void statusCheck() {
		if (PropertyUtil.getInstance().getPERFORMANCE().equalsIgnoreCase("TRUE")) {
			// statusTask.healthCheck();
			System.out.println();
			System.out.println("#########################################################");
			System.out.println("Time:" + format2.format(new Date()));
			System.out.println("acquireConfiguration=" + MidServerController.acquireConfiguration);
			System.out.println("checkEligibility=" + MidServerController.checkEligibility);
		
			System.out.println("getSIMStatus=" + MidServerController.getSIMStatus);
			System.out.println("getEntitlement=" + MidServerController.getEntitlement);
			System.out.println("carrierSpaceUpdate=" + MidServerController.carrierSpaceUpdate);
		
			System.out.println("carrierTokens=" + ICCIDController.getICCID);
			System.out.println("#########################################################");
			// MidServerController.acquireConfiguration
		}

	}
}
