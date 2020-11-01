package com.hpe.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputRequest {
	private String url="";  // no use it
	public  List<String> jsonReq_lst=new ArrayList();   // requests of JSON
	public  List<String> getReq_lst=new ArrayList();     // requests of GET
	public  Map headers = new HashMap();                // headers
	
	private String request_type="";

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<String> getJsonReq() {
		return jsonReq_lst;
	}
	public void setJsonReq(List<String> jsonReq) {
		this.jsonReq_lst = jsonReq;
	}
	public Map getHeaders() {
		return headers;
	}
	public void setHeaders(Map headers) {
		this.headers = headers;
	}
    public void setRequest_Type(String type){
    	this.request_type=type;
    }
    public String getRequest_Type(){
    	return this.request_type;
    }
    
    
	public List<String> getGetReq() {
		return getReq_lst;
	}
	public void setGetReq(List<String> getReq_lst) {
		this.getReq_lst = getReq_lst;
	}
	
    
}
