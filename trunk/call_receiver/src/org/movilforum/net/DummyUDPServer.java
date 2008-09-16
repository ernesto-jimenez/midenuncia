package org.movilforum.net;

import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class DummyUDPServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		DatagramSocket ss = new DatagramSocket(4444);
		
		//ss.bind(new InetSocketAddress("localhost", 9999));
		
		byte [] buff = new byte[4096];
		DatagramPacket datagramPacket = new DatagramPacket(buff,buff.length);
		
		System.out.println("Listening");
		
		ss.receive(datagramPacket);

		InetAddress address = datagramPacket.getAddress();
		
		System.out.println("Received " + new String(datagramPacket.getData()) + " from " + address);
		
		//Sending
		FileInputStream fis = new FileInputStream("/tmp/video.3gp");
		int available = -1;
		while((available = fis.read(buff))>=0){
			datagramPacket.setAddress(address);
			datagramPacket.setData(buff,0, available);
			
			ss.send(datagramPacket);
		}
		
	}

}
