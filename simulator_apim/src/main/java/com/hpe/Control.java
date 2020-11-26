package com.hpe;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hpe.tool.Header;
import com.hpe.tool.PropertyUtil;

@RestController
public class Control {
	public static long request_count = 0l;
	private boolean performance = false;

	private java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyyMMddhhmmss");

	public Control() {
		if (PropertyUtil.getInstance().getPerformance().equalsIgnoreCase("TRUE")) {
			performance = true;
		}
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ResponseEntity<String> getE(HttpServletRequest request) {
		Map<String,String[]> map=request.getParameterMap();
		request_count = request_count + 1;
		if (!performance) {
			   System.out.println("#####################################################");
			   System.out.println(format2.format(new Date()));
			
			   for(Map.Entry<String, String[]> entry:map.entrySet()){
				    String mapKey = entry.getKey();
				    String mapValue=request.getParameter(mapKey);
				    System.out.println(mapKey+":"+mapValue);
				}
			}
		HttpHeaders headers = new HttpHeaders();
		List lst_header = PropertyUtil.getInstance().getHeader();
		for (int i = 0; i < lst_header.size(); i++) {
			Header header = (Header) lst_header.get(i);
			headers.add(header.getName(), header.getValue());
			if (!performance) {
			   System.out.println("Response Header:" + header.getName() + ":" + header.getValue());
			}
		}

		String resp = PropertyUtil.getInstance().getBody();

		if (!performance) {
			
			 System.out.println("Response Body:" + resp);
		}
		return ResponseEntity.status(200).headers(headers).body(resp);
	}
	
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ResponseEntity<String> PostE(@RequestBody String jsonParam) {
		request_count = request_count + 1;
		
		if (!performance) {
			   System.out.println("#####################################################");
			   System.out.println(format2.format(new Date()));
			   System.out.println("Request:"+jsonParam);
			}
		HttpHeaders headers = new HttpHeaders();
		List lst_header = PropertyUtil.getInstance().getHeader();
		for (int i = 0; i < lst_header.size(); i++) {
			Header header = (Header) lst_header.get(i);
			headers.add(header.getName(), header.getValue());
			if (!performance) {
			   System.out.println("Response Header:" + header.getName() + ":" + header.getValue());
			}
		}
		String resp = PropertyUtil.getInstance().getBody();

		if (!performance) {
			
			 System.out.println("Response Body:" + resp);
		}
		return ResponseEntity.status(200).headers(headers).body(resp);
	}
}
