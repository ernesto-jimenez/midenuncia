package org.movilforum.net.media;

import java.io.IOException;

public class VideoConversor {
	
	public static void convert() throws IOException{
		ProcessBuilder pb = new ProcessBuilder("/home/davebcn/convert.sh");
		 pb.start();
	}

}
