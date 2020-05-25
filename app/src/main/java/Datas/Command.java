package Datas;

/**
 * Created by williamXia on 2017/11/24.
 */

public class Command {

    public static  final
    int F_PCGetDeviceInfo = 12;
    public static  final
    int F_ScanDeviceID = 3;
    //public static  final int F_InputPhaseGain = 65;
    //public static  final int F_OutputPhaseGain = 66;
    public static  final
    int F_InputExpGATE = 67;
    public static  final
    int F_OutputExpGATE = 68;
    public static  final
    int F_InputEQ = 69;
    public static  final
    int F_OutputEQ = 70;
    public static  final
    int F_InputCOMP = 71;
    public static  final
    int F_OutputCOMP = 72;
    public static  final
    int F_InputDelay = 73;
    public static  final
    int F_OutputDelay = 74;
    public static  final
    int F_DuckerParameter = 75;
    public static  final
    int F_DuckerInputMixer = 76;
    public static  final
    int F_DuckerGainInsert = 100;


    public static  final
    int F_RDPortOutSourceSet = 77;
    public static  final
    int F_MatrixMixer = 78;
    public static  final
    int F_PagingMixer = 79;
    public static  final
    int F_ReadStatus = 80;
    public static  final
    int F_InputDG411Gain = 81; //sensitivity
    public static  final
    int F_Ack = 82;
    public static  final
    int F_InputEQFlat = 83;
    public static  final
    int F_OutEQFlat = 84;
    public static  final
    int F_Copy = 85;
    public static  final
    int F_InputDC48VFlag = 86;
    public static  final
    int F_AutoMixerSetting = 87;


    //Reserve 87
    public static  final
    int F_WrDevInfo = 88;
    public static  final
    int F_RdDevInfo = 89;
    public static  final
    int F_ReChName = 90;//rename channel name
    //Reserve 90
    public static  final
    int F_StoreSinglePreset = 91;
    public static  final
    int F_RecallSinglePreset = 92;
    //Matrix cmd define below:
    public static  final
    int F_LoadFromPC = 93;
    public static  final
    int F_RDDefaultSetting = 94;
    public static  final
    int F_ReturnDefaultSetting = 95;

    public static  final
    int F_RecallCurrentScene = 96;
    public static  final
    int F_GetPresetList = 97;

    public static  final
    int F_DeleteSinglePreset = 98;
    public static  final
    int F_SigMeters = 99;
    //Reserve 100
    public static  final
    int F_Mute = 101;

    public static  final
    int F_MemoryExport = 102;
    public static  final
    int F_MemoryImport = 105;
    public static  final
    int F_MemoryImportAck = 106;

    public static  final
    int F_WrDevSerialNO = 107;
    public static  final
    int F_SetPageZoneIndex = 108;
    public static  final
    int F_RelayControl = 109;

    public static  final
    int F_FBCSetting = 110;
    public static  final
    int F_GetFBCStatus = 111;
    public static  final
    int F_FBCSetup = 112;
    public static  final
    int MSG_TCP_ParaConnected = 700;




    //public final int F_FBCFilterStatus = 113;


    //public final int F_FBCFilterGain_F08 = 114;
    //public final int F_FBCFilterGain_F16 = 115;
    //public final int F_FBCFilterGain_F24 = 116;
    //public final int F_FBCFilterFreq_F08 = 117;
    //public final int F_FBCFilterFreq_F16 = 118;
    //public final int F_FBCFilterFreq_F24 = 119;

    public static  final
    int F_InpuGain = 120;
    //  public static  final int F_GetInpuGain = 121;

    public static  final
    int F_InpuPhase = 122;
    // public static  final int F_GetInpuPhase = 123;

    public static  final
    int F_OutputGain = 124;
    // public static  final int F_GetOutputGain = 125;

    public static  final
    int F_OutputPhase = 126;
    // public static  final int F_GetOutputPhase = 127;

    // public static  final int F_GetInpuDelay = 128;
    //  public static  final int F_GetOutputDelay = 129;

    //  public static  final int F_GetMatrixMixer = 130;

    // public static  final int F_GetInMeter = 131;
    // public static  final int F_GetOutMeter = 132;
    public static  final
    int F_BGMSelect = 133;

    public static  final
    int F_AutoMixerCHSelect = 134;

    public static  final
    int F_ResetToFactorySetting = 135;

    public static  final
    int F_ReadLockPWD = 137;
    public static  final
    int F_WriteLockPWD = 138;

    //PC Communication ,MCU IAP Command
    public static  final
    int IAP_MODE_EXIT = 0XE0;
    public static  final
    int IAP_PROGRAMING = 0xF0;
    public static  final
    int IAP_FINISH = 0xF1;

    public static  final
    int IAP_ENTER_IN_APP = 0xEE;        //Enter IAP Mode in application
    public static  final
    int IAP_MODE_ENTER_READY = 0xEF;    //IAP Mode Prepare ready, 

    //PC Communication ,DSP Firmware Update Command
    public static  final
    int F_DSPFirmwareUpdate = 0xf2;     //DSP Firmware update 
    public static  final
    int F_UpdateProgress = 0xf3;
    public static  final
    int F_ReadDSPCode = 0xf4;
    public static  final
    int F_SendMCUStart = 0xf5;

    // #region Message Send CMD Define


    public static final String MatrixGUIClass = "MatrixPage";
    public static final String LoadLEDGUIClass = "LoadLEDForm";


    public static  final
    int MatrixA8_MSG_Transfer = 0x501;

    public static  final
    int LoadLed_MSG_Transfer = 0x707;

    public static  final
    int MainWindow_MSG_Transfer = 0x601;
    public static  final
    int WM_Msg_Exit = 0x602;

    public  static  final String RVAGUIClass = "RVADevPage";
    public static  final
    int RVA200_MSG_Transfer = 0x500;


    public static  final String RPMGUIClass = "RPM200Page";
    public static  final
    int RPM200_MSG_Transfer = 0x504;


    public  static  final String CGNewDevNameGUIClass = "CNewDeveName";  //"CNewDeveName";
    public static  final
    int ChangeDevName_MSG_Transfer = 0x502;

    public static final String CMainWindowGUIClass = "MainWindow";

    public static  final
    int MSG_NoticeDisconnect = 0xFFFD;//notice msg to send matrix to close the device
//endregion


}
