// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

class EDT_4_1 extends CompositeCircuit {
    private final static int XOR0 = 0;
    private final static int XOR1 = 1;
    private final static int   OR = 2;

    public EDT_4_1() {
	super(4, 1, 3, "EDT_4_1");
    }

    protected void createSubCircuits() throws Exception {
	subCircuits[XOR0] = new XOR_2_1();
	subCircuits[XOR1] = new XOR_2_1();
	subCircuits[  OR] = OR_2_1.newInstance();

	super.createSubCircuits();
    }

    protected void connectWires() {
	inputWires[0].connectTo(subCircuits[XOR0].inputWires, 0);
	inputWires[2].connectTo(subCircuits[XOR0].inputWires, 1);

	inputWires[1].connectTo(subCircuits[XOR1].inputWires, 0);
	inputWires[3].connectTo(subCircuits[XOR1].inputWires, 1);

	subCircuits[XOR0].outputWires[0].connectTo(subCircuits[OR].inputWires, 0);
	subCircuits[XOR1].outputWires[0].connectTo(subCircuits[OR].inputWires, 1);
    }

    protected void defineOutputWires() {
	outputWires[0] = subCircuits[OR].outputWires[0];
    }
}
