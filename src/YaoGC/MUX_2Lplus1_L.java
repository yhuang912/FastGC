// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

/*
 * Fig. 9 of [KSS09]
 * if C == 0, choose X; otherwise choose Y.
 */
public class MUX_2Lplus1_L extends CompositeCircuit {
    private final int L;

    public MUX_2Lplus1_L(int l) {
	super(2*l+1, l, l, "MUX_" + (2*l+1) + "_" + (2*l));
	L = l;
	C = 2*L;
    }
   
    public int getL() {
	return L;
    }

    protected void createSubCircuits() throws Exception {
	for (int i = 0; i < L; i++) {
	    subCircuits[i] = new MUX_3_1();
	}

	super.createSubCircuits();
    }

    protected void connectWires() {
	for (int i = 0; i < L; i++) {
	    inputWires[X(i)].connectTo(subCircuits[i].inputWires, MUX_3_1.X);
	    inputWires[Y(i)].connectTo(subCircuits[i].inputWires, MUX_3_1.Y);
	    inputWires[C   ].connectTo(subCircuits[i].inputWires, MUX_3_1.C);
	}
    }

    protected void defineOutputWires() {
	for (int i = 0; i < L; i++)
	    outputWires[i] = subCircuits[i].outputWires[0];
    }

    static int X(int i) {
	return 2*i+1;
    }

    static int Y(int i) {
	return 2*i;
    }

    final int C;
}
