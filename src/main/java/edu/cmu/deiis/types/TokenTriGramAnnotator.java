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

public class TokenTriGramAnnotator extends JCasAnnotator_ImplBase {
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
		Token fformertok = null;
		Token formertok = null;
		Token tok = null;
		if (QIter.hasNext()) {
			Question quest = (Question) QIter.next();
			Qend = quest.getEnd();
		}
		if (TIter.hasNext()) {
			fformertok = (Token) TIter.next();
		}
		if (TIter.hasNext()) {
			formertok = (Token) TIter.next();
		}
		while (TIter.hasNext()) {
			tok = (Token) TIter.next();
			// do something
			if (tok.getEnd() <= Qend) {
				NGram annotation = new NGram(aJCas);
				annotation.setBegin(fformertok.getBegin());
				annotation.setEnd(tok.getEnd());
				annotation.setCasProcessorId("TokenTriGramAnnotator");
				annotation.setConfidence(1.0);
				FSArray a = new FSArray(aJCas, 3);
				a.set(0, fformertok);
				a.set(1, formertok);
				a.set(2, tok);
				annotation.setElements(a);
				annotation.setElementType("edu.cmu.deiis.types.Token");
				annotation.addToIndexes();
				fformertok = formertok;
				formertok = tok;
			} else
				break;
		}
		while (AIter.hasNext()) {
			Answer ans = (Answer) AIter.next();
			Aend = ans.getEnd();
			if (TIter.hasNext()) {
				fformertok = formertok;
				formertok = tok;
				tok = (Token) TIter.next();
			}
			fformertok = formertok;
			formertok = tok;
			while (TIter.hasNext()) {
				tok = (Token) TIter.next();
				// do something
				if (tok.getEnd() <= Aend) {
					NGram annotation = new NGram(aJCas);
					annotation.setBegin(fformertok.getBegin());
					annotation.setEnd(tok.getEnd());
					annotation.setCasProcessorId("TokenTriGramAnnotator");
					annotation.setConfidence(1.0);
					FSArray a = new FSArray(aJCas, 3);
					a.set(0, fformertok);
					a.set(1, formertok);
					a.set(2, tok);
					annotation.setElements(a);
					annotation.setElementType("edu.cmu.deiis.types.Token");
					annotation.addToIndexes();
					fformertok = formertok;
					formertok = tok;
				} else
					break;
			}
		}
	}
}
