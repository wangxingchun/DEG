package com.hpe.mthread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.TimeUnit;



/**
 * Thread Pool configuration
 * @author
 * 
 */
public class ThreadPoolConfig {

	private int corePoolSize;
	private int maximumPoolSize;
	private long keepAliveTime;
	private TimeUnit unit;
	private BlockingQueue<Runnable> workQueue;
	private RejectedExecutionHandler handler;
	
	private static ThreadPoolConfig config;

	/**
	 * single model
	 */
	private ThreadPoolConfig() {

	}

	/**
	 *get configuration
	 * @return
	 */
	public static ThreadPoolConfig getInstance() {
		if (config == null) {
			config = new ThreadPoolConfig();
		}
		return config;
	}

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public int getMaximumPoolSize() {
		return maximumPoolSize;
	}

	public void setMaximumPoolSize(int maximumPoolSize) {
		this.maximumPoolSize = maximumPoolSize;
	}

	public long getKeepAliveTime() {
		return keepAliveTime;
	}

	public void setKeepAliveTime(long keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}

	public TimeUnit getUnit() {
		return unit;
	}

	public void setUnit(TimeUnit unit) {
		this.unit = unit;
	}

	public BlockingQueue<Runnable> getWorkQueue() {
		return workQueue;
	}

	public void setWorkQueue(BlockingQueue<Runnable> workQueue) {
		this.workQueue = workQueue;
	}

	public RejectedExecutionHandler getHandler() {
		return handler;
	}

	public void setHandler(RejectedExecutionHandler handler) {
		this.handler = handler;
	}
}