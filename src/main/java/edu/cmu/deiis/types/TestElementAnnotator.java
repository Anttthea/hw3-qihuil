package edu.cmu.deiis.types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.cmu.deiis.types.Question;
import edu.cmu.deiis.types.Answer;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;

public class TestElementAnnotator extends JCasAnnotator_ImplBase
{
	private Pattern Question =
			Pattern.compile("Q (.*)?");
	private Pattern Answer =
			Pattern.compile("A (\\d) .*.");

	public void process(JCas aJCas) {
		// get document text
		String docText = aJCas.getDocumentText();
		Matcher matcher = Question.matcher(docText);
		int pos = 0;
		while (matcher.find(pos)) {
		// found one - create annotation
		Question annotation = new Question(aJCas);
		annotation.setBegin(matcher.start()+2);
		annotation.setEnd(matcher.end());
		annotation.setCasProcessorId("TestElementAnnotator");
		annotation.setConfidence(1.0);
		annotation.addToIndexes();
		pos = matcher.end();
		}

		matcher = Answer.matcher(docText);
		pos = 0;
		while (matcher.find(pos)) {
		// found one - create annotation
		Answer annotation = new Answer(aJCas);
		annotation.setBegin(matcher.start()+4);
		annotation.setEnd(matcher.end());
		annotation.setCasProcessorId("TestElementAnnotator");
		annotation.setConfidence(1.0);
		int i = Integer.parseInt(matcher.group(1));
		annotation.setIsCorrect(i == 1);
		annotation.addToIndexes();
		pos = matcher.end();
		}
		}

}
