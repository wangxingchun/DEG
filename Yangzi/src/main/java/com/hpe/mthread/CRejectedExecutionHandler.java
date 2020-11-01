package com.hpe.mthread;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Pool runing Exception
 * @author 
 *
 */
// reject  new request
public class CRejectedExecutionHandler implements RejectedExecutionHandler {

	@Override
	public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {
		// TODO Auto-generated method stub

		System.err.println("The pool RejectedExecutionHandler = "+executor.toString());
		
		//throw new RejectedExecutionException("Task " + task.toString() +" rejected from " +executor.toString());
	}
}