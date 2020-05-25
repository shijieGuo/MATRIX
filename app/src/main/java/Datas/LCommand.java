package Datas;

import java.util.Arrays;

import cn.com.williamxia.wipack.utils.WiStaticComm;
import cn.com.williamxia.wipack.utils.qDebug;


/**
 * Created by williamXia on 2017/8/16.
 */

public class LCommand {
    public static final byte BZero = 0;

    private int packLen;
    private int _cmdType;
    public int packMinLen = 14;
    public int ID_Machine = 25;//D2000.2
    public int Device_ID = 0x1000;

    public static final int Len_Big_Times=256;
    public static final int Len_Small_Times=100;


    public LCommand(final int length, int cmdType) {

        packLen = length;
        _cmdType = cmdType;
    }

    public void setID_Machine(int ID_Machine) {
        this.ID_Machine = ID_Machine;
    }

    public void setDevice_ID(int device_ID) {
        Device_ID = device_ID;
    }

    public void set_cmdType(int _cmdType) {
        this._cmdType = _cmdType;
    }

    /// <summary>
    /// getpackage with added data:aData
    /// </summary>
    /// <param name="aData"></param>
    /// <returns></returns>
    public byte[] getPackage_withDataArray(byte[] aData) {

        byte[] m_package = new byte[packLen];
        Arrays.fill(m_package, BZero);

        if (aData == null) {
            qDebug.qLog("package data added is error......");
            return m_package;
        }

        m_package[0] = WiStaticComm.UTRAL_H0;
        m_package[1] = WiStaticComm.UTRAL_H1;
        m_package[2] = WiStaticComm.UTRAL_H2;
        //
        m_package[3] = (byte) (packLen / Len_Big_Times);
        m_package[4] = (byte) (packLen % Len_Big_Times);

        m_package[5] = (byte) (ID_Machine / Len_Big_Times);
        m_package[6] = (byte) (ID_Machine % Len_Big_Times);

        m_package[7] = (byte) (Device_ID / Len_Big_Times);
        m_package[8] = (byte) (Device_ID % Len_Big_Times);

        m_package[9] = (byte) (_cmdType / Len_Big_Times);
        m_package[10] = (byte) (_cmdType % Len_Big_Times);
        //

        System.arraycopy(aData, 0, m_package, 11, aData.length);

        m_package[packLen-4]=0x0A;
        m_package[packLen-3]= (byte) 0xF4;
        m_package[packLen-2]= (byte) 0xAA;

        m_package[packLen - 1] = WiStaticComm.UTRAL_End;

        byte checkSum = m_package[0];
        for (int i = 1; i < packLen; i++) {
            if (i != packLen - 5)
                checkSum ^= m_package[i];

        }
        m_package[packLen - 5] = checkSum;
        return m_package;

    }

    /// <summary>
    /// getm_package with added data:aData,clen is the segment index
    /// </summary>
    /// <param name="aData"></param>
    /// <returns></returns>
    public byte[] getPackage_withDataArray(byte[] aData, int clen) {

        byte[] m_package = new byte[packLen];
        Arrays.fill(m_package, BZero);
        if (aData == null) {
            qDebug.qLog("m_package data added is error......");
            return m_package;
        }
        m_package[0] = WiStaticComm.UTRAL_H0;
        m_package[1] = WiStaticComm.UTRAL_H1;
        m_package[2] = WiStaticComm.UTRAL_H2;
        //
        m_package[3] = (byte) (packLen / Len_Big_Times);
        m_package[4] = (byte) (packLen % Len_Big_Times);

        m_package[5] = (byte) (ID_Machine / Len_Big_Times);
        m_package[6] = (byte) (ID_Machine % Len_Big_Times);

        m_package[7] = (byte) (Device_ID / Len_Big_Times);
        m_package[8] = (byte) (Device_ID % Len_Big_Times);

        m_package[9] = (byte) (_cmdType / Len_Big_Times);
        m_package[10] = (byte) (_cmdType % Len_Big_Times);

        m_package[11] = (byte) (clen / 100);
        m_package[12] = (byte) ((clen) % 100);
        //
        System.arraycopy(aData, 0, m_package, 13, aData.length);

        //len-3,len-4 is reserved

        byte checkSum = m_package[0];
        for (int i = 1; i < packLen; i++) {
            checkSum ^= m_package[i];

        }
        m_package[packLen - 1] = WiStaticComm.UTRAL_End;
        for (int i = 1; i < packLen; i++) {
            if (i != packLen - 2)
                checkSum ^= m_package[i];

        }
        m_package[packLen - 2] = checkSum;
        return m_package;

    }

