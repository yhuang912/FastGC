// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC.AESComponents;

import YaoGC.*;

class Inverse_GF256 extends CompositeCircuit {
    private static final byte Map	= 0;
    private static final byte InvMap	= 1;
    private static final byte Square0	= 2;
    private static final byte Square1	= 3;
    private static final byte Mult0	= 4;
    private static final byte Mult1	= 5;
    private static final byte Mult2	= 6;
    private static final byte XOR0	= 7;
    private static final byte XOR1	= 8;
    private static final byte XOR2	= 9;
    private static final byte MultE	= 10;
    private static final byte Invt	= 11;

    public Inverse_GF256() {
	super(8, 8, 12, "Inverse_GF256");
    }

    protected void createSubCircuits() throws Exception {
	subCircuits[Map]	= new MAP();
	subCircuits[InvMap]	= new InvMAP();
	subCircuits[Square0]	= new Square();
	subCircuits[Square1]	= new Square();
	subCircuits[Mult0]	= new Mult_GF16();
	subCircuits[Mult1]	= new Mult_GF16();
	subCircuits[Mult2]	= new Mult_GF16();
	subCircuits[XOR0]	= new XOR_2L_L(4);
	subCircuits[XOR1]	= new XOR_2L_L(4);
	subCircuits[XOR2]	= new XOR_2L_L(4);
	subCircuits[MultE]	= new MultE_GF16();
	subCircuits[Invt]	= new Inverse_GF16();

	super.createSubCircuits();
    }

    protected void connectWires() {
	for (int i = 0; i < 8; i++)
	    inputWires[i].connectTo(subCircuits[Map].inputWires, i);

	for (int i = 0; i < 4; i++) {
	    subCircuits[Map].outputWires[i+4].connectTo(subCircuits[Square0].inputWires, i);

	    subCircuits[Map].outputWires[i].connectTo(subCircuits[Square1].inputWires, i);

	    subCircuits[Map].outputWires[i+4].connectTo(subCircuits[Mult0].inputWires, i);
	    subCircuits[Map].outputWires[i].connectTo(subCircuits[Mult0].inputWires, i+4);

	    subCircuits[Map].outputWires[i+4].connectTo(subCircuits[XOR0].inputWires, i);
	    subCircuits[Map].outputWires[i].connectTo(subCircuits[XOR0].inputWires, i+4);

	    subCircuits[Square0].outputWires[i].connectTo(subCircuits[MultE].inputWires, i);

	    subCircuits[Square1].outputWires[i].connectTo(subCircuits[XOR1].inputWires, i);
	    subCircuits[MultE].outputWires[i].connectTo(subCircuits[XOR1].inputWires, i+4);

	    subCircuits[Mult0].outputWires[i].connectTo(subCircuits[XOR2].inputWires, i);
	    subCircuits[XOR1].outputWires[i].connectTo(subCircuits[XOR2].inputWires, i+4);

	    subCircuits[XOR2].outputWires[i].connectTo(subCircuits[Invt].inputWires, i);

	    subCircuits[Invt].outputWires[i].connectTo(subCircuits[Mult1].inputWires, i);
	    subCircuits[Map].outputWires[i+4].connectTo(subCircuits[Mult1].inputWires, i+4);

	    subCircuits[XOR0].outputWires[i].connectTo(subCircuits[Mult2].inputWires, i);
	    subCircuits[Invt].outputWires[i].connectTo(subCircuits[Mult2].inputWires, i+4);

	    subCircuits[Mult2].outputWires[i].connectTo(subCircuits[InvMap].inputWires, i);
	    subCircuits[Mult1].outputWires[i].connectTo(subCircuits[InvMap].inputWires, i+4);
	}
    }

    protected void defineOutputWires() {
	System.arraycopy(subCircuits[InvMap].outputWires, 0, outputWires, 0, 8);
    }
}