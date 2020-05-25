package Adapters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by williamXia on 2017/8/25.
 */

public class CLocalPresetInfo {
    public String mFileName;
    public String mSaveTime;
    public String mSavePath;

    public CLocalPresetInfo() {
        mFileName = null;
        mSaveTime = null;
        mSavePath = null;
    }

    public CLocalPresetInfo(String fileName) {
        mFileName = fileName;
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mSaveTime = format.format(date);
        mSavePath = null;

    }

}
