package org.movilforum.net.media;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class VideoStreamManager extends StreamManager implements Runnable{
	
	 
	byte pDataVideo[]={(byte)0x80,(byte) 0xc9,(byte) 0x00,(byte) 0x01,(byte) 0x73,(byte) 0xaa,(byte) 0xdf,(byte) 0xe2,(byte)
			0x81,(byte) 0xca,(byte) 0x00,(byte) 0x06,(byte) 0x52,(byte) 0xb1,(byte) 0x9e,(byte) 0xd0,(byte)
			0x01,(byte) 0x0e,(byte) 0x46,(byte) 0x72,(byte) 0x61,(byte) 0x6e,(byte) 0x5f,(byte) 0x6d,(byte)
			0x40,(byte) 0x46,(byte) 0x72,(byte) 0x61,(byte) 0x6e,(byte) 0x5f,(byte) 0x6d,(byte) 0x31,(byte)
			0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x81,(byte) 0xcb,(byte) 0x00,(byte) 0x01,(byte)
			0x73,(byte) 0xaa,(byte) 0xdf,(byte) 0xe2};
	
	byte pDataVideoData[]={(byte)0x80,(byte) 0x22,(byte) 0x8e,(byte) 0x9c,(byte) 0xef,(byte) 0xdb,(byte) 0xaf,(byte) 0x08,(byte)
			0x73,(byte) 0xaa,(byte) 0xdf,(byte) 0xe2,(byte) 0x00,(byte) 0x40,(byte) 0x00,(byte) 0x00,(byte)
			0x00,(byte) 0x80,(byte) 0x02,(byte) 0x08,(byte) 0x08};

	public VideoStreamManager(String basePath, String remotePort, DatagramSocket socket , DatagramSocket controlSocket) {
		super(basePath, remotePort, socket, controlSocket);
	}

	@Override
	public void startTransMission(String from) throws IOException {
		super.startTransMission(from, "263");
		new Thread(this).start();
	}

	public void run() {
		//Sends the video control data
		try{
			this.sendData(pDataVideo, Integer.parseInt(this.remotePort) + 1);
			new ControlManager(this.controlSocket, "Video");
			
			//The video data itself
			this.sendData(pDataVideoData, Integer.parseInt(this.remotePort));
			
			while(!this.ended) {
				byte [] data = this.receiveData(this.socket);
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void terminate() throws IOException {
		byte pDataVideo[]={(byte)0x80,(byte) 0xc9,(byte) 0x00,(byte) 0x01,(byte) 0x73,(byte) 0xaa,(byte) 0xdf,(byte) 0xe2,(byte)
				0x81,(byte) 0xca,(byte) 0x00,(byte) 0x06,(byte) 0x52,(byte) 0xb1,(byte) 0x9e,(byte) 0xd0,(byte)
				0x01,(byte) 0x0e,(byte) 0x53,(byte) 0x49,(byte) 0x50,(byte) 0x54,(byte) 0x53,(byte) 0x54,(byte)
				0x40,(byte) 0x53,(byte) 0x49,(byte) 0x50,(byte) 0x54,(byte) 0x53,(byte) 0x54,(byte) 0x31,(byte)
				0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x81,(byte) 0xcb,(byte) 0x00,(byte) 0x01,(byte)
				0x73,(byte) 0xaa,(byte) 0xdf,(byte) 0xe2};
		this.controlSocket.send(new DatagramPacket(pDataVideo, pDataVideo.length));
		this.controlSocket.close();
		this.socket.close();
	}

	@Override
	protected byte[] transformDataToBeConverted(byte[] data, int length) {
		byte[] result = Arrays.copyOf(data, length);
		return result;
	}

}
