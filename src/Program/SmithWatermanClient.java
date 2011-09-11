// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package Program;

import java.math.*;

import YaoGC.*;
import Utils.*;
import LookupTable.*;

public class SmithWatermanClient extends ProgClient {
    private BigInteger cdna;
    private BigInteger[] sdnalbs, cdnalbs;
    private int sdnaLen;
    private int cdnaLen;

    private State outputState;

    public SmithWatermanClient(BigInteger dna, int length) {
	cdna = dna;
	cdnaLen = length;
    }

    public SmithWatermanClient(String dna) throws Exception {
	this(SmithWatermanCommon.toBigInteger(dna), dna.length());
    }

    protected void init() throws Exception {
	SmithWatermanCommon.oos.writeInt(cdnaLen);
	SmithWatermanCommon.oos.flush();
	sdnaLen = SmithWatermanCommon.ois.readInt();

	SmithWatermanCommon.sdnaLen = sdnaLen;
	SmithWatermanCommon.cdnaLen = cdnaLen;

	SmithWatermanCommon.initCircuits();
	
	LookupTable1DReceiver.ois = SmithWatermanCommon.ois;
	SmithWatermanCommon.agent = new LookupTable1DReceiver();

	otNumOfPairs = SmithWatermanCommon.nBitsCodon*cdnaLen;

	super.init();
    }

    protected void execTransfer() throws Exception {
	int bytelength = (Wire.labelBitLength-1)/8 + 1;

	sdnalbs = new BigInteger[SmithWatermanCommon.nBitsCodon*sdnaLen];
	for (int i = 0; i < sdnalbs.length; i++) {
	    sdnalbs[i] = Utils.readBigInteger(bytelength, SmithWatermanCommon.ois);
	}
	StopWatch.taskTimeStamp("receiving labels for peer's inputs");

	// cdnalbs = new BigInteger[SmithWatermanCommon.nBitsCodon*cdnaLen];
	rcver.execProtocol(cdna);
	cdnalbs = rcver.getData();
	StopWatch.taskTimeStamp("receiving labels for self's inputs");
    }

    protected void execCircuit() throws Exception {
	outputState = SmithWatermanCommon.execCircuit(sdnalbs, cdnalbs);
    }

    protected void interpretResult() throws Exception {
	SmithWatermanCommon.oos.writeObject(outputState.toLabels());
	SmithWatermanCommon.oos.flush();
    }

    protected void verify_result() throws Exception {
	SmithWatermanCommon.oos.writeObject(cdna);
	SmithWatermanCommon.oos.flush();
    }
}