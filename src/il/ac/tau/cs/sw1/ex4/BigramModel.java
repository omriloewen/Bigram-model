package il.ac.tau.cs.sw1.ex4;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class BigramModel {
	public static final int MAX_VOCABULARY_SIZE = 14500;
	public static final String VOC_FILE_SUFFIX = ".voc";
	public static final String COUNTS_FILE_SUFFIX = ".counts";
	public static final String SOME_NUM = "some_num";
	public static final int ELEMENT_NOT_FOUND = -1;
	
	String[] mVocabulary;
	int[][] mBigramCounts;
	
	// DO NOT CHANGE THIS !!! 
	public void initModel(String fileName) throws IOException{
		mVocabulary = buildVocabularyIndex(fileName);
		mBigramCounts = buildCountsArray(fileName, mVocabulary);
		
	}
	
	
	
	/*
	 * @post: mVocabulary = prev(mVocabulary)
	 * @post: mBigramCounts = prev(mBigramCounts)
	 */
	public String[] buildVocabularyIndex(String fileName) throws IOException{ // Q 1
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = reader.readLine();
		boolean noNums = true;
		int cnt = 0;
		String[] voc = new String[14500];
		while(line != null && cnt < 14500) {
			
			String[] lineArr = line.trim().split("\\s+");
			for ( int i = 0; i < lineArr.length; i ++) {
				if (isWord(lineArr[i])) {
					String word = lineArr[i].toLowerCase();
					if (isAt(word,voc) == -1) {
						voc[cnt] = word;
						cnt++;
						continue;
					}
					
				}
				if (noNums && isNum(lineArr[i])) {
					voc[cnt] = "some_num";
					cnt++;
					noNums = false;
				}
			}
			line = reader.readLine();
		}
		reader.close();
		if(cnt == 14500) {
			return voc;
		}
		String[] res = new String[cnt];
		for(int i = 0; i<cnt; i++) {
		res[i] = voc[i];	
		}
		return res;
	}
	
	
	
	private boolean isNum(String word) {
		for(int i=0; i < word.length(); i++) {
			char c = word.charAt(i);
			if(c<48 || c>57) {
				return false;
			}
		}
		return true;
	}



	private int isAt(String word, String[] voc) {
		word = word.toLowerCase();
		if(isNum(word)) {
			word = "some_num";
		}
		for (int i = 0; i<voc.length; i++) {
			if(word.equals(voc[i])) {
				return i;
			}
		}
		return -1;
	}



	private boolean isWord(String word) {
		for(int i=0; i < word.length(); i++) {
			char c = word.charAt(i);
			if(c>=65 && c<=122) {
				return true;
			}
		}
		return false;
	}



	/*
	 * @post: mVocabulary = prev(mVocabulary)
	 * @post: mBigramCounts = prev(mBigramCounts)
	 */
	public int[][] buildCountsArray(String fileName, String[] vocabulary) throws IOException{ // Q - 2
		int n = vocabulary.length;
		int[][] CA = new int[n][n];
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = reader.readLine();
		while(line != null) {
			String[] lineArr = line.trim().split("\\s+");
			
			for( int i = 0; i<lineArr.length - 1; i++) {
				int j = isAt(lineArr[i],vocabulary);
				int k = isAt(lineArr[i+1],vocabulary);
				if(j!=-1 && k!=-1) {
					CA[j][k]++;
				}
				if(k==-1) {
					i++;
				}
			}
			line = reader.readLine();
			
		}
		reader.close();
		return CA;

	}
	
	
	/*
	 * @pre: the method initModel was called (the language model is initialized)
	 * @pre: fileName is a legal file path
	 */
	public void saveModel(String fileName) throws IOException{ // Q-3
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + VOC_FILE_SUFFIX));
		writer.write(mVocabulary.length + " words");
		for(int i = 0; i<mVocabulary.length;i++) {
			writer.write("\n" + i + "," + mVocabulary[i]);
		}
		writer.close();
		
		BufferedWriter writer1 = new BufferedWriter(new FileWriter(fileName + COUNTS_FILE_SUFFIX));
		boolean firstrow = true;
		for(int i =0 ; i<mBigramCounts.length; i++) {
			for(int j = 0; j<mBigramCounts.length; j++) {
				if(mBigramCounts[i][j] != 0) {
					if(firstrow) {
						writer1.write( i + "," + j + ":" + mBigramCounts[i][j]);
						firstrow = false;
					}
					else{
						writer1.write("\n" + i + "," + j + ":" + mBigramCounts[i][j]);
					}
				}
			}
		}
		writer1.close();
	}
	
	
	
	/*
	 * @pre: fileName is a legal file path
	 */
	public void loadModel(String fileName) throws IOException{ // Q - 4
		BufferedReader reader = new BufferedReader(new FileReader(fileName + VOC_FILE_SUFFIX));
		String [] lineArr = reader.readLine().trim().split("\\s+");
		
		int n = Integer.valueOf(lineArr[0]);
		mVocabulary = new String[n];
		for(int i =0; i<n; i++) {
			String [] vLineArr = reader.readLine().split(",");
			mVocabulary[i] = vLineArr[1];
		}
		reader.close();
		
		mBigramCounts = new int[n][n];
		reader = new BufferedReader(new FileReader(fileName + COUNTS_FILE_SUFFIX));
		String line = reader.readLine();
		while(line != null) {
			lineArr = line.split(":");
			int cnt = Integer.valueOf(lineArr[1]);
			String[] inds = lineArr[0].split(",");
			int j = Integer.valueOf(inds[0]);
			int k = Integer.valueOf(inds[1]);
			mBigramCounts[j][k] = cnt;
			line = reader.readLine();
		}
	}

	
	
	/*
	 * @pre: word is in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post: $ret = -1 if word is not in vocabulary, otherwise $ret = the index of word in vocabulary
	 */
	public int getWordIndex(String word){  // Q - 5
		return isAt(word,mVocabulary);
	}
	
	
	
	/*
	 * @pre: word1, word2 are in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post: $ret = the count for the bigram <word1, word2>. if one of the words does not
	 * exist in the vocabulary, $ret = 0
	 */
	public int getBigramCount(String word1, String word2){ //  Q - 6
		int i = getWordIndex(word1);
		int j = getWordIndex(word2);
		if (i==-1 || j==-1) {
			return 0;
		}
		
		return mBigramCounts[i][j];
	}
	
	
	/*
	 * @pre word in lowercase, and is in mVocabulary
	 * @pre: the method initModel was called (the language model is initialized)
	 * @post $ret = the word with the lowest vocabulary index that appears most fequently after word (if a bigram starting with
	 * word was never seen, $ret will be null
	 */
	public String getMostFrequentProceeding(String word){ //  Q - 7
		int m = getWordIndex(word);
		int n = mVocabulary.length;
		int max = 0;
		int j=0;
		for(int i = 0; i<n ; i++) {
			if(mBigramCounts[m][i] > max) {
				max = mBigramCounts[m][i];
				j=i;
				
			}
		}
		if(max == 0) {
			return null;
		}
		return mVocabulary[j];
	}
	
	
	/* @pre: sentence is in lowercase
	 * @pre: the method initModel was called (the language model is initialized)
	 * @pre: each two words in the sentence are are separated with a single space
	 * @post: if sentence is is probable, according to the model, $ret = true, else, $ret = false
	 */
	public boolean isLegalSentence(String sentence){  //  Q - 8
		String[] sArr = sentence.trim().split(" ");
		for(int i = 0 ; i<sArr.length -1; i++) {
			int j = getWordIndex(sArr[i]);
			int k = getWordIndex(sArr[i+1]);
			if( j == -1 || k == -1 || mBigramCounts[j][k] == 0) {
				return false;
			}
		}
		return true;
	}
	
	
	
	/*
	 * @pre: arr1.length = arr2.legnth
	 * post if arr1 or arr2 are only filled with zeros, $ret = -1, otherwise calcluates CosineSim
	 */
	public static double calcCosineSim(int[] arr1, int[] arr2){ //  Q - 9
		double mecA = 0;
		double mecB = 0;
		double mon = 0;
		int n = arr1.length;
		for(int i = 0 ; i<n; i++) {
			mecA = mecA + arr1[i]*arr1[i];
			mecB = mecB + arr2[i]*arr2[i];
			mon = mon + arr1[i]*arr2[i];
		}
		if(mecA == 0 || mecB == 0) {
			return -1;
		}
		double mec = Math.sqrt(mecA) * Math.sqrt(mecB);
		
		return mon/mec;
	}

	
	/*
	 * @pre: word is in vocabulary
	 * @pre: the method initModel was called (the language model is initialized), 
	 * @post: $ret = w implies that w is the word with the largest cosineSimilarity(vector for word, vector for w) among all the
	 * other words in vocabulary
	 */
	public String getClosestWord(String word){ //  Q - 10
		String res = word;
		double maxS = 0;
		int n = mVocabulary.length;
		int wInd = getWordIndex(word);
		int[] wVec = mBigramCounts[wInd];
		for(int i = 0; i<n ; i++) {
			double CS = calcCosineSim(wVec,mBigramCounts[i]);
			if(CS > maxS && i != wInd) {
				maxS = CS;
				res = mVocabulary[i];
			}
		}
		return res;
	}

	
}
