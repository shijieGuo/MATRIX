package Datas;

import android.os.Environment;
import android.text.InputFilter;
import android.widget.EditText;

import java.math.RoundingMode;
import java.text.NumberFormat;

import cn.com.williamxia.matrixa8dante.MainActivity;
import cn.com.williamxia.wipack.control.CSlider;
import cn.com.williamxia.wipack.socket.TCPClient;
import cn.com.williamxia.wipack.utils.IpManager;
import cn.com.williamxia.wipack.utils.Xover;
import cn.com.williamxia.wipack.utils.qDebug;

import static java.lang.Float.parseFloat;

/**
 * Created by williamXia on 2017/9/6.
 */

public class DStatic {

    public static boolean Model;//ture:tcp   false:udp
    public static final String FloatRegex = "-?[0-9]+.*[0-9]*";
    public static byte[] pin = new byte[4];
    public static final int Pb_width_lstV = 1480;//for popup hlpassfilter width
    public static final int Pb_height_lstV = 800;//for popup hlpassfilter height


    public static byte m_vrValue[] = new byte[20];

    public static final String SD_Path_SaveLoad = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MatrixA8/presets";

    public static final String[] StrChanelList = {
            "CH01", "CH02", "CH03", "CH04", "CH05", "CH06", "CH07", "CH08", "CH09",
            "CH10", "CH11", "CH12",
    };


    public static final String[] StrChanelList1 = {
            "CH01", "CH02", "CH03", "CH04", "CH05", "CH06"
    };
    public static final int channelGap = 6;
    public static final String[] StrChanelList2 = {
            "CH07", "CH08", "CH09", "CH10", "CH11", "CH12"
    };
    public static final int HeartTimeDelay = 3000;

    public static int aChindex = 0;//current page select chindex

    public static final int CPageUPdate = 500;
    public static final String MemPresetsName = "MatrixA8Memory"; //filename for memory save/read

    public static final String StrPresetHeader = "MatrixA8 Presets File";
    public static final String StrMemoryHeader = "MatrixA8 AllMemory File";

    public static final int PKLen_ACK = 13;
    public static final int CEQ_MAX = 10;

    public static final int ChanlMax = 12;
    public static final int Matrix_CHNum = 22;
    public static final int IO_MaxMatrixBus = 24;
    public static final int Max_FBCFilter = 24;
    public static final int Max_DuckerParams = 6;
    //
    public static final int FBC_FFTNum = 24;


    public static final int AP_ALP50 = 22;
    public static final int AP_CLV = 4;
    public static final int AP_DCS_100 = 16;
    public static final int AP_DLM_A8 = 2;
    //--------
    public static final int AP_MA8 = 6;
    public static final int AP_D8 = 7;
    public static final int AP_RIO = 11;
    public static final int AP_Router = 20;
    //
    public static final int AP_RPM = 8;
    public static final int AP_RVA = 10;
    public static final int AP_RVC = 9;

    public static final int MIN_Len_Pack = 13;


    public static final int Len_FirmwareTrans = 1024 + 15;

    public static final int MIN_LEN_PACK = 18;
    public static final int LEN_CopyPack = 27;

    public static final int LEN_ACKPack = 13;
    //
    public static final int LEN_changeDevNamePack = 33;

    public static final int LEN_ComPack = 25;
    public static final int LEN_RecallSinglePresetPack = 14;//
    //
    public static final int LEN_DeletePack = 14;
    public static final int LEN_ChangeChannelName = 40;


    public static final int Memory_Per_Packeg_len = 4115;
    public static final int LEN_StoreNamePresetPack = 30;//
    public static final int Memory_Max_Package = 25;


    //
    public static final int LEN_Sence = 3806;
    public static final int LEN_MatrixPack = 30;
    public static final int LEN_DelPresetPack = 14;
    public static final int Len_RewritePass = 23;

    //---

