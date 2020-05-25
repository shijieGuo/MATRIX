package cn.com.williamxia.wipack.utils;

import java.io.Serializable;

/**
 * Created by williamXia on 2017/9/8.
 */

public class SomethingMsg implements Serializable {


    public String fID;
    public int HValue;
    public int LValue;


    public int FHValue;
    public int FLValue;

    public String strHValue;
    public String strLValue;


    public SomethingMsg(String strID) {
        HValue = LValue = FHValue = FLValue = 0;
        fID = strID;
        strHValue = strLValue = null;
    }


}
