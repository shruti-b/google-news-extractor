import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import de.l3s.boilerpipe.BoilerpipeProcessingException;


public class PastArticles {
	
	public static String convertDoubleQuotes(String oldQueryWord)
	{
		String[] queryWordParts = oldQueryWord.split("\"");
		String newQueryWord = queryWordParts[0];
		for(int j=1; j < queryWordParts.length; j++)
		{
			newQueryWord += "%22" + queryWordParts[j];
		}
		return newQueryWord;
	}
	
	public static String convertSpaces(String oldQueryWord)
	{
		String[] queryWordParts = oldQueryWord.split(" ");
		String newQueryWord = queryWordParts[0];
		for(int j=1; j < queryWordParts.length; j++)
		{
			newQueryWord += "+" + queryWordParts[j];
		}
		return newQueryWord;
	}
	
	
	public static void main(String[] args) throws IOException, BoilerpipeProcessingException
	{
		if(args.length == 0)
		{
			System.out.println("No query supplied Date Format = YYYY MM DD");
			return;
		}
		String query = "";
		
		String startYear = "";
		String startMonth = "";
		String startDay = "";
		
		String endYear = "";
		String endMonth = "";
		String endDay = "";

		
		int i = 0;
		while(i< args.length)
		{
			System.out.println(args[i]);
			if(args[i].contentEquals("-query"))
			{
				String oldQueryWord = args[++i];
				String newQueryWord = convertDoubleQuotes(oldQueryWord);
				query = newQueryWord;
				while(args[++i].charAt(0) != '-' )
				{
					oldQueryWord = args[i];
					//convert " to %22
					newQueryWord = convertDoubleQuotes(oldQueryWord);
					query += "+" + newQueryWord;
					//System.out.println(query);
					
				}
			}
			else if(args[i].contentEquals("-start"))
			{
				try
				{
					startYear = args[++i];
					startMonth = args[++i];
					startDay = args[++i];
					i++;
				}catch(Exception e)
				{
					System.out.println("Arguments wrong:");
					return;
				}
			}
			else if(args[i].contentEquals("-end"))
			{
				try
				{
					endYear = args[++i];
					endMonth = args[++i];
					endDay = args[++i];
					i++;
				}catch(Exception e)
				{
					System.out.println("Arguments wrong:");
					return;
				}
			}
			
		}
			
		
		String urlquery = "https://www.google.com/search?hl=en&gl=us&tbm=nws&q=" + query + "&oq=" + query + "&tbs=cdr%3A1%2Ccd_min%3A" + startMonth + "%2F" + startDay + "%2F" + startYear + "%2Ccd_max%3A" + endMonth + "%2F" + endDay + "%2F" + endYear;
		System.out.println(urlquery);
		
		//URL url = new URL(urlquery);
		//URLConnection connection = url.openConnection();
		
		Document doc = Jsoup.connect(urlquery).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_3) AppleWebKit/536.28.10 (KHTML, like Gecko) Version/6.0.3 Safari/536.28.10").referrer("http://www.google.com").get();
		//System.out.println(doc);
		
		Elements links = doc.select("a[href]");
		int count =0;
		for(int j=0; j<links.size(); j++)
		{
			String potentialLink = links.get(j).attr("href");
			System.out.println(potentialLink);
			int ind = potentialLink.indexOf("url=http");
			System.out.println("ind = " + ind);
			if(ind != -1)
			{
				int endIndex = potentialLink.indexOf("&rct=");
				String newsURL = potentialLink.substring(ind+1, endIndex);
				System.out.println(newsURL +"\n");
				try
				{
					String mainnewsSent = NewsExtractor.getMainSentence(newsURL);
					System.out.println(mainnewsSent+"\n");
					count++;
				}catch(Exception e)
				{
					System.out.println(e);
				}
			}
			
		}
		System.out.println(count);
	}
}
