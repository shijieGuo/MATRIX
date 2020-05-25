package Datas;

/**
 * Created by williamXia on 2017/8/11.
 */

public class LimitEdit {

    public byte limit_threshold;//
    public byte limit_ratio;//

    public int limit_attack;
    public int limit_release;
    //

    public byte limit_bypass;
    public byte limit_gain;

    public LimitEdit() {
        clearData();
    }

    public void clearData() {
        limit_attack = limit_release = 0;
        limit_bypass = limit_gain = limit_ratio = limit_threshold = 0;
    }

    public void copyLimit(LimitEdit ledata) {

        limit_bypass = ledata.limit_bypass;
        limit_attack = ledata.limit_attack;
        //
        limit_gain = ledata.limit_gain;
        limit_ratio = ledata.limit_ratio;
        limit_release = ledata.limit_release;
        //
        limit_threshold = ledata.limit_threshold;


    }


}
