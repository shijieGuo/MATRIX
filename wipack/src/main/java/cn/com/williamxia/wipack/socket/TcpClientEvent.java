package cn.com.williamxia.wipack.socket;

/**
 * Created by williamXia on 2017/6/5.
 */

public interface TcpClientEvent {

    public  void onTcpConnected(Object sender, boolean conStatus, final int conFlag);
    public  void onTcpDisconnected(Object sender, boolean staus, final int conFlag);
    public  void onTcpReceive(Object sender, byte[] mdata, int length, final int conFlag);

}
