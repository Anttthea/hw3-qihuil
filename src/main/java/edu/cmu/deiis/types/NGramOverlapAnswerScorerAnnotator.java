package edu.cmu.deiis.types;

import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.deiis.types.NGram;
import edu.cmu.deiis.types.Question;
import edu.cmu.deiis.types.Answer;
import edu.cmu.deiis.types.AnswerScore;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;

import org.cleartk.ne.type.NamedEntityMention;

public class NGramOverlapAnswerScorerAnnotator extends JCasAnnotator_ImplBase {
	public void process(JCas aJCas) {
		// get document text
		String docText = aJCas.getDocumentText();
		FSIndex AIndex = aJCas.getAnnotationIndex(Answer.type);
		Iterator AIter = AIndex.iterator();
		// get Answer size
		int Asize = AIndex.size();
		double[] Acount, AQcount,AName;
		// Array Acount stores NGram numbers in each answer
		Acount = new double[Asize];
		// Array AQcount stores number of NGrams in Answer that also appear in
		// Question
		AQcount = new double[Asize];
		AName = new double[Asize];
		for (int j = 0; j < Asize; j++) {
			Acount[j] = 0;
			AQcount[j] = 0;
			AName[j] = 0;
		}
		int i = 0;
		while (AIter.hasNext()) {
			Answer ans = (Answer) AIter.next();
			// get Token Number in an Answer
			String a = docText.substring(ans.getBegin(), ans.getEnd() - 1);
			StringTokenizer st = new StringTokenizer(a);
			int TokNum = st.countTokens();
			// get total number of 1-,2-,3-Grams in an Answer
			int NGramNum = 3 * TokNum - 3;
			Acount[i++] = NGramNum;
		}
		FSIndex QIndex = aJCas.getAnnotationIndex(Question.type);
		Iterator QIter = QIndex.iterator();
		while (QIter.hasNext()) {
			// get Question span
			Question quest = (Question) QIter.next();
			int qbegin = quest.getBegin();
			int qend = quest.getEnd() - 1;
			/*Addition for NamedEntity Integration*/
			//find NamedEntityMention in Question
			FSIndex NEIndex = aJCas.getAnnotationIndex(NamedEntityMention.type);
			Iterator NEIter = NEIndex.iterator();
			while (NEIter.hasNext()) {
				NamedEntityMention ne = (NamedEntityMention) NEIter.next();
				if (ne.getBegin() >= qbegin && ne.getEnd() <= qend) {
					int begin=ne.getBegin();
					if(begin==qbegin) begin = qbegin+2;
					String qng = docText.substring(begin, ne.getEnd());
					Pattern pqng = Pattern.compile(qng);
					// find number of Question NamedEntityMention in Answers
					FSIndex AIndex2 = aJCas.getAnnotationIndex(Answer.type);
					Iterator AIter2 = AIndex2.iterator();
					int k = 0;
					while (AIter2.hasNext()) {
						Answer ans = (Answer) AIter2.next();
						String answer = docText.substring(ans.getBegin(),
								ans.getEnd() - 1);
						Matcher matcher = pqng.matcher(answer);
						int pos = 0;
						while (matcher.find(pos)) {
							AName[k]++;
							pos = matcher.end();
						}
						k++;
					}
			}
			}
			/*NGram calculation*/
			// find NGrams in Question
			FSIndex NGIndex = aJCas.getAnnotationIndex(NGram.type);
			Iterator NGIter = NGIndex.iterator();
			while (NGIter.hasNext()) {
				NGram ng = (NGram) NGIter.next();
				if (ng.getBegin() >= qbegin && ng.getEnd() <= qend) {
					String qng = docText.substring(ng.getBegin(), ng.getEnd());
					Pattern pqng = Pattern.compile(qng);
					// find number of Question NGrams in Answers
					FSIndex AIndex1 = aJCas.getAnnotationIndex(Answer.type);
					Iterator AIter1 = AIndex1.iterator();
					int k = 0;
					while (AIter1.hasNext()) {
						Answer ans = (Answer) AIter1.next();
						String answer = docText.substring(ans.getBegin(),
								ans.getEnd() - 1);
						Matcher matcher = pqng.matcher(answer);
						int pos = 0;
						while (matcher.find(pos)) {
							AQcount[k]++;
							pos = matcher.end();
						}
						k++;
					}
				}
			}
		}
		// calculate AnswerScore of each Answer
		FSIndex AIndex2 = aJCas.getAnnotationIndex(Answer.type);
		Iterator AIter2 = AIndex2.iterator();
		int p = 0;
		while (AIter2.hasNext()) {
			Answer ans = (Answer) AIter2.next();
			AnswerScore annotation = new AnswerScore(aJCas);
			annotation.setBegin(ans.getBegin());
			annotation.setEnd(ans.getEnd());
			annotation.setCasProcessorId("NGramOverlapAnswerScorerAnnotator");
			annotation.setConfidence(1.0);
			annotation.setAnswer(ans);
			double score = AQcount[p] / Acount[p]+AName[p]*0.1;
			annotation.setScore(score);
			annotation.addToIndexes();
			p++;
		}
	}

}

