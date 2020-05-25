package Datas;

import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.com.williamxia.wipack.utils.DevInfo;
import cn.com.williamxia.wipack.utils.IpManager;
import cn.com.williamxia.wipack.utils.LConstaint;
import cn.com.williamxia.wipack.utils.WiStaticComm;
import cn.com.williamxia.wipack.utils.Xover;
import cn.com.williamxia.wipack.utils.qDebug;

import static Datas.DStatic.FBC_FFTNum;
import static Datas.DStatic.IO_MaxMatrixBus;
import static Datas.DStatic.Len_DevName;
import static Datas.DStatic.e_dsp;
import static cn.com.williamxia.wipack.utils.WiStaticComm.BZero;
import static cn.com.williamxia.wipack.utils.WiStaticComm.HByte;
import static cn.com.williamxia.wipack.utils.WiStaticComm.LByte;
import static cn.com.williamxia.wipack.utils.WiStaticComm.LEN_1M;
import static cn.com.williamxia.wipack.utils.WiStaticComm.SHIFT;
import static cn.com.williamxia.wipack.utils.Xover.stringToByte;

/**
 * Created by williamXia on 2017/9/6.
 */

//(data[count++] & LConstaint.CTByte)

public class XData {
    public static final int Num_Firm = 7;
    private static XData gData = null;
    public DevInfo mDeviceInf;

    public int mRoutChanel = 0;

    public XData() {
        if (m_segOfFirmware == null)
            m_segOfFirmware = new int[Num_Firm];
        mDeviceInf = new DevInfo();
        if (m_updateEnd == null) m_updateEnd = new boolean[Num_Firm];
        mRoutChanel = 0;
        initData();
        initialMatrix();
    }

    public static XData gInstance() {
        if (gData == null) {
            gData = new XData();
        }
        return gData;
    }


    public byte[] m_DMcu;//mcu 1024*1024
    public byte[] m_DMcu_1763;//key 1024*1024
    public byte[] m_DDsp;//dsp1 data 1024*1024
    public byte[] m_DRpm;//dsp2 data1 024*1024   //

    public byte[] m_DRio;
    public byte[] m_DRva;
    public byte[] m_DRvc;
    public byte[][] m_matrixAry;
    public int[][] m_matrixAryGain;

    public static final byte Con_Star = 100;//default is paral mode
    public static final byte Con_Daisy = 101;

    public byte m_conDevMode;//serail or paral modes

    public CHEdit[] m_chEdit;

    public int[] m_segOfFirmware; //mcu,dsp,1763,rpm,rio,rva,rvc
    public boolean isNeedLoop = true;
    public int iChindex = 0;
    public int oChindex = 0;

    public byte[] lock_pass = new byte[4];


    public static final int Max_Presets = 25;
    public static final int Len_PresetName = 20;
    public static final int Len_factName = 16;
    public byte[][] m_presetName;
    public byte[][] m_chanelName;//24*20

    public byte[] m_DeviceName = new byte[Len_PresetName];


    public int backDeviID;

    public byte[] m_Relay = new byte[2];
    public byte m_pageZone = 0;
    public byte m_MCUVer = 0;

    public int mFirmProgress = 0;
    public byte lockFlag = 0;

    //region
    public int[] m_duckerParameters = new int[DStatic.Max_DuckerParams];

    public byte[] m_DuckerSourch = new byte[IO_MaxMatrixBus];
    public byte[] m_DuckerBgm = new byte[IO_MaxMatrixBus];

    public static final int FBCParamLen = 6;
    public byte[] m_FbcParam = new byte[FBCParamLen];
    public EQEdit[] m_FbcEQData = new EQEdit[FBC_FFTNum];

    public CAutoMixerParam autoMixerParam = new CAutoMixerParam();

    public byte[] m_autoMixerCHSelect = new byte[DStatic.ChanlMax * 2];
    public byte[] pDevSNONumber = new byte[5];

    //endregion


    private void initData() {

        m_conDevMode = Con_Star;//

        if (lock_pass == null) {
            lock_pass = new byte[4];
            for (int i = 0; i < 4; i++) {
                lock_pass[i] = 0;
            }
        }
        if (m_FbcParam == null)
            m_FbcParam = new byte[FBCParamLen];
        if (m_chEdit == null)
            m_chEdit = new CHEdit[DStatic.ChanlMax * 2];
        for (int i = 0; i < DStatic.ChanlMax * 2; i++) {
            m_chEdit[i] = new CHEdit();

        }
        if (pDevSNONumber == null)
            pDevSNONumber = new byte[5];

        if (m_autoMixerCHSelect == null)
            m_autoMixerCHSelect = new byte[DStatic.ChanlMax * 2];
        if (autoMixerParam == null)

            autoMixerParam = new CAutoMixerParam();

        if (m_FbcEQData == null)
            m_FbcEQData = new EQEdit[FBC_FFTNum];
        for (int i = 0; i < FBC_FFTNum; i++) {
            m_FbcEQData[i] = new EQEdit();

        }
        if (autoMixerParam == null)
            autoMixerParam = new CAutoMixerParam();

        m_presetName = new byte[Max_Presets][];
        for (int i = 0; i < Max_Presets; i++) {
            m_presetName[i] = new byte[Len_PresetName];
        }
        //-----
        m_chanelName = new byte[DStatic.ChanlMax * 2][];
        for (int i = 0; i < DStatic.ChanlMax * 2; i++) {
            m_chanelName[i] = new byte[Len_PresetName];
            setChanelName(i, str_GChName[i]);
        }

        _dspPageIndex = 0;
        initDataMatrix();

    }

    final static String str_GChName[] = {
            "CH01", "CH02", "CH03", "CH04", "CH05", "CH06", "CH07", "CH08", "CH09", "CH10", "CH11", "CH12",
            "CH01", "CH02", "CH03", "CH04", "CH05", "CH06", "CH07", "CH08", "CH09", "CH10", "CH11", "CH12",

    };

    /*****************************************************************
     * @author williamXia
     * created at 2018/4/27 16:48
     * chindex:0..23
     ******************************************************************/

    public void setChanelName(int iChindex, String strName) {
        int chindex = 0;
        if (iChindex < 0) chindex = 0;
        else if (iChindex > DStatic.ChanlMax * 2 - 1) chindex = DStatic.ChanlMax * 2 - 1;
        else chindex = iChindex;

        byte[] tmpAry = Xover.stringToByte(strName, 16);

        Arrays.fill(m_chanelName[chindex], LConstaint.ZEROByte);
        System.arraycopy(tmpAry, 0, m_chanelName[chindex], 0, tmpAry.length);

    }

