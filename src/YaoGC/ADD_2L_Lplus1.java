// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

/*
 * Fig. 1 of [KSS09]
 */
public class ADD_2L_Lplus1 extends CompositeCircuit {
    private final int L;

    public ADD_2L_Lplus1(int l) {
	super(2*l, l+1, l, "ADD_" + (2*l) + "_" + (l+1));
	
	L = l;
    }

    protected void createSubCircuits() throws Exception {
	for (int i = 0; i < L; i++) 
	    subCircuits[i] = new ADD_3_2();

	super.createSubCircuits();
    }

    protected void connectWires() {
	inputWires[X(0)].connectTo(subCircuits[0].inputWires, ADD_3_2.X);
	inputWires[Y(0)].connectTo(subCircuits[0].inputWires, ADD_3_2.Y);
	
	for (int i = 1; i < L; i++) {
	    inputWires[X(i)].connectTo(subCircuits[i].inputWires, ADD_3_2.X);
	    inputWires[Y(i)].connectTo(subCircuits[i].inputWires, ADD_3_2.Y);
	    subCircuits[i-1].outputWires[ADD_3_2.COUT].connectTo(subCircuits[i].inputWires,
								 ADD_3_2.CIN);
	}
    }

    protected void defineOutputWires() {
	for (int i = 0; i < L; i++)
	    outputWires[i] = subCircuits[i].outputWires[ADD_3_2.S];
	outputWires[L] = subCircuits[L-1].outputWires[ADD_3_2.COUT];
    }

    protected void fixInternalWires() {
    	Wire internalWire = subCircuits[0].inputWires[ADD_3_2.CIN];
    	internalWire.fixWire(0);
    }

    static int X(int i) {
	return 2*i+1;
    }

    static int Y(int i) {
	return 2*i;
    }


    /*
     * Connect xWires[xStartPos...xStartPos+L] to the wires representing bits of X;
     * yWires[yStartPos...yStartPos+L] to the wires representing bits of Y;
     */
    public void connectWiresToXY(Wire[] xWires, int xStartPos, Wire[] yWires, int yStartPos) throws Exception {
	if (xStartPos + L > xWires.length || yStartPos + L > yWires.length)
	    throw new Exception("Unmatched number of wires.");
	
	for (int i = 0; i < L; i++) {
	    xWires[xStartPos+i].connectTo(inputWires, X(i));
	    yWires[yStartPos+i].connectTo(inputWires, Y(i));
	}
    }
}
