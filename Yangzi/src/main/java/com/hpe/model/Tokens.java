package com.hpe.model;

import java.io.Serializable;

public class Tokens implements Serializable{
private String imsi;
private int mdn;
private String uniqueid;
private String apptoken;
private String subtoken;
public String getImsi() {
	return imsi;
}
public void setImsi(String imsi) {
	this.imsi = imsi;
}
public int getMdn() {
	return mdn;
}
public void setMdn(int mdn) {
	this.mdn = mdn;
}
public String getUniqueid() {
	return uniqueid;
}
public void setUniqueid(String uniqueid) {
	this.uniqueid = uniqueid;
}
public String getApptoken() {
	return apptoken;
}
public void setApptoken(String apptoken) {
	this.apptoken = apptoken;
}
public String getSubtoken() {
	return subtoken;
}
public void setSubtoken(String subtoken) {
	this.subtoken = subtoken;
}

@Override
public String toString(){
	return "imsi:"+imsi+" token="+subtoken;
}
}
