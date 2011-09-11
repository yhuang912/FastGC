// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

public class MUX_3_1 extends CompositeCircuit {
    private final static int XOR0 = 0;
    private final static int XOR1 = 1;
    private final static int AND0 = 2;
    
    public  final static int X    = 0;
    public  final static int Y    = 1;
    public  final static int C    = 2;

    public MUX_3_1() {
	super(3, 1, 3, "MUX");
    }

    protected void createSubCircuits() throws Exception {
	subCircuits[XOR0] = new XOR_2_1();
	subCircuits[XOR1] = new XOR_2_1();
	subCircuits[AND0] = AND_2_1.newInstance();
	
	super.createSubCircuits();
    }

    protected void connectWires() {
	inputWires[X].connectTo(subCircuits[XOR0].inputWires, 0);
	inputWires[X].connectTo(subCircuits[XOR1].inputWires, 0);
	inputWires[Y].connectTo(subCircuits[XOR1].inputWires, 1);
	inputWires[C].connectTo(subCircuits[AND0].inputWires, 0);

	subCircuits[XOR1].outputWires[0].connectTo(subCircuits[AND0].inputWires, 1);
	subCircuits[AND0].outputWires[0].connectTo(subCircuits[XOR0].inputWires, 1);
    }

    protected void defineOutputWires() {
	outputWires[0] = subCircuits[XOR0].outputWires[0];
    }

    // public int[] testCircuit(int[] inputs, String s) throws Exception {
    // 	int[] outputs = super.testCircuit(inputs, s);

    // 	return outputs;
    // }

    // public boolean verification(int[] inputs, int[] outputs) {
    // 	System.out.print(Color.blue + name + "("+ inputs[0] + ", " + inputs[1] + ", " + 
    // 			 inputs[2] + ") = " + Color.cyan + 
    // 			 outputs[0] + "\t" + Color.black);

    // 	boolean isCorrect = false;
    // 	if (outputs[0] == inputs[inputs[2]])
    // 	    isCorrect = true;
    // 	System.out.println((isCorrect? (Color.green+"Correct") : (Color.red+"Wrong")) + 
    // 			 Color.black);
    // 	return isCorrect;
    // }
}
