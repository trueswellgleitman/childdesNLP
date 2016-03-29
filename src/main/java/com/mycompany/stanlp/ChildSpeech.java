/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.stanlp;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.pipeline.Annotation;
import java.util.Properties;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.util.CoreMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
/**
 *
 * @author arelin
 */
public class ChildSpeech {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        PrintWriter pw = new PrintWriter(new File("out.csv"));
        StringBuilder sb = new StringBuilder();
        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    TreeMap<String, String[]> tm = new TreeMap<String, String[]>();
    String csvFile = "C:\\Users\\steve\\Downloads\\verbsome.csv";
	BufferedReader br = null;
	String line = "";
	String cvsSplitBy = ",";

	try {

		br = new BufferedReader(new FileReader(csvFile));
		while ((line = br.readLine()) != null) {
                        System.out.println("reached");
			String[] country = line.split(cvsSplitBy);
                        String[] input = new String[2];
                        input[0] = country[0];
                        input[1] = country[5];
                        tm.put(country[4], input);
                        

		}

	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (br != null) {
			try { 
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
    
    
    for (  Map.Entry<String, String[]> entry : tm.entrySet()) {
    String[] value = entry.getValue();
    Annotation document = new Annotation(value[1]);

    pipeline.annotate(document);
    List<CoreMap> sentences = document.get(SentencesAnnotation.class);

    for(CoreMap sentence: sentences) {
   
        for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
        // this is the text of the token
        String word = token.get(TextAnnotation.class);
        //ArrayList<CoreLabel> al = new ArrayList();
        if(word.equals(value[0]))
        {
            Tree tree = sentence.get(TreeAnnotation.class);
            //TregexPattern patternMW = TregexPattern.compile("VP([ >># VB | >># VBP | >># VBD] <<" + value[0] +
             //       ")");
             TregexPattern patternMW = TregexPattern.compile("VP  [ <# VB | <# VBP | <# VBD] & <<" + value[0]);
            TregexMatcher matcher = patternMW.matcher(tree); 
            while (matcher.findNextMatchingNode()) { 
                Tree match = matcher.getMatch(); 
                String tempString = tree.toString();
                sb.append(entry.getKey());
                sb.append(",");
                sb.append(value[0]);
                sb.append(",");
                sb.append(tempString);
                sb.append(",");
            for(Label l: match.preTerminalYield())
            {
                sb.append(l.toString());
                sb.append("&");
            }
            sb.append(",");
            sb.append(match.toString());
            //sb.append(",");
            //sb.append(token.get(PartOfSpeechAnnotation.class));
            sb.append('\n');
            }

        }
        // this is the POS tag of the token
        // this is the NER label of the token
        //String ne = token.get(NamedEntityTagAnnotation.class);       
      }
      


      //SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
    }

    Map<Integer, edu.stanford.nlp.dcoref.CorefChain> graph = 
      document.get(CorefChainAnnotation.class);

    }
    pw.write(sb.toString());
        pw.close();
    }
    
}