    public String getChanelName(int chindex) {
        byte[] mtmpAry = new byte[16];
        System.arraycopy(m_chanelName[chindex], 0, mtmpAry, 0, 16);
        return Xover.byteToString(mtmpAry, 16);

    }

    public String getShowNameInput(int chindex) {
        String tmpStr = "";
        if (chindex < 12)
            tmpStr = String.format("Input%02d-%s", chindex + 1,getChanelName(chindex));
        else if (chindex >= 12 && chindex < 20)
            tmpStr = String.format("Net%02d", chindex - 11);
        return tmpStr;
    }

    public String getShowNameOut(int chindex) { //chindex is 0....1
        String tmpStr = "";
        if (chindex < 12)
            tmpStr = String.format("Output%02d-%s", chindex + 1, getChanelName(chindex + 12));
        else if (chindex >= 12 && chindex < 20)
            tmpStr = String.format("Net%02d", chindex - 11);
        return tmpStr;
    }
    //-------------------------------------

    public byte iMute(int iChindex) {
        return m_chEdit[iChindex].chMute;
    }

    public byte oMute(int oChindex) {
        return m_chEdit[oChindex + DStatic.ChanlMax].chMute;

    }

    public byte[] gPackageofLoad() {
        final int offset = 11;   //total:3597-->3649

        byte[] data = new byte[DStatic.LEN_Sence - 16];//?11+5+2=18

        int count = 0;
        for (int i = 0; i < Len_PresetName; i++) {
            data[count++] = m_presetName[0][i];
        }

        count = 31 - offset;
        int ch = 0;
        //region input channel Data receive
        byte fType = 0;
        // int tmpfreq = 0;

        for (ch = 0; ch < DStatic.ChanlMax; ch++) {

            data[count++] = m_chEdit[ch].invert;
            data[count++] = (byte) m_chEdit[ch].chGain;
            //begin input gateExp
            data[count++] = m_chEdit[ch].gateExpEdit.limit_threshold;

            //attack
            data[count++] = (byte) (m_chEdit[ch].gateExpEdit.limit_attack / 100);
            data[count++] = (byte) (m_chEdit[ch].gateExpEdit.limit_attack % 100);

            //release
            data[count++] = (byte) (m_chEdit[ch].gateExpEdit.limit_release / 100);
            data[count++] = (byte) (m_chEdit[ch].gateExpEdit.limit_release % 100);


            data[count++] = m_chEdit[ch].gateExpEdit.limit_ratio;
            data[count++] = m_chEdit[ch].gateExpEdit.limit_bypass;//expgate Power OFF/ON
            //delay
            data[count++] = m_chEdit[ch].delayPower;

            data[count++] = (byte) (m_chEdit[ch].delayTime / 256);
            data[count++] = (byte) (m_chEdit[ch].delayTime % 256); //delay time
            data[count++] = m_chEdit[ch].eqAllBypass;// eq all bypass

            for (int eqindex = 8; eqindex < DStatic.CEQ_MAX; eqindex++) //firt save hlpf filter pass
            {
                fType = m_chEdit[ch].m_eqEdit[eqindex].eq_filterindex;//eqtype
                if (fType >= 1 && fType < 21)
                    fType += 3;
                data[count++] = fType;
                data[count++] = (byte) m_chEdit[ch].m_eqEdit[eqindex].eq_gaindex;
                data[count++] = m_chEdit[ch].m_eqEdit[eqindex].eq_qfactor;
                data[count++] = (byte) (m_chEdit[ch].m_eqEdit[eqindex].eq_freqindex / 100);
                data[count++] = (byte) (m_chEdit[ch].m_eqEdit[eqindex].eq_freqindex % 100);
                data[count++] = m_chEdit[ch].m_eqEdit[eqindex].eq_bypass;

            }

            for (int eqindex = 0; eqindex < DStatic.CEQ_MAX - 2; eqindex++) //normal eq
            {
                fType = m_chEdit[ch].m_eqEdit[eqindex].eq_filterindex;//eqtype                    
                fType += 1;
                data[count++] = fType;
                data[count++] = (byte) m_chEdit[ch].m_eqEdit[eqindex].eq_gaindex;
                data[count++] = m_chEdit[ch].m_eqEdit[eqindex].eq_qfactor;
                data[count++] = (byte) (m_chEdit[ch].m_eqEdit[eqindex].eq_freqindex / 100);
                data[count++] = (byte) (m_chEdit[ch].m_eqEdit[eqindex].eq_freqindex % 100);
                data[count++] = m_chEdit[ch].m_eqEdit[eqindex].eq_bypass;
            }
            //outpu channel has dynlimit data

            data[count++] = m_chEdit[ch].dynLimit.limit_threshold;

            data[count++] = (byte) (m_chEdit[ch].dynLimit.limit_attack / 100);
            data[count++] = (byte) (m_chEdit[ch].dynLimit.limit_attack % 100);

            data[count++] = (byte) (m_chEdit[ch].dynLimit.limit_release / 100);
            data[count++] = (byte) (m_chEdit[ch].dynLimit.limit_release % 100);

            data[count++] = m_chEdit[ch].dynLimit.limit_ratio;
            data[count++] = m_chEdit[ch].dynLimit.limit_gain;
            data[count++] = m_chEdit[ch].dynLimit.limit_bypass;//limit poweroff

            ///
            data[count++] = m_chEdit[ch].chMute;
            data[count++] = m_chEdit[ch].sensitivityindex;
            data[count++] = m_chEdit[ch].dc48vFlag;
            //channel name 
            for (int i = 0; i < Len_PresetName; i++) {
                data[count++] = m_chanelName[ch][i];
            }

        }
        //endregion

        //region output channel Data receive

        // Debug.WriteLine("get current sence pacakge count value is :   {0}", count);

        count = 1471 - offset;

        for (ch = 12; ch < DStatic.ChanlMax * 2; ch++) {
            data[count++] = m_chEdit[ch].invert;
            data[count++] = (byte) m_chEdit[ch].chGain;
            //delay
            data[count++] = m_chEdit[ch].delayPower;
            data[count++] = (byte) (m_chEdit[ch].delayTime / 256);
            data[count++] = (byte) (m_chEdit[ch].delayTime % 256); //delay time

            data[count++] = m_chEdit[ch].eqAllBypass;// eq all bypass
            //EQ below              

            for (int eqindex = 8; eqindex < DStatic.CEQ_MAX; eqindex++) {
                fType = m_chEdit[ch].m_eqEdit[eqindex].eq_filterindex;//eqtype
                if (fType >= 1 && fType < 21)
                    fType += 3;
                data[count++] = fType;
                data[count++] = (byte) m_chEdit[ch].m_eqEdit[eqindex].eq_gaindex;
                data[count++] = m_chEdit[ch].m_eqEdit[eqindex].eq_qfactor;
                data[count++] = (byte) (m_chEdit[ch].m_eqEdit[eqindex].eq_freqindex / 100);
                data[count++] = (byte) (m_chEdit[ch].m_eqEdit[eqindex].eq_freqindex % 100);
                data[count++] = m_chEdit[ch].m_eqEdit[eqindex].eq_bypass;

            }

            for (int eqindex = 0; eqindex < DStatic.CEQ_MAX - 2; eqindex++) //normal eq
            {
                fType = m_chEdit[ch].m_eqEdit[eqindex].eq_filterindex;//eqtype
                fType += 1;
                data[count++] = fType;
                data[count++] = (byte) m_chEdit[ch].m_eqEdit[eqindex].eq_gaindex;
                data[count++] = m_chEdit[ch].m_eqEdit[eqindex].eq_qfactor;
                data[count++] = (byte) (m_chEdit[ch].m_eqEdit[eqindex].eq_freqindex / 100);
                data[count++] = (byte) (m_chEdit[ch].m_eqEdit[eqindex].eq_freqindex % 100);
                data[count++] = m_chEdit[ch].m_eqEdit[eqindex].eq_bypass;
            }

            data[count++] = m_chEdit[ch].dynLimit.limit_threshold;

            data[count++] = (byte) (m_chEdit[ch].dynLimit.limit_attack / 100);
            data[count++] = (byte) (m_chEdit[ch].dynLimit.limit_attack % 100);
            //
            data[count++] = (byte) (m_chEdit[ch].dynLimit.limit_release / 100);
            data[count++] = (byte) (m_chEdit[ch].dynLimit.limit_release % 100);

            data[count++] = m_chEdit[ch].dynLimit.limit_ratio;
            data[count++] = m_chEdit[ch].dynLimit.limit_gain;
            data[count++] = m_chEdit[ch].dynLimit.limit_bypass;//limit poweroff

            data[count++] = m_chEdit[ch].chMute;

            //channel name 
            for (int i = 0; i < Len_PresetName; i++) {
                data[count++] = m_chanelName[ch][i];
            }
        }
        //endregion over define output channel

        //region begin ...other pages....
        count = 2911 - offset;
        // StringBuilder strbd = new StringBuilder();
        byte ftemp = 0;

        for (int matrixIndex = 0; matrixIndex < DStatic.Matrix_CHNum; matrixIndex++) {
            for (int f = 0; f < 22; f++) {
                data[count++]= (byte) m_matrixAryGain[matrixIndex][f];
            }

        }
        for (int matrixIndex = 0; matrixIndex < DStatic.Matrix_CHNum-2; matrixIndex++) {
            int temp = 0;
            for (int i = 0; i <20; i++) {
                temp+= m_matrixAry[matrixIndex][i]<<i;
            }
            int a=(temp>>16);
            int b=((temp>>8)&0xff);
            int c=(temp&0xff);

            data[count++]= (byte) (temp>>16);
            data[count++]= (byte) ((temp>>8)&0xff);
            data[count++]= (byte) (temp&0xff);
        }
//        count = 3423 - offset;
//
//        for (int duck = 0; duck < DStatic.IO_MaxMatrixBus; duck++) {
//
//            byte tmpH = (byte) ((m_DuckerSourch[duck] & LByte) << 4);
//            byte tmpL = (byte) (m_DuckerBgm[duck] & LByte);
//            data[count++] = (byte) (tmpH | tmpL);
//
//        }
//
//        //duck inputmixer
//        data[count++] = (byte) m_duckerParameters[0];//duckerthreshold
//        data[count++] = (byte) (m_duckerParameters[2] / 100);//attack attack high
//        data[count++] = (byte) (m_duckerParameters[2] % 100);//attack low
//
//        data[count++] = (byte) (m_duckerParameters[4] / 100);//release high
//        data[count++] = (byte) (m_duckerParameters[4] % 100);//release low
//
//        data[count++] = (byte) (m_duckerParameters[2] / 100);//attack hold
//        data[count++] = (byte) (m_duckerParameters[2] % 100);//attack hold
//
//        data[count++] = (byte) m_duckerParameters[1];//depth
//        data[count++] = (byte) m_duckerParameters[5];//duck power on
        // FBC parameter,len=122
        count = 3456 - offset;
        data[count++] = m_FbcParam[0];//fbc bypas
        data[count++] = m_FbcParam[3];//fbc qvalue
        data[count++] = m_FbcParam[4];//fbc_modeflag //add two byte 
        data[count++] = m_FbcParam[5];//fbc_filterReleaseTime
        //-------------

        for (int k = 0; k < DStatic.IO_MaxMatrixBus; k++) //fbc status  IO_MaxMatrixBus<-->24
        {
            data[count++] = m_FbcFilterStatus[k];
        }
        //
        for (int k = 0; k < DStatic.IO_MaxMatrixBus; k++) {
            data[count++] = (byte) (m_FbcEQData[k].eq_freqindex / 100);
            data[count++] = (byte) (m_FbcEQData[k].eq_freqindex % 100);
        }

        //
        for (int k = 0; k < DStatic.IO_MaxMatrixBus; k++) {
            data[count++] = (byte) (m_FbcEQData[k].eq_gaindex / 100);
            data[count++] = (byte) (m_FbcEQData[k].eq_gaindex % 100);
        }
        //
        /////Relay status:
        count = 3580 - offset;
        data[count++] = (byte) (autoMixerParam.autoAttack / 100);
        data[count++] = (byte) (autoMixerParam.autoAttack % 100);
        //
        data[count++] = (byte) (autoMixerParam.autoRelease / 100);
        data[count++] = (byte) (autoMixerParam.autoRelease % 100);

        data[count++] = autoMixerParam.autoHavgTau;
        data[count++] = autoMixerParam.autoPower;

        for (int f = 0; f < 24; f++) {
            data[count++] = m_autoMixerCHSelect[f];
        }
        count = 3610 - offset;

        for (int k = 0; k < 2; k++) {
            data[count++] = m_Relay[k];
        }

        //zone status
        count = 3612 - offset;
        data[count++] = m_pageZone;
        //

        count=3620-offset;
        for (int i=0;i<8;i++)


        count = 3628 - offset;
        for (int k = 0; k < Len_DevName; k++) {
            data[count++] = m_DeviceName[k];
        }

        //device firmare version
        count = 3648 - offset;
        data[count++] = m_MCUVer;
        data[count++] = m_FbcParam[2];
        //endregion
        //remainder:checksum and endbyte       
        count++;
        count++;
        for (int f = 0; f < 4; f++) {
            data[count++] = lock_pass[f];
        }
        data[count++] = lockFlag;


        return data;
    }

