package Datas;

import android.content.Context;

import java.io.Serializable;

import cn.com.williamxia.matrixa8dante.MainActivity;
import cn.com.williamxia.wipack.socket.TCPClient;
import cn.com.williamxia.wipack.utils.IpManager;
import cn.com.williamxia.wipack.utils.WiStaticComm;
import cn.com.williamxia.wipack.utils.Xover;
import cn.com.williamxia.wipack.utils.qDebug;
import comcontrol.UdpLongUtil;

/**
 * Created by williamXia on 2017/9/6.
 */

public class CmdSender {

    public static final int SYNC_END = 0x40;
    public static final int NORMAL_RECEIVE_LEN = 15;
    public static final int CHECK_TIMER_COUNTER = 4;


    public static final int CMD_INDEX = 8;
    public static final int CMD_READ_DSP = 0xf2;


    public static byte[] getPackage_ack() {
        byte[] HEART_CMD = new byte[]{

                WiStaticComm.UTRAL_H0,
                WiStaticComm.UTRAL_H1,
                WiStaticComm.UTRAL_H2,
                0x00,
                0x13,
                0x00,
                0x06,
                0x01,
                0x00,
                0x00,
                0x52,
                0x00,
                0x00,
                0x00,
                0x00,
                0x0A,
                (byte) 0xF4,
                (byte) 0xAA,
                0x40
        };
        byte checkSum = HEART_CMD[0];
        for (int i = 1; i < 19; i++)
        {
            if (i != 19 - 5)
                checkSum ^= HEART_CMD[i];

        }
        HEART_CMD[19 - 5] = checkSum;
        return HEART_CMD;
    }


    public static byte[] getPackage_searchDevice() {
//        final int len = 13;
//        byte[] m_package = new byte[len];
//        Arrays.fill(m_package, WiStaticComm.BZero);
//        m_package[0] = WiStaticComm.UTRAL_H0;
//        m_package[1] = WiStaticComm.UTRAL_H1;
//        m_package[2] = WiStaticComm.UTRAL_H2;
//        m_package[3] = 0;
//        m_package[4] = 0x0d;
//
//        m_package[5] = (byte) CHByte;
//        m_package[6] = (byte) CHByte;
//        m_package[7] = (byte) CHByte;
//        m_package[8] = (byte) CHByte;
//
//        m_package[9] = 0;
//        m_package[10] = 0x03;
//
//        m_package[11] = 0;
//
//        m_package[len - 1] = SYNC_END;
//        return m_package;
        final int len = 16;
        byte[] m_package = new byte[len];
//        Arrays.fill(m_package, WiStaticComm.BZero);
        m_package[0] = WiStaticComm.UTRAL_H0;
        m_package[1] = WiStaticComm.UTRAL_H1;
        m_package[2] = WiStaticComm.UTRAL_H2;
        m_package[3] = 0x00;
        m_package[4] = 0x10;
        m_package[5] = (byte) 0xFF;
        m_package[6] = (byte) 0xFF;
        m_package[7] = (byte) 0xFF;
        m_package[8] = (byte) 0xFF;
        m_package[9] = 0x00;
        m_package[10] = 0x03;
        m_package[11] = 0x25;
        m_package[12] = 0x0A;
        m_package[13] = (byte) 0xF4;
        m_package[14] = (byte) 0xAA;
        m_package[15] = SYNC_END;
//        byte checkSum = m_package[0];
//        for (int i = 1; i < len; i++)
//        {
//            if (i != len - 5)
//                checkSum ^= m_package[i];
//
//        }
//        m_package[len - 5] = checkSum;
        return m_package;
    }

    //public static void  sendCMD_
    public static void sendCMD_SearchDevice() {

        if (TCPClient.getInstance().isConnected()) {
            byte[] m_data = getPackage_searchDevice();
           qDebug.fprintBytes("william","sendcmd-search device",m_data);
            TCPClient.getInstance().notifySendCommand(m_data);
        }

    }


