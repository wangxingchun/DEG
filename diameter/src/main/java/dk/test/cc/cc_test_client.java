package dk.test.cc;

import java.util.logging.Level;
import java.util.logging.Logger;

import dk.i1.diameter.AVP;
import dk.i1.diameter.AVP_UTF8String;
import dk.i1.diameter.AVP_Unsigned32;
import dk.i1.diameter.InvalidAVPLengthException;
import dk.i1.diameter.Message;
import dk.i1.diameter.ProtocolConstants;
import dk.i1.diameter.node.Capability;
import dk.i1.diameter.node.InvalidSettingException;
import dk.i1.diameter.node.NodeSettings;
import dk.i1.diameter.node.Peer;
import dk.i1.diameter.node.SimpleSyncClient;

class cc_test_client {
	
	static Logger log = Logger.getLogger("");
	public static final void main(String args[]) throws Exception {
		
		log.setLevel(Level.FINEST);
		 
		 log.getHandlers()[0].setLevel( Level.FINE );
		 
		if(args.length!=4) {
			System.out.println("Usage: <host-id> <realm> <peer> <peer-port>");
			//return;
		}
		String host_id ="192.168.75.1";// args[0];
		String host_name="client.swm.com";
		String realm = "swm.com";//args[1];
		String dest_host ="15.119.6.192"; //args[2];
		String dest_hostname="mgr.swm.com";
		int dest_port =3868; //Integer.parseInt(args[3]);
		
		Capability capability = new Capability();
	
		capability.addAuthApp(ProtocolConstants.DIAMETER_AUTH_APPLICATION_ID_SWm);
		//capability.addAcctApp(ProtocolConstants.DIAMETER_APPLICATION_CREDIT_CONTROL);
		
		NodeSettings node_settings;
		try {
			node_settings  = new NodeSettings(
				host_name,
				host_id, realm,
				ProtocolConstants.VENDER_ID, //vendor-id
				capability,
				0,
				"cc_test_client", 0x01000000);
			
			node_settings.setUseTCP(true);
		} catch (InvalidSettingException e) {
			System.out.println(e.toString());
			return;
		}
		
		Peer peers[] = new Peer[]{new Peer(dest_hostname,dest_host,dest_port,Peer.TransportProtocol.tcp)};
		
		
		
		SimpleSyncClient ssc = new SimpleSyncClient(node_settings,peers);
		ssc.start();
		ssc.waitForConnection();        //allow connection to be established.
		
		System.out.println("## CER-CEA OK");
		System.in.read();
		
	    Message ccr=EAP_First.get_First_DEA(ssc);
		
		//Send it
		Message cca = ssc.sendRequest(ccr);
		
		//Now look at the result
		if(cca==null) {
			System.out.println("No response");
			return;
		}
		AVP result_code = cca.find(ProtocolConstants.DI_RESULT_CODE);
		if(result_code==null) {
			System.out.println("No result code");
			return;
		}
		try {
			AVP_Unsigned32 result_code_u32 = new AVP_Unsigned32(result_code);
			int rc = result_code_u32.queryValue();
			switch(rc) {
				case ProtocolConstants.DIAMETER_RESULT_SUCCESS:
					System.out.println("Success");
					break;
				case ProtocolConstants.DIAMETER_RESULT_END_USER_SERVICE_DENIED:
					System.out.println("End user service denied");
					break;
				case ProtocolConstants.DIAMETER_RESULT_CREDIT_CONTROL_NOT_APPLICABLE:
					System.out.println("Credit-control not applicable");
					break;
				case ProtocolConstants.DIAMETER_RESULT_CREDIT_LIMIT_REACHED:
					System.out.println("Credit-limit reached");
					break;
				case ProtocolConstants.DIAMETER_RESULT_USER_UNKNOWN:
					System.out.println("User unknown");
					break;
				case ProtocolConstants.DIAMETER_RESULT_RATING_FAILED:
					System.out.println("Rating failed");
					break;
				default:
					//Some other error
					//There are too many to decode them all.
					//We just print the classification
					if(rc>=1000 && rc<1999){
						System.out.println("Informational: " + rc);
					
					AVP session_avp = cca.find(ProtocolConstants.DI_SESSION_ID);
					AVP_UTF8String session = new AVP_UTF8String(session_avp);
					String session_id=session.queryValue();
				    Message second_eap=EAP_Second.get_Second_DEA(session_id);     // get Second DEA
				    
				    System.out.println("Send the Second EAP..................");
				    
				    ssc.node().addOurHostAndRealm(second_eap);
				    
				    Message second_cca = ssc.sendRequest(second_eap);           // send Second DEA
				    
				    AVP code = second_cca.find(ProtocolConstants.DI_RESULT_CODE);
				    AVP_Unsigned32 code_1 = new AVP_Unsigned32(code);
				    int code_2=code_1.queryValue();
				    System.out.println("Result="+code_2);
					
			        }else if(rc>=2000 && rc<2999)
						System.out.println("Success: " + rc);
					else if(rc>=3000 && rc<3999)
						System.out.println("Protocl error: " + rc);
					else if(rc>=4000 && rc<4999)
						System.out.println("Transient failure: " + rc);
					else if(rc>=5000 && rc<5999)
						System.out.println("Permanent failure: " + rc);
					else
						System.out.println("(unknown error class): " + rc);
				
			}
		} catch(InvalidAVPLengthException ex) {
			System.out.println("result-code was illformed");
			return;
		}
		//Stop the stack
		ssc.stop();
	}
}
