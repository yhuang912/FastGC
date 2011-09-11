// Copyright (C) 2010 by Yan Huang <yh8h@virginia.edu>

package Program;

import java.math.*;
import java.util.*;
import java.io.*;

import Utils.*;
import YaoGC.*;
import LookupTable.*;

public class SmithWatermanCommon extends ProgCommon {
    public static int[][] score;
    public static String  codons;
    static int nBitsCodon;
    static int maxscore;
    public static int nBits;   // the number of bits used to denote an entry in the score matix.
    static int gapA = 12, gapB = 7;  // gap function g(k) = A+B*k, A >= 0, B >= 0.

    static int sdnaLen;
    static int cdnaLen;

    static int[] temp = new int[32];
    static {
	for (int j = 20; j < 32; j++)
	    temp[j] = 0;
    }

    static LookupTable1DAgent agent;

    public static void loadScoreMatrix(String file) throws Exception {
	Scanner scn = new Scanner(new File(file));

	codons = scn.nextLine();
	codons = codons.replaceAll("\\s", "");
	nBitsCodon = bitLength(codons.length()-1);

	scn.nextLine();

	score = new int[codons.length()][codons.length()];
	
	int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
	for (int i = 0; i < score.length; i++)
	    for(int j = 0; j < score[0].length; j++) {
		score[i][j] = scn.nextInt();
		if (score[i][j] < min)
		    min = score[i][j];
		if (score[i][j] > max)
		    max = score[i][j];
	    }
	
	nBits = bitLength(Math.max(Math.abs(max), Math.abs(min))) + 1;
	System.out.println("nBits: " + nBits);

	maxscore = max;
	scn.close();
    }

    static int bitLength(int x) {
	return BigInteger.valueOf(x).bitLength();
    }

    static BigInteger toBigInteger(String dna) {
	BigInteger res = BigInteger.ZERO;

    	for (int i = dna.length()-1; i >= 0; i--) {
	    res = res.shiftLeft(nBitsCodon).
		xor(BigInteger.valueOf(codons.indexOf(dna.charAt(i))));
	}

	return res;
    }

    static String toString(BigInteger dna, int len) {
	StringBuilder res = new StringBuilder("");
	BigInteger mask = BigInteger.ONE.shiftLeft(nBitsCodon).subtract(BigInteger.ONE);

	for (int i = 0; i < len; i++) {
	    int index = dna.and(mask).intValue();
	    res = res.append(codons.charAt(index));
	    dna = dna.shiftRight(nBitsCodon);
	}

	return res.toString();
    }

    static int widthOfEntry(int x, int y) {
	int min = Math.min(x, y);
	min = Math.max(1, min);

	return min*nBits;
    }

    private static int MAX_CIRCUIT(int i) {
	return 2*i;
    }

    private static int ADD_CIRCUIT(int i) {
	return 2*i + 1; // Math.min(sdnaLen, cdnaLen);
    }

    static void initCircuits() {
	ccs = new Circuit[2*(Math.min(sdnaLen, cdnaLen)+1)];
	ccs[0] = new SpecialMAX_2L_L(nBits);
	ccs[1] = new ADD_2L_L(nBits);
	for (int i = 1; i <= Math.min(sdnaLen, cdnaLen); i++) {
	    ccs[MAX_CIRCUIT(i)] = new SpecialMAX_2L_L(i*nBits);
	    ccs[ADD_CIRCUIT(i)] = new ADD_2L_L(i*nBits);
	}
    }

    private static State computeSimilarityWithGap(State[][] H, int row, int col, int gap) {
	BigInteger vGap = BigInteger.valueOf(gapA + gapB*gap);
	State stH = H[row][col];
	State stG = new State(vGap, vGap.bitLength());

	if (gapA + gapB*gap > maxscore*Math.min(row, col)) {
	    return new State(BigInteger.ZERO, widthOfEntry(row, col));
	}
	if (stG.largerThan(stH)) {
	    return new State(BigInteger.ZERO, widthOfEntry(row, col));
	}
	else if (stH.plainValue != null && stG.plainValue != null &&
		 stH.plainValue.compareTo(stG.plainValue) > 0) {
	    BigInteger res = stH.plainValue.subtract(stG.plainValue);
	    return new State(res, widthOfEntry(row, col));
	}
	else {
	    int width = widthOfEntry(row, col);
	    stH = State.signExtend(stH, width);
	    stG = new State(vGap.negate(), width);
	    State inSt = State.concatenate(stH, stG);

	    State outSt = ccs[ADD_CIRCUIT(Math.min(row, col))].startExecuting(inSt);
	    return outSt;
	}
    }