    public static final String Ver_MCU = " Loading firmware MCU sucessfully, v1.1";
    public static final String Ver_Dsp = " Loading firmware DSP sucessfully,  v1.1";
    public static final String Ver_Mcu1763 = " Loading firmware MCUII  sucessfully,   v1.1";
    //------------------------
    public static final String Ver_Rpm = " Loading firmware RPM200 sucessfully,  v1.1";
    public static final String Ver_RIO = " Loading firmware RIO200  sucessfully, v1.1";
    public static final String Ver_Rva = " Loading firmware RVA200  sucessfully, v1.1";
    public static final String Ver_Rvc = " Loading firmware RVC200  sucessfully, v1.1";

    public static final String[] M_verAry = {
            Ver_MCU, Ver_Dsp, Ver_Mcu1763, Ver_Rpm, Ver_RIO, Ver_Rva, Ver_Rvc

    };

    public static final int CMD_CHECK_DEVICE = 0xAA;

    public static final int CMD_MCU_IAP_Enter_IN_App = 0xEE; //enter the iap mode for firmware begin..
    public static final int CMD_MCU_READY_PROGRAM = 0xEF;
    public static final int CMD_MCU_DO_PROGRAM = 0xF0;
    public static final int CMD_MCU_FINISH_PROGRAM = 0xF1;

    //dsp firmware update
    public static final int CMD_DSP_EnterUpdateBegin = 0xF2;
    public static final int CMD_DSP_DO_PROGRAM = 0xF3;
    public static final int CMD_DSP_Finish_PROGRAM = 0xF5;  //test
    public static final int SCAN_ALL_APID = 0xFFFF;


    //read DSP below
    public static final int CMD_Read_DSPCode = 0xF4;

    public static final int MAX_DSP_Package = 322;
    //

    public static final int CMD_READY_STOP = 0xE0;
    public static final int CMD_GETADDRS_RECALL = 0X21;
    public static final int CMD_ChangeChanelName = 0X5A;

    public static final int Len_DevName = 16;

    public static final String RebootString = "Equipment is rebooting,please wait... %d";

    public static final byte BZero = 0;
    public static final byte BOne = 1;

    public static final int LEN_Preset_Dsp = 779;
    public static final int LEN_Preset_GEQ = 779;
    public static final int LEN_Preset_Dfx = 1675;
    public static final int LEN_Preset_Scen = 395;


    public static final int PKLen_ReadDevInf = 13;


    public static final int MODE_FATDSP = 1, MODE_GEQ = 2, MODE_DFX = 3, MODE_SCENE = 4, MODE_CURRENT_SCENE = 5;


    public static final int LEN_FILE_PRESET[] = {
            70, 44, 25, 4569
    };

    public static final int DSP_LEN = 269;//for read dsp data from device

    public static final int EpromSeneLength = 4612;
    public static final int EpromFatDSPLength = 2892;
    public static final int EpromDfxLength = 2300;
    public static final int EpromGEQLength = 3276;

    public static final int CountDownDelay = 20 * 1000;
    public static final int CountDownInteval = 1000;

    //--------
    //region--for braadcast
    public static final String Action_MainActivity = "Action.MatrixA8_mainActivity";
    public static final String Msg_Key_MainActivity = "Key_MainActivity_Do";
    public static final String Msg_ID_BeginConnect = "ID_MainActivity_BeginConnect";

    public static final int MConnect = 1938;
    public static final int MDisconnect = 1939;

    //
    public static final String Action_DspChanelFrag = "Action.MatrixA8_dspFrag";
    public static final String Msg_Key_DspChanel = "Key_DspChanelFrag";
    public static final String Msg_ID_DspChanel = "ID_DspChanelFrag";


    public static final String Action_IPConfigFrag = "Action.MatrixA8_IpConfigFrag";
    public static final String Msg_Key_IpConfig = "Key_IpConfigFrag";
    public static final String Msg_ID_IPConfig = "ID_IpConfigFrag";

    //
    public static final String Action_MatrixFrag = "Action.MatrixFrag";
    public static final String Msg_Key_Matrix = "Key_MatrixFrag";
    public static final String Msg_ID_Matrix = "ID_MatrixFrag";

    //
    public static final String Action_Preset = "Action.PresetFrag";
    public static final String Msg_Key_Preset = "Key_PresetFrag";
    public static final String Msg_ID_Preset = "ID_PresetFrag";

    //--------
    //endregion


    public static final String FM_MCU = "DMMB20160905";
    public static final String FM_DSP = "DMDB20160905";//
    public static final String FM_MA8_1763 = "EMMB20160905";// for matrix A8 only

