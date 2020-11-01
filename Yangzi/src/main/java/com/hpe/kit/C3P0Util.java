package com.hpe.kit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.hpe.model.Variable;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3P0Util {
	private  ComboPooledDataSource dataSource=null ;
	
	private static C3P0Util instance = null;
    
	private static java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyyMMddhhmmss");
	
	
	private C3P0Util(){
		//System.out.println("inital C3P0Util");
		
		// close C3P0 log
		Properties p = new Properties(System.getProperties());
		p.put("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
		p.put("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "OFF"); // Off or any other level
		System.setProperties(p);
		
		try{
		Properties c3p0	=PropertyUtil.getInstance().getC3P0_config();
		dataSource = new ComboPooledDataSource();
		//dataSource.setAutoCommitOnClose(autoCommitOnClose)
		dataSource.setUser(c3p0.getProperty("c3p0.user"));
		dataSource.setPassword(c3p0.getProperty("c3p0.password"));
		dataSource.setJdbcUrl(c3p0.getProperty("c3p0.jdbcUrl"));
        dataSource.setDriverClass(c3p0.getProperty("c3p0.driverClass"));
        dataSource.setInitialPoolSize(Integer.valueOf(c3p0.getProperty("c3p0.InitialPoolSize")));
        //dataSource.setAcquireIncrement(1);
        dataSource.setMinPoolSize(Integer.valueOf(c3p0.getProperty("c3p0.MinPoolSize")));
        dataSource.setMaxPoolSize(Integer.valueOf(c3p0.getProperty("c3p0.MaxPoolSize")));
        //dataSource.setMaxStatements(1);
        //dataSource.setMaxIdleTime(1);
        //dataSource.setIdleConnectionTestPeriod();
        //dataSource.setAcquireRetryAttempts();
		}catch(Exception  e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	

	public static synchronized  C3P0Util getInstance( ){
		if(instance == null){
			instance = new C3P0Util();
		}
		return instance;
	 }
	
// get connection from pool
	public   Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

// release connection
	public  void release(Connection conn, Statement stmt, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			rs = null;
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			stmt = null;
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			conn = null;
		}
	}
	
	
	//get APP Token
	public String getAppToken(String imsi) {
		PreparedStatement ps = null;
        ResultSet rs = null;
        String token="MyToken";
           
        try {
        	int current_DB_Pool=dataSource.getNumConnections();
        	//System.out.println("Current DB Pool="+current_DB_Pool);
        	Connection conn = dataSource.getConnection();
        	ps = (PreparedStatement) conn.prepareStatement("SELECT DEG_APPTOKEN FROM DEG_TOKENS WHERE DEG_IMSI=?");
            ps.setString(1, imsi);
            rs = ps.executeQuery();
            while (rs.next()) {
            	token=rs.getString(1);
            	//u.setImsi(rs.getString(1));
                //u.setId(rs.getInt(1));  
            }
            
            release(conn, ps, rs);

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
        	if(rs!=null){
        		try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	if(ps!=null){
        		try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
            
        }
        return token;
	}
	
	//get android token
    public String getAndroidToken(String imsi) {
    	PreparedStatement ps = null;
        ResultSet rs = null;
        String token="MyToken";
           
        try {
        	int current_DB_Pool=dataSource.getNumConnections();
        	//System.out.println("Current DB Pool="+current_DB_Pool);
        	Connection conn = dataSource.getConnection();
        	ps = (PreparedStatement) conn.prepareStatement("SELECT DEG_SUBSTOKEN FROM DEGDROID_TOKENS WHERE DEG_IMSI=?");
            ps.setString(1, imsi);
            rs = ps.executeQuery();
            while (rs.next()) {
            	token=rs.getString(1);
            	//u.setImsi(rs.getString(1));
                //u.setId(rs.getInt(1));  
            }
            
            release(conn, ps, rs);

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
        	if(rs!=null){
        		try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	if(ps!=null){
        		try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
            
        }
        return token;
    }
	
	//get iso token by IMSI
	public String getToken(String imsi){
		
		PreparedStatement ps = null;
        ResultSet rs = null;
        String token="MyToken";
           
        try {
        	int current_DB_Pool=dataSource.getNumConnections();
        	//System.out.println("Current DB Pool="+current_DB_Pool);
        	Connection conn = dataSource.getConnection();
        	ps = (PreparedStatement) conn.prepareStatement("SELECT DEG_SUBSTOKEN FROM DEG_TOKENS WHERE DEG_IMSI=?");
            ps.setString(1, imsi);
            rs = ps.executeQuery();
            while (rs.next()) {
            	token=rs.getString(1);
            	//u.setImsi(rs.getString(1));
                //u.setId(rs.getInt(1));  
            }
            
            release(conn, ps, rs);

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
        	if(rs!=null){
        		try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	if(ps!=null){
        		try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
            
        }
        return token;
	}
	
	// get all subscribers from deg_subscribers
	public List getAllSubscribers(){
		List<Variable> variable_lst = new ArrayList(); 
		PreparedStatement ps = null;
        ResultSet rs = null;
        String imsi="";
        String mdn="";
        Long sum=0l;
       
        String strStartTime=format2.format(new Date());
		System.out.println(strStartTime+" Loading DEG_SUBSCRIBERS......");
		
        try {
        	int current_DB_Pool=dataSource.getNumConnections();
        	//System.out.println("Current DB Pool="+current_DB_Pool);
        	Connection conn = dataSource.getConnection();
        	ps = (PreparedStatement) conn.prepareStatement("SELECT DEG_IMSI,DEG_MDN FROM DEG_SUBSCRIBERS");
           // ps.setString(1, imsi);
            rs = ps.executeQuery();
            while (rs.next()) {
            	imsi=rs.getString(1);
            	mdn=rs.getString(2);
            	Variable var=new Variable();
            	var.setIMSI(imsi);
            	var.setUNIQUEID(mdn);
            	variable_lst.add(var);
            	sum=sum+1;
            }
            release(conn, ps, rs);
            String strEndTime=format2.format(new Date());
    		System.out.println(strEndTime+" Loaded "+sum+ " SUBSCRIBERS......");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
        	if(rs!=null){
        		try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	if(ps!=null){
        		try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
            
        }
        return variable_lst;
	}
}