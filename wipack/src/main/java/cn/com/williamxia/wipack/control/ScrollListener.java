package cn.com.williamxia.wipack.control;

/**
 * Created by williamXia on 2017/11/18.
 */

public interface ScrollListener {
    public void onScrollChanged(int nx, int ny, int oldX, int oldY);

    public void onScrollStopped();

    public void onScrolling();
}
