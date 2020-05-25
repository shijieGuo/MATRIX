package Events;

public class E_refreshConnectInfo {
    private boolean isConnect;

    public boolean isConnect() {
        return isConnect;
    }


    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    public E_refreshConnectInfo(boolean miscon) {
        isConnect = miscon;
    }
}
