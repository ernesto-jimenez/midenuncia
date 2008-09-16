package org.movilforum.net.media;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ControlManager implements Runnable{

	private DatagramSocket socket; 
	private String managerName;
	
	public ControlManager(DatagramSocket socket, String managerName) {
		super();
		this.socket = socket;
		this.managerName = managerName;
		
		new Thread(this).start();
	}
	public void run() {
		DatagramPacket pack = new DatagramPacket(new byte[1024], 1024);
		
		while(!this.socket.isClosed()){
			try {
				System.out.println("Waiting for receive data from " + managerName);
				this.socket.receive(pack);
				System.out.println("Receiving from " + managerName + "content " + pack.getLength());
				System.out.println("Data is " + new String(pack.getData()));
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
	}
}
