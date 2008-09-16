package org.movilforum.net;



import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;

/**
 * Performs call handshaking
 * @author dave
 *
 */
public class CallHandshakeManager extends UDPBasedClass{
	
	public static CallHandshakeManager instance ;
	
	private Map<String,String> parameters = new HashMap<String, String>();

	
	public CallHandshakeManager(String hostName) throws SocketException, UnknownHostException{
		this.socket = new DatagramSocket(localPort, InetAddress.getByName(hostName));
		instance = this;
	}
	
	private void register(String localIP, String login, String pass, String nonce, String registerAttempts, String ncallId, String nTag, String expiration) throws NoSuchAlgorithmException, IOException{
		MessageDigest digest = MessageDigest.getInstance("MD5");
		
		org.apache.commons.codec.binary.Hex hex = new Hex();
		
		String loginMessage = 	login + ":movistar.es:" + pass;
		
		String digest1 = new String(hex.encode(digest.digest(loginMessage.getBytes())));
		
		String sipIP = "REGISTER:sip:" + serverIP; 
		String digest2 = new String(hex.encode(digest.digest(sipIP.getBytes())));
		
		String finalString = digest1 + ":" + nonce + ":00000001:14122:auth:"+digest2;
		
		String digestResponse = new String(hex.encode(digest.digest(finalString.getBytes())));
		
		
		String registerMessage = 
				"REGISTER sip:" + serverIP + " SIP/2.0\r\n" + 
				"Via: SIP/2.0/UDP " + localIP + ":" + localPort +";rport;branch=z9hG4bK23080\r\n" +
				"From: <sip:" + login +"@movistar.es>;tag=" + nTag+ "\r\n" +
				"To: <sip:" + login + "@movistar.es>\r\n"+
				"Call-ID: " + ncallId + "@" + localIP + "\r\n" + 
 				"CSeq: " + registerAttempts + " REGISTER\r\n" +
				"Contact: <sip:" + login + "@" + localIP + ":" + localPort + ">\r\n" +
				"Authorization: digest username=\""+ login + "\", realm=\"movistar.es\", nonce=\"" + nonce + "\", uri=\"sip:" + serverIP + "\", response=\"" + digestResponse+ "\", algorithm=md5, cnonce=\"14122\", qop=auth, nc=00000001\r\n" +
				"Max-Forwards: 70\r\n" +
				"Expires: " + expiration + "\r\n" +
				"Allow-Events: presence\r\n" +
				"Event: registration\r\n" +
				"User-Agent: Intellivic/PC\r\n" +
				"Allow: ACK, BYE, CANCEL, INVITE, MESSAGE, OPTIONS, REFER, PRACK\r\n" +
				"Content-Length: 0\r\n\r\n";
		
		
		System.out.println("Sending re-auth message \n" + registerMessage);
		
		this.sendInfo(registerMessage);
		
		String response = this.receiveInfo();
		
		System.out.println("Served responded " + response);
		
		checkregisterResult(response, login, localIP);
	}
	
	private void checkregisterResult(String serverResult, String login, String localIP) throws IOException{
		//Not authorized
		if (serverResult.indexOf("401 Unauthorized") >= 0){
			System.err.println("Server returned a non-authorized response");
		}else if (serverResult.indexOf("200 OK") >= 0) {
			System.out.println("Auth ok, starting listening process");
			new CallReceiver(this.socket, login, localIP).receive();
		}else{
			System.err.println("Server returned a weird response");
		}
	}
	
	private void unregister() throws NoSuchAlgorithmException, IOException{
		System.out.println("Unregistering!, please wait!!!");
		//Calls register with 0 time
		this.register(parameters.get("localIP"), parameters.get("login"), parameters.get("pass"), parameters.get("nonce"), 
				parameters.get("attempts"), parameters.get("ncallId"), parameters.get("nTag"), "0");
		this.parameters.clear();
	}
	
	private void handshakeStart(String localIP, String login, String pass, int attempts) throws IOException, NoSuchAlgorithmException{
		
		//Random identifiers
		int nTag, ncallId;
		nTag = (int)(32000 * Math.random());
		ncallId = (int)(10000 * Math.random());
		long expiration  = 1200;
		
		String handShakeStartMessage = 
				"REGISTER sip:" + serverIP + " SIP/2.0\r\n" + 
				"Via: SIP/2.0/UDP " + localIP +":" + localPort + ";rport;branch=z9hG4bK21898\r\n" +
				"From: <sip:" + login + "@movistar.es>;tag=" + nTag + "\r\n" +
				"To: <sip:" + login + "@movistar.es>\r\n" +
				"Call-ID: " + ncallId + "@" + localIP +"\r\n" +
				"CSeq: " + attempts+" REGISTER\r\n" +
				"Contact: <sip:" + login + "@" + localIP+ ":" + localPort + ">\r\n" +
				"Max-Forwards: 70\r\n" +
				"Expires: " + expiration + " \r\n" +
				"Allow-Events: presence\r\n" +
				"Event: registration\r\n" +
				"User-Agent: Intellivic/PC\r\n" +
				"Allow: ACK, BYE, CANCEL, INVITE, MESSAGE, OPTIONS, REFER, PRACK\r\n" +
				"Content-Length: 0\r\n\r\n";
					
		//this.socket.connect(new InetSocketAddress(serverIP , 5070));
		this.sendInfo(handShakeStartMessage);

		System.out.println("Request sent " + handShakeStartMessage);
	
		String received  = this.receiveInfo();
		
		//Already registered!!!
		if (received.indexOf("SIP/2.0 200 OK")>=0){
			System.out.println("Already resgitered!");
		}else{
			System.out.println("Response  received " + received);
			
			String nonce = getNonce(received);
			
			
			//Go further on the registration process
			this.parameters.put("localIP", localIP);
			this.parameters.put("login", login);
			this.parameters.put("pass", pass);
			this.parameters.put("nonce", nonce);
			this.parameters.put("attempts", ""+attempts);
			this.parameters.put("ncallId", ""+ncallId);
			this.parameters.put("nTag", ""+nTag);
			this.parameters.put("expiration", ""+nTag);
			
			this.register(parameters.get("localIP"), parameters.get("login"), parameters.get("pass"), parameters.get("nonce"), 
					parameters.get("attempts"), parameters.get("ncallId"), parameters.get("nTag"), parameters.get("expiration"));			
		}
		
	}
	
	private String getNonce(String received){
		String result = received.substring(received.indexOf("nonce") + 7 );
		result = result.substring(0, result.indexOf("\"")  );
		
		return result;
	}
	
	
	
	public static void main(String args[])throws Exception{
		System.out.println("Usage is program localIP localHostName");
		
		for(String current : args) System.out.println("Provided arg " + current);
		
		String localIP =  args[0];
			//"87.221.119.80";
 
		String login = "689311958" ;
		String pass = "606657307" ;
		int attempts = 1;
		CallHandshakeManager callHandshakeManager = new CallHandshakeManager(args[1]);
		callHandshakeManager.handshakeStart(localIP, login, pass, attempts);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() { System.out.println("Shutdown!!!!") ;if (CallHandshakeManager.instance != null)
				try {
					CallHandshakeManager.instance.unregister();
				} catch (Exception e) {
				} }
		});

		
	}
}
