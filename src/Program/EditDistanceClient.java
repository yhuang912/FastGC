// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package Program;

import java.math.*;

import YaoGC.*;
import Utils.*;

public class EditDistanceClient extends ProgClient {
    private BigInteger cdna;
    private BigInteger[] sdnalbs, cdnalbs;

    private EditDistanceCommon.State outputState;

    public EditDistanceClient(BigInteger dna, int length) {
	cdna = dna;
	EditDistanceCommon.cdnaLen = length;
    }

    protected void init() throws Exception {
    	EditDistanceCommon.oos.writeInt(EditDistanceCommon.cdnaLen);
    	EditDistanceCommon.oos.flush();
    	EditDistanceCommon.sdnaLen = EditDistanceCommon.ois.readInt();
	
	EditDistanceCommon.initCircuits();

    	otNumOfPairs = EditDistanceCommon.sigma*EditDistanceCommon.cdnaLen;

    	super.init();
    }

    protected void execTransfer() throws Exception {
	int bytelength = (Wire.labelBitLength-1)/8 + 1;

	sdnalbs = new BigInteger[EditDistanceCommon.sigma*EditDistanceCommon.sdnaLen];
	for (int i = 0; i < sdnalbs.length; i++) {
	    sdnalbs[i] = Utils.readBigInteger(bytelength, EditDistanceCommon.ois);
	}
	StopWatch.taskTimeStamp("receiving labels for peer's inputs");

	cdnalbs = new BigInteger[EditDistanceCommon.sigma*EditDistanceCommon.cdnaLen];
	rcver.execProtocol(cdna);
	cdnalbs = rcver.getData();
	StopWatch.taskTimeStamp("receiving labels for self's inputs");
    }

    protected void execCircuit() throws Exception {
	outputState = EditDistanceCommon.execCircuit(sdnalbs, cdnalbs);
    }

    protected void interpretResult() throws Exception {
	EditDistanceCommon.oos.writeObject(outputState.lbls);
	EditDistanceCommon.oos.flush();
    }

    protected void verify_result() throws Exception {
	EditDistanceCommon.oos.writeObject(cdna);
	EditDistanceCommon.oos.flush();
    }
}