    public int _dspPageIndex;

    public int iGain() {
        return m_chEdit[iChindex].chGain;
    }

    public int iGain(int channel) {
        return m_chEdit[channel].chGain;
    }


    public String strIGain() {
        return strFGain(iGain());
    }

    public String strIGain(int channel) {
        return strFGain(iGain(channel));
    }

    //---------------------------------

    public String strIchSel() {

        String string = "CH" + (new DecimalFormat("00").format(iChindex + 1));
        return string;
    }

    public String strOchSel() {
        String string = "CH" + (new DecimalFormat("00").format(oChindex + 1));
        return string;
    }

    public static String strFGain(int mgain) {
        double gain = (0.5 * mgain - 80);
        //String str = new DecimalFormat("#.0dB").format(gain);
        String str = gain + "dB";
        //qDebug.qLog("gian======="+gain+"string====="+str);
        return str;

    }

    public String iRead_deviceInfo(byte[] data) {
        if (data == null || data.length < DStatic.PKLen_ReadDevInf + 7) return "";
        int count = 15;

        byte[] tmp = new byte[DStatic.Len_DevName];
        for (int i = 0; i < DStatic.Len_DevName; i++) {
            tmp[i] = data[count++];
        }
        return Xover.byteToString(tmp, DStatic.Len_DevName);

    }


