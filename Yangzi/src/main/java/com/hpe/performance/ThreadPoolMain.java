package com.hpe.performance;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import com.hpe.kit.PropertyUtil;
import com.hpe.model.AssignWeight;
import com.hpe.model.InputRequest;
import com.hpe.model.Variable;
import com.hpe.mthread.CRejectedExecutionHandler;
import com.hpe.mthread.ThreadPoolConfig;
import com.hpe.mthread.ThreadPoolFactory;
import com.hpe.mthread.ThreadTask_APP;
import com.hpe.mthread.ThreadTask_HTTPS_GET;
import com.hpe.mthread.ThreadTask_HTTPS_POST;


/**
 * @author wanxingc
 */
public class ThreadPoolMain {
    private  long averageRespnse=0;     // DEG response time average
    
    // response result
    public static long total_response_time=0;
    public static long resp_500=0;
    public static long resp_1000=0;
    public static long resp_2000=0;
    public static long resp_other=0;
    private static long total_requests=0;
    public static long total_failure_requests=0;
    
    private  long duration=0;          // performance run duration minutes
    private  int tps=0;
    private  long period=2;            //mins  the Ramp-up period
    
    private static int intIndex=0;
    
    private static java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyyMMddhhmmss");
    
    
    // get IMSI by loop all IMS List
    public static synchronized Variable getIMSI() {
    	int var_size=PropertyUtil.getInstance().getVariable().size();
    	int index=Math.floorMod(intIndex, var_size);
		Variable var=(Variable)PropertyUtil.getInstance().getVariable().get(index);
		intIndex++;
		return var;
    }
    
    //calculate response time
    public static synchronized void synchronizedMethod1(long responeTime) throws InterruptedException {
    	total_response_time=total_response_time+responeTime;
    	total_requests++;
    	
    	if(responeTime<=500){
    		resp_500++;
    	}else if(responeTime<=1000){
    		resp_1000++;
    	}else if(responeTime <= 2000){
    		resp_2000++;
    	}else{
    		resp_other++;
    	}
    }
    
    //calculate failure requests
    public static synchronized void synchronizedFailure(){
    	total_failure_requests++;
    }
    