    /// <summary>
    /// read Deivce info
    /// </summary>
    public static void sendCMD_readDevices( final int pDeviceID)
    {
        int count = DStatic.LEN_ACKPack;
        LCommand comcmd = new LCommand(count, Command.F_RdDevInfo);//

        comcmd.ID_Machine = DStatic.AP_MA8;
        comcmd.Device_ID = pDeviceID;
        byte[] m_Data = new byte[count];
        m_Data = comcmd.getPackage_withoutDataArray();


        byte checkSum = m_Data[0];
        for (int i = 1; i < count; i++)
        {
            if (i != count - 2)
                checkSum ^= m_Data[i];

        }
        m_Data[count - 2] = checkSum;
        qDebug.printBytes("command cmd byte:-- ", m_Data);
        if (TCPClient.getInstance().isConnected())
        {
            TCPClient.getInstance().notifySendCommand(m_Data);
        }

    }

    /// <summary>
    /// change channel name
    /// </summary>
    public static void sendCMD_changeChannelName(byte[] mdata, final int pDeviceID)
    {
        int count = DStatic.LEN_ChangeChannelName+1;
        LCommand comcmd = new LCommand(count, Command.F_ReChName);//1

        comcmd.ID_Machine = DStatic.AP_MA8;
        comcmd.Device_ID = pDeviceID;
        byte[] m_Data = new byte[count];
        m_Data = comcmd.getPackage_withDataArray(mdata);


        byte checkSum = m_Data[0];
        for (int i = 1; i < count; i++)
        {
            if (i != count - 5)
                checkSum ^= m_Data[i];

        }
        m_Data[count - 5] = checkSum;
        qDebug.printBytes("channel command cmd byte:-- ", m_Data);
        if (DStatic.Model&&TCPClient.getInstance().isConnected())
        {
            TCPClient.getInstance().notifySendCommand(m_Data);
        }
        if(!DStatic.Model&&!MainActivity.cuurentIp.isEmpty()){
            UdpLongUtil.getInstance().notifySendThread(m_Data);
        }

    }



    public static void sendCMD_ChangeDeviceName(byte[] mdata,  final int pDeviceID)
    {
        int count = DStatic.LEN_changeDevNamePack;
        LCommand comcmd = new LCommand(count, Command.F_WrDevInfo);//

        comcmd.ID_Machine = DStatic.AP_MA8;
        comcmd.Device_ID = pDeviceID;
        byte[] m_Data = new byte[count];
        m_Data = comcmd.getPackage_withDataArray(mdata);


        byte checkSum = m_Data[0];
        for (int i = 1; i < count; i++)
        {
            if (i != count - 2)
                checkSum ^= m_Data[i];

        }
        m_Data[count - 2] = checkSum;
        qDebug.printBytes("command cmd byte:-- ", m_Data);
        if (TCPClient.getInstance().isConnected())
        {
            TCPClient.getInstance().notifySendCommand(m_Data);
        }

    }
    //---------------------------------------
    /// <summary>
    ///
    /// </summary>
    /// <param name="mcilent"></param>
    /// <param name="apID"></param>
    /// <param name="devID"></param>
    public static void sendCMD_ResetToDefaultSetting( final int pDeviceID)
    {
        int count = DStatic.LEN_ACKPack;
        LCommand comcmd = new LCommand(count, Command.F_ReturnDefaultSetting);//

        comcmd.ID_Machine = DStatic.AP_MA8;
        comcmd.Device_ID = pDeviceID;
        byte[] m_Data = new byte[count];
        m_Data = comcmd.getPackage_withoutDataArray();
        

        byte checkSum = m_Data[0];
        for (int i = 1; i < count; i++)
        {
            if (i != count - 2)
                checkSum ^= m_Data[i];

        }
        m_Data[count - 2] = checkSum;

        qDebug.printBytes("command cmd byte:-- ", m_Data);

        if (TCPClient.getInstance().isConnected())
        {
            TCPClient.getInstance().notifySendCommand(m_Data);
        }

    }

