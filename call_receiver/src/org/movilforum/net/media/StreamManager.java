package org.movilforum.net.media;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public abstract class StreamManager {

	protected String basePath;
	protected String remotePort;
	protected DatagramSocket socket;
	protected DatagramSocket controlSocket;
	protected FileOutputStream currentFile = null;
	
	protected boolean ended = false;

	public StreamManager(String basePath, String remotePort, DatagramSocket socket, DatagramSocket controlSocket) {
		this.basePath = basePath;
		this.remotePort = remotePort;
		this.socket = socket;
		this.controlSocket = controlSocket;
	}
	
	public abstract void startTransMission(String from) throws IOException;
	
	protected void startTransMission(String from, String fileExtension) throws IOException{
		if (this.currentFile != null) throw new RuntimeException("A call is being processed, cannot process incoming call yet ");
		
		this.currentFile = new FileOutputStream(this.basePath + '/' + "call_from_" + from + "at" + System.currentTimeMillis() + "." + fileExtension);
	}
	
	public void saveContent(String from, byte[] content, int offset, int length) throws IOException{
		this.currentFile.write(content, offset, length);
	}
	
	protected void sendData(byte[] data, int port) throws IOException{
		DatagramPacket dataPacket = new DatagramPacket(data,data.length);
		dataPacket.setPort(port);
		dataPacket.setAddress(InetAddress.getByName("telefonica"));
		
		if (!this.socket.isClosed()) this.socket.send(dataPacket);
	}
	
	protected byte[] receiveData(DatagramSocket socket) throws IOException{
		byte [] buffer = new byte[1024]; //max is 16384
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		
		if (!this.socket.isClosed()){
			this.socket.receive(packet);
			//Data is saved
			byte []receivedData = transformDataToBeConverted(packet.getData(), packet.getLength());
			this.saveContent("",receivedData, 0, receivedData.length);
			
			return packet.getData();
		}
		return new byte[0];
		
	}
	
	/**
	 * Used to perform data conversions from received previously to be stored on the file
	 * @param data
	 * @param length
	 * @return
	 */
	protected abstract byte[] transformDataToBeConverted(byte[] data, int length);
	
	public void endTransmission(String from) throws IOException{
		//Ends transmission
		this.ended = true;
		//from is not used because currently only one concurrent tr can be made
		if (this.currentFile == null) throw new RuntimeException("No call is currently being made");
		
		this.currentFile.flush();
		this.currentFile.close();
		
		this.currentFile = null;
		
		this.socket.close();
		this.controlSocket.close();
	}
	
	protected abstract void terminate() throws IOException;
}