    public static final String FM_RIO = "RIOB20160905";
    public static final String FM_RPM = "RPMB20160905";
    public static final String FM_RVA = "RVAB20160905";//
    public static final String FM_RVC = "RVCB20160905";


    public static final int e_mcu = 0, e_dsp = 1, e_ma8_1763 = 2, e_rpm = 3, e_rio = 4, e_rva = 5, e_rvc = 6;

    public static final String[] m_Firmwares = {

            FM_MCU, FM_DSP, FM_MA8_1763, FM_RPM, FM_RIO, FM_RVA, FM_RVC
    };

    private static float convertInputGainStr_toFloat(String strInput) {
        String strTmp = strInput.toUpperCase();
        if (strTmp.contains("DB"))
            strTmp = strInput.substring(0, strTmp.length() - 2);

        float tmp = 0f;
        try {
            float checkDB = 0;
            checkDB = parseFloat(strTmp);
            if (checkDB > 15f)
                tmp = 15f;
            else if (checkDB < -80f)
                tmp = -80f;
            else tmp = checkDB;

        } catch (Exception e) {
            qDebug.qLog("identInput string meet error because strinput string is " + strTmp);
            tmp = 0;
        }
        return tmp;

    }

    private static int getGainIndex(float InputGain) {
        String realGainStrData = "";
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(1);
        nf.setRoundingMode(RoundingMode.HALF_UP);
        String strGain = nf.format(InputGain);
        double tmp = Double.parseDouble(strGain);
        int tmpInt = (int) (2 * tmp + 160);
        return tmpInt;

    }

    public static double getGain(int gindex) {
        double gain = gindex * 0.5 - 80;
        return gain;
    }

    public static int getGainIndex_fromInputStr(String strGain) {
        float tmpGain = convertInputGainStr_toFloat(strGain);
        int gaindex = getGainIndex(tmpGain);
        return gaindex;
    }

    public static double getRealGain_fromInputStr(String strGain) {
        int gaindex = getGainIndex_fromInputStr(strGain);
        return getGain(gaindex);
    }


    /*****************************************************************
     * @author williamXia
     * created at 2018/5/2 20:41
     * chindex:[0..23]
     ******************************************************************/
    public static void updateGainEdit(EditText edTxt, CSlider sbx, int chindex) {
        String strInput = edTxt.getText().toString();
        if (Xover.identyInputString(strInput, DStatic.FloatRegex)) {
            //验证成功


            int gainindex = DStatic.getGainIndex_fromInputStr(strInput);
            XData.gInstance().m_chEdit[chindex].chGain = gainindex;

            qDebug.qLog("------验证成功----gain: " + gainindex + "  input string is " + strInput);
        }
        edTxt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
        if ((DStatic.Model&&TCPClient.getInstance().isConnected())||(!DStatic.Model&& !MainActivity.cuurentIp.isEmpty())) {
            XData.gInstance().sendCMD_FaderGain(chindex, IpManager.getInstance().getSelDevID());
        } else {
            qDebug.qLog("------开始变----strinput: " + strInput + "    gain value:  " + XData.gInstance().strIGain(chindex));

            edTxt.setText(XData.gInstance().strIGain(chindex));
            sbx.setValue(XData.gInstance().m_chEdit[chindex].chGain);
        }

    }

    public static final String[] ChannelOutName = {
            "Output01",
            "Output02",
            "Output03",
            "Output04",
            "Output05",
            "Output06",
            "Output07",
            "Output08",
            "Output09",
            "Output10",
            "Output11",
            "Output12",
            "Net01",
            "Net02",
            "Net03",
            "Net04",
            "Net05",
            "Net06",
            "Net07",
            "Net08"
    };


    public static final String[] ChannelInName = {
            "Input01",
            "Input02",
            "Input03",
            "Input04",
            "Input05",
            "Input06",
            "Input07",
            "Input08",
            "Input09",
            "Input10",
            "Input11",
            "Input12",
            "Net01",
            "Net02",
            "Net03",
            "Net04",
            "Net05",
            "Net06",
            "Net07",
            "Net08"

    };

}