    /// <summary>
    /// reset to default factory
    /// </summary>
    /// <param name="mcilent"></param>
    /// <param name="devp"></param>
    public static void sendCMD_ResetToDefaultFactory( final int pDeviceID)
    {
        int count = DStatic.LEN_ACKPack;
        LCommand comcmd = new LCommand(count, Command.F_ResetToFactorySetting);//

        comcmd.ID_Machine = DStatic.AP_MA8;
        comcmd.Device_ID = pDeviceID;
        byte[] m_Data = new byte[count];
        m_Data = comcmd.getPackage_withoutDataArray();
        

        byte checkSum = m_Data[0];
        for (int i = 1; i < count; i++)
        {
            if (i != count - 2)
                checkSum ^= m_Data[i];

        }
        m_Data[count - 2] = checkSum;
        qDebug.printBytes("command cmd byte:-- ", m_Data);
        if (TCPClient.getInstance().isConnected())
        {
            TCPClient.getInstance().notifySendCommand(m_Data);
        }

    }
    //---------------------------------------
    public static void sendCMD_ReadPresetList( final int pDeviceID)
    {
        int count = DStatic.LEN_ACKPack;
        LCommand comcmd = new LCommand(count, Command.F_GetPresetList);//
        comcmd.ID_Machine = DStatic.AP_MA8;
        comcmd.Device_ID = pDeviceID;
        byte[] m_Data = new byte[count];
        m_Data = comcmd.getPackage_withoutDataArray();
        

        byte checkSum = m_Data[0];
        for (int i = 1; i < count; i++)
        {
            if (i != count - 2)
                checkSum ^= m_Data[i];

        }
        m_Data[count - 2] = checkSum;
        qDebug.printBytes("command cmd byte:-- ", m_Data);
        if (TCPClient.getInstance().isConnected())
        {
            TCPClient.getInstance().notifySendCommand(m_Data);
        }

    }

    /// <summary>
    /// read Deivce info
    /// </summary>
    public static void sendCMD_RecallCurrentScene( final int pDeviceID)
    {
        int count = DStatic.MIN_LEN_PACK+1; //18
        LCommand comcmd = new LCommand(count, Command.F_RecallCurrentScene);//
        comcmd.ID_Machine = DStatic.AP_MA8;
        comcmd.Device_ID = pDeviceID;
        byte[] m_Data = new byte[count];
        m_Data = comcmd.getPackage_withoutDataArray();
        
        byte checkSum = m_Data[0];
        for (int i = 1; i < count; i++)
        {
            if (i != count - 5)
                checkSum ^= m_Data[i];

        }
        m_Data[count - 5] = checkSum;
        qDebug.printBytes("send current sence cmd byte:-- ", m_Data);
        if (DStatic.Model&&TCPClient.getInstance().isConnected())
        {
            TCPClient.getInstance().notifySendCommand(m_Data);
        }
        if(!DStatic.Model&&!MainActivity.cuurentIp.isEmpty()){
            UdpLongUtil.getInstance().notifySendThread(m_Data);
        }

    }
    //---------------------------------------
    public static void sendCMD_RecallSinglePreset(int preindex,  final int pDeviceID)
    {
//        int count = DStatic.LEN_RecallSinglePresetPack;
        int count = 20;
        LCommand comcmd = new LCommand(count, Command.F_RecallSinglePreset);//
        comcmd.ID_Machine = DStatic.AP_MA8;
        comcmd.Device_ID = pDeviceID;
        byte[] m_Data = new byte[count];
        m_Data = comcmd.getPackage_withoutDataArray(preindex);
        m_Data[20-4]=0x0A;
        m_Data[20-3]= (byte) 0xF4;
        m_Data[20-2]= (byte) 0xAA;

        byte checkSum = m_Data[0];
        for (int i = 1; i < count; i++)
        {
            if (i != count - 5)
                checkSum ^= m_Data[i];

        }
        m_Data[count - 5] = checkSum;
        qDebug.printBytes("send recall single preset command  byte:-- ", m_Data);
        if (DStatic.Model&&TCPClient.getInstance().isConnected())
        {
            TCPClient.getInstance().notifySendCommand(m_Data);
        }
        if(!DStatic.Model&&!MainActivity.cuurentIp.isEmpty()){
            UdpLongUtil.getInstance().notifySendThread(m_Data);
        }

    }
    /// <summary>
    /// delte single preset
    /// </summary>
    /// <param name="preNum"></param>
    /// <param name="mcilent"></param>
    /// <param name="apID"></param>
    /// <param name="devID"></param>
    public static void sendCMD_DeleteSinglePreset(int preNum,  final int pDeviceID)
    {

//        int count = DStatic.LEN_DeletePack;
        int count = 20;
        LCommand comcmd = new LCommand(count, Command.F_DeleteSinglePreset);//
        comcmd.ID_Machine = DStatic.AP_MA8;
        comcmd.Device_ID = pDeviceID;
        byte[] m_Data = new byte[count];
        m_Data = comcmd.getPackage_withoutDataArray(preNum+1);
        m_Data[20-4]=0x0A;
        m_Data[20-3]= (byte) 0xF4;
        m_Data[20-2]= (byte) 0xAA;
        byte checkSum = m_Data[0];
        for (int i = 1; i < count; i++)
        {
            if (i != count - 5)
                checkSum ^= m_Data[i];

        }
        m_Data[count - 5] = checkSum;
        qDebug.printBytes("send Delete Single Preset command byte:--  ", m_Data);
        if (DStatic.Model&&TCPClient.getInstance().isConnected())
        {
            TCPClient.getInstance().notifySendCommand(m_Data);
        }
        if(!DStatic.Model&&!MainActivity.cuurentIp.isEmpty()){
            UdpLongUtil.getInstance().notifySendThread(m_Data);
        }

    }
    //
    /// <summary>
    /// send cmd import memory from local
    /// </summary>
    public static void sendCMD_MemoryImportFromPC(byte[] mdata, 
                                                  final int pDeviceID)  //chindex :0..2
    {

        int count = DStatic.Memory_Per_Packeg_len;

        LCommand comcmd = new LCommand(count, Command.F_MemoryImport);

        comcmd.ID_Machine = DStatic.AP_MA8;
        comcmd.Device_ID = pDeviceID;
        byte[] m_Data = new byte[count];
        m_Data = comcmd.getPackage_withDataMemory(mdata);
        

        byte checkSum = m_Data[0];
        for (int i = 1; i < count; i++)
        {
            if (i != count - 2)
                checkSum ^= m_Data[i];
        }
        m_Data[count - 2] = checkSum;
        qDebug.printBytes("send Memory Import From PC command byte:-- ", m_Data);
        if (DStatic.Model&&TCPClient.getInstance().isConnected())
        {
            TCPClient.getInstance().notifySendCommand(m_Data);
        }
        if(!DStatic.Model&&!MainActivity.cuurentIp.isEmpty()){
            UdpLongUtil.getInstance().notifySendThread(m_Data);
        }

    }