    //region receive channel mute gain matrix  below..
    public void iRead_chanelMute(byte[] data) {
        if (data == null || data.length < DStatic.PKLen_ACK) return;
        int count = 11;
        byte tmp = 0;
        for (int i = 0; i < DStatic.ChanlMax; i++) {
            tmp = data[count++];
            m_chEdit[i].chMute = (byte) ((tmp & HByte) >> WiStaticComm.SHIFT);
            m_chEdit[i + DStatic.ChanlMax].chMute = (byte) (tmp & LByte);
        }
    }

    public void iRead_chanelGain(byte[] data) {
        if (data == null || data.length < DStatic.PKLen_ACK) return;

        int cmdindex = 9;
        int cmdType = Xover.combineHLByte(data[cmdindex++], data[cmdindex++], 1);
        int bindex = 0;
        int eMax = DStatic.ChanlMax;

        if (cmdType == Command.F_OutputGain) {
            bindex = DStatic.ChanlMax;
            eMax = DStatic.ChanlMax * 2;
        }

        for (int ch = bindex; ch < eMax; ch++) {
            m_chEdit[ch].chGain = bytetoint(data[cmdindex++]);
        }

    }

    public int bytetoint(byte tb) {
        int temp;
        temp = Integer.valueOf(tb);
        if (temp < 0) {
            temp = temp & 0x7F + 128;
        }

        return temp;
    }

    public void sendCMD_FaderGain(int chindex, final int devID) { //0..23

        byte[] m_data = new byte[DStatic.ChanlMax];
        int t = (chindex < DStatic.ChanlMax ? 0 : DStatic.ChanlMax);
        for (int i = t; i < t + DStatic.ChanlMax; i++) {
            m_data[i - t] = (byte) (m_chEdit[i].chGain);
        }
        CmdSender.sendCMD_FaderGain(m_data, (chindex < DStatic.ChanlMax ? 0 : 1), devID);

    }

    public void sendCMD_ChanlMute(final int devID) { //0..23

        byte[] m_data = new byte[DStatic.ChanlMax];
        for (int i = 0; i < DStatic.ChanlMax; i++) {
            byte tmpH = (byte) ((m_chEdit[i].chMute & WiStaticComm.LByte) << SHIFT);
            byte tmpL = (byte) (m_chEdit[i + DStatic.ChanlMax].chMute & WiStaticComm.LByte);
            m_data[i] = (byte) (tmpH | tmpL);
        }
        CmdSender.sendCMD_ChanelMute(m_data, devID);
    }

    /*****************************************************************
     * @author williamXia
     * created at 2018/4/28 10:50
     * iread channel name
     ******************************************************************/
    public void iRead_chanelName(byte[] data) {
        int count = 11;
        int chindex = data[count++] - 1;
        int flag = data[count++];
        if (flag == 1) {
            chindex += DStatic.ChanlMax;
        }
        for (int b = 0; b < Len_PresetName; b++) {
            m_chanelName[chindex][b] = data[count++];
        }


    }

    public List<matrixBean> m_beanList;

    private void initDataMatrix() {
        if (m_beanList == null)
            m_beanList = new ArrayList<matrixBean>();
        for (int i = 0; i < 20; i++) {
           m_beanList.add(new matrixBean(WiStaticComm.BZero, (i + 1), getShowNameInput(i)));

        }

    }

    public void iRead_matrixSingleChanel(byte[] data) { //one row as a channel
//        qDebug.printBytes("iRead_matrixSingleChanel",data);
        int Chindex=data[11]-1;
        for (int ch = 0; ch < 20; ch++) {
            m_matrixAryGain[Chindex][ch]=data[12+ch]&0xff;
            m_matrixAry[Chindex][ch]=data[34+ch];
        }
    }

