package dk.test.cc;

import dk.i1.diameter.AVP_Integer32;
import dk.i1.diameter.AVP_OctetString;
import dk.i1.diameter.AVP_UTF8String;
import dk.i1.diameter.AVP_Unsigned32;
import dk.i1.diameter.Message;
import dk.i1.diameter.ProtocolConstants;
import dk.i1.diameter.Utils;

public class EAP_Second {

	
	static public Message get_Second_DEA(String session_id) {
		Message ccr = new Message();
		ccr.hdr.command_code = ProtocolConstants.DIAMETER_COMMAND_EAP;
		ccr.hdr.application_id = ProtocolConstants.DIAMETER_AUTH_APPLICATION_ID_SWm;
		ccr.hdr.setRequest(true);
		ccr.hdr.setProxiable(true);
		
	//  < Session-Id >
			ccr.add(new AVP_UTF8String(ProtocolConstants.DI_SESSION_ID,session_id));
			//  { Origin-Host }
			//  { Origin-Realm }
			//ssc.node().addOurHostAndRealm(ccr);
			//  { Destination-Realm }
			ccr.add(new AVP_UTF8String(ProtocolConstants.DI_DESTINATION_REALM,"swm.com"));
			//  { Auth-Application-Id }
			ccr.add(new AVP_Unsigned32(ProtocolConstants.DI_AUTH_APPLICATION_ID,ProtocolConstants.DIAMETER_AUTH_APPLICATION_ID_SWm)); // a lie but a minor one
			//  { Service-Context-Id }
			
			ccr.add(new AVP_Integer32(ProtocolConstants.DI_AUTH_REQUEST_TYPE,ProtocolConstants.DI_AUTH_REQUEST_TYPE_AUTHENTICATE));
			
			// User-Name
			ccr.add(new AVP_UTF8String(ProtocolConstants.DI_USER_NAME ,"0250015037838167@nai.epc.mnc001.mcc420.3gppnetwork.org"));
			
			//EAP-Play
			String eap_playload="RUFQX09L";
			ccr.add(new AVP_OctetString(ProtocolConstants.DI_EAP_PAYLOAD ,eap_playload.getBytes()));
			
			//State
			String state="1";
			ccr.add(new AVP_OctetString(ProtocolConstants.DI_STATE ,state.getBytes()));
			
			// Authorizatin-Lifetime
			ccr.add(new AVP_Unsigned32(ProtocolConstants.DI_AUTHORIZATION_LIFETIME ,2592000));
			
			//Auth-Grache-Period
			ccr.add(new AVP_Unsigned32(ProtocolConstants.DI_AUTH_GRACE_PERIOD ,7200));
			
			//Auth-Session-State   ------  eIUM Enumerated 
			ccr.add(new AVP_Unsigned32(ProtocolConstants.DI_AUTH_SESSION_STATE ,0));
				
			//Calling-Station-Id
			ccr.add(new AVP_UTF8String(ProtocolConstants.DI_CALLING_STATION_ID ,"250015037838167"));
			
			//RAT-Type
			ccr.add(new AVP_Unsigned32(ProtocolConstants.DI_RAT_TYPE ,0));
			
			//ANID vendor_id=10415
			ccr.add(new AVP_UTF8String(ProtocolConstants.DI_ANID ,10415,"HRPD"));
			
			Utils.setMandatory_RFC3588(ccr);
			Utils.setMandatory_RFC4006(ccr);
		
		return ccr;
	}
}
