// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package Program;

import java.math.*;

import YaoGC.*;
import LookupTable.*;
import Utils.*;

public class AESEncryptServer extends ProgServer {

    private BigInteger[][] slps, clps;

    AESEncryptCommon aes;

    State outputState;

    public AESEncryptServer(short[] key, int NkIn) {
	aes = new AESEncryptCommon(NkIn);
	aes.initServer(key);
    }

    protected void init() throws Exception {
	AESEncryptCommon.oos.writeInt(AESEncryptCommon.Nk);
	AESEncryptCommon.oos.flush();

	AESEncryptCommon.initCircuits();

	LookupTableSender.oos = AESEncryptCommon.oos;
	AESEncryptCommon.agent = new LookupTableSender(AESEncryptCommon.STable);

	generateLabelPairs();

	super.init();
    }

    private void generateLabelPairs() {
	slps = new BigInteger[(AESEncryptCommon.Nr+1)*128][2];
	clps = new BigInteger[ AESEncryptCommon.Nb*32][2];

	for (int i = 0; i < slps.length; i++) {
	    slps[i] = Wire.newLabelPair();
	}

	for (int i = 0; i < clps.length; i++) {
	    clps[i] = Wire.newLabelPair();
	}
    }

    protected void execTransfer() throws Exception {
	int bytelength = (Wire.labelBitLength-1)/8 + 1;

	for (int i = 0; i < slps.length; i++) {
	    int idx = AESEncryptCommon.testBit(AESEncryptCommon.w, i);
	    Utils.writeBigInteger(slps[i][idx], bytelength, AESEncryptCommon.oos);
	}
	System.err.println();
	AESEncryptCommon.oos.flush();
	StopWatch.taskTimeStamp("sending labels for selfs inputs");

	snder.execProtocol(clps);
	StopWatch.taskTimeStamp("sending labels for peers inputs");
    }

    protected void execCircuit() throws Exception {
	BigInteger[] slbs = new BigInteger[(AESEncryptCommon.Nr+1)*128];
	BigInteger[] clbs = new BigInteger[ AESEncryptCommon.Nb*32];

	for (int i = 0; i < slbs.length; i++)
	    slbs[i] = slps[i][0];

	for (int i = 0; i < clbs.length; i++)
	    clbs[i] = clps[i][0];

	outputState = AESEncryptCommon.Cipher(State.fromLabels(slbs), State.fromLabels(clbs));
    }

    protected void interpretResult() throws Exception {
	BigInteger[] outLabels = (BigInteger[]) AESEncryptCommon.ois.readObject();

	BigInteger output = BigInteger.ZERO;
	for (int i = 0; i < outLabels.length; i++) {
	    if (outputState.wires[i].value != Wire.UNKNOWN_SIG) {
		if (outputState.wires[i].value == 1)
		    output = output.setBit(i);
		continue;
	    }
	    else if (outLabels[i].equals(outputState.wires[i].invd ? 
					 outputState.wires[i].lbl :
					 outputState.wires[i].lbl.xor(Wire.R.shiftLeft(1).setBit(0)))) {
		    output = output.setBit(i);
	    }
	    else if (!outLabels[i].equals(outputState.wires[i].invd ? 
					  outputState.wires[i].lbl.xor(Wire.R.shiftLeft(1).setBit(0)) :
					  outputState.wires[i].lbl)) 
		throw new Exception("Bad label encountered: i = " + i + "\t" +
				    outLabels[i] + " != (" + 
				    outputState.wires[i].lbl + ", " +
				    outputState.wires[i].lbl.xor(Wire.R.shiftLeft(1).setBit(0)) + ")");
	}
	
	StopWatch.taskTimeStamp("output labels received and interpreted");

	// System.out.println("output (pp): " + Color.blue + output + Color.black);
	BigInteger mask = BigInteger.valueOf(255);
	for (int i = 0; i < 16; i++) {
	    int temp = output.shiftRight(i*8).and(mask).intValue();
	    System.out.print(Integer.toString(temp, 16) + " ");
	}
	System.out.println();
    }

    protected void verify_result() throws Exception {

    }
}

