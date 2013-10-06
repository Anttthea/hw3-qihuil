package edu.cmu.deiis.types;

import java.io.IOException;
import java.util.Iterator;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.cmu.deiis.types.AnswerScore;
import edu.cmu.deiis.types.Question;

public class CollectionConsumer extends CasConsumer_ImplBase {
    double totalpreci=0;
    int times = 0;
	@Override
	public void processCas(CAS aCAS) throws ResourceProcessException {
		// TODO Auto-generated method stub
		JCas jcas;
		try {
		jcas = aCAS.getJCas();
		} catch (CASException e) {
		throw new ResourceProcessException(e);
		}
		
        String docText = jcas.getDocumentText();
		FSIndex ASIndex = jcas.getAnnotationIndex(AnswerScore.type);
		Iterator ASIter = ASIndex.iterator();
		int Asize = ASIndex.size();
		int Astart[],Aend[],order[];
		double Ascore[],Ascore1[];
		boolean Acorrect[];
		Astart = new int[Asize];
		Aend = new int[Asize];
		order = new int[Asize];
		Ascore = new double[Asize];
		Acorrect = new boolean[Asize];
		Ascore1 = new double[Asize];
		for (int i = 0; i < Asize; i++) {
			Ascore[i] = 0;
			Acorrect[i] = true;
			Astart[i] = 0;
			Aend[i] = 0;
			order[i] = 0;
		}
		int j = 0;
		double totallcorrect = 0;
		//store features of each AnswerScore Annotation
		while (ASIter.hasNext()) {
			AnswerScore ans = (AnswerScore) ASIter.next();
			Astart[j] = ans.getBegin();
			Aend[j] = ans.getEnd();
			Acorrect[j]=ans.getAnswer().getIsCorrect();
			Ascore[j] = ans.getScore();
			Ascore1[j] = Ascore[j];
			if(Acorrect[j]) totallcorrect++;
			j++;
		}
		//selection sort
		for(int k = 0;k<Asize;k++){
			int max = 0;
			for(int y = 0;y <Asize;y++){
				if(Ascore1[max] < Ascore1[y]) {
					max = y;
				}
			}
			order[k]=max;
			Ascore1[max] = -1;
		}
		//print result
		FSIndex QIndex = jcas.getAnnotationIndex(Question.type);
		Iterator QIter = QIndex.iterator();
		while (QIter.hasNext()) {
			// get Question span
			Question quest = (Question) QIter.next();
			int qbegin = quest.getBegin();
			int qend = quest.getEnd();
			String Quest = docText.substring(qbegin, qend);
			System.out.println("Question: "+Quest);
			//iterate over sorted order
			double predictcorrect = 0;
			for(int u = 0;u<Asize;u++){
				int current = order[u];
				if(Acorrect[current]){
					if(u < totallcorrect) predictcorrect++;
					System.out.print("+ ");
				}else System.out.print("- ");
				System.out.print(Ascore[current]);
				String Ans = docText.substring(Astart[current], Aend[current]);
				System.out.println(" "+Ans);
				}
			double precision = predictcorrect/totallcorrect;
			System.out.println("Precision at "+totallcorrect+": "+precision+"\n");
		    totalpreci = totalpreci + precision;
		    times++;
		}
	}
	public void collectionProcessComplete(ProcessTrace aTrace) throws ResourceProcessException,
    IOException {
		System.out.println("Average Precision: "+totalpreci/times+"\n");
}
}
