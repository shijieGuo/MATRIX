package Datas;

/**
 * Created by zhihuafeng on 3/28/2018.
 */

public class matrixBean {
    private static final long serialVersionUID = -8379602526383806649L;
    public byte activeFlag;
    public int No;
    private String channelName;

    public matrixBean(byte isAct, int No, String channelName) {
        this.activeFlag = isAct;
        this.No = No;
        this.channelName = channelName;
    }

    public byte isAct() {
        return activeFlag;
    }

    public String getStrAct() {
        return (activeFlag>0 ? "ON" : "OFF");
    }

    public void setAct(byte act) {
        activeFlag = act;
    }

    public int getNo() {
        return No;
    }

    public void setNo(int no) {
        No = no;
    }

    public String getChannelName() {
        channelName = XData.gInstance().getShowNameInput(this.No - 1);
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
