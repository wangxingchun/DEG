package com.hpe;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ICCIDController {
	public static long getICCID = 0l;
	private boolean performance = Boolean.valueOf(PropertyUtil.getInstance().getPERFORMANCE());

	private java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyyMMddhhmmss");

	public ICCIDController() {
		if (PropertyUtil.getInstance().getPERFORMANCE().equalsIgnoreCase("TRUE")) {
			performance = true;
		}
	}
	
	

	@GetMapping("/getICCID")
	public String getICCID(@RequestParam Map<String, Object> params) {
		String RESPCODE = PropertyUtil.getInstance().getRESPCODE();
		String RESPMSG = PropertyUtil.getInstance().getRESPMSG();
		String ICCID = "";

		Iterator<Map.Entry<String, Object>> entries = params.entrySet().iterator();
		System.out.print(format2.format(new Date()) + " Request Parameters:");

		while (entries.hasNext()) {
			Map.Entry<String, Object> entry = entries.next();
			String key = entry.getKey();
			String value = (String) entry.getValue();
			System.out.print(entry.getKey() + ":" + entry.getValue() + "  ");
			if (key.equals("IMSI")) {
				ICCID = value;
			}
		}
		if (ICCID.length() > 5) {
			ICCID = ICCID + PropertyUtil.getInstance().getICCID();
		} else {
			RESPCODE = "01";
			RESPMSG = "Query backend System failure";
		}
		String Response = "RESPCODE=" + RESPCODE + "&RESPMSG=" + RESPMSG + "&ICCID=" + ICCID;
		System.out.print("Responses:" + Response);
		System.out.println();
		return Response;
	}

	@GetMapping("/NIF/CASInterface.jsp")
	public String getICCIDInfo(@RequestParam Map<String, Object> params) {
		getICCID = getICCID + 1;
		String RESPCODE = PropertyUtil.getInstance().getRESPCODE();
		String RESPMSG = PropertyUtil.getInstance().getRESPMSG();
		String ICCID = "";
		Iterator<Map.Entry<String, Object>> entries = params.entrySet().iterator();
		if (!performance) {
			System.out.print(format2.format(new Date()) + " Request Parameters:");
		}

		while (entries.hasNext()) {
			Map.Entry<String, Object> entry = entries.next();
			String key = entry.getKey();
			String value = (String) entry.getValue();
			if (!performance) {
				System.out.print(entry.getKey() + ":" + entry.getValue() + "  ");
			}
			if (key.equals("IMSI")) {
				ICCID = value;
			}
		}
		if (ICCID.length() > 5) {
			ICCID = ICCID + PropertyUtil.getInstance().getICCID();
		} else {
			RESPCODE = "01";
			RESPMSG = "Query backend System failure";
		}
		String Response = "RESPCODE=" + RESPCODE + "&RESPMSG=" + RESPMSG + "&USIM_ICCID_NO=" + ICCID;

		if (!performance) {
			System.out.print("Responses:" + Response);
		}

		System.out.println();

		return Response;
	}

}