    //
    public static void sendCMD_MemoryExportFromDevice(
                                                      final int pDeviceID)  //chindex :0..2
    {

        int count = DStatic.MIN_LEN_PACK; //18
        LCommand comcmd = new LCommand(count, Command.F_MemoryExport);
        comcmd.ID_Machine = DStatic.AP_MA8;
        comcmd.Device_ID = pDeviceID;
        byte[] m_Data = new byte[count];
        m_Data = comcmd.getPackage_withoutDataArray();
        

        byte checkSum = m_Data[0];
        for (int i = 1; i < count; i++)
        {
            if (i != count - 2)
                checkSum ^= m_Data[i];
        }
        m_Data[count - 2] = checkSum;
        qDebug.printBytes("command cmd byte:-- ", m_Data);
        if (TCPClient.getInstance().isConnected())
        {
            TCPClient.getInstance().notifySendCommand(m_Data);
        }

    }

    //

    /// <summary>
    /// save presetname online to save to device
    /// 0:user model,1:factory model
    /// </summary>
    public static void sendCMD_SavePresetWithName(byte[] prestDatas, 
                                                  final int pDeviceID)//
    {
//        int count = DStatic.LEN_StoreNamePresetPack;
        int count = 36;
        LCommand comcmd = new LCommand(count, Command.F_StoreSinglePreset);
        comcmd.ID_Machine = DStatic.AP_MA8;
        comcmd.Device_ID = pDeviceID;
        byte[] m_Data = new byte[count];
        m_Data = comcmd.getPackage_withDataArray(prestDatas);//preset name
        

        byte checkSum = m_Data[0];
        for (int i = 1; i < count; i++)
        {
            if (i != count - 5)
                checkSum ^= m_Data[i];

        }
        m_Data[count - 5] = checkSum;
        qDebug.printBytes("send Save Preset With Name command byte:-- ", m_Data);
        if (DStatic.Model&&TCPClient.getInstance().isConnected())
        {
            TCPClient.getInstance().notifySendCommand(m_Data);
        }
        if(!DStatic.Model&&!MainActivity.cuurentIp.isEmpty()){
            UdpLongUtil.getInstance().notifySendThread(m_Data);
        }


    }

