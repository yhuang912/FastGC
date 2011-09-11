// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package Program;

import java.math.*;

import YaoGC.*;
import Utils.*;

public class EditDistanceServer extends ProgServer {
    private BigInteger sdna;
    private BigInteger[][] sdnalps, cdnalps;

    private EditDistanceCommon.State outputState;

    public EditDistanceServer(BigInteger dna, int length) {
	sdna = dna;

	// length of dna sequence. Effective bit length of variable dna is twice of 'length'
	EditDistanceCommon.sdnaLen = length;
    }

    protected void init() throws Exception {
	EditDistanceCommon.cdnaLen = EditDistanceCommon.ois.readInt();
	EditDistanceCommon.oos.writeInt(EditDistanceCommon.sdnaLen);
	EditDistanceCommon.oos.flush();

	EditDistanceCommon.initCircuits();

	generateLabelPairsForDNAs();

	super.init();
    }

    private void generateLabelPairsForDNAs() {
	sdnalps = new BigInteger[EditDistanceCommon.sigma*EditDistanceCommon.sdnaLen][2];
	cdnalps = new BigInteger[EditDistanceCommon.sigma*EditDistanceCommon.cdnaLen][2];

	for (int i = 0; i < EditDistanceCommon.sdnaLen; i++) {
	    for (int j = 0; j < EditDistanceCommon.sigma; j++) {
		sdnalps[EditDistanceCommon.sigma*i+j] = Wire.newLabelPair();
	    }
	}

	for (int i = 0; i < EditDistanceCommon.cdnaLen; i++) {
	    for (int j = 0; j < EditDistanceCommon.sigma; j++) {
		cdnalps[EditDistanceCommon.sigma*i+j] = Wire.newLabelPair();
	    }
	}
    }

    protected void execTransfer() throws Exception {
	int bytelength = (Wire.labelBitLength-1)/8 + 1;

	for (int i = 0; i < sdnalps.length; i++) {
	    int idx = sdna.testBit(i) ? 1 : 0;
	    Utils.writeBigInteger(sdnalps[i][idx], bytelength, EditDistanceCommon.oos);
	}
	EditDistanceCommon.oos.flush();
	StopWatch.taskTimeStamp("sending labels for selfs inputs");

	snder.execProtocol(cdnalps);
	StopWatch.taskTimeStamp("sending labels for peers inputs");
    }

    protected void execCircuit() throws Exception {
	BigInteger[] sdnalbs = new BigInteger[EditDistanceCommon.sigma*EditDistanceCommon.sdnaLen];
	BigInteger[] cdnalbs = new BigInteger[EditDistanceCommon.sigma*EditDistanceCommon.cdnaLen];

	for (int i = 0; i < sdnalps.length; i++)
	    sdnalbs[i] = sdnalps[i][0];

	for (int i = 0; i < cdnalps.length; i++)
	    cdnalbs[i] = cdnalps[i][0];

	outputState = EditDistanceCommon.execCircuit(sdnalbs, cdnalbs);
    }

    protected void interpretResult() throws Exception {
	BigInteger[] outLabels = (BigInteger[]) EditDistanceCommon.ois.readObject();

	BigInteger output = BigInteger.ZERO;
	for (int i = 0; i < outLabels.length; i++) {
	    if (outputState.values[i] != Wire.UNKNOWN_SIG) {
		if (outputState.values[i] == 1)
		    output = output.setBit(i);
		continue;
	    }
	    else if (outLabels[i].equals(outputState.invd[i] ? 
					 outputState.lbls[i] :
					 outputState.lbls[i].xor(Wire.R.shiftLeft(1).setBit(0)))) {
		    output = output.setBit(i);
	    }
	    else if (!outLabels[i].equals(outputState.invd[i] ? 
					  outputState.lbls[i].xor(Wire.R.shiftLeft(1).setBit(0)) :
					  outputState.lbls[i])) 
		throw new Exception("Bad label encountered: i = " + i + "\t" +
				    outLabels[i] + " != (" + 
				    outputState.lbls[i] + ", " +
				    outputState.lbls[i].xor(Wire.R.shiftLeft(1).setBit(0)) + ")");
	}
	
	System.out.println("output (pp): " + output);
	StopWatch.taskTimeStamp("output labels received and interpreted");
    }

    static String biToString(BigInteger encoding, int sigma, int n) {
	StringBuilder res = new StringBuilder("");
	BigInteger mask = BigInteger.ONE.shiftLeft(sigma).subtract(BigInteger.ONE);

	for (int i = 0; i < n; i++) {
	    res.append((char) encoding.shiftRight(i*sigma).and(mask).intValue());
	}
	return res.toString();
    }

    protected void verify_result() throws Exception {
	BigInteger cdna = (BigInteger) EditDistanceCommon.ois.readObject();
	
	String sdnaStr = biToString(sdna, EditDistanceCommon.sigma, EditDistanceCommon.sdnaLen);
	String cdnaStr = biToString(cdna, EditDistanceCommon.sigma, EditDistanceCommon.cdnaLen);

	int[][] D = new int[EditDistanceCommon.sdnaLen+1][EditDistanceCommon.cdnaLen+1];

	for (int i = 0; i < EditDistanceCommon.sdnaLen+1; i++)
	    D[i][0] = i;

	for (int j = 0; j < EditDistanceCommon.cdnaLen+1; j++)
	    D[0][j] = j;

	for (int i = 1; i < EditDistanceCommon.sdnaLen+1; i++)
	    for (int j = 1; j < EditDistanceCommon.cdnaLen+1; j++) {
		int t = (sdnaStr.charAt(i-1) == cdnaStr.charAt(j-1)) ? 0 : 1;
		D[i][j] = Math.min(Math.min(D[i-1][j]+1, D[i][j-1]+1),
				   D[i-1][j-1]+t);
	    }

	System.out.println("output (verify): " + 
			   D[EditDistanceCommon.sdnaLen][EditDistanceCommon.cdnaLen]);
    }
}