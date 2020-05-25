package cn.com.williamxia.wipack.utils;

/**
 * Created by williamXia on 2017/8/29.
 */

public class CommunicateStatus {


    public final static int M_ConnectedNormal = 0;//after connected
    public final static int M_ConNormalDetected = 99;//normal
    public final static int M_Disconnect = 100;
    public final static int Max_status_Counter=150;


    public int commuteStatus;
    public int responseAckCount = 1;

    public CommunicateStatus() {
        resetCommunitStatus();
    }

    public CommunicateStatus(int commuteStatus) {
        this.commuteStatus = commuteStatus;
    }

    public void setCommutStatusParam(final int comtatus, final int resCount) {
        commuteStatus = comtatus;
        responseAckCount = resCount;
    }


    public CommunicateStatus(int commuteStatus, int responseAckCount) {
        this.commuteStatus = commuteStatus;
        this.responseAckCount = responseAckCount;
    }


    public void resetCommunitStatus() {
        commuteStatus = M_ConnectedNormal;
        responseAckCount = 1;
    }

    public void resetCommunitStatus(int ncount) {
        commuteStatus = M_ConnectedNormal;
        responseAckCount = ncount;
    }

}
