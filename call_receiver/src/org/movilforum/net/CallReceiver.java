package org.movilforum.net;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.movilforum.net.media.AudioStreamManager;
import org.movilforum.net.media.VideoConversor;
import org.movilforum.net.media.VideoStreamManager;


/**
 * Receives calls!
 * @author dave
 *
 */
public class CallReceiver extends UDPBasedClass{

	private boolean hang = false;
	private String login;
	private String localIP;
	
	private DatagramSocket audioSocket, audioSocketCtrl, videoSocket, videoSocketCtrl;
	
	private AudioStreamManager audioManager ;
	private VideoStreamManager videoStreamManager ;
	
	private int nToTag;


	public CallReceiver(DatagramSocket socket, String login, String localIP) {
		super();
		this.socket = socket;
		this.login = login;
		this.localIP = localIP;
	}
	
	public void receive() throws IOException{
		while(!hang){
			String info  = this.receiveInfo();
			if (info.startsWith("INVITE")){
				this.startInvitation(info);
			}else if (info.startsWith("ACK")){
				this.startTransmission(info);
			}else if (info.startsWith("BYE")){
				this.endTransmission(info);
			} else{
				System.err.println("Received unknown message , discarding " +  info);
			}
		}
	}
	
	/**
	 * starts protocol negotiation
	 * @param message
	 * @throws IOException 
	 */
	private void startInvitation(String message) throws IOException{
		
		/**
		 * INVITE sip:689311958@movistar.es:5061 SIP/2.0
			Via: SIP/2.0/UDP 195.76.180.160:5060;branch=z9hG4bK1o36r610dgthja4uq4c0.1
			From: <sip:606657307@movistar.es>;tag=SDampq701-0082-00001124-0ec7
			To: <sip:689311958@movistar.es:5060;user=phone>
			Call-ID: SDampq701-9df65cd29dc85b86a9e7e4576d57bd23-v3000i1
			CSeq: 1 INVITE
			Max-Forwards: 26
			Contact: "sipua" <sip:606657307@195.76.180.160:5060;transport=udp>
			Allow: INVIE,REGISTER,ACK,OPTIONS,BYE,INFO,REFER,NOTIFY,SUBSCRIBE,MESSAGE,CANCEL,PRACK,UPDATE
			Content-Length: 0

		 */
		System.out.println("Initiation message is " + message);
		String callId = getCallId(message);
		String branch = getBranch(message);
		String from = getFrom(message);
		String fromTag = getFromTag(message);
		
		System.out.println("---------------------------------------------------");
		System.out.println("login " + this.login );
		System.out.println("callId " + callId );
		System.out.println("branch " + branch );
		System.out.println("from " + from );
		System.out.println("fromTag " + fromTag );
		System.out.println("\n\n---------------------------------------------------\n\n");
		
		String response =  "SIP/2.0 100 Trying\r\n" + 
				"Via: SIP/2.0/UDP 195.76.180.160:5060;branch=" + branch + "\r\n" +
				"From: <sip:" + from + "@movistar.es>;tag=" + fromTag + "\r\n" +
				"To: <sip:" + login + "@movistar.es:5060;user=phone>\r\n" +
				"Call-Id: " + callId + "\r\n" +
				"CSeq: 1 INVITE\r\n" +
				"Content-Length: 0\r\n\r\n";
		
		System.out.println("Sending trying \n" + response);
		System.out.println("----------------------------");
		
		this.sendInfo(response);
		
		nToTag=(int) (10000*Math.random());
		
		response =  "SIP/2.0 180 Ringing\r\n" +
					"Via: SIP/2.0/UDP 195.76.180.160:5060;branch=" + branch + "\r\n" +
					"From: <sip:" + from + "@movistar.es>;tag=" + fromTag + "\r\n" +
					"To: <sip:" + login + "@movistar.es:5060;user=phone>;tag=" + nToTag +"\r\n" +
					"Call-Id: " + callId + "\r\n" + 
					"CSeq: 1 INVITE\r\n" + 
					"Contact: <sip:" + login + "@" + this.localIP + ":5061>\r\n" + 
					"Content-Length: 0\r\n\r\n";
		
		System.out.println("Sending ringing \n" + response);
		System.out.println("----------------------------");
		this.sendInfo(response);
		
		//The stream negotiation is started
		int videoPort = 7779, audioport = 7777;
		this.negotiateStreaming(audioport, videoPort, branch, from, fromTag, nToTag, callId);
	}
	
	private void createSockets(int audioport, int videoport) throws SocketException{
		System.out.println("Starting services at audio " + audioport + "  " + (audioport + 1) + " video " + videoport + ":" + (videoport +1));
		this.audioSocket = new DatagramSocket(audioport);
		this.audioSocketCtrl = new DatagramSocket(audioport + 1);
		
		this.videoSocket = new DatagramSocket(videoport);
		this.videoSocketCtrl = new DatagramSocket(videoport + 1);
	}
	