    public void sendCMD_matrixWithChanel(final int sChindex, final int mDevid) { //one row as a channel
        byte[] mData = new byte[43];
        mData[0] = (byte) (sChindex + 1);
        for (int ch = 0; ch < 20; ch++) {
//            byte tmpH = (byte) ((m_matrixAry[sChindex][2 * ch + 1] & WiStaticComm.LByte) << SHIFT);
//            byte tmpL = (byte) ((m_matrixAry[sChindex][2 * ch] & WiStaticComm.LByte));
            mData[ch + 1] = (byte) m_matrixAryGain[sChindex][ch];
        }
        mData[21]=0x00;
        mData[22]=0x00;
        for (int ch = 0; ch < 20; ch++) {
            mData[ch + 23] = m_matrixAry[sChindex][ch];
        }
        CmdSender.sendCMD_MatrixMixer(mData, mDevid);
    }
    public void sendCMD_matrixWithChanel(final int sChindex, final int x,final int Gain,final int mDevid) { //one row as a channel
        byte[] mData = new byte[43];
        mData[0] = (byte) (sChindex + 1);
        for (int ch = 0; ch < 20; ch++) {
//            byte tmpH = (byte) ((m_matrixAry[sChindex][2 * ch + 1] & WiStaticComm.LByte) << SHIFT);
//            byte tmpL = (byte) ((m_matrixAry[sChindex][2 * ch] & WiStaticComm.LByte));
            mData[ch + 1] = (byte) m_matrixAryGain[sChindex][ch];
        }
        mData[x + 1] = (byte) Gain;
        mData[21]=0x00;
        mData[22]=0x00;
        for (int ch = 0; ch < 20; ch++) {
            mData[ch + 23] = m_matrixAry[sChindex][ch];
        }
        CmdSender.sendCMD_MatrixMixer(mData, mDevid);
    }

    public boolean isUserPresetEmpty_inThePosition(int index) {
        return (m_presetName[index][17] == 0xaa &&
                m_presetName[index][18] == 0x55 &&
                m_presetName[index][19] == 0xaa);
    }

    public String nameOfDevice() {

        return Xover.byteToString(m_DeviceName, Len_DevName);

    }

    public void setUserPresetName(String strName, int index) {
        byte[] m_name = stringToByte(strName, Len_PresetName - 4);
        Arrays.fill(m_presetName[index], 0, Len_PresetName, WiStaticComm.BZero);
        System.arraycopy(m_name, 0, m_presetName[index], 0, m_name.length);
    }

    public String nameUserPreset(int index) {
        String strUPName = Xover.byteToString(m_presetName[index], Len_PresetName - 4);
        String strRes = new DecimalFormat(" 00. ").format(index).concat(strUPName);
        return strRes;
    }

    public void iRead_allPresetsList(byte[] data) {
        int count = 11;
        for (int presetNum = 0; presetNum < Max_Presets; presetNum++) {
            for (int i = 0; i < Len_PresetName; i++) {
                m_presetName[presetNum][i] = data[count++];
            }
        }


    }

    public byte[] m_meoryRead = new byte[DStatic.Memory_Max_Package * DStatic.Memory_Per_Packeg_len];

    public void pushMemoryData_toExport(final byte[] mdata) {
        int seindex = mdata[11];
        if (mdata.length == DStatic.Memory_Per_Packeg_len && seindex < DStatic.Memory_Max_Package) {
            System.arraycopy(mdata, 0, m_meoryRead, seindex * DStatic.Memory_Per_Packeg_len, DStatic.Memory_Per_Packeg_len);
        }
    }

    public void sendCMD_LoadPresteFlile_fromLocal(final int preindex) {
        // CmdSender.sendCMD_loadFromLocalToDevice();
        byte[] tmp = new byte[DStatic.Memory_Per_Packeg_len];
        System.arraycopy(m_meoryRead, preindex * DStatic.Memory_Per_Packeg_len, tmp, 0, DStatic.Memory_Per_Packeg_len);

        CmdSender.sendCMD_MemoryImportFromPC(tmp, IpManager.getInstance().getSelDevID());

    }


    public void sendCMD_SavePresetWithName(final int preindex, final int pDevid) {
        byte[] m_data = new byte[17];
        m_data[0] = (byte) (preindex + 1);
        System.arraycopy(m_presetName[preindex], 0, m_data, 1, Len_factName);
        Log.d("presetname :", String.valueOf(m_presetName[preindex]));
        CmdSender.sendCMD_SavePresetWithName(m_data, pDevid);
    }

    /*****************************************************************
     * @author williamXia
     * created at 2018/4/28 10:15
     * chindex:[0..23]
     ******************************************************************/
    public void sendCMD_SaveChannelName(final int chindex) {
        int fchindex = (chindex >= DStatic.ChanlMax ? chindex - DStatic.ChanlMax : chindex);
        int flag = (chindex >= DStatic.ChanlMax ? 1 : 0);
        byte[] m_data = new byte[22];
        //
        m_data[0] = (byte) (fchindex + 1);
        m_data[1] = (byte) flag;
        System.arraycopy(m_chanelName[chindex], 0, m_data, 2, Len_PresetName);
        CmdSender.sendCMD_changeChannelName(m_data, IpManager.getInstance().getSelDevID());
    }

    public byte[] m_FbcFilterStatus = new byte[DStatic.Max_FBCFilter];

