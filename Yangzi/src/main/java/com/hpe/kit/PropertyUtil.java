package com.hpe.kit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.hpe.model.AssignWeight;
import com.hpe.model.InputRequest;
import com.hpe.model.Variable;
import com.hpe.performance.ThreadPoolMain;

public class PropertyUtil {

	private long Duration = 0;
	private int Period = 1; // ramp up period
	private int CorePoolSize = 0;
	private int MaxinumPoolSize = 0;
	private int KeepAliveTime = 0;
	private int QueueLength = 0;
	private int TPS = 0;
	private int ConnectTimeout = 0;
	private int ReadTimeout = 0;

	private List<Variable> variable_lst = new ArrayList(); // variable list
	private List<AssignWeight> weight_lst = new ArrayList(); // request files list and weight

	private static PropertyUtil instance = null;
	private Properties c3p0 = new Properties(); // database connection configuration

	String filePath = System.getProperty("user.dir") + File.separator + "config" + File.separator;

	private PropertyUtil() {

		Properties props_config = new Properties();

		try {
			// configuration file
			File configFile = new File(filePath + "config.properties");
			if (configFile.exists()) {
				props_config.load(new FileInputStream(filePath + "config.properties"));
				Duration = Long.parseLong(props_config.getProperty("Duration"));
				CorePoolSize = Integer.valueOf(props_config.getProperty("CorePoolSize"));
				MaxinumPoolSize = Integer.valueOf(props_config.getProperty("MaxinumPoolSize"));
				KeepAliveTime = Integer.valueOf(props_config.getProperty("KeepAliveTime"));
				QueueLength = Integer.valueOf(props_config.getProperty("QueueLength"));
				TPS = Integer.valueOf(props_config.getProperty("TPS"));
				ConnectTimeout = Integer.valueOf(props_config.getProperty("ConnectTimeout"));
				ReadTimeout = Integer.valueOf(props_config.getProperty("ReadTimeout"));
				Period = Integer.valueOf(props_config.getProperty("Peroid"));

				// load request file object according to this attribute
				System.out.println("Loadconfig Request files......");
				System.out.println("Target TPS:" + TPS + "  Duration:" + Duration + " mins" + "  Ramp UP Peroid:"
						+ Period + " mins");

				weight_lst = initWeight(props_config.getProperty("Requests"));
			} else {
				System.err.println("configuration file is not exist:" + filePath + "config.properties");
			}

			// load c3p0 properity
			configFile = new File(filePath + "c3p0.properties");
			if (configFile.exists()) {
				c3p0.load(new FileInputStream(filePath + "c3p0.properties"));

			} else {
				System.err.println("c3p0 configuration file is not exist:" + filePath + "c3p0.properties");
			}

			// load IMSI and UNIQUE ID

			if (TPS == -1) {
				// load variable data
				System.out.println("Loadconfig variable data start......");

				configFile = new File(filePath + "variable.cfg");
				if (configFile.exists()) {
					BufferedReader reader = new BufferedReader(new FileReader(configFile));
					String line;
					Variable var = new Variable();

					while ((line = reader.readLine()) != null) {
						if (line.startsWith("#") || line.startsWith("Desc") || line.startsWith("desc")) {
							continue;
						}

						int p = line.indexOf(",");
						String imsi = "";
						String uniqueid = "";
						String token = "";
						if (p > 0) {
							imsi = line.substring(0, p).trim();
							uniqueid = line.substring(p + 1).trim();
							var.setIMSI(imsi);
							var.setUNIQUEID(uniqueid);
							variable_lst.add(var);
							var = new Variable();
						}
					}
					reader.close();
				} else {
					System.err.println("variable file is not exist:" + filePath + "variable.cfg");
				}

				System.out.println("Loadconfig variable data end......");
			} else {
				// load deg_Subscribers
				//System.out.println("loadconfig subscribers......");
				//variable_lst = C3P0Util.getInstance().getAllSubscribers();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static synchronized PropertyUtil getInstance() {
		if (instance == null) {
			instance = new PropertyUtil();
		}
		return instance;
	}

	public long getDuration() {
		return this.Duration;
	}

	public int getCorePoolSize() {
		return this.CorePoolSize;
	}

	public int getMaxinumPoolSize() {
		return this.MaxinumPoolSize;
	}

	public int getKeepAliveTime() {
		return this.KeepAliveTime;
	}

	public int getQueueLength() {
		return this.QueueLength;
	}

	public int getTPS() {
		return this.TPS;
	}

	public int getConnectTimeout() {
		return this.ConnectTimeout;
	}

	public int getReadTimeout() {
		return this.ReadTimeout;
	}

	public int getPeroid() {
		return this.Period;
	}
	
	public void setVariable(List list) {
		this.variable_lst=list;
	}

	public List<Variable> getVariable() {
		return this.variable_lst;
	}

	public Properties getC3P0_config() {
		return this.c3p0;
	}

	public List<AssignWeight> getWeight() {
		return this.weight_lst;
	}

	// initial weight
	private List<AssignWeight> initWeight(String requests_config) {
		List<AssignWeight> lst = new ArrayList();
		String[] array = requests_config.split(",");
		AssignWeight obj = null;
		;
		for (String request : array) {
			obj = new AssignWeight();

			int p = request.indexOf(":");
			int weight = 0;
			String name = "";
			if (p > 0) {
				weight = Integer.valueOf(request.substring(0, p).trim());
				name = request.substring(p + 1).trim();
			}
			obj.setName(name);
			obj.setWeight(weight);
			obj.setJsonRequest(getRequestJson(name)); // set request file content
			lst.add(obj);
		}
		return lst;
	}

	// load request file
	private InputRequest getRequestJson(String request_name) {

		InputRequest req = new InputRequest();
		try {

			File configFile = new File(filePath + request_name);
			if (configFile.exists()) {
				BufferedReader reader = new BufferedReader(new FileReader(configFile));
				String line;

				while ((line = reader.readLine()) != null) {
					if (line.startsWith("#") || line.startsWith("Desc") || line.startsWith("desc")) {
						continue;
					}

					int p = line.indexOf("=");
					String key = "";
					String value = "";
					if (p > 0) {
						key = line.substring(0, p).trim();
						value = line.substring(p + 1).trim();
					}
					if (key.equals("url")) {
						req.setUrl(value);
					}
					if (key.equals("jsonReq")) { // it is for ios
						req.jsonReq_lst.add(value);
					}
					if (key.equals("request_get")) { // it is for android
						req.getReq_lst.add(value);
					}
					if (key.equals("header")) {

						int k = value.indexOf(",");
						if (k > 0) {
							req.headers.put(value.substring(0, k), value.substring(k + 1));
						}
					}
					if (key.equals("request_type")) {
						req.setRequest_Type(value);
					}
				}
				reader.close();
			} else {
				System.err.println("Request file is not exist:" + filePath + request_name);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return req;
	}

	// log file names
	public String getLogFileName() {
		java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyyMMddhhmmss");
		String logfile = format2.format(new Date()) + "_" + this.TPS + ".log";
		String filePath = System.getProperty("user.dir") + File.separator + "log" + File.separator + logfile;
		return filePath;
	}
}
