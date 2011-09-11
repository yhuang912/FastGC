package Program;

import java.math.*;

import YaoGC.*;
import Utils.*;
import LookupTable.*;

public class SmithWatermanServer extends ProgServer {
    // public static int L = 5;

    public static BigInteger sdna;
    private BigInteger[][] sdnalps, cdnalps;
    public static int sdnaLen;
    private int cdnaLen;

    private State outputState;

    public SmithWatermanServer(BigInteger dna, int length) {
	sdna = dna;

	// length of dna sequence. Effective bit length of variable dna is twice of 'length'
	sdnaLen = length;
    }

    public SmithWatermanServer(String dna) throws Exception {
	this(SmithWatermanCommon.toBigInteger(dna), dna.length());
    }

    protected void init() throws Exception {
	cdnaLen = SmithWatermanCommon.ois.readInt();
	SmithWatermanCommon.oos.writeInt(sdnaLen);
	SmithWatermanCommon.oos.flush();
	
	SmithWatermanCommon.sdnaLen = sdnaLen;
	SmithWatermanCommon.cdnaLen = cdnaLen;

	SmithWatermanCommon.initCircuits();

	LookupTable1DSender.oos = SmithWatermanCommon.oos;
	SmithWatermanCommon.agent = new LookupTable1DSender();

	generateLabelPairsForDNAs();

	super.init();
    }

    private void generateLabelPairsForDNAs() {
	sdnalps = new BigInteger[SmithWatermanCommon.nBitsCodon*sdnaLen][2];
	cdnalps = new BigInteger[SmithWatermanCommon.nBitsCodon*cdnaLen][2];

	for (int i = 0; i < sdnaLen; i++) {
	    for (int j = 0; j < SmithWatermanCommon.nBitsCodon; j++) {
		sdnalps[SmithWatermanCommon.nBitsCodon*i+j] = Wire.newLabelPair();
	    }
	}

	for (int i = 0; i < cdnaLen; i++) {
	    for (int j = 0; j < SmithWatermanCommon.nBitsCodon; j++) {
		cdnalps[SmithWatermanCommon.nBitsCodon*i+j] = Wire.newLabelPair();
	    }
	}
    }

    protected void execTransfer() throws Exception {
	int bytelength = (Wire.labelBitLength-1)/8 + 1;

	for (int i = 0; i < sdnalps.length; i++) {
	    int idx = sdna.testBit(i) ? 1 : 0;
	    Utils.writeBigInteger(sdnalps[i][idx], bytelength, SmithWatermanCommon.oos);

	}
	SmithWatermanCommon.oos.flush();
	StopWatch.taskTimeStamp("sending labels for selfs inputs");

	snder.execProtocol(cdnalps);
	StopWatch.taskTimeStamp("sending labels for peers inputs");
    }

    protected void execCircuit() throws Exception {
	BigInteger[] sdnalbs = new BigInteger[SmithWatermanCommon.nBitsCodon*sdnaLen];
	BigInteger[] cdnalbs = new BigInteger[SmithWatermanCommon.nBitsCodon*cdnaLen];

	for (int i = 0; i < sdnalbs.length; i++)
	    sdnalbs[i] = sdnalps[i][0];

	for (int i = 0; i < cdnalbs.length; i++)
	    cdnalbs[i] = cdnalps[i][0];

	outputState = SmithWatermanCommon.execCircuit(sdnalbs, cdnalbs);
    }

    protected void interpretResult() throws Exception {
	BigInteger[] outLabels = (BigInteger[]) SmithWatermanCommon.ois.readObject();

	BigInteger output = BigInteger.ZERO;
	for (int i = 0; i < outLabels.length; i++) {
	    if (outputState.wires[i].value != Wire.UNKNOWN_SIG) {
		if (outputState.wires[i].value == 1)
		    output = output.setBit(i);
		continue;
	    }
	    else if (outLabels[i].equals(outputState.wires[i].invd ? 
					 outputState.wires[i].lbl :
					 outputState.wires[i].lbl.xor(Wire.R.shiftLeft(1).setBit(0)))) {
		    output = output.setBit(i);
	    }
	    else if (!outLabels[i].equals(outputState.wires[i].invd ? 
					  outputState.wires[i].lbl.xor(Wire.R.shiftLeft(1).setBit(0)) :
					  outputState.wires[i].lbl)) 
		throw new Exception("Bad label encountered: i = " + i + "\t" +
				    outLabels[i] + " != (" + 
				    outputState.wires[i].lbl + ", " +
				    outputState.wires[i].lbl.xor(Wire.R.shiftLeft(1).setBit(0)) + ")");
	}
	
	System.out.println("output (pp): " + output);
	StopWatch.taskTimeStamp("output labels received and interpreted");
    }

    protected void verify_result() throws Exception {
	BigInteger cdna = (BigInteger) SmithWatermanCommon.ois.readObject();
	
	String sdnaStr = SmithWatermanCommon.toString(sdna, sdnaLen);
	String cdnaStr = SmithWatermanCommon.toString(cdna, cdnaLen);

	System.out.println(sdnaStr);
	System.out.println(cdnaStr);

	int[][] D = new int[sdnaLen+1][cdnaLen+1];

	for (int i = 0; i < sdnaLen+1; i++)
	    D[i][0] = 0;

	for (int j = 0; j < cdnaLen+1; j++)
	    D[0][j] = 0;

	for (int i = 1; i < sdnaLen+1; i++)
	    for (int j = 1; j < cdnaLen+1; j++) {
		int s = SmithWatermanCommon.score
		    [SmithWatermanCommon.codons.indexOf(sdnaStr.charAt(i-1))]
		    [SmithWatermanCommon.codons.indexOf(cdnaStr.charAt(j-1))];
		D[i][j] = 0;
		for (int o = 1; o <= i; o++) {
		    int g = SmithWatermanCommon.gapA + SmithWatermanCommon.gapB * o;
		    D[i][j] = Math.max(D[i][j], D[i-o][j] - g);
		}
		for (int o = 1; o <= j; o++) {
		    int g = SmithWatermanCommon.gapA + SmithWatermanCommon.gapB * o;
		    D[i][j] = Math.max(D[i][j], D[i][j-o] - g);
		}
		D[i][j] = Math.max(D[i][j], D[i-1][j-1] + s);
	    }

	System.out.println("output (verify): " + D[sdnaLen][cdnaLen]);
    }
}