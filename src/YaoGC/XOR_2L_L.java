// Copyright (C) 2010 by Yan Huang <yh8h@virginia.edu>

package YaoGC;

public class XOR_2L_L extends CompositeCircuit {

    public XOR_2L_L(int l) {
	super(2*l, l, l, "XOR_"+(2*l)+"_"+l);
    }

    protected void createSubCircuits() throws Exception {
	for (int i = 0; i < outDegree; i++) 
	    subCircuits[i] = new XOR_2_1();

	super.createSubCircuits();
    }

    protected void connectWires() {
	for (int i = 0; i < outDegree; i++) {
	    inputWires[X(i)].connectTo(subCircuits[i].inputWires, 0);
	    inputWires[Y(i)].connectTo(subCircuits[i].inputWires, 1);
	}
    }

    protected void defineOutputWires() {
	for (int i = 0; i < outDegree; i++)
	    outputWires[i] = subCircuits[i].outputWires[0];
    }

    private int X(int i) {
	return i + outDegree;
    }

    private int Y(int i) {
	return i;
    }
}