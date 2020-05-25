package Datas;

/**
 * Created by williamXia on 2017/11/28.
 */

public class CAutoMixerParam {
    public int autoAttack;
    public int autoRelease;
    public byte autoHavgTau;
    public byte autoPower;
    //


    public CAutoMixerParam() {
        resetParam();
    }

    public void resetParam() {
        autoAttack = autoRelease = autoHavgTau = autoPower = 0;
    }
}
