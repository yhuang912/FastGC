// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

class EDT_2L_1 extends CompositeCircuit {
    final int L;

    public EDT_2L_1(int l) {
	super(2*l, 1, l+1, "EDT_2L_1");
	L = l;
    }

    protected void createSubCircuits() throws Exception {
	for (int i = 0; i < L; i++)
	    subCircuits[i] = new XOR_2_1();

	subCircuits[L] = new OR_L_1(L);

	super.createSubCircuits();
    }

    protected void connectWires() {
	for (int i = 0; i < L; i++) {
	    inputWires[i  ].connectTo(subCircuits[i].inputWires, 0);
	    inputWires[i+L].connectTo(subCircuits[i].inputWires, 1);
	    subCircuits[i].outputWires[0].connectTo(subCircuits[L].inputWires, i);
	}
    }

    protected void defineOutputWires() {
	outputWires[0] = subCircuits[L].outputWires[0];
    }
}