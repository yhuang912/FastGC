// Copyright (C) 2010 by Yan Huang <yhuang@virginia.edu>

package YaoGC;

class G_AND_2_1 extends AND_2_1 {
    public G_AND_2_1() {
	super();
    }

    protected void execYao() {
	fillTruthTable();
	encryptTruthTable();
	sendGTT();
	gtt = null;
    }
}
