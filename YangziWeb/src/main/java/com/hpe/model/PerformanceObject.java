package com.hpe.model;

public class PerformanceObject {
	String tps="1";
	String duratin="1";
	
	
	String start_time="0";
	String end_time="0";
	String current_time="0";
	String total_tasks="0";
	String current_threads="0";
	String current_tasks="0";
	String average_response="0";
	String thread_failure="0";
	String response_failure="0";
	String messages="";
	String percent="0";    // end_time - start/current_time*100 (1-100)
	String stop_flag="0";  // 0: continue 1: stop
	
	
	public String getPercent() {
		return percent;
	}
	public void setPercent(String percent) {
		this.percent = percent;
	}
	public String getStop_flag() {
		return stop_flag;
	}
	public void setStop_flag(String stop_flag) {
		this.stop_flag = stop_flag;
	}
	public String getAverage_response() {
		return average_response;
	}
	public void setAverage_response(String average_response) {
		this.average_response = average_response;
	}
	
	public String getMessages() {
		return messages;
	}
	public void setMessages(String messages) {
		this.messages = messages;
	}
	public String getTps() {
		return tps;
	}
	public void setTps(String tps) {
		this.tps = tps;
	}
	public String getDuratin() {
		return duratin;
	}
	public void setDuratin(String duratin) {
		this.duratin = duratin;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getCurrent_time() {
		return current_time;
	}
	public void setCurrent_time(String current_time) {
		this.current_time = current_time;
	}
	public String getTotal_tasks() {
		return total_tasks;
	}
	public void setTotal_tasks(String total_tasks) {
		this.total_tasks = total_tasks;
	}
	public String getCurrent_threads() {
		return current_threads;
	}
	public void setCurrent_threads(String current_threads) {
		this.current_threads = current_threads;
	}
	public String getCurrent_tasks() {
		return current_tasks;
	}
	public void setCurrent_tasks(String current_tasks) {
		this.current_tasks = current_tasks;
	}
	public String getThread_failure() {
		return thread_failure;
	}
	public void setThread_failure(String thread_failure) {
		this.thread_failure = thread_failure;
	}
	public String getResponse_failure() {
		return response_failure;
	}
	public void setResponse_failure(String response_failure) {
		this.response_failure = response_failure;
	}
}
