package cn.com.williamxia.wipack.socket;

/**
 * Created by williamXia on 2017/6/10.
 */

public interface UDPScanedEvent {
    public void onUDPScanedEvent(String strScannedIP, final int sport);

}
