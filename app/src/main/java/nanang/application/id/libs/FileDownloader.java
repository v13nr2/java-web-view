package nanang.application.id.libs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloader {
    
	public static void downloadFile(String fileURL, File directory){
    	try {
    		FileOutputStream file = new FileOutputStream(directory);
    		URL url = new URL(fileURL);
    		HttpURLConnection connection = (HttpURLConnection) url .openConnection();
    		connection .setRequestMethod("GET");
    		connection .setDoOutput(true);
    		connection .connect();
    		InputStream input = connection .getInputStream();
    		byte[] buffer = new byte[1024];
    		int len = 0;
    		while ((len = input .read(buffer)) > 0) {
    			file .write(buffer, 0, len );
    		}
    	  
    		file .close();
    	  
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
}