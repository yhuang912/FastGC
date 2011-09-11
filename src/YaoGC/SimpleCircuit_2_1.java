// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

import java.math.*;
import Cipher.Cipher;
import Utils.*;

public abstract class SimpleCircuit_2_1 extends Circuit {

    protected BigInteger[][] gtt;

    public SimpleCircuit_2_1(String name) {
	super(2, 1, name);
    }

    public void build() throws Exception {
	createInputWires();
	createOutputWires();
    }

    protected void createInputWires() {
	super.createInputWires();

	for (int i = 0; i < inDegree; i++) 
	    inputWires[i].addObserver(this, new TransitiveObservable.Socket(inputWires, i));
    }

    protected void createOutputWires() {
	outputWires[0] = new Wire();
    }

    protected void execute() {

	Wire inWireL = inputWires[0];
	Wire inWireR = inputWires[1];
	Wire outWire = outputWires[0];

	if (inWireL.value != Wire.UNKNOWN_SIG && inWireR.value != Wire.UNKNOWN_SIG) {
	    compute();
	}
	else if (inWireL.value != Wire.UNKNOWN_SIG) {
	    if (shortCut())
		outWire.invd = false;
	    else {
		outWire.value = Wire.UNKNOWN_SIG;
		outWire.invd = inWireR.invd;
		outWire.setLabel(inWireR.lbl);
	    }
	}
	else if (inWireR.value != Wire.UNKNOWN_SIG) {
	    if (shortCut()) 
		outWire.invd = false;
	    else {
		outWire.value = Wire.UNKNOWN_SIG;
		outWire.invd = inWireL.invd;
		outWire.setLabel(inWireL.lbl);
	    }
	}
	else {
	    outWire.value = Wire.UNKNOWN_SIG;
	    outWire.invd = false;

	    if (collapse()) {

	    }
	    else {
		execYao();
	    }
	}
	
	outWire.setReady();
    }

    protected abstract void execYao();

    protected abstract boolean shortCut();
    protected abstract boolean collapse();

    protected void sendGTT() {
    	try {
	    Utils.writeBigInteger(gtt[0][1], 10, oos);
	    Utils.writeBigInteger(gtt[1][0], 10, oos);
	    Utils.writeBigInteger(gtt[1][1], 10, oos);
    		    
    	    oos.flush();
    	}
    	catch (Exception e) {
    	    e.printStackTrace();
    	    System.exit(1);
    	}
    }
    
    protected void receiveGTT() {
	try {
	    gtt = new BigInteger[2][2];

	    gtt[0][0] = BigInteger.ZERO;
	    gtt[0][1] = Utils.readBigInteger(10, ois);
	    gtt[1][0] = Utils.readBigInteger(10, ois);
	    gtt[1][1] = Utils.readBigInteger(10, ois);
	}
	catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    protected void encryptTruthTable() {
    	Wire inWireL = inputWires[0];
    	Wire inWireR = inputWires[1];
    	Wire outWire = outputWires[0];

    	BigInteger[] labelL = {inWireL.lbl, Wire.conjugate(inWireL.lbl)};
    	if (inWireL.invd == true) {
    	    BigInteger tmp = labelL[0];
    	    labelL[0] = labelL[1];
    	    labelL[1] = tmp;
    	}
	    
    	BigInteger[] labelR = {inWireR.lbl, Wire.conjugate(inWireR.lbl)};
    	if (inWireR.invd == true) {
    	    BigInteger tmp = labelR[0];
    	    labelR[0] = labelR[1];
    	    labelR[1] = tmp;
    	}

    	int k = outWire.serialNum;

	int cL = inWireL.lbl.testBit(0) ? 1 : 0;
	int cR = inWireR.lbl.testBit(0) ? 1 : 0;

	if (cL != 0 || cR != 0)
	    gtt[0 ^ cL][0 ^ cR] = Cipher.encrypt(labelL[0], labelR[0], k, gtt[0 ^ cL][0 ^ cR]);
	if (cL != 0 || cR != 1)
	    gtt[0 ^ cL][1 ^ cR] = Cipher.encrypt(labelL[0], labelR[1], k, gtt[0 ^ cL][1 ^ cR]);
	if (cL != 1 || cR != 0)
	    gtt[1 ^ cL][0 ^ cR] = Cipher.encrypt(labelL[1], labelR[0], k, gtt[1 ^ cL][0 ^ cR]);
	if (cL != 1 || cR != 1)
	    gtt[1 ^ cL][1 ^ cR] = Cipher.encrypt(labelL[1], labelR[1], k, gtt[1 ^ cL][1 ^ cR]);
    }
}
