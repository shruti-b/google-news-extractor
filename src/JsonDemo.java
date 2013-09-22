import java.io.*;
import java.util.Scanner;


import java.io.InputStream;

import org.json.JSONObject;








public class JsonDemo {

	/**
	 * @param args
	 * @throws IOException 
	 */
	// Ne
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
			String filepath="/Users/shrutib/Desktop/news_obama.json";
			String textObama = new Scanner( new File(filepath) ).useDelimiter("\\A").next();
			//String str = "{'string':'JSON', 'integer': 1, 'double': 2.0, 'boolean': true}";  
			
			JSONObject json = new JSONObject(textObama);
			System.out.println();
			System.out.println(json.getJSONObject("responseData").getJSONArray("results").getJSONObject(0).getJSONArray("relatedStories").optJSONObject(0).getString("unescapedUrl"));
			System.out.println(json.getJSONObject("responseData").getJSONArray("results").getJSONObject(0).getJSONArray("relatedStories").getJSONObject(0).getString("unescapedUrl"));
			
			
			
	        
	
	}

}
