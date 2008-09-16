package org.movilforum.net;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


/**
 * Common features for udp classes
 * @author dave
 *
 */
public abstract class UDPBasedClass {

	protected DatagramSocket socket;
	
	protected Integer localPort = 5061;
	protected String serverIP = "195.76.180.160";
	protected String serverPort = "5060";
	
	protected void sendInfo(String info) throws IOException{
		InetAddress server = InetAddress.getByName("telefonica"); //telefonica
		Integer serverPort = Integer.parseInt(this.serverPort);
		DatagramPacket dp = new DatagramPacket(info.getBytes(), info.getBytes().length, server , serverPort);
		this.socket.send(dp);
	}
	
	protected String receiveInfo() throws IOException{
		byte[] buffer = new byte[4096];
		DatagramPacket dp = new DatagramPacket(buffer , buffer.length);
		this.socket.receive(dp);
		
		return new String(dp.getData());
	}
}
