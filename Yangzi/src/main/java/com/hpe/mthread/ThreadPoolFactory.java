package com.hpe.mthread;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Thread pools factory
 * @author
 * 
 */
public class ThreadPoolFactory {


	public static ThreadPoolExecutor getInstance(ThreadPoolConfig config) {

		ThreadPoolExecutor pool=null;;
        /**
         * generate thread pools object
         */
		if (config.getHandler() == null) {
			pool = new ThreadPoolExecutor(config.getCorePoolSize(),
					config.getMaximumPoolSize(), config.getKeepAliveTime(),
					config.getUnit(), config.getWorkQueue());
		} else {
			pool = new ThreadPoolExecutor(config.getCorePoolSize(),
					config.getMaximumPoolSize(), config.getKeepAliveTime(),
					config.getUnit(), config.getWorkQueue(),
					config.getHandler());
		}
		
		//System.out.println("pool  create= " + pool.toString());
		return pool;
	}
}