    /// <summary>
    /// load local preset to device
    ///
    /// </summary>
    public static void sendCMD_loadFromLocalToDevice(byte[] localDatas, 
                                                     final int pDeviceID)//
    {
        int count = DStatic.LEN_Sence;
        LCommand comcmd = new LCommand(count, Command.F_LoadFromPC);
        comcmd.ID_Machine = DStatic.AP_MA8;
        comcmd.Device_ID = pDeviceID;
        byte[] m_Data = new byte[count];
        m_Data = comcmd.getPackage_withDataArray(localDatas);//preset name
        

        byte checkSum = m_Data[0];
        for (int i = 1; i < count; i++)
        {
            if (i != count - 2)
                checkSum ^= m_Data[i];

        }
        m_Data[count - 2] = checkSum;
        qDebug.printBytes("send load From Local To Device command byte:-- ", m_Data);
        if (DStatic.Model&&TCPClient.getInstance().isConnected())
        {
            TCPClient.getInstance().notifySendCommand(m_Data);
        }
        if(!DStatic.Model&&!MainActivity.cuurentIp.isEmpty()){
            UdpLongUtil.getInstance().notifySendThread(m_Data);
        }


    }
    public static void sendCMD_MatrixMixer(byte[] mData,  final int pDeviceID)
    {

//        int count = DStatic.LEN_MatrixPack;
        int count = 59;
        LCommand comcmd = new LCommand(count, Command.F_MatrixMixer);//

        comcmd.ID_Machine = DStatic.AP_MA8;
        comcmd.Device_ID = pDeviceID;
        byte[] m_Data = new byte[count];
        m_Data = comcmd.getPackage_withDataArray(mData);
        
        byte checkSum = m_Data[0];
        for (int i = 1; i < count; i++)
        {
            if (i != count - 5)
                checkSum ^= m_Data[i];

        }
        m_Data[count - 5] = checkSum;
        qDebug.printBytes("send MatrixMixer command byte:-- ", m_Data);
        if (DStatic.Model&&TCPClient.getInstance().isConnected())
        {
            TCPClient.getInstance().notifySendCommand(m_Data);
        }
        if(!DStatic.Model&&!MainActivity.cuurentIp.isEmpty()){
            UdpLongUtil.getInstance().notifySendThread(m_Data);
        }

    }

