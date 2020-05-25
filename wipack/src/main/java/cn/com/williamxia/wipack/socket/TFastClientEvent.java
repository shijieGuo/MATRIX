package cn.com.williamxia.wipack.socket;

/**
 * Created by williamXia on 2017/8/23.
 */

public interface TFastClientEvent {
    public  void onFastConnected(Object sender, boolean conStatus, final int conFlag);
    public  void onFastReceive(Object sender, byte[] mdata, int length, final int conFlag);

}
