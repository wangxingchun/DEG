package com.hpe.model;

//it is for replace the variable in the HTTP Request
public class Variable {
	private String imsi = "";
	private String unique = "";
	private String token ="";

	public void setIMSI(String imsi) {
		this.imsi = imsi;
	}

	public String getIMSI() {
		return this.imsi;
	}

	public void setUNIQUEID(String uniqueid) {
		this.unique = uniqueid;
	}

	public String getUNIQUEID() {
		return this.unique;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
