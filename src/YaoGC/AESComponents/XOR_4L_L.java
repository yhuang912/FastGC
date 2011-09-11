// Copyright (C) 2010 by Yan Huang <yh8h@virginia.edu>

package YaoGC.AESComponents;

import YaoGC.*;

public class XOR_4L_L extends CompositeCircuit {

    public XOR_4L_L(int l) {
	super(4*l, l, 3, "XOR_"+(4*l)+"_"+l);
    }

    protected void createSubCircuits() throws Exception {
	subCircuits[0] = new XOR_2L_L(outDegree);
	subCircuits[1] = new XOR_2L_L(outDegree);
	subCircuits[2] = new XOR_2L_L(outDegree);

	super.createSubCircuits();
    }

    protected void connectWires() {
	for (int i = 0; i < outDegree; i++) {
	    inputWires[i          ].connectTo(subCircuits[0].inputWires, i);
	    inputWires[i+outDegree].connectTo(subCircuits[0].inputWires, i+outDegree);

	    inputWires[i+2*outDegree].connectTo(subCircuits[1].inputWires, i);
	    inputWires[i+3*outDegree].connectTo(subCircuits[1].inputWires, i+outDegree);

	    subCircuits[0].outputWires[i].connectTo(subCircuits[2].inputWires, i);
	    subCircuits[1].outputWires[i].connectTo(subCircuits[2].inputWires, i+outDegree);
	}
    }

    protected void defineOutputWires() {
	System.arraycopy(subCircuits[2].outputWires, 0, outputWires, 0, outDegree);
    }
}