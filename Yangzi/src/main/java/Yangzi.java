import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.hpe.kit.C3P0Util;
import com.hpe.kit.PropertyUtil;
import com.hpe.performance.ThreadPoolMain;

public class Yangzi {
	
	/**
	 * @author wanxingc
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//initial DB Pool
		//String token=C3P0Util.getInstance().getToken("460030912121004");
		
		int TPS=PropertyUtil.getInstance().getTPS();
		if(TPS!=-1) {
			List lst=C3P0Util.getInstance().getAllSubscribers();
			PropertyUtil.getInstance().setVariable(lst);
		}
		
		//System.out.println("token="+token);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}// load DB Pool
	     ThreadPoolMain pool= new ThreadPoolMain();
	     
	     try {
			String result=pool.run();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}