	private void negotiateStreaming(int audioport, int videoport, String branch, String from, String fromTag, int nToTag, String callId) throws IOException{
		int rand1 = (int) (30000 * Math.random()), rand2 = (int) (30000 * Math.random());
		
		String negotiationFormat =  "v=0\r\n" +
				"o=- "+ rand1 +" " + rand2+ " IN IP4 " + this.localIP + "\r\n" +
				"s=-\r\n" +
				"c=IN IP4 " + this.localIP + "\r\n" +
				"t=0 0\r\n" +
				"m=audio " + audioport + " RTP/AVP 101 99 0 8 104\r\n" +
				"a=rtpmap:101 speex/16000\r\n" +
				"a=fmtp:101 vbr=on;mode=6\r\n" +
				"a=rtpmap:99 speex/8000\r\n" + 
				"a=fmtp:99 vbr=on;mode=3\r\n" +
				"a=rtpmap:0 PCMU/8000\r\n" +
				"a=rtpmap:8 PCMA/8000\r\n" +
				"a=rtpmap:104 telephone-event/8000\r\n" +
				"a=fmtp:104 0-15\r\n" +
				"m=video " + videoport + " RTP/AVP 97 34\r\n" +
				"a=rtpmap:97 MP4V-ES/90000\r\n" +
				"a=fmtp:97 profile-level-id=1\r\n" +
				"a=rtpmap:34 H263/90000\r\n" +
				"a=fmtp:34 QCIF=2 SQCIF=2/MaxBR=560\r\n" ;
		
		String negotiationMessage = 
				"SIP/2.0 200 OK\r\n" + 
				"Via: SIP/2.0/UDP 195.76.180.160:5060;branch=" + branch + "\r\n" +
				"From: <sip:" + from + "@movistar.es>;tag=" + fromTag + "\r\n" +
				"To: <sip:" + login + "@movistar.es:5060;user=phone>;tag=" + nToTag + "\r\n" +
				"Call-Id: " + callId + "\r\n" +
				"CSeq: 1 INVITE\r\n" + 
				"Contact: <sip:" + this.login+ "@" + this.localIP + ":5061>\r\n" +
				"User-Agent: Intellivic/PC\r\n" +
				"Supported: replaces\r\n" +
				"Allow: ACK, BYE, CANCEL, INVITE, OPTIONS\r\n" +
				"Content-Type: application/sdp\r\n" +
				"Accept: application/sdp, application/media_control+xml, application/dtmf-relay\r\n" +
				"Content-Length: " + negotiationFormat.length()+"\r\n\r\n" + negotiationFormat;

		
		System.out.println("Sending protocol negotiation message" + negotiationMessage);
		//Send negotiation
		this.sendInfo(negotiationMessage);
		
		this.createSockets(audioport, videoport);
		//receive the message
		this.receive();		
	}
	
	private String getFrom(String message){
		
		String result = message.substring(message.indexOf("From:") + 11);
		result = result.substring(0, result.indexOf('@')).trim();
		
		return result;
	}
	
	private String getFromTag(String message){
		String result = message.substring(message.indexOf("tag=") + 4);
		result = result.substring(0, result.indexOf('\n')).trim();
		return result;
	}
	
	private String getBranch(String message){
		String branch = message.substring(message.indexOf("branch=") + 7);
		branch = branch.substring(0,branch.indexOf('\n')).trim();
		
		return branch;
	}
	
	private String getCallId(String message){
		String callId = message.substring(message.indexOf("Call-ID: ") + 9);
		callId = callId.substring(0, callId.indexOf('\n')).trim();
		
		return callId;
	}
	
	/**
	 * manages transmission
	 * @param message
	 * @throws IOException 
	 */
	private void startTransmission(String message) throws IOException{
		String remoteAudioPort, remoteVideoPort;
		System.out.println("Initialization message sent by server is " +  message);
		remoteAudioPort = getAudioPort(message);
		remoteVideoPort = getVideoPort(message);
		
		String from = getFrom(message);
		
		audioManager = new AudioStreamManager("/tmp/", remoteAudioPort, this.audioSocket, this.audioSocketCtrl);
		videoStreamManager = new VideoStreamManager("calls/", remoteVideoPort, this.videoSocket, this.videoSocketCtrl);
		
		audioManager.startTransMission(from);
		videoStreamManager.startTransMission(from);
		
		System.out.println("Waiting for ending signal");
		this.receive();	
	}
	
	
	
	private String getVideoPort(String message) {
		String result = message.substring(message.indexOf("m=audio ") + 8);
		result = result.substring(0, result.indexOf(' ')).trim();
		
		return result;
	}

	private String getAudioPort(String message) {
		String result = message.substring(message.indexOf("m=video ") + 8);
		result = result.substring(0, result.indexOf(' ')).trim();
		
		return result;
	}

	/**
	 * Ends the given transmission
	 * @param message
	 * @throws IOException 
	 */
	private void endTransmission(String message) throws IOException{
		
		System.out.println("Closing comms");
		
		String byeMessage =  "SIP/2.0 200 OK\r\n" + 
				"Via: SIP/2.0/UDP 195.76.180.160:5060;branch=" + this.getBranch(message)+ "\r\n" +
				"From: <sip:" + getFrom(message)+ "@movistar.es>;tag=" + getFromTag(message)+ "\r\n"+ 
				"To: <sip:" + this.login + "@movistar.es:5060;user=phone>;tag=" + nToTag+ "\r\n" +
				"Call-Id: " + getCallId(message) + "\r\n" +
				"CSeq: 2 Bye\r\n" +
				"Content-Length: 0\r\n\r\n";
		this.sendInfo(byeMessage);
		
		System.out.println("Closed, closing audio/video");
		
		this.audioManager.endTransmission("");
		this.videoStreamManager.endTransmission("");
		
		
		System.out.println("Closed audio/video");
		
		VideoConversor.convert();
	}
}
