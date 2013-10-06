package edu.cmu.deiis.types;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.deiis.types.Token;
import edu.cmu.deiis.types.Question;
import edu.cmu.deiis.types.Answer;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;

public class TokenAnnotator extends JCasAnnotator_ImplBase
{
	  private Pattern Token = Pattern.compile("[a-zA-Z]+");//needs further modification
 public void process(JCas aJCas){
	// get document text
	 String docText = aJCas.getDocumentText();
	 FSIndex QIndex = aJCas.getAnnotationIndex(Question.type);
	 Iterator QIter = QIndex.iterator();
	 while (QIter.hasNext()) {
		 Question quest = (Question)QIter.next();
	 //do something
		 String qu = docText.substring(quest.getBegin(),quest.getEnd());
		 Matcher matcher = Token.matcher(qu);
		 int pos = 0;
		 int offset = quest.getBegin();
			while (matcher.find(pos)) {
			// found one - create annotation
			Token annotation = new Token(aJCas);
			annotation.setBegin(matcher.start()+offset);
			annotation.setEnd(matcher.end()+offset);
			annotation.setCasProcessorId("TokenAnnotator");
			annotation.setConfidence(1.0);
			annotation.addToIndexes();
			pos = matcher.end();
			}
	 }

	 FSIndex AIndex = aJCas.getAnnotationIndex(Answer.type);
	 Iterator AIter = AIndex.iterator();
	 while (AIter.hasNext()) {
		 Answer ans = (Answer)AIter.next();
	 //do something
		 String an = docText.substring(ans.getBegin(),ans.getEnd());
		 Matcher matcher = Token.matcher(an);
		 int pos = 0;
		 int offset = ans.getBegin();
			while (matcher.find(pos)) {
			// found one - create annotation
			Token annotation = new Token(aJCas);
			annotation.setBegin(matcher.start()+offset);
			annotation.setEnd(matcher.end()+offset);
			annotation.setCasProcessorId("TokenAnnotator");
			annotation.setConfidence(1.0);
			annotation.addToIndexes();
			pos = matcher.end();
			}
	 }
 }
}