    public void iRead_CurrentScene(byte[] data, boolean isload) {
        //  int mc = data.Length; package length:364
        //   Debug.WriteLine("receive currentscene length is : {0}", mc);
        //Debug.WriteLine("read current sence.......................");
        int count = 11;
        int offset = 11;
        if (isload) {
            count = 0;//only for load current scene from local
            offset = 11;
        } else  //recall from device
        {
            count = 11;//total
            offset = 0;
        }
        for (int i = 0; i < Len_PresetName; i++)
            m_presetName[0][i] = data[count++];


        count = 31 - offset;
        int fType = 0;
        int ch = 0;
        int tmpfreq = 0;

        //region input channel Data receive

        for (ch = 0; ch < DStatic.ChanlMax; ch++) {

            m_chEdit[ch].invert = data[count++];
            m_chEdit[ch].chGain = bytetoint(data[count++]);
            //begin input gateExp

            m_chEdit[ch].gateExpEdit.limit_threshold = data[count++];
            m_chEdit[ch].gateExpEdit.limit_attack = (byte) (data[count++] * 100 + data[count++]);
            m_chEdit[ch].gateExpEdit.limit_release = (data[count++] * 100 + data[count++]);//release

            m_chEdit[ch].gateExpEdit.limit_ratio = data[count++];
            m_chEdit[ch].gateExpEdit.limit_bypass = data[count++];//expgate Power OFF/ON
            //delay
            m_chEdit[ch].delayPower = data[count++];
            m_chEdit[ch].delayTime = data[count++] * 256 + data[count++];//delaytime
            m_chEdit[ch].eqAllBypass = data[count++];//

            for (int eqindex = 8; eqindex < DStatic.CEQ_MAX; eqindex++) {
                fType = Xover.bTi(data[count++]);  //eqtype
                if (fType >= 4) fType -= 3;
                m_chEdit[ch].m_eqEdit[eqindex].eq_filterindex = (byte) fType; //default is bypass
                m_chEdit[ch].m_eqEdit[eqindex].eq_gaindex = data[count++];
                //  Debug.WriteLine("current sence recieve ..eqindex is  {0}   freqfiltertype :{1} chindex {2} gainindex {3}",
                //   eqindex, fType, ch, m_chEdit[ch].m_eqEdit[eqindex].eq_gaindex);
                m_chEdit[ch].m_eqEdit[eqindex].eq_qfactor = data[count++];
                tmpfreq = Xover.combineHLByte(data[count++], data[count++], 0);
                m_chEdit[ch].m_eqEdit[eqindex].eq_freqindex = Xover.LimitFreq(tmpfreq);
                m_chEdit[ch].m_eqEdit[eqindex].eq_bypass = data[count++];

            }


            for (int eqindex = 0; eqindex < DStatic.CEQ_MAX - 2; eqindex++) //0..7
            {
                fType = data[count++];//eqtype
                //  Debug.WriteLine("receive currentsence...eqfilter type is :  {0}  of eqindex is : {1}", fType,eqindex);
                if (fType > 0) fType -= 1;

                m_chEdit[ch].m_eqEdit[eqindex].eq_filterindex = (byte) fType;

                m_chEdit[ch].m_eqEdit[eqindex].eq_gaindex = data[count++];
                m_chEdit[ch].m_eqEdit[eqindex].eq_qfactor = data[count++];
                tmpfreq = Xover.combineHLByte(data[count++], data[count++], 0);
                m_chEdit[ch].m_eqEdit[eqindex].eq_freqindex = Xover.LimitFreq(tmpfreq);
                m_chEdit[ch].m_eqEdit[eqindex].eq_bypass = data[count++];

            }

            m_chEdit[ch].dynLimit.limit_threshold = data[count++];
            m_chEdit[ch].dynLimit.limit_attack = (byte) (data[count++] * 100 + data[count++]);

            m_chEdit[ch].dynLimit.limit_release = (data[count++] * 100 + data[count++]);

            m_chEdit[ch].dynLimit.limit_ratio = data[count++];
            m_chEdit[ch].dynLimit.limit_gain = data[count++];
            m_chEdit[ch].dynLimit.limit_bypass = data[count++]; //limit poweroff

            m_chEdit[ch].chMute = data[count++];
            m_chEdit[ch].sensitivityindex = data[count++];
            m_chEdit[ch].dc48vFlag = data[count++];
            //channel name
            for (int i = 0; i < Len_PresetName; i++) {
                m_chanelName[ch][i] = data[count++];
            }


        }
        //endregion
        //region output channel Data receive
        count = 1471 - offset;
        for (ch = 12; ch < DStatic.ChanlMax * 2; ch++) {
            m_chEdit[ch].invert = data[count++];
            m_chEdit[ch].chGain = bytetoint(data[count++]);
            //delay
            m_chEdit[ch].delayPower = data[count++];

            m_chEdit[ch].delayTime = Xover.combineHLByte(data[count++], data[count++], 1);//delaytime
            //EQ below
            m_chEdit[ch].eqAllBypass = data[count++];

            for (int eqindex = 8; eqindex < DStatic.CEQ_MAX; eqindex++) //high or low filter bypass
            {

                fType = Xover.bTi(data[count++]);  //eqtype
                if (fType >= 4) fType -= 3;
                m_chEdit[ch].m_eqEdit[eqindex].eq_filterindex = (byte) fType; //default is bypass
                m_chEdit[ch].m_eqEdit[eqindex].eq_gaindex = data[count++];
                //  Debug.WriteLine("current sence recieve ..eqindex is  {0}   freqfiltertype :{1} chindex {2} gainindex {3}",
                //   eqindex, fType, ch, m_chEdit[ch].m_eqEdit[eqindex].eq_gaindex);
                m_chEdit[ch].m_eqEdit[eqindex].eq_qfactor = data[count++];
                tmpfreq = Xover.combineHLByte(data[count++], data[count++], 0);
                m_chEdit[ch].m_eqEdit[eqindex].eq_freqindex = Xover.LimitFreq(tmpfreq);
                m_chEdit[ch].m_eqEdit[eqindex].eq_bypass = data[count++];

            }
            ///
            for (int eqindex = 0; eqindex < DStatic.CEQ_MAX - 2; eqindex++) ////normal eq
            {
                fType = data[count++];//eqtype
                //  Debug.WriteLine("receive currentsence...eqfilter type is :  {0}  of eqindex is : {1}", fType,eqindex);
                if (fType > 0) fType -= 1;

                m_chEdit[ch].m_eqEdit[eqindex].eq_filterindex = (byte) fType;

                m_chEdit[ch].m_eqEdit[eqindex].eq_gaindex = data[count++];
                m_chEdit[ch].m_eqEdit[eqindex].eq_qfactor = data[count++];
                tmpfreq = Xover.combineHLByte(data[count++], data[count++], 0);
                m_chEdit[ch].m_eqEdit[eqindex].eq_freqindex = Xover.LimitFreq(tmpfreq);
                m_chEdit[ch].m_eqEdit[eqindex].eq_bypass = data[count++];
            }

            byte ftemp = m_chEdit[ch].dynLimit.limit_threshold = data[count++];
            // Debug.WriteLine("receve current sence output channel : {0}  compressor threshold value {1}", ch, ftemp);
            m_chEdit[ch].dynLimit.limit_attack = (byte) (data[count++] * 100 + data[count++]);
            m_chEdit[ch].dynLimit.limit_release = (data[count++] * 100 + data[count++]);
            //  m_chEdit[ch].dynLimit.showLimitInformation(ch);

            m_chEdit[ch].dynLimit.limit_ratio = data[count++];
            m_chEdit[ch].dynLimit.limit_gain = data[count++];
            m_chEdit[ch].dynLimit.limit_bypass = data[count++]; //limit poweroff
            m_chEdit[ch].chMute = data[count++];
            //channel name
            for (int i = 0; i < Len_PresetName; i++) {
                m_chanelName[ch][i] = data[count++];
            }
        }
        //endregion over define output channel

        //region begin ...other pages....
        count = 2911 - offset;
        byte tmp = 0;
        byte mH = 0;
        byte mL = 0;
        for (int matrixIndex = 0; matrixIndex < DStatic.Matrix_CHNum; matrixIndex++) {
            for (int f = 0; f < 22; f++) {
                m_matrixAryGain[matrixIndex][f]=data[count++]&0xff;
            }

        }
        for (int matrixIndex = 0; matrixIndex < DStatic.Matrix_CHNum-2; matrixIndex++) {
            int temp = 0;
            for (int f = 2; f >=0; f--) {
                temp+= (data[count++]&0xff)<<f*8;
            }
            for (int i=0;i<20;i++){
                m_matrixAry[matrixIndex][i]=(byte)((temp&(1<<i))>>i);
            }
        }

        count = 3423 - offset;

        for (int duck = 0; duck < IO_MaxMatrixBus; duck++) {
            tmp = data[count++];

            m_DuckerSourch[duck] = (byte) ((tmp & HByte) >> WiStaticComm.SHIFT);
            m_DuckerBgm[duck] = (byte) (tmp & LByte);
        }


        //duck inputmixer
        m_duckerParameters[0] = data[count++];//duckerthreshold
        m_duckerParameters[2] = Xover.combineHLByte(data[count++], data[count++], BZero);//attack
        m_duckerParameters[4] = Xover.combineHLByte(data[count++], data[count++], BZero);//release

        m_duckerParameters[3] = Xover.combineHLByte(data[count++], data[count++], BZero);//ducker hold
        m_duckerParameters[1] = data[count++];//duck depth
        m_duckerParameters[5] = data[count++]; //duck powON

        //StringBuilder sb = new StringBuilder();
        //sb.Append("current scene receive ducker parameters :\t");
        //for(int gg=0;gg<6;gg++)
        //{
        //    sb.Append("ducker pamter index {0}  value {1}", gg, m_duckerParameters[gg]);
        //}
        //Debug.WriteLine(sb.ToString());


        // FBC parameter,len=122
        count = 3455 - offset;
        m_FbcParam[2] = data[count++]; //fbc bypas
        m_FbcParam[0] = data[count++]; //fbc qvalue for preserved
        //----
        m_FbcParam[1] = data[count++];
        m_FbcParam[4] = data[count++]; //fbc_modeflag
        m_FbcParam[5] = data[count++]; //fbc_filterReleaseTime   //

        byte tmpby = 0;
        for (int k = 0; k < IO_MaxMatrixBus; k++) //fbc status  IO_MaxMatrixBus<-->24
        {
            tmpby = data[count++];
            m_FbcFilterStatus[k] = tmpby;
            //  Debug.WriteLine("fbc status {0}  with index : {1}", tmpby, k);
        }
        //
        for (int k = 0; k < IO_MaxMatrixBus; k++) {
            m_FbcEQData[k].eq_freqindex = Xover.combineHLByte(data[count++], data[count++], WiStaticComm.BZero);
            // Debug.WriteLine("fbc freq {0}  with index : {1}", kofbcGain, k);
        }
        //
        for (int k = 0; k < IO_MaxMatrixBus; k++) {
            int kofbcGain = m_FbcEQData[k].eq_gaindex = Xover.combineHLByte(data[count++], data[count++], WiStaticComm.BZero);
            //Debug.WriteLine("fbc gain {0}  with index : {1}", kofbcGain, k);

        }
        //
        /////Relay status:
        count = 3580 - offset;
        autoMixerParam.autoAttack = data[count++] * 100 + data[count++];
        autoMixerParam.autoRelease = data[count++] * 100 + data[count++];
        //Debug.WriteLine("-------autoRelease is  {0}", autoMixerParam.autoRelease);
        autoMixerParam.autoHavgTau = data[count++];
        autoMixerParam.autoPower = data[count++];
        for (int f = 0; f < 24; f++) //3587
        {
            m_autoMixerCHSelect[f] = data[count++];
        }

        count = 3610 - offset;

        for (int k = 0; k < 2; k++) {
            m_Relay[k] = data[count++];
        }

        //zone status
        count = 3612 - offset;
        m_pageZone = data[count++];
        //
        count = 3628 - offset;
        for (int k = 0; k < Len_PresetName; k++) {
            m_DeviceName[k] = data[count++];
        }

        //device firmare version
        count = 3648 - offset;
        m_MCUVer = data[count++];
        m_FbcParam[2] = data[count++]; //fbc static filter
        //  Debug.WriteLine("mcuver read from device is : {0}", m_MCUVer);
        count++; //fbc all filter clear Flag
        count++; //fbc dynamictFilterClear flag
        for (int f = 0; f < 4; f++) {
            lock_pass[f] = data[count++];
        }

        lockFlag = data[count++];
        Log.d("lockFlag", lockFlag+"");
        //device serial No.
        if (isload) return;
        count = 3642;
        for (int k = 0; k < 5; k++) {
            pDevSNONumber[k] = data[count++];
        }

        //..................................over
        //endregion --------------------------over..all data of current scence
    }
    //endregion


