package Events;

public class ReceiveCommandEvent {
    public byte[] datas;
    public String recAddr;
    public ReceiveCommandEvent(byte[] datas,String recAddr){
        this.datas=datas;
        this.recAddr=recAddr;
    }

}
