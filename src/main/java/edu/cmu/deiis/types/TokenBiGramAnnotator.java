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

public class TokenBiGramAnnotator extends JCasAnnotator_ImplBase {
	public void process(JCas aJCas) {
		// get document text
		String docText = aJCas.getDocumentText();
		FSIndex TIndex = aJCas.getAnnotationIndex(Token.type);
		Iterator TIter = TIndex.iterator();
		FSIndex QIndex = aJCas.getAnnotationIndex(Question.type);
		Iterator QIter = QIndex.iterator();
		FSIndex AIndex = aJCas.getAnnotationIndex(Answer.type);
		Iterator AIter = AIndex.iterator();
		int Qend = 0;
		int Aend = 0;
		Token formertok = null;
		Token tok = null;
		if (QIter.hasNext()) {
			Question quest = (Question) QIter.next();
			Qend = quest.getEnd();
		}
		if (TIter.hasNext()) {
			formertok = (Token) TIter.next();
		}
		while (TIter.hasNext()) {
			tok = (Token) TIter.next();
			// do something
			if (tok.getEnd() <= Qend) {
				NGram annotation = new NGram(aJCas);
				annotation.setBegin(formertok.getBegin());
				annotation.setEnd(tok.getEnd());
				annotation.setCasProcessorId("TokenBiGramAnnotator");
				annotation.setConfidence(1.0);
				FSArray a = new FSArray(aJCas, 2);
				a.set(0, formertok);
				a.set(1, tok);
				annotation.setElements(a);
				annotation.setElementType("edu.cmu.deiis.types.Token");
				annotation.addToIndexes();
				formertok = tok;
			} else
				break;
		}
		while (AIter.hasNext()) {
			Answer ans = (Answer) AIter.next();
			Aend = ans.getEnd();
			while (TIter.hasNext()) {
				formertok = tok;
				tok = (Token) TIter.next();
				// do something
				if (tok.getEnd() <= Aend) {
					NGram annotation = new NGram(aJCas);
					annotation.setBegin(formertok.getBegin());
					annotation.setEnd(tok.getEnd());
					annotation.setCasProcessorId("TokenBiGramAnnotator");
					annotation.setConfidence(1.0);
					FSArray a = new FSArray(aJCas, 2);
					a.set(0, formertok);
					a.set(1, tok);
					annotation.setElements(a);
					annotation.setElementType("edu.cmu.deiis.types.Token");
					annotation.addToIndexes();
					// formertok = tok;
				} else
					break;
			}
		}
	}
}