    private void initialMatrix() {
        if (m_matrixAry == null) {
            m_matrixAry = new byte[DStatic.Matrix_CHNum][];
            for (int i = 0; i < DStatic.Matrix_CHNum; i++) {
                m_matrixAry[i] = new byte[DStatic.Matrix_CHNum];
            }
        }

        for (int ic = 0; ic < DStatic.Matrix_CHNum; ic++) {
            for (int k = 0; k < DStatic.Matrix_CHNum; k++) {
                m_matrixAry[ic][k] = 0;
            }
        }
        if (m_matrixAryGain == null) {
            m_matrixAryGain = new int[DStatic.Matrix_CHNum][];
            for (int i = 0; i < DStatic.Matrix_CHNum; i++) {
                m_matrixAryGain[i] = new int[DStatic.Matrix_CHNum];
            }
        }

        for (int ic = 0; ic < DStatic.Matrix_CHNum; ic++) {
            for (int k = 0; k < DStatic.Matrix_CHNum; k++) {
                m_matrixAryGain[ic][k] = 0;
            }
        }
    }

    public int getCurrentProgramIndex() {
        int res = -1;
        for (int i = 0; i < Num_Firm; i++) {
            if (m_updateEnd[i] == false) {
                res = i;
                break;
            }
        }
        return res;
    }

