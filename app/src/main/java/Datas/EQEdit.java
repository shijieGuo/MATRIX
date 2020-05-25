package Datas;

/**
 * Created by williamXia on 2017/8/11.
 *
 * this class stored the index value not the caculate value,you know must
 */

public class EQEdit {

    public byte eq_bypass;
    public byte eq_filterindex;
    public byte eq_qfactor;
    //
    public int eq_freqindex;
    public int eq_gaindex;

    public void clearData() {
        eq_bypass = eq_filterindex = eq_qfactor = 0;
        eq_freqindex = eq_gaindex = 0;
    }


    public EQEdit() {
        clearData();
    }

    public void copyEQ(EQEdit edata) {
        eq_bypass = edata.eq_bypass;
        eq_filterindex = edata.eq_filterindex;
        eq_qfactor = edata.eq_qfactor;
        //
        eq_freqindex = edata.eq_freqindex;
        eq_gaindex = edata.eq_gaindex;
    }


}
