package com.hpe.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import com.hpe.kit.C3P0Util;


public class TokenService {

	
    public static String getToken(String imsi,Properties c3p0) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String token="MyToken";
        Connection conn = (Connection) C3P0Util.getInstance().getConnection();
        try {
            ps = (PreparedStatement) conn.prepareStatement("SELECT DEG_SUBSTOKEN FROM DEG_TOKENS WHERE DEG_IMSI=?");
            ps.setString(1, imsi);
            rs = ps.executeQuery();
            while (rs.next()) {
            	token=rs.getString(1);
            	//u.setImsi(rs.getString(1));
                //u.setId(rs.getInt(1));  
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            C3P0Util.getInstance().release(conn, ps, rs);
        }
        return token;
    }
}
