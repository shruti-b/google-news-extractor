
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;



/** This is a demo of calling CRFClassifier programmatically.
 *  <p>
 *  Usage: <code> java -mx400m -cp "stanford-ner.jar:." NERDemo [serializedClassifier [fileName]]</code>
 *  <p>
 *  If arguments aren't specified, they default to
 *  ner-eng-ie.crf-3-all2006.ser.gz and some hardcoded sample text.
 *  <p>
 *  To use CRFClassifier from the command line:
 *  java -mx400m edu.stanford.nlp.ie.crf.CRFClassifier -loadClassifier
 *      [classifier] -textFile [file]
 *  Or if the file is already tokenized and one word per line, perhaps in
 *  a tab-separated value format with extra columns for part-of-speech tag,
 *  etc., use the version below (note the 's' instead of the 'x'):
 *  java -mx400m edu.stanford.nlp.ie.crf.CRFClassifier -loadClassifier
 *      [classifier] -testFile [file]
 *
 *  @author Jenny Finkel
 *  @author Christopher Manning
 */

public class NerDemo {

    public static void main(String[] args) throws IOException {
    	
      String serializedClassifier = "/Users/shruti/Downloads/stanford-ner-2013-04-04/classifiers/english.all.3class.distsim.crf.ser.gz";

      AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);

      
        String s1 = "Ferrari driver Fernando Alonso has won the Formula One Chinese Grand Prix.";
        String namedEntities = "";
        int numPersons=0;
        int numOrg=0;
        
        List<List<CoreLabel>> nerSent = classifier.classify(s1);
        //System.out.println(nerSent);
        for (List<CoreLabel> lcl : nerSent) {
          int i=0;
          
          while(i< lcl.size()) {
        	  CoreLabel cl = lcl.get(i);
        	  System.out.println(cl.word());
        	  
        	  String nerTag = cl.get(CoreAnnotations.AnswerAnnotation.class);
        	  if(nerTag.contentEquals("LOCATION") || nerTag.contentEquals("PERSON") || nerTag.contentEquals("ORGANIZATION"))
        	  {
        		  namedEntities += cl.word()+" ";
        		  if(nerTag.contentEquals("PERSON"))
        		  {
        			  numPersons++;
        		  }
        		  else if(nerTag.contentEquals("ORGANIZATION"))
        		  {
        			  numOrg++;
        		  }
        	  }
        	  
        	  i++;
          }
            
          
        }
        
        System.out.println(namedEntities);
        System.out.println(numPersons/2); //first name last name
        System.out.println(numOrg);
      }
 }


