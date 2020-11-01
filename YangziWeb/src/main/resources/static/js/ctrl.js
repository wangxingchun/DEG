var myTPS="2";
var myDuration="2";
var time_id=null;

$(function() {
	$("#p_tps").val(myTPS);
	$("#p_duration").val(myTPS);
	
	$( "#p_percent").progressbar({
	      width: 400,
          height: 40,
          value: 50,
          text: '{value}%',
	    });
	
	//start performance testing
    $("#btnstart").click(function() 
     {
	    	var postData = {}
	    	postData["tps"] = $("#p_tps").val();
	    	postData["duratin"] = $("#p_duration").val();
    	
    	    $("#p_message").html("Start Performance testing..."); 
    	    
    	    $("#btnstart").attr("disabled", true);
    	    $("#btnend").attr("disabled", false);
    	    
    	    $("#getResp").html("Sending request and getting response...");
    	    $.ajax(
    	    {
    	        url : "/performance/start",
    	        type: "POST",
    	        contentType: "application/json",
    	        data : JSON.stringify(postData),
    	        cache: false,
    	        timeout: 600000,
    	        success:function(data, textStatus, jqXHR){
    	            var json =  JSON.stringify(data, null, 4);
    	            $("#p_performance").html(json);
    	            
    	            
    	            var start_time=data.start_time;
    	            var end_time=data.end_time;
    	            var current_time=data.current_time;
    	            var totaltasks=data.total_tasks;
    	            var current_threads=data.current_threads;
    	            var current_tasks=data.current_tasks;
    	            var average_response=data.average_response;
    	            var thread_failure=data.thread_failure;
    	            var response_failure=data.response_failure;
    	            var messages=data.messages;
    	            var percent=data.percent;
    	            
    	            console.log(data.start_time);
    	             
    	            $("#p_starttime").html(start_time);
    	            $("#p_endtime").html(end_time);
    	            $("#p_message").html(messages);
    	            
    	            $("#p_totaltasks").val(totaltasks);
    	            $("#p_currentthreads").val(current_threads);
    	            $("#p_currenttasks").val(current_tasks);
    	            $("#p_averageresponse").val(average_response);
    	            
    	            $("#p_failurethreads").val(thread_failure);
    	            $("#p_failureresponse").val(response_failure);
    	            
    	            
    	            $("#p_percent").progressbar("value", Number(percent));
    	            
    	            
    	            // setInterval invoke collect performance status
    	            
    	            time_id=setInterval(getPerformanceStatus, 5000);
    	        	
    	        },
    	        error: function(jqXHR, textStatus, errorThrown) 
    	        {
    	            $("#p_message").html(textStatus +"<br>"+jqXHR+"<br>"+errorThrown);
    	        }
    	    });
    	   
    	}); 

    // end performance testing
    $("#btnend").click(function() 
    {
 
    	$("#btnstart").attr("disabled", false);
	    $("#btnend").attr("disabled", true);
	    var postData = {};
    	postData["stop_flag"] ="1";
    	
	    $.ajax({
    	url : "/performance/end",
        type: "POST",
        contentType: "application/json",
        data : JSON.stringify(postData),
        cache: false,
        timeout: 600000,
        success:function(data, textStatus, jqXHR){
            var json =  JSON.stringify(data, null, 4);
            $("#p_performance").html(json);
            
            var totaltasks=data.total_tasks;
            var current_threads=data.current_threads;
            var current_tasks=data.current_tasks;
            var average_response=data.average_response;
            var thread_failure=data.thread_failure;
            var response_failure=data.response_failure;
            var messages=data.messages;
            var percent=data.percent;
              
            $("#p_message").html(messages);
            
            $("#p_totaltasks").val(totaltasks);
            $("#p_currentthreads").val(current_threads);
            $("#p_currenttasks").val(current_tasks);
            $("#p_averageresponse").val(average_response);
            
            $("#p_failurethreads").val(thread_failure);
            $("#p_failureresponse").val(response_failure);
            
               
            $("#p_percent").progressbar("value", Number(percent));
            
            
            // close interval
            window.clearInterval(time_id);
            time_id = null;
          
        },
        error: function(jqXHR, textStatus, errorThrown) 
        {
            $("#p_message").html(textStatus +"<br>"+jqXHR+"<br>"+errorThrown);
        }
	   });
    	     
   });
    
});


// collect performance status
function getPerformanceStatus(){
	
	 var postData = {};
 	 postData["stop_flag"] ="0";
	 $.ajax({
	    	url : "/performance/status",
	        type: "POST",
	        contentType: "application/json",
	        data : JSON.stringify(postData),
	        cache: false,
	        timeout: 600000,
	        success:function(data, textStatus, jqXHR){
	            var json =  JSON.stringify(data, null, 4);
	            $("#p_performance").html(json);
	            
	            var totaltasks=data.total_tasks;
	            var current_threads=data.current_threads;
	            var current_tasks=data.current_tasks;
	            var average_response=data.average_response;
	            var thread_failure=data.thread_failure;
	            var response_failure=data.response_failure;
	            var messages=data.messages;
	            var percent=data.percent;
	            var stop_flag=data.stop_flag;
	              
	            $("#p_message").html(messages);
	            
	            $("#p_totaltasks").val(totaltasks);
	            $("#p_currentthreads").val(current_threads);
	            $("#p_currenttasks").val(current_tasks);
	            $("#p_averageresponse").val(average_response);
	            
	            $("#p_failurethreads").val(thread_failure);
	            $("#p_failureresponse").val(response_failure);
	            
	               
	            $("#p_percent").progressbar("value", Number(percent));
	            // flag check
	            if(stop_flag == "1"){
	            	$("#btnstart").attr("disabled", false);
	        	    $("#btnend").attr("disabled", true);    
	        	    // close interval
	                window.clearInterval(time_id);
	                time_id = null;
	            }
	            
	          
	        },
	        error: function(jqXHR, textStatus, errorThrown) 
	        {
	            $("#p_message").html(textStatus +"<br>"+jqXHR+"<br>"+errorThrown);
	        }
		   });
	 
}

