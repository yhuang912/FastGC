// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

class G_OR_2_1 extends OR_2_1 {
    public G_OR_2_1() {
	super();
    }

    protected void execYao() {
	fillTruthTable();
	encryptTruthTable();
	sendGTT();
	gtt = null;
    }
}
