package cn.com.williamxia.wipack.utils;

import android.os.CountDownTimer;
import android.util.Log;

/**
 * Created by williamXia on 2017/6/15.
 * <p>
 * first par: total timer
 * second par:interval time
 */

public class myCounterTimer extends CountDownTimer {

    public interface CountDownEvent {
        public void onCountDownEvent(final int dValue);

        public void onCountDownFinish();
    }

    private CountDownEvent countDownEvent;

    public void setCountDownEvent(CountDownEvent countDownEvent) {
        this.countDownEvent = countDownEvent;
    }


    public myCounterTimer(long miliInFuture, long countDownInterval) {
        super(miliInFuture, countDownInterval);
    }

    @Override
    public void onTick(long l) {
        //  Log.v("havea", "mili remain second: " + (int)(l / 1000) + " ....");
        int nDow = (int) (l / 1000);
        if (countDownEvent != null) {
            countDownEvent.onCountDownEvent(nDow);
        }
        //  Log.d("kook", "mili remain second: " + nDow + " ....");
    }

    @Override
    public void onFinish() {
        // Log.v("havea", "finish ok");
        Log.d("kook", "finish..............");
        if (countDownEvent != null) {
            countDownEvent.onCountDownFinish();
        }

    }
}
