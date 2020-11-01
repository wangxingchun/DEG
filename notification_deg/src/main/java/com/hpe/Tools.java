package com.hpe;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import org.apache.commons.codec.binary.Hex;

public class Tools {

	public static void main(String[] args) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		String token="uZEKfI+DL9NuUpa/ji11guRN7uw=";
        
		final Base64.Decoder decoder = Base64.getDecoder();
		final Base64.Encoder encoder = Base64.getEncoder();
		
		byte[] decoded = decoder.decode(token);
		String hexString = Hex.encodeHexString(decoded);
		System.out.println(hexString);

	}
}
