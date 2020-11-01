package com.hpe.performance;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;

import com.hpe.model.AssignWeight;
import com.hpe.model.InputRequest;
import com.hpe.mthread.ThreadTask_APP;
import com.hpe.mthread.ThreadTask_HTTPS_GET;
import com.hpe.mthread.ThreadTask_HTTPS_POST;


public class MTimerTask extends TimerTask{
	private ThreadPoolExecutor factory;
	private List<AssignWeight> weight_lst;
	private int TPS;
	private Double[] weight;
	private  Integer[] count;
	private long averageRespnse;

	private static java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyyMMddhhmmss");
	
	
	public MTimerTask(ThreadPoolExecutor factory,int tps,List<AssignWeight> weight_lst){
		this.factory=factory;
		this.TPS=tps;
		this.weight_lst=weight_lst;
		
		this.weight=GenerateWeight(weight_lst);
		this.count=new Integer[weight.length];	
	}

	@Override
	public void run() {
		
		int currentThread=factory.getPoolSize();
		int currentTasks=factory.getActiveCount();
		long totalTasks=factory.getTaskCount();
		
		try{
		
		for (int i = 0; i < TPS; i++) {
			
			// according to the weight obtain the input request object
			AssignWeight obj=(AssignWeight)weight_lst.get(MTimerTask.getWeight(weight,count));
						
			InputRequest req=obj.getJsonRequest();
			
			String request_type=req.getRequest_Type();
			
			if(request_type.equals("app")){
				
				factory.execute(new ThreadTask_APP(i + "",req));                 // start thread for carrier token
				
			}else if(request_type.equals("ios")){
				
				factory.execute(new ThreadTask_HTTPS_POST(i + "",req));          // start thread for ios
			}else if(request_type.equals("android")){
				
				factory.execute(new ThreadTask_HTTPS_GET(i + "",req));           // start thread for android
			}else {
				System.err.println("request configuration is not correct in request_type attribute");
				break;
			}
		}
		
		if(totalTasks > 0 && ThreadPoolMain.getTotal_Requests()>0){
			averageRespnse=ThreadPoolMain.total_response_time/ThreadPoolMain.getTotal_Requests();
		}
	
		System.out.println(format2.format(new Date())+": TotalTasks="+totalTasks + " TotalRequests="+ThreadPoolMain.getTotal_Requests()+ " FailuredRequest="+ThreadPoolMain.total_failure_requests+" CurrentThread="+ currentThread +" CurrentTasks="+currentTasks +" AverageRespnse="+averageRespnse
				
				+" Rep(0-500)="+ThreadPoolMain.resp_500+"  Rep(500-1000)="+ThreadPoolMain.resp_1000+" Rep(1000-2000)="+ThreadPoolMain.resp_2000+" Rep(more than 2000)="+ThreadPoolMain.resp_other);
		
	 }catch(Exception ex){
		ex.printStackTrace();
	}
	}
	
	
	// generate weight 
	public static Double[] GenerateWeight(List<AssignWeight> lst){
		Double[] weight=new Double[lst.size()];
		for(int i = 0 ; i < lst.size() ; i++) {
			AssignWeight obj=(AssignWeight)lst.get(i);
			double w=Double.valueOf(obj.getWeight());
			weight[i]=w;
	    }
		return weight;
	}

	// get the weight of request files
    public static  int getWeight(Double[] weight,Integer[] count){
    	    
        // current weight
         Double[] current = new Double[weight.length];
         for(int w=0;w<weight.length;w++)
         {
             current[w] = weight[w]/(count[w]==null?1:count[w]);
         }
         int index = 0;
         Double currentMax = current[0];
         for(int d=1; d<current.length;d++)
         {
             Boolean isTrue = true;
             while (isTrue)
             {
                 Set set = new HashSet();
                 for(Double c : current)
                 {
                     set.add(c);
                 }
                 if(set.size()==1)
                 {// it means equal
                     for(int e=0; e<current.length;e++)
                     {
                         current[e] = current[e]*Math.random();
                     }
                 }else{
                         isTrue = false;
                     }
             }
             //compare all data to find next maxvalue
             if(currentMax<current[d])
             {
                 currentMax=current[d];
                 index =d;
             }
         }
         count[index]=count[index]==null?1:count[index]+1;   
	  return index;
}
}