    ///
    /// <summary>
    /// sendcmd for phaseGain,input or output
    /// </summary>
    /// <param name="mcilent"></param>
    public static void sendCMD_FaderGain(byte[] mData, int flag,  final int pDeviceID)
    {
//        int count = DStatic.LEN_ComPack;
        int count = 31;
        LCommand comcmd = null;
        if (flag == 0)
            comcmd = new LCommand(count, Command.F_InpuGain);//sensitivity define
        else if (flag == 1)
            comcmd = new LCommand(count, Command.F_OutputGain);//sensitivity define

        comcmd.ID_Machine = DStatic.AP_MA8;
        comcmd.Device_ID = pDeviceID;
        byte[] m_Data = new byte[count];
        m_Data = comcmd.getPackage_withDataArray(mData);
        
        byte checkSum = m_Data[0];
        for (int i = 1; i < count; i++)
        {
            if (i != count - 2)
                checkSum ^= m_Data[i];

        }
        m_Data[count - 2] = checkSum;

        qDebug.printBytes("send fadergain cmd byte:-- ", m_Data);
        if (DStatic.Model&&TCPClient.getInstance().isConnected())
        {
            TCPClient.getInstance().notifySendCommand(m_Data);
        }
        if(!DStatic.Model&&!MainActivity.cuurentIp.isEmpty()){
            UdpLongUtil.getInstance().notifySendThread(m_Data);
        }

    }
    /// <summary>
    /// sendcmd for phaseGain,input or output
    /// </summary>
    /// <param name="mcilent"></param>
    public static void sendCMD_ChanelMute(byte[] mData,  final int pDeviceID)
    {
//        int count = DStatic.LEN_ComPack;
        int count = 31;

        LCommand comcmd = new LCommand(count, Command.F_Mute);//channel mute

        comcmd.ID_Machine = DStatic.AP_MA8;
        comcmd.Device_ID = pDeviceID;
        byte[] m_Data = new byte[count];
        m_Data = comcmd.getPackage_withDataArray(mData);
        
        byte checkSum = m_Data[0];
        for (int i = 1; i < count; i++)
        {
            if (i != count - 5)
                checkSum ^= m_Data[i];

        }
        m_Data[count - 5] = checkSum;

        qDebug.printBytes("send Chanel Mute command byte:-- ", m_Data);

        if (DStatic.Model&&TCPClient.getInstance().isConnected())
        {
            TCPClient.getInstance().notifySendCommand(m_Data);
        }
        if(!DStatic.Model&&!MainActivity.cuurentIp.isEmpty()){
            UdpLongUtil.getInstance().notifySendThread(m_Data);
        }

    }
    //overwrite password
    public static void sendCMD_RewritePassword(byte[] password,
                                                  int flag)//
    {
        int count = DStatic.Len_RewritePass+1;
        LCommand comcmd;
        if(flag==0){
            comcmd = new LCommand(count, Command.F_WriteLockPWD);
        }
        else {
            comcmd = new LCommand(count, 138);
        }

        comcmd.ID_Machine = DStatic.AP_MA8;
        comcmd.Device_ID = IpManager.getInstance().getSelDevID();
        byte[] m_Data = new byte[5];
        m_Data[0]=password[0];
        m_Data[1]=password[1];
        m_Data[2]=password[2];
        m_Data[3]=password[3];
        m_Data[4]= (byte) flag;
        m_Data = comcmd.getPackage_withDataArray(m_Data);//password


        byte checkSum = m_Data[0];
        for (int i = 1; i < count; i++)
        {
            if (i != count - 5)
                checkSum ^= m_Data[i];

        }
        m_Data[count - 5] = checkSum;
        qDebug.printBytes("command passwordcmd byte:-- ", m_Data);
        if (DStatic.Model&&TCPClient.getInstance().isConnected())
        {
            TCPClient.getInstance().notifySendCommand(m_Data);
        }
        if(!DStatic.Model&&!MainActivity.cuurentIp.isEmpty()){
            UdpLongUtil.getInstance().notifySendThread(m_Data);
        }


    }
    /*****************************************************************
     *
     *@author williamXia
     *created at 2018/4/27 16:59
     *
    ******************************************************************/
    public  void  sendCMD_ChangeChannelName()
    {



    }

    //Msg_Transfor main activity
    public static void sendBroadCastMsg_NoteMainActivity(Context context, Serializable serObj) {
        Xover.sendBroadCastMsg(context, DStatic.Action_MainActivity, DStatic.Msg_Key_MainActivity, serObj);

    }


    //Msg_Transfor dsp channel
    public static void sendBroadCastMsg_NoteIPChanelFrag(Context context, Serializable serObj) {
        Xover.sendBroadCastMsg(context, DStatic.Action_IPConfigFrag, DStatic.Msg_Key_IpConfig, serObj);
    }
    //Msg_Transfor dsp channel
    public static void sendBroadCastMsg_NoteChanelFrag(Context context, Serializable serObj) {
        Xover.sendBroadCastMsg(context, DStatic.Action_DspChanelFrag, DStatic.Msg_Key_DspChanel, serObj);
    }

    //Msg_Transfor  matrix
    public static void sendBroadCastMsg_NoteMatrixFrag(Context context, Serializable serObj) {
        Xover.sendBroadCastMsg(context, DStatic.Action_MatrixFrag, DStatic.Msg_Key_Matrix, serObj);
    }

    //Msg_Transfor Presets
    public static void sendBroadCastMsg_NotePresetsFrag(Context context, Serializable serObj) {

        Xover.sendBroadCastMsg(context, DStatic.Action_Preset, DStatic.Msg_Key_Preset, serObj);
    }


    /*
    //Msg_FUpdate_Progres
    public static void sendBroadCastMsg_forFirmwareUpdate(Context context, Serializable serObj) {
        Xover.sendBroadCastMsg(context, DStatic.Action_FirmwarUPdate, DStatic.Msg_FirmwarUpdate, serObj);
    }
    */

}
