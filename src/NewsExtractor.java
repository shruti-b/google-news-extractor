import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;



public class NewsExtractor {
	public static ArticlesTimeRange pastRelExtractor;
	

	/*public static void getPastArticles(String query, String startYear, String endYear) throws IOException
	{
		String startDay = "01";
		String startMonth = "01";
		String endDay = "31";
		String endMonth = "12";
		
	String urlquery = "https://www.google.com/search?hl=en&gl=us&tbm=nws&q=" + query + "&oq=" + query + "&tbs=cdr%3A1%2Ccd_min%3A" + startMonth + "%2F" + startDay + "%2F" + startYear + "%2Ccd_max%3A" + endMonth + "%2F" + endDay + "%2F" + endYear;
	System.out.println(urlquery);
	
	//URL url = new URL(urlquery);
	//URLConnection connection = url.openConnection();
	Connection conn = Jsoup.connect(urlquery).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_3) AppleWebKit/536.28.10 (KHTML, like Gecko) Version/6.0.3 Safari/536.28.10").referrer("http://www.google.com"); 
	conn.timeout(300000);
	Document doc = conn.get();
	//System.out.println(doc);
	
	Elements links = doc.select("a[href]");
	//System.out.println("No of links = "+ links.size());
	int count =0;
	for(int j=10; j<50; j++) // links.size()
	{
		String potentialLink = links.get(j).attr("href");
		
		int ind = potentialLink.indexOf("url=http");
		System.out.println("ind = "+ ind);
		if(ind != -1)
		{
			int endIndex = potentialLink.indexOf("&rct=");
			String newsURL = potentialLink.substring(ind+4, endIndex);
			System.out.println(newsURL +"\n");
			try
			{
				String mainnewsSent = getMainSentence(newsURL);
				System.out.println(mainnewsSent+"\n");
				count++;
				
			}catch(Exception e)
			{
				System.out.println(e);
			}
		}
		
	}
	System.out.println(links.size()+" " + count);
}
	*/
	
	/*public static String getNamedEntitiesQuery(AbstractSequenceClassifier<CoreLabel> classifier, String sent) {
		

		String namedEntities = "";
		List<List<CoreLabel>> nerSent = classifier.classify(sent);
		for (List<CoreLabel> lcl : nerSent) {
			int i=0;
        
			while(i< lcl.size()) {
				CoreLabel cl = lcl.get(i);
				//System.out.println(cl.word());
      	  
				String nerTag = cl.get(CoreAnnotations.AnswerAnnotation.class);
				if( nerTag.contentEquals("PERSON") )
				{
					namedEntities += cl.word()+"+";
				}
      	  
				i++;
			}
          
        
      }
      
      System.out.println("Named Ent Query = " + namedEntities);
      return namedEntities;
    } */
	
	
	static String getMainSentence(String newsUrl) throws MalformedURLException, BoilerpipeProcessingException
	{
		URL url = new URL(newsUrl);
		
		String text = ArticleExtractor.INSTANCE.getText(url);
		String[] sentences = text.split("\n");
		int checkFlag = 0;
		int lastCharFullStop = 0;
		int acceptable = 10;
		
		for(int i=0; i < sentences.length ; i++ )
		{
			String[] words = sentences[i].split(" ");
			if(words.length < 1)
				continue;
			HashSet<String> wordsset = new HashSet<String>();
			for(String word : words)
			{
				wordsset.add(word);
			}
			int len = words.length;
			if(wordsset.contains("Copyright") )
			{	
				//System.out.println("copy");
				continue;
			}
			if(sentences[i].length() > 0 && sentences[i].charAt(sentences[i].length()-1) == '.')
			{
				lastCharFullStop = 1;
			}
			else
			{
				lastCharFullStop = 0;
			}
			if(len >= acceptable && lastCharFullStop == 1 )
			{
				if(checkFlag == 1)
				{
					return sentences[i-1];
				}
				else
				{
					checkFlag = 1;
				}
			}
			else
			{
				checkFlag = 0;
			}
		}
		if(sentences.length > 4)
			return "Default ==> " + sentences[4];
		else return "Default ==> " + sentences[sentences.length-1];
	}
	/**
	 * @param args
	 * @throws IOException 
	 * @throws BoilerpipeProcessingException 
	 */
	//NewsExtractor <keywords to be searched>
	public static void main(String[] args) throws IOException, BoilerpipeProcessingException {
		// TODO Auto-generated method stub
		
		
		if(args.length == 0)
		{
			System.out.println("No query supplied");
			return;
		}
		String query = args[0];
		for(int i=1; i < args.length; i++)
			query += "+" + args[i] ;
		//System.out.println(query);
		
		//-------------------------------
		
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
		
		String ip = in.readLine();
		//System.out.println(ip);
		
		//-----------------------------
		
		String urlquery = "https://ajax.googleapis.com/ajax/services/search/news?" + "v=1.0&q=" + query + "&rsz=8" +"&userip=" + ip;
		//System.out.println(urlquery);
		
		URL url = new URL(urlquery);
		URLConnection connection = url.openConnection();
		
		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		while((line = reader.readLine()) != null) {
		 builder.append(line);
		}
		//System.out.println(builder.toString());
		JSONObject json = new JSONObject(builder.toString());
		
		JSONArray resultsArray = json.getJSONObject("responseData").getJSONArray("results");
		
		pastRelExtractor = new ArticlesTimeRange(query); //to get past articles
		
		for(int i=0; i< 6; i++) //resultsArray.length()
		{
			String mainnews = resultsArray.getJSONObject(i).getString("unescapedUrl");
			try
			{
			String mainnewsSent = getMainSentence(mainnews);
			System.out.println(mainnewsSent);
			System.out.println("---------");
			// function call to get related sentences
			pastRelExtractor.getRelatedArticles(mainnewsSent);
			
			}catch(Exception e)
			{
				System.err.println("Exception: " + e.getMessage());
			}
			
			if(resultsArray.getJSONObject(i).has("relatedStories"))
			{
				JSONArray relatedStories = resultsArray.getJSONObject(i).getJSONArray("relatedStories");
				for(int j=0; j<relatedStories.length(); j++)
				{
					String relStoryUrl = relatedStories.getJSONObject(j).getString("unescapedUrl");
					//System.out.println(relStoryUrl);
					try
					{
						String mainSentence = getMainSentence(relStoryUrl);
						System.out.println(mainSentence);
						System.out.println("---------");
						// function call to get related sentences
						pastRelExtractor.getRelatedArticles(mainSentence);
					}catch(Exception e)
					{
						System.err.println("Exception: " + e.getMessage());
					}
				}
				System.out.println("---");
			}
			
		}
		
		
	}

}
