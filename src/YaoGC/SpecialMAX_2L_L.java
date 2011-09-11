// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

/* 
 * This MAX circuit is special in that, of the two numbers X and Y, X
 * is assumed to be non-negative, while Y can be either negative or
 * non-negative.
 *
 */
public class SpecialMAX_2L_L extends CompositeCircuit {
    private final int L;

    public final static int  GT = 0;
    public final static int MUX = 1;
    private final static int OR = 2;

    public SpecialMAX_2L_L(int l) {
	super(2*l, l, 3, "SpecialMAX_" + 2*l + "_" + l);
	L = l;
    }

    protected void createSubCircuits() throws Exception {
	subCircuits[GT]  = new GT_2L_1(L);
	subCircuits[MUX] = new MUX_2Lplus1_L(L);
	subCircuits[OR] = OR_2_1.newInstance();
	
	super.createSubCircuits();
    }

    protected void connectWires() throws Exception {
	for (int i = 0; i < L; i++) {
	    inputWires[i+L].connectTo(subCircuits[GT].inputWires, GT_2L_1.X(i));
	    inputWires[i  ].connectTo(subCircuits[GT].inputWires, GT_2L_1.Y(i));

	    inputWires[i  ].connectTo(subCircuits[MUX].inputWires, MUX_2Lplus1_L.X(i));
	    inputWires[i+L].connectTo(subCircuits[MUX].inputWires, MUX_2Lplus1_L.Y(i));
	}

	subCircuits[GT].outputWires[0].connectTo(subCircuits[OR].inputWires, 0);
	inputWires[L-1].connectTo(subCircuits[OR].inputWires, 1);
	subCircuits[OR].outputWires[0].connectTo(subCircuits[MUX].inputWires, 2*L);
    }

    protected void defineOutputWires() {
	for (int i = 0; i < L; i++)
	    outputWires[i] = subCircuits[MUX].outputWires[i];
    }
}