    private static State computeMax(State left, State right) {
	if (left.largerThan(right)) {
	    return left;
	}
	else if (right.largerThan(left)) {
	    return right;
	}
	else {
	    int width = Math.max(left.getWidth(), right.getWidth());
	    left  = State.signExtend(left,  width);
	    right = State.signExtend(right, width);
	    State inSt = State.concatenate(left, right);
	    return ccs[MAX_CIRCUIT(width/nBits)].startExecuting(inSt);
	}
    }

    private static State computeSimilarityWithScoreMatrix(State[][] H, int row, int col,
							  BigInteger[] sdnalbs, 
							  BigInteger[] cdnalbs) {
	State x = H[row-1][col-1];

	BigInteger[] colLbs = Arrays.copyOfRange(cdnalbs, (col-1)*nBitsCodon, col*nBitsCodon);

	BigInteger[] lbls;
	if (Circuit.isForGarbling) {
	    String sdnaStr = toString(SmithWatermanServer.sdna, SmithWatermanServer.sdnaLen);
	    for (int j = 0; j < 20; j++)
		temp[j] = score[codons.indexOf(sdnaStr.charAt(row-1))][j];
	    
	    agent.setTable(temp);
	    lbls = agent.execute(colLbs);
	}
	else 
	    lbls = agent.execute(colLbs);

	State y = State.fromLabels(lbls);
	
	int width = widthOfEntry(row, col);
	x = State.signExtend(x, width);
	y = State.signExtend(y, width);
	State inSt = State.concatenate(x, y);

	return ccs[ADD_CIRCUIT(Math.min(row, col))].startExecuting(inSt);
    }

    public static State execCircuit(BigInteger[] sdnalbs, BigInteger[] cdnalbs) throws Exception {
	State[][] H = new State[sdnaLen+1][cdnaLen+1];

	for (int i = 0; i < sdnaLen+1; i++) {
	    H[i][0] = new State(BigInteger.ZERO, 1);
	}
	Circuit.oos.flush();

	for (int j = 0; j < cdnaLen+1; j++) {
	    H[0][j] = new State(BigInteger.ZERO, 1);
	}
	Circuit.oos.flush();

	for (int l = 2; l <= sdnaLen + cdnaLen; l++) {
	    for (int m = 1; m <= l; m++) {
		int i = m;
		int j = l - m;
		if (i >= 1 && i <= sdnaLen && j >= 1 && j <= cdnaLen) {

		    State temp = new State(BigInteger.ZERO, widthOfEntry(0, j));
		    State stMaxRow = computeSimilarityWithGap(H, 0, j, i);
		    stMaxRow = computeMax(temp, stMaxRow);
		    for (int o = i-1; o >= 1; o--) {
			State st = computeSimilarityWithGap(H, i-o, j, o);
			stMaxRow = computeMax(stMaxRow, st);
		    }

		    temp = new State(BigInteger.ZERO, widthOfEntry(i, 0));
		    State stMaxCol = computeSimilarityWithGap(H, i, 0, j);
		    stMaxCol = computeMax(temp, stMaxCol);
		    for (int o = j-1; o >= 1; o--) {
			State st = computeSimilarityWithGap(H, i, j-o, o);
			stMaxCol = computeMax(stMaxCol, st);
		    }

		    State stScore = computeSimilarityWithScoreMatrix(H, i, j, 
								     sdnalbs, cdnalbs);
		    
		    State stMax = computeMax(stMaxRow, stMaxCol);
		    stMax = computeMax(stMax, stScore);

		    H[i][j] = stMax;
		}
	    }
	}

	StopWatch.taskTimeStamp("circuit garbling");

	return H[sdnaLen][cdnaLen];
    }
}