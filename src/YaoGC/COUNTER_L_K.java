// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

public class COUNTER_L_K extends CompositeCircuit {

    public COUNTER_L_K(int l, int k) {
	super(l, k, 1, "COUNTER_" + l + "_" + k);

	if (l > (2<<k)) {
	    System.err.println("The COUNTER will overflow.");
	    (new Exception()).printStackTrace();
	    System.exit(1);
	}
    }

    protected void createSubCircuits() throws Exception {
	subCircuits[0] = new COUNTER_2toK_K(outDegree);

	super.createSubCircuits();
    }

    protected void connectWires() throws Exception {
	for (int i = 0; i < inDegree; i++)
	    inputWires[i].connectTo(subCircuits[0].inputWires, i);
    }

    protected void defineOutputWires() {
	System.arraycopy(subCircuits[0].outputWires, 0, outputWires, 0, outDegree);
    }

    protected void fixInternalWires() {
	for (int i = inDegree; i < subCircuits[0].inDegree; i++) 
	    subCircuits[0].inputWires[i].fixWire(0);
    }
}