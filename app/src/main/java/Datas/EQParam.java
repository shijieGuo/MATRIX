package Datas;


import cn.com.williamxia.wipack.utils.qDebug;

/**
 * Created by williamXia on 2017/8/11.
 * this class stored the value for caculate fft directly not the index number
 */

public class EQParam {

    public double Freq;
    public double Gain;
    public double QValue;
    public byte TypeFilter;
    public byte ByPass;

    public EQParam() {
        clearData();
    }

    public void EQParamCopy(EQParam eqParam) {
        Freq = eqParam.Freq;
        Gain = eqParam.Gain;
        //
        QValue = eqParam.QValue;
        TypeFilter = eqParam.TypeFilter;
        ByPass = eqParam.ByPass;
    }

    public void clearData() {
        Freq = Gain = QValue = 0;
        TypeFilter = 0;
        ByPass = 0;

    }

    public EQParam(double freq, double gain, double qvalue, byte typef, byte bypass) {
        Freq = freq;
        Gain = gain;
        QValue = qvalue;
        TypeFilter = typef;
        ByPass = bypass;
    }

    public void setEQParam(double freq, double gain, double qvalue, byte typef, byte bypass) {
        Freq = freq;
        Gain = gain;
        QValue = qvalue;
        TypeFilter = typef;
        ByPass = bypass;
    }

    public void showEQParam() {

        StringBuilder strParam = new StringBuilder();
        strParam.append("param freq :" + Freq);
        strParam.append("\tparam Gai :" + Gain);
        strParam.append("\tparam Qvalue :" + QValue);
        strParam.append("\tparam typfilter :" + TypeFilter);
        strParam.append("\tparam bypass : " + ByPass);
        qDebug.qLog(strParam.toString());
    }


}
