import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticlesTimeRange {
	
	public AbstractSequenceClassifier<CoreLabel> classifier;
	public String originalQuery;
	public ArticlesTimeRange(String query){
		String serializedClassifier = "/Users/shruti/Downloads/stanford-ner-2013-04-04/classifiers/english.all.3class.distsim.crf.ser.gz";

		classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
		originalQuery = query;
	}
	
	public String concatRandom(String sentence)
	{
		String[] words = sentence.split(" ");
		double dice=0;
		for(int i= words.length-1; i>=0; i--)
		{
			String word = words[i];
			if(word.length() > 7)
			{
				dice = Math.random();
				if(dice < 0.3)
				{
					return word;
				}
			}
		}
		return "";
	}
	public void getPastArticles(String query, String startYear, String endYear) throws IOException
	{
		String startDay = "01";
		String startMonth = "01";
		String endDay = "31";
		String endMonth = "12";
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String urlquery = "https://www.google.com/search?hl=en&gl=us&tbm=nws&q=" + query + "&oq=" + query + "&tbs=cdr%3A1%2Ccd_min%3A" + startMonth + "%2F" + startDay + "%2F" + startYear + "%2Ccd_max%3A" + endMonth + "%2F" + endDay + "%2F" + endYear;
		System.out.println("URLQuery = "+ urlquery);
		//Document doc = Jsoup.connect(urlquery).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_3) AppleWebKit/536.28.10 (KHTML, like Gecko) Version/6.0.3 Safari/536.28.10").referrer("http://www.google.com").get();
		Document doc = Jsoup.connect(urlquery).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_3) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65").referrer("http://www.google.com").get();

		//System.out.println(doc);
		
		Elements links = doc.select("a[href]");
		
		for(int j=0; j<links.size(); j++)
		{
			String potentialLink = links.get(j).attr("href");
			
			if(potentialLink.contains(".google") == false && potentialLink.contains(".youtube") == false && potentialLink.contains(".blogger") == false && potentialLink.charAt(0) != '/')
			{
				if(potentialLink.startsWith("http") )
				{
					
					System.out.println(potentialLink);
					try {
						String mainnewsSent = NewsExtractor.getMainSentence(potentialLink);
						System.out.println(mainnewsSent+"\n");
						
						
					} catch (BoilerpipeProcessingException e) {
						// TODO Auto-generated catch block
						System.out.println(e);
					}
					
				}
			}
		}
	}

	public String getQuery(String sentence)
	{
		String nerInlineXml = classifier.classifyWithInlineXML(sentence);
		ArrayList<String> matches = new ArrayList<String>();
		Matcher m = Pattern.compile("<(PERSON|ORGANIZATION)>(.+?)</(PERSON|ORGANIZATION)>").matcher(nerInlineXml);
		while(m.find()) {
           matches.add(nerInlineXml.substring(m.start(2), m.end(2)));
        }
		System.out.println(matches);
		if(matches.size() < 2)
			return "";
		else
		{
			String query = "";
			for(String nerEl : matches)
			{
				nerEl = PastArticles.convertDoubleQuotes(nerEl);
				nerEl = PastArticles.convertSpaces(nerEl);
				//System.out.println(nerEl);
				query += nerEl + "+";
			}
			if(query.length() < originalQuery.length())
			{
				query += originalQuery;
				query += "+" + concatRandom(sentence);
			}
			else
			{
				query +=  concatRandom(sentence);
			}
			System.out.println("Query = " + query);
			return query;
		}
	}
	
	public void getRelatedArticles(String sentence)
	{
		String query = getQuery(sentence);
		if(query.length() == 0) 
			System.out.println("no query");
		else
		{
			try {
				System.out.println("\n---------2002-2007-------\n");
				getPastArticles(query, "2002", "2007");
				System.out.println("\n---------2008-2011-------\n");
				getPastArticles(query, "2008", "2011");
				
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
		
		
	
	public static void main(String[] args) throws IOException
	{
		String 	query = "obama+netanyahu";
		String sent = "Fernando Alonso is the man to beat in this year's Formula One world championship and not the title holder and current leader, Sebastian Vettel, according to Lewis Hamilton. China is a very front limited circuit and it is all about looking after the front tyres.";
		String sent2 = "BlackRock said that investors have begun to return to stock markets, as clients of the world's largest asset manager put a net $34bn into stocks in the first quarter as they withdrew $2.6bn from bond funds.";
		String sent3 = "A portion of the company's stock will remain publicly traded if Blackstone or Icahn prevail.";
		ArticlesTimeRange x = new ArticlesTimeRange("");
		System.out.println(x);
		x.getRelatedArticles(sent);
		
	}
}
