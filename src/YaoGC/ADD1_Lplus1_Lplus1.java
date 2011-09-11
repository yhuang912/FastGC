// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

public class ADD1_Lplus1_Lplus1 extends CompositeCircuit {
    private final int L;

    public ADD1_Lplus1_Lplus1(int l) {
	super(l+1, l+1, 1, "ADD1_" + (l+1) + "_" + (l+1));
	L = l;
    }

    protected void createSubCircuits() throws Exception {
	subCircuits[0] = new ADD_2L_Lplus1(L);

	super.createSubCircuits();
    }

    protected void connectWires() {
	inputWires[0].connectTo(subCircuits[0].inputWires, 0);

	for (int i = 0; i < L; i++)
	    inputWires[i+1].connectTo(subCircuits[0].inputWires, 2*i+1);
    }

    protected void defineOutputWires() {
	System.arraycopy(subCircuits[0].outputWires, 0, outputWires, 0, L+1);
    }

    protected void fixInternalWires() {
    	Wire internalWire;
    	for (int i = 1; i < L; i++) {
    	    internalWire = subCircuits[0].inputWires[2*i];
    	    internalWire.fixWire(0);
    	}
    }
}