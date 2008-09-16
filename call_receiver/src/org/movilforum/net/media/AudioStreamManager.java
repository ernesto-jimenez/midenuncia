package org.movilforum.net.media;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class AudioStreamManager extends StreamManager implements Runnable{
	
	//Wav files head
	static byte head[]={0x52,(byte) 0x49,(byte) 0x46,(byte) 0x46,(byte) 0xA4,(byte) 0x95,(byte) 0x08,(byte) 0x00,(byte)
		0x57,(byte) 0x41,(byte) 0x56,(byte) 0x45,(byte) 0x66,(byte) 0x6D,(byte) 0x74,(byte) 0x20,(byte)
		0x10,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x01,(byte) 0x00,(byte) 0x01,(byte) 0x00,(byte)
		0x40,(byte) 0x1F,(byte) 0x00,(byte) 0x00,(byte) 0x80,(byte) 0x3E,(byte) 0x00,(byte) 0x00,(byte)
		0x02,(byte) 0x00,(byte) 0x10,(byte) 0x00,(byte) 0x64,(byte) 0x61,(byte) 0x74,(byte) 0x61};

	
	//Audio Control
	byte pDataAudio[]={(byte) 0x80,(byte) 0xc9,(byte) 0x00,(byte) 0x01,(byte) 0x52,(byte) 0xb1,(byte) 0x9e,(byte) 0xd0,(byte)
						0x81,(byte) 0xca,(byte) 0x00,(byte) 0x06,(byte) 0x52,(byte) 0xb1,(byte) 0x9e,(byte) 0xd0,(byte)
						0x01,(byte) 0x0e,(byte) 0x46,(byte) 0x72,(byte) 0x61,(byte) 0x6e,(byte) 0x5f,(byte) 0x6d,(byte)
						0x40,(byte) 0x46,(byte) 0x72,(byte) 0x61,(byte) 0x6e,(byte) 0x5f,(byte) 0x6d,(byte) 0x31,(byte)
						0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x81,(byte) 0xcb,(byte) 0x00,(byte) 0x01,(byte)
						0x52,(byte) 0xb1,(byte) 0x9e,(byte) 0xd0};
	
	byte pDataAudioData[]={(byte) 0x80, (byte) 0x80, 0x04, (byte) 0xae, (byte) 0xbd, (byte) 0xb7, (byte) 0xc1, 0x40,
			0x52, (byte) 0xb1, (byte) 0x9e, (byte) 0xd0};	

	public AudioStreamManager(String basePath, String remotePort, DatagramSocket socket, DatagramSocket controlSocket) {
		super(basePath, remotePort, socket, controlSocket);
	}
	
	@Override
	public void startTransMission(String from) throws IOException {
		super.startTransMission(from, "raw");
		//Wav head
		this.currentFile.write(head);
		new Thread(this).start();
	}

	public void run() {
		//Send control data
		try {
			//Control data is sent to the remote port + 1
			int audioRemotePort = Integer.parseInt(this.remotePort);
			this.sendData(pDataAudio, audioRemotePort + 1);
			
			new ControlManager(this.controlSocket, "Audio");
			
			//Send the audio data itself
			this.sendData(pDataAudioData, audioRemotePort );
			
			while(!this.ended) {
				byte [] data = this.receiveData(this.socket);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}

	//Closes all comms but before sends an ending message on the control
	@Override
	protected void terminate() throws IOException {
		byte pDataAudio[]={(byte)0x80,(byte) 0xc9,(byte) 0x00,(byte) 0x01,(byte) 0x52,(byte) 0xb1,(byte) 0x9e,(byte) 0xd0,(byte)
				0x81,(byte) 0xca,(byte) 0x00,(byte) 0x06,(byte) 0x52,(byte) 0xb1,(byte) 0x9e,(byte) 0xd0,(byte)
				0x01,(byte) 0x0e,(byte) 0x53,(byte) 0x49,(byte) 0x50,(byte) 0x54,(byte) 0x53,(byte) 0x54,(byte)
				0x40,(byte) 0x53,(byte) 0x49,(byte) 0x50,(byte) 0x54,(byte) 0x53,(byte) 0x54,(byte) 0x31,(byte)
				0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x81,(byte) 0xcb,(byte) 0x00,(byte) 0x01,(byte)
				0x52,(byte) 0xb1,(byte) 0x9e,(byte) 0xd0};
		this.controlSocket.send(new DatagramPacket(pDataAudio, pDataAudio.length));
		this.controlSocket.close();
		this.socket.close();
	}

	@Override
	protected byte[] transformDataToBeConverted(byte[] data, int length) {
		//Converts raw to wav
		/*int btPayload=(data[0] & 0x7f);
		if (length > 12){
			int payloadLength = length - 12;
			byte[] result = new byte[2 * payloadLength];
			if (btPayload == 0){
				this.decodeULaw(data, 12, payloadLength, result);
			}else if (btPayload == 8){
				this.decodeALaw(data, 12, payloadLength, result);
			}else{
				System.out.println("Don't know which bit payload is " + btPayload);
			}
			return result;
		}
		//Nothing
		return new byte[0];*/
		//No conversion!
		byte[] result = Arrays.copyOf(data, length);
		return result;
	}
	
	private void decodeULaw(byte[] in, int start, int length, byte[] out){
		short pcm_u2lin[] = {
				-32124,-31100,-30076,-29052,-28028,-27004,-25980,-24956,-23932,-22908,-21884,
				-20860,-19836,-18812,-17788,-16764,-15996,-15484,-14972,-14460,-13948,-13436,
				-12924,-12412,-11900,-11388,-10876,-10364, -9852, -9340, -8828, -8316, -7932,
				 -7676, -7420, -7164, -6908, -6652, -6396, -6140, -5884, -5628, -5372, -5116,
				 -4860, -4604, -4348, -4092, -3900, -3772, -3644, -3516, -3388, -3260, -3132,
				 -3004, -2876, -2748, -2620, -2492, -2364, -2236, -2108, -1980, -1884, -1820,
				 -1756, -1692, -1628, -1564, -1500, -1436, -1372, -1308, -1244, -1180, -1116,
				 -1052,  -988,  -924,  -876,  -844,  -812,  -780,  -748,  -716,  -684,  -652,
				  -620,  -588,  -556,  -524,  -492,  -460,  -428,  -396,  -372,  -356,  -340,
				  -324,  -308,  -292,  -276,  -260,  -244,  -228,  -212,  -196,  -180,  -164,
				  -148,  -132,  -120,  -112,  -104,   -96,   -88,   -80,   -72,   -64,   -56,
				   -48,   -40,   -32,   -24,   -16,    -8,     0, 32124, 31100, 30076, 29052,
				 28028, 27004, 25980, 24956, 23932, 22908, 21884, 20860, 19836, 18812, 17788,
				 16764, 15996, 15484, 14972, 14460, 13948, 13436, 12924, 12412, 11900, 11388,
				 10876, 10364,  9852,  9340,  8828,  8316,  7932,  7676,  7420,  7164,  6908,
				  6652,  6396,  6140,  5884,  5628,  5372,  5116,  4860,  4604,  4348,  4092,
				  3900,  3772,  3644,  3516,  3388,  3260,  3132,  3004,  2876,  2748,  2620,
				  2492,  2364,  2236,  2108,  1980,  1884,  1820,  1756,  1692,  1628,  1564,
				  1500,  1436,  1372,  1308,  1244,  1180,  1116,  1052,   988,   924,   876,
				   844,   812,   780,   748,   716,   684,   652,   620,   588,   556,   524,
				   492,   460,   428,   396,   372,   356,   340,   324,   308,   292,   276,
				   260,   244,   228,   212,   196,   180,   164,   148,   132,   120,   112,
				   104,    96,    88,    80,    72,    64,    56,    48,    40,    32,    24,
				    16,     8,     0};
			
			try{
			for (int i = 0; i < length; i++)
			{
				//First byte is the less significant byte
				out[2 * i] = (byte)(pcm_u2lin[(byte)in[(byte)start + i]] & 0xff);
				//Second byte is the more significant byte
				out[2 * i + 1] = (byte)(pcm_u2lin[(byte)in[(byte)start + i]] >> 8);
			}
			}catch (IndexOutOfBoundsException e) {
				//System.out.println("Out of bounds " + e);
			}
	}
	
	private void decodeALaw(byte[] in, int start, int length, byte[] out)
	{
		short pcm_A2lin[] = {
			 -5504, -5248, -6016, -5760, -4480, -4224, -4992, -4736, -7552, -7296, -8064,
			 -7808, -6528, -6272, -7040, -6784, -2752, -2624, -3008, -2880, -2240, -2112,
			 -2496, -2368, -3776, -3648, -4032, -3904, -3264, -3136, -3520, -3392,-22016,
			-20992,-24064,-23040,-17920,-16896,-19968,-18944,-30208,-29184,-32256,-31232,
			-26112,-25088,-28160,-27136,-11008,-10496,-12032,-11520, -8960, -8448, -9984,
			 -9472,-15104,-14592,-16128,-15616,-13056,-12544,-14080,-13568,  -344,  -328,
			  -376,  -360,  -280,  -264,  -312,  -296,  -472,  -456,  -504,  -488,  -408,
			  -392,  -440,  -424,   -88,   -72,  -120,  -104,   -24,    -8,   -56,   -40,
			  -216,  -200,  -248,  -232,  -152,  -136,  -184,  -168, -1376, -1312, -1504,
			 -1440, -1120, -1056, -1248, -1184, -1888, -1824, -2016, -1952, -1632, -1568,
			 -1760, -1696,  -688,  -656,  -752,  -720,  -560,  -528,  -624,  -592,  -944,
			  -912, -1008,  -976,  -816,  -784,  -880,  -848,  5504,  5248,  6016,  5760,
			  4480,  4224,  4992,  4736,  7552,  7296,  8064,  7808,  6528,  6272,  7040,
			  6784,  2752,  2624,  3008,  2880,  2240,  2112,  2496,  2368,  3776,  3648,
			  4032,  3904,  3264,  3136,  3520,  3392, 22016, 20992, 24064, 23040, 17920,
			 16896, 19968, 18944, 30208, 29184, 32256, 31232, 26112, 25088, 28160, 27136,
			 11008, 10496, 12032, 11520,  8960,  8448,  9984,  9472, 15104, 14592, 16128,
			 15616, 13056, 12544, 14080, 13568,   344,   328,   376,   360,   280,   264,
			   312,   296,   472,   456,   504,   488,   408,   392,   440,   424,    88,
			    72,   120,   104,    24,     8,    56,    40,   216,   200,   248,   232,
			   152,   136,   184,   168,  1376,  1312,  1504,  1440,  1120,  1056,  1248,
			  1184,  1888,  1824,  2016,  1952,  1632,  1568,  1760,  1696,   688,   656,
			   752,   720,   560,   528,   624,   592,   944,   912,  1008,   976,   816,
			   784,   880,   848 };

		for (int i = 0; i < length; i++)
		{
			//First byte is the less significant byte
			out[2 * i] = (byte)(pcm_A2lin[(byte)in[i]] & 0xff);
			//Second byte is the more significant byte
			out[2 * i + 1] = (byte)(pcm_A2lin[(byte)in[i]] >> 8);
		}
	}
}
