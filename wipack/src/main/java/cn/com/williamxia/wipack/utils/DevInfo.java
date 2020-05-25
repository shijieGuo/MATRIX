package cn.com.williamxia.wipack.utils;

import java.util.Arrays;

/**
 * Created by williamXia on 2017/9/13.
 */

public class DevInfo {

    public int mAppID;
    public int mDevID;
    public byte Hver;//mcu
    public byte lVer;//dsp
    public String devceAddr;//devIP
    public String devMac;
    public byte[] m_devName;

    public static final int LenDevName = 16;


    public byte mStatus;

    public DevInfo() {
        m_devName = new byte[LenDevName];
        resetDev();
    }

    public DevInfo(String s){

    }

    public void resetDev() {
        devceAddr = "";
        devMac = "";
        mAppID = 0;
        mDevID = 0;
        Hver = 0;
        lVer = 0;
        Arrays.fill(m_devName, (byte) 0x20);
    }

    public boolean isTheSameDevceID(DevInfo devInfo) {
        return (mDevID == devInfo.mDevID);
    }

    public boolean isTheSameAddress(DevInfo devInfo) {
        return (devceAddr.equals(devInfo.devceAddr));
    }


    //
    public String getShortDevInfo() {
        return String.format("AppID: %s  DeviceID: %s ", getHexAppID(), getHexDevID());
    }

    public String getHexAppID() {
        return String.format("%02x", mAppID).toUpperCase();
    }

    public String getHexDevID() {
        return String.format("%04x", mDevID).toUpperCase();
    }

    public String getStrVer() {
        String strVer = null;
        if (Hver == 0)
            strVer = "Mcu: " + verCal(lVer);
        else
            strVer = "Mcu: " + verCal(Hver) + "  DSP: " + verCal(lVer);
        return strVer;
    }

    public String getStrStatus() {
        return (mStatus > 0 ? "true" : "false");

    }

    public String verCal(byte ver) {
        if (ver == 0) return null;
        String strv = (String.format("%02x", ver));
        StringBuilder strbd = new StringBuilder(strv);
        strbd.insert(1, ".");
        return strbd.toString();
    }

    public void setM_devName(String strN) {
        Xover.stringToByte(strN, LenDevName);
    }

    public String getDevName() {
        return Xover.byteToString(m_devName, LenDevName);

    }


}
