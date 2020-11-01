package com.hpe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hpe.PerformanceService;
import com.hpe.model.PerformanceObject;	

@RestController
public class PerformanceController {
	
	PerformanceService pService;
	
    @Autowired
    public void setPerformanceService(PerformanceService pService) {
    	this.pService=pService;
    }
	
	@PostMapping("/performance/start")
	public ResponseEntity<?> DoPerformance(@RequestBody PerformanceObject p_object, Errors errors) {
		String tps="";
		String duration="";
		if(p_object instanceof PerformanceObject) {
			tps=p_object.getTps();
			duration=p_object.getDuratin();
			p_object=pService.StartPerformance(tps, duration);
		
		}else {
			return (ResponseEntity<?>) ResponseEntity.badRequest();
		}
		System.out.println("#############################");
		System.out.println("Start Performance test");
		System.out.println("#############################");

		return ResponseEntity.ok(p_object);
	}
	
	@PostMapping("/performance/end")
	public ResponseEntity<?> StopPerformance(@RequestBody PerformanceObject p_object, Errors errors){
		
		if(p_object instanceof PerformanceObject) {
			String stop_flag=stop_flag=p_object.getStop_flag();
			p_object=pService.StopPerformance();
		}
		System.out.println("Stop Performance test");
	
	  return ResponseEntity.ok(p_object);
	}

	
	@PostMapping("/performance/status")
	public ResponseEntity<?> GetPerformanceStatus(@RequestBody PerformanceObject p_object, Errors errors){
		
		System.out.println("getPerformance Status");
		PerformanceObject p_status= pService.getPerformanceStatus();
		return ResponseEntity.ok(p_status);
	}
	
}
