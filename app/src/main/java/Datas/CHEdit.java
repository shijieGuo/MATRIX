package Datas;

/**
 * Created by williamXia on 2017/8/11.
 */

public class CHEdit {

    public byte invert;//
    public int chGain;//it xoverseed 128
    public byte chMute;

    public int delayTime;
    public byte delayPower;//for reserved

    public byte sensitivityindex;
    public byte dc48vFlag;

    //
    public EQEdit[] m_eqEdit;//10 segment
    public byte eqAllBypass;
    //----
    public LimitEdit gateExpEdit;

    public LimitEdit dynLimit;

    public byte hlp_AllBypass;//reserved
    //
    public byte deviceTempreature;//reserved

    public CHEdit() {
        dynLimit = new LimitEdit();
        gateExpEdit = new LimitEdit();
        m_eqEdit = new EQEdit[DStatic.CEQ_MAX];
        for (int i = 0; i < DStatic.CEQ_MAX; i++) {
            m_eqEdit[i] = new EQEdit();
        }
        clearData();
    }

    public void clearEQData() {
        for (int i = 0; i < DStatic.CEQ_MAX; i++) {
            if (m_eqEdit[i] != null)
                m_eqEdit[i].clearData();
        }
    }

    public void clearData() {
        chGain = chMute = dc48vFlag = delayPower = eqAllBypass = invert = sensitivityindex = 0;
        hlp_AllBypass=0;
        delayTime = 0;
        deviceTempreature = 0;
        if (dynLimit != null)
            dynLimit.clearData();
        if (gateExpEdit != null)
            gateExpEdit.clearData();
        clearEQData();

    }


}