    // get the total requests
    public static synchronized long getTotal_Requests() {
    	return total_requests;
    }
    
    
    // Do performance running
    public String run() throws InterruptedException, IOException,KeyManagementException, NoSuchAlgorithmException{
    	this.duration= PropertyUtil.getInstance().getDuration();
    	this.tps= PropertyUtil.getInstance().getTPS();
    	this.period=(long)PropertyUtil.getInstance().getPeroid();
    	

		// set Pool configuration
		ThreadPoolConfig config = ThreadPoolConfig.getInstance();
		config.setCorePoolSize(PropertyUtil.getInstance().getCorePoolSize());
		config.setMaximumPoolSize(PropertyUtil.getInstance().getMaxinumPoolSize());
		config.setKeepAliveTime(PropertyUtil.getInstance().getKeepAliveTime());
		config.setUnit(TimeUnit.SECONDS);
		config.setWorkQueue(new ArrayBlockingQueue<Runnable>(PropertyUtil.getInstance().getQueueLength()));
		config.setHandler(new CRejectedExecutionHandler());
		
		// thread factory
		ThreadPoolExecutor factory = ThreadPoolFactory.getInstance(config);
		factory.allowCoreThreadTimeOut(true);     // keep-alive time out
		// start Ramp-up period
		
		
		// check whether single time
		if(PropertyUtil.getInstance().getTPS()==-1) {
			System.out.println("###############################################################");
			System.out.println("One time running");
			Run_Single(factory,PropertyUtil.getInstance().getWeight()) ;
			//Thread.sleep(2000);
			factory.shutdown(); 
			return "Success";
			
		}
		
		System.out.println("Start Ramp-up period");	
		
		//run the peroid for ramp up
		Run_peroid(factory,PropertyUtil.getInstance().getWeight()) ;
		
		System.out.println("End Ramp-up period");
	
		
		System.out.println("####################################################################################");
		
		
		//house clean
		Thread.sleep(2000);
	    total_response_time=0;
	    resp_500=0;
	    resp_1000=0;
	    resp_2000=0;
	    resp_other=0;
	    total_requests=0;
	    total_failure_requests=0;
	    
		
		// start time
	    String strStartTime=format2.format(new Date());
		System.out.println("Start Time:"+strStartTime);

		// start normal run performance
		Timer timer = new Timer();
		TimerTask timerTask = new MTimerTask(factory,tps,PropertyUtil.getInstance().getWeight());
		timer.schedule(timerTask, 0 , 1000); //interval 1 second
		
		long starttime=System.currentTimeMillis();
		while (true) {
			 long endtime=System.currentTimeMillis();
		     if((endtime-starttime)>(duration*60*1000)){
		    	 timerTask.cancel();    // cancle timer
		    	 factory.shutdown();    // cancel thread factory
		    	 break;    // exit the performance running
		     }
		}
		String strEndTime=format2.format(new Date());
		System.out.println("End Time:"+strEndTime);
		
		Thread.sleep(5000); 
		
		 long totalTasks=factory.getTaskCount();
		 if(total_requests > 0 && total_response_time>0){
				averageRespnse=total_response_time/total_requests;
			}
		 
		 System.out.println();
		 System.out.println("#################################################################################################################");
		 System.out.println();
		 System.out.println("TPS="+ tps+"  Duration="+ duration + " Peroid="+ this.period );
		 System.out.println();
		 System.out.println(strStartTime+"-"+strEndTime);
		 System.out.println();
		 System.out.println("Summarization TotalRequest="+total_requests+" Should request="+tps*duration*60+"  TotalTasks="+totalTasks +" FailuredRequest="+total_failure_requests+" AverageRespnse="+averageRespnse);
		 System.out.println();
		 NumberFormat numberFormat = NumberFormat.getInstance();   
         numberFormat.setMaximumFractionDigits(2);   
		 String in_500= numberFormat.format((float)resp_500/(float)total_requests*100);
		 String in_1000= numberFormat.format((float)resp_1000/(float)total_requests*100);
		 String in_2000= numberFormat.format((float)resp_2000/(float)total_requests*100);
		 String in_other= numberFormat.format((float)resp_other/(float)total_requests*100); 
		 String in_failure= numberFormat.format((float)total_failure_requests/(float)total_requests*100);
		 System.out.println("(0-500):"+in_500+"% "+"(500-1000):"+in_1000+"% "+"(1000-2000):"+in_2000+"% "+" (>2000 millisecond):"+in_other+"% "+" Failures:"+in_failure+"%");
		 System.out.println();
		 System.out.println("#################################################################################################################");
		 System.out.println();
		 return "success";
    }
    
    
    /**
     * Ramp up peroid
     * @param factory
     * @param var_lst
     * @param weight_lst
     */
    private void Run_peroid(ThreadPoolExecutor factory,List<AssignWeight> weight_lst) {
    	//int currentThread=factory.getPoolSize();
		//int currentTasks=factory.getActiveCount();
		long totalTasks=factory.getTaskCount();
		int init_tps=50;     // min tps for ramp up
		
		Double[] weight=MTimerTask.GenerateWeight(weight_lst);
		Integer[] count=new Integer[weight.length];
	try{
		if(init_tps< this.tps) {
		int increase_tps=(int) Math.ceil((tps-init_tps)/(period*60));
		System.out.println("Increase Step:"+increase_tps);
		
		for(;init_tps<=this.tps;) {
			System.out.println("Current TPS :"+init_tps);
			
			Thread.sleep(1000);
			
		for (int i = 0; i < init_tps; i++) {
			// according to the weight obtain the input request object
			AssignWeight obj=(AssignWeight)weight_lst.get(MTimerTask.getWeight(weight,count));			
			InputRequest req=obj.getJsonRequest();
			String request_type=req.getRequest_Type();
			if(request_type.equals("app")){
				factory.execute(new ThreadTask_APP(i + "",req));                    // start thread for carrier token
			}else if(request_type.equals("ios")){
				factory.execute(new ThreadTask_HTTPS_POST(i + "",req));             // start thread for device
			}else if(request_type.equals("android")) {
				factory.execute(new ThreadTask_HTTPS_GET(i + "",req)); 
			}else{
				System.err.println("Request configuration is not correct in request_type attribute");
				break;
			}
		}
		if(totalTasks > 0){	
			averageRespnse=ThreadPoolMain.total_response_time/ThreadPoolMain.getTotal_Requests();
		}
	//	System.out.println(format2.format(new Date())+": TotalTasks="+totalTasks + " TotalRequests="+ThreadPoolMain.getTotal_Requests()+ " FailuredRequest="+ThreadPoolMain.total_failure_requests+" CurrentThread="+ currentThread +" CurrentTasks="+currentTasks +" AverageRespnse="+averageRespnse
				
	//			+" Rep(0-500)="+ThreadPoolMain.resp_500+"  Rep(500-1000)="+ThreadPoolMain.resp_1000+" Rep(1000-2000)="+ThreadPoolMain.resp_2000+" Rep(more than 2000)="+ThreadPoolMain.resp_other);
	
		
		init_tps=init_tps+increase_tps;
		}
		}else {
			System.out.println("The TPS less than "+init_tps+", this is not Ramp-up Period");
			
		}
		}catch(Exception ex){
		ex.printStackTrace();
	}	
    }
    
    
    
    private void Run_Single(ThreadPoolExecutor factory,List<AssignWeight> weight_lst) {
		Double[] weight=MTimerTask.GenerateWeight(weight_lst);
		Integer[] count=new Integer[weight.length];
	try{
     		// according to the weight obtain the input request object
			AssignWeight obj=(AssignWeight)weight_lst.get(MTimerTask.getWeight(weight,count));			
			InputRequest req=obj.getJsonRequest();
			String request_type=req.getRequest_Type();
			if(request_type.equals("app")){
				factory.execute(new ThreadTask_APP("one" + "",req));                    // start thread for carrier token
			}else if(request_type.equals("ios")){
				factory.execute(new ThreadTask_HTTPS_POST("one" + "",req));             // start thread for device
			}else if(request_type.equals("android")) {
				factory.execute(new ThreadTask_HTTPS_GET("one" + "",req)); 
			}else{
				System.err.println("Request configuration is not correct in request_type attribute");
			}
		}catch(Exception ex){
		ex.printStackTrace();
	}	
    }
    

	public class NullHostNameVerifier implements HostnameVerifier {
		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.net.ssl.HostnameVerifier#verify(java.lang.String,
		 * javax.net.ssl.SSLSession)
		 */
		@Override
		public boolean verify(String arg0, SSLSession arg1) {
			// TODO Auto-generated method stub
			return true;
		}
	}
}