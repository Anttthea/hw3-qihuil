package edu.cmu.deiis.types;

import java.util.Iterator;

import edu.cmu.deiis.types.NGram;
import edu.cmu.deiis.types.Question;
import edu.cmu.deiis.types.Answer;
import edu.cmu.deiis.types.Token;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

public class TokenUniGramAnnotator extends JCasAnnotator_ImplBase {

	public void process(JCas aJCas) {
		// get document text
		String docText = aJCas.getDocumentText();
		FSIndex TIndex = aJCas.getAnnotationIndex(Token.type);
		Iterator TIter = TIndex.iterator();
		Token tok = null;
		while (TIter.hasNext()) {
			tok = (Token) TIter.next();
			// do something
			NGram annotation = new NGram(aJCas);
			annotation.setBegin(tok.getBegin());
			annotation.setEnd(tok.getEnd());
			annotation.setCasProcessorId("TokenUniGramAnnotator");
			annotation.setConfidence(1.0);
			FSArray a = new FSArray(aJCas, 1);
			a.set(0, tok);
			annotation.setElements(a);
			annotation.setElementType("edu.cmu.deiis.types.Token");
			annotation.addToIndexes();

		}

	}
}
