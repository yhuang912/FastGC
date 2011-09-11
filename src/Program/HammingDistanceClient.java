// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package Program;

import java.math.*;

import YaoGC.*;
import Utils.*;

public class HammingDistanceClient extends ProgClient {
    private BigInteger cBits;
    private BigInteger[] sBitslbs, cBitslbs;

    private State outputState;

    public HammingDistanceClient(BigInteger bv, int length) {
	cBits = bv;
	HammingDistanceCommon.bitVecLen = length;
    }

    protected void init() throws Exception {
    	HammingDistanceCommon.bitVecLen = HammingDistanceCommon.ois.readInt();
	
	HammingDistanceCommon.initCircuits();

    	otNumOfPairs = HammingDistanceCommon.bitVecLen;

    	super.init();
    }

    protected void execTransfer() throws Exception {
	sBitslbs = new BigInteger[HammingDistanceCommon.bitVecLen];

	for (int i = 0; i < HammingDistanceCommon.bitVecLen; i++) {
	    int bytelength = (Wire.labelBitLength-1)/8 + 1;
	    sBitslbs[i]   = Utils.readBigInteger(bytelength, HammingDistanceCommon.ois);
	}
	StopWatch.taskTimeStamp("receiving labels for peer's inputs");

	cBitslbs = new BigInteger[HammingDistanceCommon.bitVecLen];
	rcver.execProtocol(cBits);
	cBitslbs = rcver.getData();
	StopWatch.taskTimeStamp("receiving labels for self's inputs");
    }

    protected void execCircuit() throws Exception {
	outputState = HammingDistanceCommon.execCircuit(sBitslbs, cBitslbs);
    }


    protected void interpretResult() throws Exception {
	HammingDistanceCommon.oos.writeObject(outputState.toLabels());
	HammingDistanceCommon.oos.flush();
    }

    protected void verify_result() throws Exception {
	HammingDistanceCommon.oos.writeObject(cBits);
	HammingDistanceCommon.oos.flush();
    }
}