    public byte[] getPackage_withoutDataArray(int item) //length 14
    {

        byte[] m_package = new byte[packLen];
        Arrays.fill(m_package, BZero);

        m_package[0] = WiStaticComm.UTRAL_H0;
        m_package[1] = WiStaticComm.UTRAL_H1;
        m_package[2] = WiStaticComm.UTRAL_H2;
        //
        m_package[3] = (byte) (packLen / Len_Big_Times);
        m_package[4] = (byte) (packLen % Len_Big_Times);

        m_package[5] = (byte) (ID_Machine / Len_Big_Times);
        m_package[6] = (byte) (ID_Machine % Len_Big_Times);

        m_package[7] = (byte) (Device_ID / Len_Big_Times);
        m_package[8] = (byte) (Device_ID % Len_Big_Times);

        m_package[9] = (byte) (_cmdType / Len_Big_Times);
        m_package[10] = (byte) (_cmdType % Len_Big_Times);
        m_package[11] = (byte) item;

        //
        byte checkSum = m_package[0];

        m_package[packLen - 1] = WiStaticComm.UTRAL_End;

        for (int i = 1; i < packLen; i++) {
            if (i != packLen - 2)
                checkSum ^= m_package[i];

        }
        m_package[packLen - 2] = checkSum;
        return m_package;

    }

    public byte[] getPackage_withoutDataArray() //about 13 length 11+1+1
    {

        byte[] m_package = new byte[packLen];
        Arrays.fill(m_package, BZero);

        m_package[0] = WiStaticComm.UTRAL_H0;
        m_package[1] = WiStaticComm.UTRAL_H1;
        m_package[2] = WiStaticComm.UTRAL_H2;
        //
        m_package[3] = (byte) (packLen / Len_Big_Times);
        m_package[4] = (byte) (packLen % Len_Big_Times);

        m_package[5] = (byte) (ID_Machine / Len_Big_Times);
        m_package[6] = (byte) (ID_Machine % Len_Big_Times);

        m_package[7] = (byte) (Device_ID / Len_Big_Times);
        m_package[8] = (byte) (Device_ID % Len_Big_Times);

        m_package[9] = (byte) (_cmdType / Len_Big_Times);
        m_package[10] = (byte) (_cmdType % Len_Big_Times);
        //
        byte checkSum = m_package[0];
        m_package[packLen-4]=0x0A;
        m_package[packLen-3]= (byte) 0xF4;
        m_package[packLen-2]= (byte) 0xAA;
        m_package[packLen - 1] = WiStaticComm.UTRAL_End;

        for (int i = 1; i < packLen; i++) {
            if (i != packLen - 5)
                checkSum ^= m_package[i];

        }
        m_package[packLen - 5] = checkSum;
        return m_package;

    }

    /// <summary>
    /// getpackage with added Memorydata:aData,clen is the segment index
    /// </summary>
    /// <param name="aData"></param>
    /// <returns></returns>
    public byte[] getPackage_withDataMemory(byte[] aData) {

        byte[] m_package = new byte[packLen];
        Arrays.fill(m_package, BZero);
        if (aData == null) {
            qDebug.qLog("m_package data added is error......");
            return m_package;
        }
        m_package[0] = WiStaticComm.UTRAL_H0;
        m_package[1] = WiStaticComm.UTRAL_H1;
        m_package[2] = WiStaticComm.UTRAL_H2;
        //
        m_package[3] = (byte) (packLen / Len_Big_Times);
        m_package[4] = (byte) (packLen % Len_Big_Times);

        m_package[5] = (byte) (ID_Machine / Len_Big_Times);
        m_package[6] = (byte) (ID_Machine % Len_Big_Times);

        m_package[7] = (byte) (Device_ID / Len_Big_Times);
        m_package[8] = (byte) (Device_ID % Len_Big_Times);

        m_package[9] = (byte) (_cmdType / Len_Big_Times);
        m_package[10] = (byte) (_cmdType % Len_Big_Times);
        //
        System.arraycopy(aData, 11, m_package, 11, aData.length - 11);

        //len-3,len-4 is reserved
        byte checkSum = m_package[0];
        for (int i = 1; i < packLen; i++) {
            checkSum ^= m_package[i];

        }
        m_package[packLen - 2] = checkSum;
        m_package[packLen - 1] = WiStaticComm.UTRAL_End;
        return m_package;

    }
}


