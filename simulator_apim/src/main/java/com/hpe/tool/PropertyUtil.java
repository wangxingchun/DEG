package com.hpe.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class PropertyUtil {
	private String BODY = "";
	private String performance="";

	private List<Header> header_list = new ArrayList(); // Header list

	private static PropertyUtil instance = null;
	static String filePath = System.getProperty("user.dir") + File.separator + "config" + File.separator;
	static Long lastModified = 0l;

	public static synchronized PropertyUtil getInstance() {
		if (instance == null) {
			instance = new PropertyUtil();      // refresh the value
		} else {
			File configFile = new File(filePath + "config.properties");
			if (configFile.exists() && configFile.lastModified() > lastModified) {
				instance = new PropertyUtil();
				
				System.out.println("Configuration was updated !!!");
			}
		}
		return instance;
	}

	private PropertyUtil() {
		try {
			
			File configFile = new File(filePath + "config.properties");
			lastModified=configFile.lastModified();
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
					if (key.equals("body")) {
						BODY = value;
					}
					if(key.equals("performance"))  {
						performance=value;
					}
					if (key.equals("header")) {
						int k = value.indexOf(",");
						if (k > 0) {
							Header header = new Header();
							header.setName(value.substring(0, k));
							header.setValue(value.substring(k + 1));
							header_list.add(header);
						}
					}
				}
				reader.close();
			} else {
				System.err.println("Request file is not exist:" + filePath + " config.properties");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public String getBody() {
		return this.BODY;
	}

	public List getHeader() {
		return this.header_list;
	}
	public String getPerformance() {
		return this.performance;
	}
}
