// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

/*
 * Fig. 6 of [KSS09]
 */
public class GT_3_1 extends CompositeCircuit {
    private final static int XOR0 = 0;
    private final static int XOR1 = 1;
    private final static int XOR2 = 2;
    private final static int AND0 = 3;

    public  final static int X    = 0;
    public  final static int Y    = 1;
    public  final static int C    = 2;

    public GT_3_1() {
	super(3, 1, 4, "GT_3_1");
    }

    protected void createSubCircuits() throws Exception {
	subCircuits[XOR0] = new XOR_2_1();
	subCircuits[XOR1] = new XOR_2_1();
	subCircuits[XOR2] = new XOR_2_1();
	subCircuits[AND0] = AND_2_1.newInstance();

	super.createSubCircuits();
    }

    protected void connectWires() {
	inputWires[X].connectTo(subCircuits[XOR0].inputWires, 0);
	inputWires[X].connectTo(subCircuits[XOR1].inputWires, 0);
	inputWires[Y].connectTo(subCircuits[XOR2].inputWires, 0);
	inputWires[C].connectTo(subCircuits[XOR1].inputWires, 1);
	inputWires[C].connectTo(subCircuits[XOR2].inputWires, 1);

	subCircuits[XOR1].outputWires[0].connectTo(subCircuits[AND0].inputWires, 0);
	subCircuits[XOR2].outputWires[0].connectTo(subCircuits[AND0].inputWires, 1);
	subCircuits[AND0].outputWires[0].connectTo(subCircuits[XOR0].inputWires, 1);
    }

    protected void defineOutputWires() {
	outputWires[0] = subCircuits[XOR0].outputWires[0];
    }
}