    public boolean isThisDevice(final int apID, final int devID) {

        qDebug.qLog("select apid " + mDeviceInf.mAppID + "    mdeviceID " + mDeviceInf.mDevID + "    comped apid: " + apID + " devid: " + devID);

        return (mDeviceInf.mAppID == apID && mDeviceInf.mDevID == devID);
    }

    public boolean m_updateEnd[];

    public void clearStatus() {
        Arrays.fill(m_updateEnd, false);
    }

    /*****************************************************************
     * @author williamXia
     * created at 2017/9/8 上午8:34
     * flag :0..3
     * segindex:
     ******************************************************************/
    public byte[] getFirmwareDatas(int segindex, final int flag) {

        if (m_DMcu == null || m_DMcu_1763 == null || m_DDsp == null || m_DRpm == null)
            return null;
        if (segindex >= m_segOfFirmware[flag]) return null;

        byte[] m_temp = new byte[LEN_1M];

        switch (flag) {
            case DStatic.e_mcu://mcu
            {
                if (m_DMcu.length >= (segindex + 1) * LEN_1M)
                    System.arraycopy(m_DMcu, segindex * LEN_1M, m_temp, 0, LEN_1M);
            }
            break;
            case e_dsp://dsp
            {
                if (m_DDsp.length >= (segindex + 1) * LEN_1M)
                    System.arraycopy(m_DDsp, segindex * LEN_1M, m_temp, 0, LEN_1M);
            }
            break;
            case DStatic.e_ma8_1763://dsp1763
            {
                if (m_DMcu_1763.length >= (segindex + 1) * LEN_1M)
                    System.arraycopy(m_DMcu_1763, segindex * LEN_1M, m_temp, 0, LEN_1M);
            }
            break;
            case DStatic.e_rpm://dsp2
            {
                if (m_DRpm.length >= (segindex + 1) * LEN_1M)
                    System.arraycopy(m_DRpm, segindex * LEN_1M, m_temp, 0, LEN_1M);
            }
            break;
            case DStatic.e_rio://dsp2
            {
                if (m_DRio.length >= (segindex + 1) * LEN_1M)
                    System.arraycopy(m_DRio, segindex * LEN_1M, m_temp, 0, LEN_1M);
            }
            break;

            case DStatic.e_rva://dsp2
            {
                if (m_DRva.length >= (segindex + 1) * LEN_1M)
                    System.arraycopy(m_DRva, segindex * LEN_1M, m_temp, 0, LEN_1M);
            }
            break;
            case DStatic.e_rvc://dsp2
            {
                if (m_DRvc.length >= (segindex + 1) * LEN_1M)
                    System.arraycopy(m_DRvc, segindex * LEN_1M, m_temp, 0, LEN_1M);
            }
            break;

        }
        return m_temp;

    }

    /*****************************************************************
     * @author williamXia
     * created at 2017/9/8 上午8:55
     * tindex:0..3(MCU,key,dsp1,dsp2)
     * segindex: the segpart of every firmware dived by 1024
     ******************************************************************/
    public void sendCMD_UpdateFirmware(final int segindex, final int tindex) {
        byte[] m_Firmware = getFirmwareDatas(segindex, tindex);
        // qDebug.qLog("will send cmd mcuupdate firm  with segindex  tindex : "+segindex+"   "+tindex);
        //  if (tindex != e_dsp)
        //  CmdSender.sendCMD_MCU_FirmwareUpdate(segindex, m_Firmware, mDeviceInf.mAppID, mDeviceInf.mDevID);
        // else
        //   CmdSender.sendCMD_DSP_FirmwareUpdate(segindex, m_Firmware, mDeviceInf.mAppID, mDeviceInf.mDevID);

    }

    public void sendCMD_MCU1763_GotoReset() {
        backDeviID = mDeviceInf.mDevID;
        mDeviceInf.mDevID = devID1763;
        //  CmdSender.sendCMD_MCU_GotoReset(mDeviceInf.mAppID, mDeviceInf.mDevID);

    }

    public void sendCMD_MCU_GotoReset() {
        // CmdSender.sendCMD_MCU_GotoReset(mDeviceInf.mAppID, mDeviceInf.mDevID);

    }

    public final static int F16Header = 0xff00;

    public int devID1763 = 0;

    public int gDevid1763() {
        int resid = (mDeviceInf.mDevID & F16Header);
        resid += 0x80;
        devID1763 = resid;
        return resid;
    }

    public boolean isMCU1763() {
        return (mDeviceInf.mDevID == devID1763);

    }

    public void sendCMD_DSP_GotoReset() {

        // CmdSender.sendCMD_DSP_GotoReset(mDeviceInf.mAppID, mDeviceInf.mDevID, m_segOfFirmware[e_dsp]-1);

    }

    public void sendCMD_MCU_EnterReady() {

        int segm = 0;
        switch (mDeviceInf.mAppID) {
            case DStatic.AP_MA8: {
                if (mDeviceInf.mDevID != devID1763) {
                    segm = m_segOfFirmware[DStatic.e_mcu];
                } else {
                    segm = m_segOfFirmware[DStatic.e_ma8_1763];
                }
            }
            break;
            case DStatic.AP_RPM: {
                segm = m_segOfFirmware[DStatic.e_rpm];
            }
            break;
            case DStatic.AP_RIO:
                segm = m_segOfFirmware[DStatic.e_rio];
                break;
            case DStatic.AP_RVA:
                segm = m_segOfFirmware[DStatic.e_rva];
                break;
            case DStatic.AP_RVC:
                segm = m_segOfFirmware[DStatic.e_rvc];
                break;

        }
        // if (segm > 0)
        //  CmdSender.sendCMD_MCU_EnterReady(mDeviceInf.mAppID, mDeviceInf.mDevID, segm);

    }

    public int gtypeindex() {
        int tindex = 0;
        switch (mDeviceInf.mAppID) {

            case DStatic.AP_MA8:
                if (isMCU1763())
                    tindex = DStatic.e_ma8_1763;
                else
                    tindex = DStatic.e_mcu;
                break;

            case DStatic.AP_RPM:
                tindex = DStatic.e_rpm;
                break;
            case DStatic.AP_RVA:
                tindex = DStatic.e_rva;
                break;
            case DStatic.AP_RIO:
                tindex = DStatic.e_rio;
                break;
            case DStatic.AP_RVC:
                tindex = DStatic.e_rvc;
                break;

        }
        return tindex;

    }

}
