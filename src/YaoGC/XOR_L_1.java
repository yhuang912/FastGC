// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

public class XOR_L_1 extends CompositeCircuit {

    public XOR_L_1(int l) {
	super(l, 1, l-1, "XOR_L_1");
    }

    protected void createSubCircuits() throws Exception {
	for (int i = 0; i < inDegree-1; i++)
	    subCircuits[i] = new XOR_2_1();

	super.createSubCircuits();
    }

    protected void connectWires() {
	inputWires[0].connectTo(subCircuits[0].inputWires, 0);
	inputWires[1].connectTo(subCircuits[0].inputWires, 1);

	for (int i = 1; i < inDegree-1; i++) {
	    subCircuits[i-1].outputWires[0].connectTo(subCircuits[i].inputWires, 0);
	    inputWires[i+1].connectTo(subCircuits[i].inputWires, 1);
	}
    }

    protected void defineOutputWires() {
	outputWires[0] = subCircuits[inDegree-2].outputWires[0];
   }
}