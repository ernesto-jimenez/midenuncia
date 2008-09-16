package org.movilforum.net.media;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Performs audio conversion from received data to wav file format
 * @author dave
 *
 */
public class AudioConversor {

	static byte head[]={0x52,(byte) 0x49,(byte) 0x46,(byte) 0x46,(byte) 0xA4,(byte) 0x95,(byte) 0x08,(byte) 0x00,(byte)
			0x57,(byte) 0x41,(byte) 0x56,(byte) 0x45,(byte) 0x66,(byte) 0x6D,(byte) 0x74,(byte) 0x20,(byte)
			0x10,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x01,(byte) 0x00,(byte) 0x01,(byte) 0x00,(byte)
			0x40,(byte) 0x1F,(byte) 0x00,(byte) 0x00,(byte) 0x80,(byte) 0x3E,(byte) 0x00,(byte) 0x00,(byte)
			0x02,(byte) 0x00,(byte) 0x10,(byte) 0x00,(byte) 0x64,(byte) 0x61,(byte) 0x74,(byte) 0x61};
	
	public static void convert(InputStream input, OutputStream output) throws IOException{
		output.write(head);
		int available = -1;
		byte [] buffer = new byte[4096];
		while((available = input.read(buffer)) >= 0){
			output.write(buffer,0 , available);
		}
		
		output.flush();
		output.close();
	}
	
	public static void main(String args[]) throws IOException{
		InputStream source = new FileInputStream("/home/dave/telefonica-cpp/testfiles/tmp/call_from_606657307at1220795941031.raw");
		OutputStream dest = new FileOutputStream("/tmp/out.wav");
		
		convert(source, dest);
	}
}
