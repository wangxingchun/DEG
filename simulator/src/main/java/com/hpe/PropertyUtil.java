package com.hpe;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyUtil {
	private String ICCID = "";
	private String RESPCODE = "";
	private String RESPMSG = "";
	private static PropertyUtil instance = null;
	private String PERFORMANCE="";
	static String filePath = System.getProperty("user.dir") + File.separator + "config" + File.separator;
	static Long lastModified = 0l;

	private PropertyUtil() {
		Properties props_config = new Properties();

		try {
			// configuration file
			File configFile = new File(filePath + "config.properties");
			if (configFile.exists()) {
				lastModified = configFile.lastModified();
				props_config.load(new FileInputStream(filePath + "config.properties"));
				ICCID = props_config.getProperty("ICCID");
				RESPCODE = props_config.getProperty("RESPCODE");
				RESPMSG = props_config.getProperty("RESPMSG");
				PERFORMANCE=props_config.getProperty("PERFORMANCE");
			} else {
				System.err.println("configuration file is not exist:" + filePath + "config.properties");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static synchronized PropertyUtil getInstance() {
		if (instance == null) {
			instance = new PropertyUtil(); // refresh the value
		} else {
			File configFile = new File(filePath + "config.properties");
			if (configFile.exists() && configFile.lastModified() > lastModified) {
				instance = new PropertyUtil();
			}
		}
		return instance;
	}

	public String getICCID() {
		return this.ICCID;
	}

	public String getRESPCODE() {
		return this.RESPCODE;
	}

	public String getRESPMSG() {
		return this.RESPMSG;
	}
	public String getPERFORMANCE() {
		return this.PERFORMANCE;
	}
	
}
