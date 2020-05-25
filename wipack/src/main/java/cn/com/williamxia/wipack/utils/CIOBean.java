package cn.com.williamxia.wipack.utils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by williamXia on 2017/6/22.
 */

public class CIOBean implements Serializable {

    private String strHeader16;
    private byte[] beanData;
    private String fileName;
    private String strRecordTime;
    //----
    private boolean isNeedHead;

    private String beanTag;
    public static final int max_len_header=16;

    public void setBeanTag(String mtag) {
        this.beanTag = mtag;
    }

    public String getBeanTag() {
        return beanTag;
    }

    public CIOBean(String filename, byte[] datas, String shead) {

        this.fileName = filename;
        beanData = datas;
        //
        if (shead.length() > max_len_header)
            strHeader16 = shead.trim().substring(0, max_len_header);
        else
            strHeader16 = shead.trim();
        strRecordTime = getStrCurrentTime();
        isNeedHead = true;
    }

    public CIOBean(String mfileName) {
        this.fileName = mfileName;
        strHeader16 = null;
        isNeedHead = true;
        strRecordTime = getStrCurrentTime();

    }


    public CIOBean(String filename, String shead) {
        this.fileName = filename;
        if (shead.length() > max_len_header)
            strHeader16 = shead.trim().substring(0, max_len_header);
        else
            strHeader16 = shead.trim();
        isNeedHead = true;
        strRecordTime = getStrCurrentTime();
    }

    public CIOBean(String filename, byte[] datas) {
        this.fileName = filename;
        beanData = datas;
        strRecordTime = getStrCurrentTime();
        isNeedHead = true;
        strHeader16 = null;

    }

    public void setNeedHead(boolean misNeedHead) {
        isNeedHead = misNeedHead;
    }

    public boolean getIsNeedHead() {
        return isNeedHead;
    }

    //
    public String getStrRecordTime() {
        return strRecordTime;
    }

    public void setStrRecordTime(String stme) {
        strRecordTime = stme;
    }

    public void setStrHeader16(String strHead) {
        if (strHead.length() > max_len_header)
            strHeader16 = strHead.trim().substring(0, max_len_header);
        else
            strHeader16 = strHead.trim();
    }

    public String getStrHeader16() {
        return strHeader16;
    }

    public byte[] getByteHeader() {
        return Xover.stringToByte(strHeader16, max_len_header);
    }

    private void setBeanData(byte[] m_data) {
        beanData = m_data;
    }

    public byte[] getBeanData() {
       // qDebug.printBytes("get bean data-", beanData);
        return beanData;
    }

    /*
       * yyyy-MM-dd HH:mm:ss
       * */
    public static String getStrCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = new Date(System.currentTimeMillis());
        return format.format(currentDate);
    }

    /*
    * yyyy-MM-dd HH:mm:ss
    * */
    public String getStrCurrentTime(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = new Date(time);
        return formatter.format(currentDate);
    }

    public void setFileName(String sfileName) {
        this.fileName = sfileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void printBeanData() {
        qDebug.printBytes("Bean Head: " + strHeader16 + " FileName is " + fileName + "  Bean data ", beanData);

    }

}
