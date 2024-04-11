import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class IRS_Project2 {

	
	private static TreeMap<String, Double> calTF_IDF_Normalisation(TreeMap<String, Integer> weightedtermfrequencies) {
	    int sumofallfrequencies = weightedtermfrequencies.values().stream().mapToInt(Integer::intValue).sum();
	    
	    Map<String, Integer> documentFrequency = new HashMap<>();
	    weightedtermfrequencies.entrySet().stream()
	            .filter(entry -> entry.getKey() instanceof String && entry.getValue() instanceof Integer)
	            .forEach(entry -> documentFrequency.put((String) entry.getKey(), documentFrequency.getOrDefault(entry.getKey(), 0) + 1));
    
	    return weightedtermfrequencies.entrySet().stream().collect(Collectors.toMap(
	                    Map.Entry::getKey,
	                    entry -> {
	                        double t_f = entry.getValue();
	                        double normalizedTf_length = t_f / sumofallfrequencies;
	                        double i_df = Math.log(1.0 + weightedtermfrequencies.size() / (double) weightedtermfrequencies.size());

	                        return normalizedTf_length  * i_df;
	                    },
	                    (a1, a2) -> a1,
	                    TreeMap::new
	            ));
	}


	
	
	public static void main(String[] args) throws IOException {
		  long startT = System.currentTimeMillis();
		  int countofdoc=0;
		String[] stoplist = {"a", "about", "above", "according", "across", "actually", "adj", "after", "afterwards", "again",
	            "against", "all", "almost", "alone", "along", "already", "also", "although", "always", "among",
	            "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anywhere", "are",
	            "area", "areas", "aren't", "around", "as", "ask", "asked", "asking", "asks", "at", "away", "b", "back",
	            "backed", "backing", "backs", "be", "became", "because", "become", "becomes", "becoming", "been",
	            "before", "beforehand", "began", "begin", "beginning", "behind", "being", "beings", "below", "beside",
	            "besides", "best", "better", "between", "beyond", "big", "billion", "both", "but", "by", "c", "came",
	            "can", "can't", "cannot", "caption", "case", "cases", "certain", "certainly", "clear", "clearly", "co",
	            "come", "could", "couldn't", "d", "did", "didn't", "differ", "different", "differently", "do", "does",
	            "doesn't", "don't", "done", "down", "downed", "downing", "downs", "during", "e", "each", "early", "eg",
	            "eight", "eighty", "either", "else", "elsewhere", "end", "ended", "ending", "ends", "enough", "etc",
	            "even", "evenly", "ever", "every", "everybody", "everyone", "everything", "everywhere", "except", "f",
	            "face", "faces", "fact", "facts", "far", "felt", "few", "fifty", "find", "finds", "first", "five",
	            "for", "former", "formerly", "forty", "found", "four", "from", "further", "furthered", "furthering",
	            "furthers", "g", "gave", "general", "generally", "get", "gets", "give", "given", "gives", "go", "going",
	            "good", "goods", "got", "great", "greater", "greatest", "group", "grouped", "grouping", "groups", "h",
	            "had", "has", "hasn't", "have", "haven't", "having", "he", "he'd", "he'll", "he's", "hence", "her",
	            "here", "here's", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "high", "higher",
	            "highest", "him", "himself", "his", "how", "however", "hundred", "i", "i'd", "i'll", "i'm", "i've", "ie",
	            "if", "important", "in", "inc", "indeed", "instead", "interest", "interested", "interesting", "interests",
	            "into", "is", "isn't", "it", "it's", "its", "itself", "j", "just", "k", "l", "large", "largely", "last",
	            "later", "latest", "latter", "latterly", "least", "less", "let", "let's", "lets", "like", "likely", "long",
	            "longer", "longest", "ltd", "m", "made", "make", "makes", "making", "man", "many", "may", "maybe", "me",
	            "meantime", "meanwhile", "member", "members", "men", "might", "million", "miss", "more", "moreover",
	            "most", "mostly", "mr", "mrs", "much", "must", "my", "myself", "n", "namely", "necessary", "need", "needed",
	            "needing", "needs", "neither", "never", "nevertheless", "new", "newer", "newest", "next", "nine", "ninety",
	            "no", "nobody", "non", "none", "nonetheless", "noone", "nor", "not", "nothing", "now", "nowhere", "number",
	            "numbers", "o", "of", "off", "often", "old", "older", "oldest", "on", "once", "one", "one's", "only", "onto",
	            "open", "opened", "opens", "or", "order", "ordered", "ordering", "orders", "other", "others", "otherwise",
	            "our", "ours", "ourselves", "out", "over", "overall", "own", "p", "part", "parted", "parting", "parts", "per",
	            "perhaps", "place", "places", "point", "pointed", "pointing", "points", "possible", "present", "presented",
	            "presenting", "presents", "problem", "problems", "put", "puts", "q", "quite", "r", "rather", "really",
	            "recent", "recently", "right", "room", "rooms", "s", "said", "same", "saw", "say", "says", "second", "seconds",
	            "see", "seem", "seemed", "seeming", "seems", "seven", "seventy", "several", "she", "she'd", "she'll", "she's",
	            "should", "shouldn't", "show", "showed", "showing", "shows", "sides", "since", "six", "sixty", "small",
	            "smaller", "smallest", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes",
	            "somewhere", "state", "states", "still", "stop", "such", "sure", "t", "take", "taken", "taking", "ten", "than",
	            "that", "that'll", "that's", "that've", "the", "their", "them", "themselves", "then", "thence", "there", "there'd", "there'll", "there're", "there's", "there've", "thereafter", "thereby", "therefore", "therein", "thereupon",
	            "these", "they", "they'd", "they'll", "they're", "they've", "thing", "things", "think", "thinks", "thirty", "this",
	            "those", "though", "thought", "thoughts", "thousand", "three", "through", "throughout", "thru", "thus", "to", "today",
	            "together", "too", "took", "toward", "towards", "trillion", "turn", "turned", "turning", "turns", "twenty", "two",
	            "u", "under", "unless", "unlike", "unlikely", "until", "up", "upon", "us", "use", "used", "uses", "using", "v",
	            "very", "via", "w", "want", "wanted", "wanting", "wants", "was", "wasn't", "wa	y", "ways", "we", "we'd", "we'll",
	            "we're", "we've", "well", "wells", "were", "weren't", "what", "what'll", "what's", "what've", "whatever", "when",
	            "whence", "whenever", "where", "where's", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever",
	            "whether", "which", "while", "whither", "who", "who'd", "who'll", "who's", "whoever", "whole", "whom", "whomever",
	            "whose", "why", "will", "with", "within", "without", "won't", "work", "worked", "working", "works", "would", "wouldn't",
	            "x", "y", "year", "years", "yes", "yet", "you", "you'd", "you'll", "you're", "you've", "young", "younger", "youngest",
	            "your", "yours", "yourself", "yourselves", "z"};
		//		String input = "'jai";
//
//		String[] msg = input.split("[.@,|!?:,;'()]");
//
//		//
//		if (msg.length > 0) {
//		    System.out.println(msg[1]);
//		} else {
//		    System.out.println("No elements found after splitting.");
//		}

		//java maven_assignment input_dir output_dir
		
		
		//now task is to create a frequency file for all the tokens
		//-->sorted by tokens alphabetic order
		//-->sorted by token s
		
		//Here I have given two command line arguments one is the input directory and the other one is the output direcotory
		if(args.length!=2)
		{
			System.out.println(args[0]);
			System.out.println(args[1]);
			System.out.println("Here we are trying to enter the inputdirectory path and output directory path to tokenize the following input files");
			System.exit(1);
			
		}
		
		String inDp= args[0];
		String outDp = args[1];
		
		//Here we are trying to validate if the input directory exists
		
		
		File inD = new File(inDp);
		if(!inD.isDirectory() || !inD.exists()) {
			System.out.println("The input directory path is invalid");
			System.exit(1);
			
		}
		
		
		File outD = new File(outDp);
		if(!outD.exists()) {
			outD.mkdirs();
			
		}
		String fname="";
		int val=0;
		int c=0;
		
		
		 

		
		
	//Here in we try to pass the input and output directory paths below. 	
//	File[] files = new File("/Users/jaikishantimmapatruni/Downloads/IRSassignment/input files/").listFiles();
//	String tokenbyfreqall= "/Users/jaikishantimmapatruni/Downloads/IRSassignment/output files/"+"totalsortbytoken.txt";
		//TreeMap<String,Integer> mytotal=new TreeMap<String,Integer>(); 
File[] files = new File(inDp).listFiles();	

for(File f:files )
{ countofdoc+=1;
	fname= f.getName();
	TreeMap<String,Integer> mytokhalfpre=new TreeMap<String,Integer>(); 
	TreeMap<String, Double> mytokweights=new TreeMap<String,Double>(); 
try {
	String content = Jsoup.parse(new File(inDp+fname), "UTF-8").toString();
	Document doc =  Jsoup.parse(content);
	
	String text;
	
	if(doc!=null)
	{
		if(doc.head()!=null && doc.body()!=null)
		{
	     text = doc.body().text() + doc.head().text();
		}else{
		 text = doc.head().text();
		}
		String m;
		String outfilename = outDp+fname+"weights"+".wts"; 
		FileWriter tokpre = new FileWriter(outfilename,true);
		BufferedWriter tokDataB = new BufferedWriter(tokpre);
		StringTokenizer st1 = new StringTokenizer(text);
		 for (int i = 1; st1.hasMoreTokens(); i++)
	      {
	    	
	         m = st1.nextToken();
//	         System.out.println(msg);
	         String msg = m.replaceAll("[^a-zA-Z]","");
	         //System.out.println(m);
	       
	         if(msg.length()>1 && Arrays.asList(stoplist).contains(msg)!=true)
	         {
	        	 if(mytokhalfpre.containsKey(msg)==false ){
			    	  val=1;
			    	  mytokhalfpre.put(msg, val);
			    	  //here we try to insert the value of the token if its a new one
			      }
	        	 
			      else {
			    	  val=mytokhalfpre.get(msg);
			    	  val+=1;
			    	  //otherwise we try to pull the value of the token and increment it.
			    	  mytokhalfpre.replace(msg,mytokhalfpre.get(msg), val);
			    	 
			    	  
			      
		      }
	         }
	      }
		 
		 Iterator<Map.Entry<String, Integer>> mymap = mytokhalfpre.entrySet().iterator();
	        while (mymap.hasNext()) {
	            Map.Entry<String, Integer> entry = mymap.next();
	            if (entry.getValue() == 1) {
	                mymap.remove(); 
	            }
	        }
	
	
	
	//System.out.println(mytokhalfpre);
	mytokweights = calTF_IDF_Normalisation(mytokhalfpre);
	
	String estring;

	 for (Map.Entry<String, Double> entry : mytokweights.entrySet()) {
			estring = entry.getKey()+": " +entry.getValue().floatValue();
			tokDataB.write(estring);
			tokDataB.write("\n");
			}
	 tokDataB.close();	
	 
	 if(countofdoc==10)
	 {
		 long eT = System.currentTimeMillis();
		 long timeelapsed = eT-startT;
		 System.out.println("The total time taken to get the "+countofdoc+" weighted documents : "+ timeelapsed + " milliseconds");
		 
	 }else if(countofdoc==20)
	 {
		 long eT = System.currentTimeMillis();
		 long timeelapsed = eT-startT;
		 System.out.println("The total time taken to get the "+countofdoc+" weighted documents : "+ timeelapsed + " milliseconds");
	 }
	 else if(countofdoc==40)
	 {
		 long eT = System.currentTimeMillis();
		 long timeelapsed = eT-startT;
		 System.out.println("The total time taken to get the "+countofdoc+" weighted documents : "+ timeelapsed + " milliseconds");
	 }
	 else if(countofdoc==80)
	 {
		 long eT = System.currentTimeMillis();
		 long timeelapsed = eT-startT;
		 System.out.println("The total time taken to get the "+countofdoc+" weighted documents : "+ timeelapsed + " milliseconds" );
	 }
	 else if(countofdoc==100)
	 {
		 long eT = System.currentTimeMillis();
		 long timeelapsed = eT-startT;
		 System.out.println("The total time taken to get the "+countofdoc+" weighted documents : "+ timeelapsed + " milliseconds");
	 }
	 else if(countofdoc==200)
	 {
		 long eT = System.currentTimeMillis();
		 long timeelapsed = eT-startT;
		 System.out.println("The total time taken to get the "+countofdoc+" weighted documents : "+ timeelapsed + " milliseconds");
	 }
	 else if(countofdoc==300)
	 {
		 long eT = System.currentTimeMillis();
		 long timeelapsed = eT-startT;
		 System.out.println("The total time taken to get the "+countofdoc+" weighted documents : "+ timeelapsed + " milliseconds");
	 }
	 else if(countofdoc==400)
	 {
		 long eT = System.currentTimeMillis();
		 long timeelapsed = eT-startT;
		 System.out.println("The total time taken to get the "+countofdoc+" weighted documents : "+ timeelapsed + " milliseconds" );
	 }
	 else if(countofdoc==500)
	 {
		 long eT = System.currentTimeMillis();
		 long timeelapsed = eT-startT;
		 System.out.println("The total time taken to get the "+countofdoc+" weighted documents : "+ timeelapsed + " milliseconds" );
	 }
	 
		 
		 
		 
}
	
}


catch(Exception e)
{
	System.out.println(e.getMessage()+ "\n"+e.getCause());
	
}

}



		
}

}
	


