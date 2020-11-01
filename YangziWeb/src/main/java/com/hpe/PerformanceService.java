package com.hpe;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.hpe.model.PerformanceObject;

@Service
public class PerformanceService {
	
	private  String process_percent="0";
	private  int tps=1;
	private  int duration=1*60*1000;   // minutes
	private  long start_time=0l;
	private  long end_time=0l;
	PerformanceObject obj=null;
	
	private  int running_flag=1; // 0:running 1:stopped
    
	// return performance run percent
	private String getProcess_percent() {
		long current_time=System.currentTimeMillis();
		long spend_time=current_time-start_time;
		
		if(spend_time <= duration) {
			DecimalFormat df = new DecimalFormat("0");
			process_percent = df.format(((float)spend_time/duration)*100);
			
		}else {
			this.running_flag=1;       // set stop performance
			process_percent="100";
		}
		return process_percent;
	}
	
	// collect performance status
	public PerformanceObject getPerformanceStatus() {
		
		 PerformanceObject p_status=new PerformanceObject();
		 
		 if(this.running_flag == 0) {
			 p_status.setPercent(getProcess_percent());
			 p_status.setMessages("We are running performance testing");
			 p_status.setCurrent_tasks("100");
			 p_status.setCurrent_threads("20");
			 p_status.setCurrent_tasks("5");
			 p_status.setAverage_response("10");
			 p_status.setThread_failure("1");
			 p_status.setResponse_failure("1");

		 }else {
			 p_status.setMessages("Performance is already stopped");
			 p_status.setStop_flag("1");
		 }
		 
			
		return p_status;
	}
	
	public PerformanceObject StartPerformance(String tps,String duration) {
		PerformanceObject p_status=new PerformanceObject();
		this.tps=Integer.valueOf(tps);
		this.duration=Integer.valueOf(duration)*60*1000;
		this.running_flag=0; // set running flag
		
		
		this.start_time=System.currentTimeMillis();
		this.end_time=this.start_time+this.duration;
		
        SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddHHmmss");  
		java.util.Date date = new Date(start_time); 
		java.util.Date date1 = new Date(end_time);
		p_status.setEnd_time(sdf.format(date1));
		p_status.setStart_time(sdf.format(date));
		p_status.setPercent(getProcess_percent());
		
		p_status.setMessages("We are starting performance test");
		p_status.setCurrent_tasks("100");
		p_status.setCurrent_threads("20");
		p_status.setCurrent_tasks("5");
		p_status.setAverage_response("10");
		p_status.setThread_failure("1");
		p_status.setResponse_failure("1");
		p_status.setStop_flag("0");
		
		return p_status;
	}
	
	public PerformanceObject StopPerformance() {
		 this.running_flag=1; // set performance stopped
		 PerformanceObject p_status=new PerformanceObject();
		 p_status.setPercent(getProcess_percent());
		 p_status.setMessages("We arestopping performance testing");
		 p_status.setCurrent_tasks("100");
		 p_status.setCurrent_threads("20");
		 p_status.setCurrent_tasks("5");
		 p_status.setAverage_response("10");
		 p_status.setThread_failure("1");
		 p_status.setResponse_failure("1");
		 
		 p_status.setStop_flag("1");
		
		return p_status;
	}
	
}
