// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

import java.math.*;
import java.io.*;

abstract public class Circuit implements TransitiveObserver {
    public static boolean isForGarbling;

    public Wire[] inputWires;
    public Wire[] outputWires;

    protected int inDegree, outDegree;
    protected String name;

    public static ObjectOutputStream oos = null;
    public static ObjectInputStream  ois = null;

    private int inputWireCount = 0;

    public Circuit(int inDegree, int outDegree, String name) {
	this.inDegree = inDegree;
	this.outDegree = outDegree;
	this.name = name;
	
	inputWires = new Wire[inDegree];
	outputWires = new Wire[outDegree];
    }

    public static void setIOStream (ObjectInputStream ois, ObjectOutputStream oos) {
	Circuit.ois = ois;
	Circuit.oos = oos;
    }
    
    abstract public void build() throws Exception;

    protected void createInputWires() {
	for (int i = 0; i < inDegree; i++) {
	    inputWires[i] = new Wire();
	}
    }

    public void startExecuting(int[] vals, boolean[] invd, BigInteger[] glbs) throws Exception {
	if (vals.length != invd.length || 
	    invd.length != glbs.length || 
	    glbs.length != this.inDegree)
    	    throw new Exception("Unmatched number of input labels.");

    	for (int i = 0; i < this.inDegree; i++) {
	    inputWires[i].value = vals[i];
	    inputWires[i].invd = invd[i];
	    inputWires[i].setLabel(glbs[i]);
	    inputWires[i].setReady();
	}
    }

    public State startExecuting(State s) {
	if (s.getWidth() != this.inDegree) {
    	    Exception e = new Exception("Unmatched number of input labels." + 
					s.getWidth() + " != " + inDegree);
	    e.printStackTrace();
	    System.exit(1);
	}

    	for (int i = 0; i < this.inDegree; i++) {
	    inputWires[i].value = s.wires[i].value;
	    inputWires[i].invd = s.wires[i].invd;
	    inputWires[i].setLabel(s.wires[i].lbl);
	    inputWires[i].setReady();
	}

	return State.fromWires(this.outputWires);
    }

    public BigInteger interpretOutputELabels(BigInteger[] eLabels) throws Exception {
	if (eLabels.length != outDegree)
	    throw new Exception("Length Error.");
	    
	BigInteger output = BigInteger.ZERO;
	for (int i = 0; i < this.outDegree; i++) {
	    if (outputWires[i].value != Wire.UNKNOWN_SIG) {
		if (outputWires[i].value == 1)
		    output = output.setBit(i);
	    }
	    else if (eLabels[i].equals(outputWires[i].invd ? 
				       outputWires[i].lbl :
				       outputWires[i].lbl.xor(Wire.R.shiftLeft(1).setBit(0)))) {
		output = output.setBit(i);
	    }
	    else if (!eLabels[i].equals(outputWires[i].invd ? 
					outputWires[i].lbl.xor(Wire.R.shiftLeft(1).setBit(0)) :
					outputWires[i].lbl)) 
		throw new Exception("Bad Label encountered at ouputWire[" + i + "]:\n" +
				    eLabels[i] + " is neither " +
				    outputWires[i].lbl + " nor " + 
				    outputWires[i].lbl.xor(Wire.R.shiftLeft(1).setBit(0)));
	}

	return output;
    }

    public void update(TransitiveObservable o, Object arg) {
	inputWireCount++;
	if (inputWireCount % inDegree == 0)
	    execute();
    }

    abstract protected void compute();
    abstract protected void execute();
}
