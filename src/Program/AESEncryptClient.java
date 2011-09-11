// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package Program;

import java.math.*;

import YaoGC.*;
import LookupTable.*;
import Utils.*;

public class AESEncryptClient extends ProgClient {

    private BigInteger[] slbs, clbs;
    private short[] msg;

    AESEncryptCommon aes;
    
    State outputState;

    public AESEncryptClient(short[] msgIn, int NkIn) {
	msg = msgIn;
	aes = new AESEncryptCommon(4);
    }

    protected void init() throws Exception {
    	AESEncryptCommon.Nk = AESEncryptCommon.ois.readInt();
    	AESEncryptCommon.Nr = AESEncryptCommon.Nk + 6;

	AESEncryptCommon.initCircuits();

	LookupTableReceiver.ois = AESEncryptCommon.ois;
	AESEncryptCommon.agent = new LookupTableReceiver();

	otNumOfPairs = AESEncryptCommon.Nb*32;

	super.init();
    }

    protected void execTransfer() throws Exception {
	int bytelength = (Wire.labelBitLength-1)/8 + 1;

	slbs = new BigInteger[(AESEncryptCommon.Nr+1)*128];
	for (int i = 0; i < slbs.length; i++) {
	    slbs[i] = Utils.readBigInteger(bytelength, AESEncryptCommon.ois);
	}
	StopWatch.taskTimeStamp("receiving labels for peer's inputs");

	BigInteger m = BigInteger.ZERO;
	for (int i = 0; i < 16; i++)
	    m = m.shiftLeft(8).xor(BigInteger.valueOf(msg[15-i]));

	rcver.execProtocol(m);
	clbs = rcver.getData();
	StopWatch.taskTimeStamp("receiving labels for self's inputs");
    }

    protected void execCircuit() throws Exception {
	outputState = AESEncryptCommon.Cipher(State.fromLabels(slbs), State.fromLabels(clbs));
    }

    protected void interpretResult() throws Exception {
	AESEncryptCommon.oos.writeObject(outputState.toLabels());
	AESEncryptCommon.oos.flush();
    }

    protected void verify_result() throws Exception {

    }
}

