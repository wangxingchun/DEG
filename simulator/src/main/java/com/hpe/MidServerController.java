package com.hpe;

import java.util.Date;
import java.util.Iterator;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RestController
public class MidServerController {
	public static long checkEligibility=0l;
	public static long acquireConfiguration=0l;
	public static long getSIMStatus=0l;
	public static long getEntitlement=0l;
	public static long carrierSpaceUpdate=0l;

	private java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyyMMddhhmmss");
	private boolean performance= false;
	
	public MidServerController() {
		if(PropertyUtil.getInstance().getPERFORMANCE().equalsIgnoreCase("TRUE")) {
			performance=true;
		}
	}

	// checkEnglibility
	@ResponseBody
	@RequestMapping(value = "/checkEligibility", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String docheckEligibility(@RequestBody JSONObject jsonParam) {
		String IMSI = (String) jsonParam.get("imsi-num");
		checkEligibility=checkEligibility+1;
		if(!performance) {
			System.out.println(format2.format(new Date()) + ":checkEligibility:IMSI=" + IMSI+":Count="+checkEligibility);
		}
		String resp = "{\"OperationResult\":1,\"MSISDN\":\"01012345678\",\"CompanionAppEligibility\":1,\"CompanionDeviceServices\":\"\"}";

		return resp;

	}

	// AcureConfiguration
	@ResponseBody
	@RequestMapping(value = "/acquireConfiguration", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String doAccureConfiguration(@RequestBody JSONObject jsonParam) {
		String IMSI = (String) jsonParam.get("imsi-num");
		acquireConfiguration=acquireConfiguration+1;
		if(!performance) {
		  System.out.println(format2.format(new Date()) + ":AccureConfiguration:IMSI=" + IMSI+"：Count="+acquireConfiguration);
		}
		String resp = "{\"OperationResult\":1,\"CompanionConfigurations\":[{\"ICCID\":\"8982051801000011361\",\"CompanionDeviceService\":\"SharedNumber\",\"ServiceStatus\":\"1\",\"CallForkStatus\":\"2\"}]} ";
		return resp;

	}

	// getSIMStatus
	@ResponseBody
	@RequestMapping(value = "/getSIMStatus", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String dogetSIMStatus(@RequestBody JSONObject jsonParam) {
		String IMSI = (String) jsonParam.get("imsi-num");
		String request_id = (String) jsonParam.get("request-id");
		getSIMStatus=getSIMStatus+1;
		if(!performance) {
		   System.out.println(format2.format(new Date()) + ":getSIMStatus:IMSI=" + IMSI+":Count="+getSIMStatus);
		}
		String resp = "{\"response-id\":" + request_id
				+ ",\"status\":6000,\"secondary-devices-paired\":[{\"eid\":\"89049032003008882300000402618477\",\"iccid\":\"8982051801000011361\",\"activation-status\":\"Active\"}]}";

		return resp;

	}

	// getEntitlement
	@ResponseBody
	@RequestMapping(value = "/getEntitlement", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String dogetEntitlement(@RequestBody JSONObject jsonParam) {
		String IMSI = (String) jsonParam.get("imsi-num");
		getEntitlement=getEntitlement+1;
		if(!performance) {
			System.out.println(format2.format(new Date()) + ":getEntitlement:IMSI=" + IMSI+"：Count="+getEntitlement);
		}
		String request_id = (String) jsonParam.get("request-id");

		String resp = "{\"response-id\":" + request_id + ",\"status\":6000,\"response\":[";

		JSONArray jsonArray = (JSONArray) jsonParam.get("entitlement-names");
		Iterator<String> iterator = jsonArray.iterator();
		String tmp = "";
		while (iterator.hasNext()) {
			String entitlement_name = iterator.next();
			String entitlement_status = "6100";
			tmp = tmp + ",{\"entitlement-name\":\"" + entitlement_name + "\",\"entitlement-status\":"
					+ entitlement_status + "}";
		}

		// tmp.replace(",", "");
		tmp = tmp.replaceFirst(",", "");
		tmp = tmp + "]}";
		resp = resp + tmp;
		return resp;
	}

	// carrierSpaceUpdate
	@ResponseBody
	@RequestMapping(value = "/carrierSpaceUpdate", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String getUsage(@RequestBody JSONObject jsonParam) {
		carrierSpaceUpdate=carrierSpaceUpdate+1;
		String IMSI = (String) jsonParam.get("imsi-num");
		if(!performance) {
		    System.out.println(format2.format(new Date()) + ":carrierSpaceUpdate:IMSI=" + IMSI+":Count="+carrierSpaceUpdate);
		}
		String request_id = (String) jsonParam.get("request-id");

		String jsonStr = "{" + 
				"    \"response-id\": "+request_id+"," + 
				"    \"status\": 6000," + 
				"    \"carrier-plans\": {" + 
				"        \"plans-list\": [" + 
				"            {" + 
				"                \"plan-category\": 1," + 
				"                \"plan-group-options\": [" + 
				"                    {" + 
				"                        \"plan-id\": \"Group1\"," + 
				"                        \"plan-label\": \"LTE plane\"," + 
				"                        \"plan-value\": \"200G\"," + 
				"                        \"plan-subscribed\": \"subscribed\"," + 
				"                        \"plan-purchasable\": false" + 
				"                    }" + 
				"                ]" + 
				"            }" + 
				"        ]," + 
				"        \"more-plans-url\": \"http://m.tworld.co.kr/tglink.jsp?urlname=mTG_1401\"" + 
				"    }," + 
				"    \"network-usage\": {" + 
				"        \"account-metrics\": [" + 
				"            {" + 
				"                \"network-usage-label\": \"simul01\"," + 
				"                \"local-device-metrics\": true," + 
				"                \"subscription-status\": \"subscribed\"," + 
				"                \"plan-type\": \"postpaid\"," + 
				"                 \"billing-monthly-cycle\":\"true\","+
				"                \"billing-cycle-ends-date\": \"2020-09-30T14:59:59Z\"," + 
				"                \"applicable-plans\": [" + 
				"                    {" + 
				"                        \"plan-label\": \"DATA1\"," + 
				"                        \"current-used-plan\": true," + 
				"                        \"data-measure-unit\": \"KB\"," + 
				"                        \"data-capacity\": \"2549999999984\"," + 
				"                        \"data-used\": \"1049999999984\"," + 
				"                        \"current-used-plan\":\"true\","+
				"                        \"messages-capacity\": \"limited\"," + 
				"                        \"max-data-before-throttling\":\"\","+
				"                        \"messages-used\": \"0\"," + 
				"                        \"voice-minutes-capacity\": \"200\"," + 
				"                        \"voice-minutes-used\": \"15\"," + 
				"                        \"last-updated-at\": \"2020-09-11T14:05:37Z\"" + 
				"                    }," + 
				"                    {" + 
				"                        \"plan-label\": \"DATA2\"," + 
				"                        \"current-used-plan\": true," + 
				"                        \"data-measure-unit\": \"KB\"," + 
				"                        \"data-capacity\": \"1048576\"," + 
				"                        \"data-used\": \"367761\"," + 
				"                        \"last-updated-at\": \"2020-09-11T14:05:37Z\"" + 
				"                    }," + 
				"                    {" + 
				"                        \"plan-label\": \"DATA3\"," + 
				"                        \"current-used-plan\": true," + 
				"                        \"data-measure-unit\": \"KB\"," + 
				"                        \"data-capacity\": \"1048576\"," + 
				"                        \"data-used\": \"0\"," + 
				"                        \"last-updated-at\": \"2020-09-11T14:05:37Z\"" + 
				"                    }" + 
				"                ]" + 
				"            }" + 
				"        ]" + 
				"    }," + 
				"    \"carrier-apps\": {" + 
				"        \"apps-list\": [" + 
				"            \"6BMWJ35A44.com.sktelecom.miniTworld\"" + 
				"        ]," + 
				"        \"apps-url\":\"https://itunes.apple.com/kr/developer/sk-telecom/id329992155?l=kr\"" + 
				"    }," + 
				"    \"background-refresh-interval-hrs\": \"1\"" + 
				"}";
		
		return jsonStr;
	}
}
