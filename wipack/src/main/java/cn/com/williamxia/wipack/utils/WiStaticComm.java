package cn.com.williamxia.wipack.utils;

/**
 * Created by williamXia on 2017/6/22.
 */
public class WiStaticComm {

//    public static final String StrPresetHeader = "D2000.2 Presets File";
//
//    public static final String Action_SaveLoad = "Action.SaveLoad";
//    public static final String Msg_saveLoad = "Msg_saveLoad";
//
//
//    public static final String Msg_v_online = "value_saveload_online";
//    public static final String Msg_v_offline = "value_saveload_offline";
//    //region begin proces message
//
//    public static final String Msg_v_sLoad_returnPresetList = "value_saveload_return_prelist";
//
//
//    public static final String Msg_v_sLoad_returnSaveOnLine = "value_saveload_return_saveonline";
//
//    public static final String Msg_v_sLoad_returnLoadOnLine = "value_saveload_return_loadonline";
//    public static final String Msg_v_sLoad_returnDeleteOnLine = "value_saveload_return_deleteonline";
//    public static final String Msg_v_sLoad_returnLoadLocal = "value_saveload_return_loadlocal";
//
//    //endregion


    //endregion


    public static final int LEN_MinIP = 11;

    public static final int SHIFT=4;
    public static final int LEN_1M = 1024;
    public static final int Len_Big_Time = 256;
    public static final int Len_Small_Time = 100;
    public static final byte BOne = 1;
    public static final byte BZero = 0;

    public static final byte UTRAL_H0 = 0x01;
    public static final byte UTRAL_H1 = 0x20;
    public static final byte UTRAL_H2 = 0x03;
    public static final byte UTRAL_End = 0x40;

    public static final int TCPScanedFlag = 99;
    public static final int TCPNormalFlag = 0;
    public static final int CHByte = 0xff;
    public static final int HByte=0xf0;
    public static final byte LByte=0x0f;

    public static final int rDevPort = 5000;
    public static final int rPort_HAF11 = 48899;
    public static final String BordcastAddr = "255.255.255.255";


    public static final int AfterScan_toCheckIP = 0x04;
    public static final int HAS_FIND_IP = 0x00;
    public static final int NOT_FIND_IP = 0x01;

    public static final int SocketCHECK_TimeOut = 0x03;

    public static final String HAF11ModuleDetect = "HF-A11ASSISTHREAD";
    public static final byte[] IP120ModuleDetect = new byte[]{0x01, 0x02, 0x00, 0x0c, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, (byte) 0xff, (byte) 0xff};


    public static final String[] StrRatio = new String[]{
            "1:1", "1:2", "1:3", "1:4", "1:5", "1:6", "1:7", "1:8", "1:9", "Limit"
    };

    public static final String[] HLP_FILTER_TYPES = new String[]{"Bypass", "BW6", "BES6", "BW12", "BES12", "LK12", "BW18"
            , "BES18", "BW24", "BES24", "LK24", "BW30", "BES30", "BW36", "BES36", "LK36", "BW42", "BES42", "BW48", "BES48"
            , "LK48"};


}
