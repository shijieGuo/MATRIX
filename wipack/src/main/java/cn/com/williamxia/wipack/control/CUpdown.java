package cn.com.williamxia.wipack.control;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.com.williamxia.wipack.R;
import cn.com.williamxia.wipack.utils.qDebug;


/**
 * Created by williamXia on 2017/8/4.
 */

public class CUpdown extends LinearLayout {
    private Button upLeft;
    private Button upRight;

    private int iTag;
    private boolean isByPass;

    private int MaxValue;
    private int MinValue;
    private int Value;

    private int resRelativeID;

    public int getResRelativeID() {
        return resRelativeID;
    }

    private TextView labelView;

    public TextView getLabelView() {
        return labelView;
    }

    public void setLabelView(TextView labelView) {
        this.labelView = labelView;
    }

    public void setResRelativeID(int resRelativeID) {
        this.resRelativeID = resRelativeID;
    }

    private UpdownEventListener updownEventListener;

    public int getValue() {
        return Value;
    }

    public int getMaxValue() {
        return MaxValue;
    }

    public int getMinValue() {
        return MinValue;
    }

    public boolean isByPass() {
        return isByPass;
    }

    public int getiTag() {
        return iTag;
    }

    public void setiTag(int iTag) {
        this.iTag = iTag;
    }

    public void setUpdownEventListener(UpdownEventListener mListener) {
        this.updownEventListener = mListener;
    }

    public void setByPass(boolean byPass) {
        isByPass = byPass;
    }

    public void setMaxValue(int maxValue) {
        MaxValue = maxValue;
    }

    public void setMinValue(int minValue) {
        MinValue = minValue;
    }

    public void setValue(int value) {
        Value = value;
    }

    public CUpdown(Context context) {
        this(context, null);
    }

    public CUpdown(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CUpdown(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.updown, this);
        initCompoents();

        TypedArray resAry = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CUpdown, defStyleAttr, 0);
        int atrCount = resAry.getIndexCount();
        for (int i = 0; i < atrCount; i++) {

            int index = resAry.getIndex(i);

            if (index == R.styleable.CUpdown_uiTag)
                iTag = resAry.getInteger(index, 0);

            else if (index == R.styleable.CUpdown_upMaxValue)
                MaxValue = resAry.getInteger(index, 100);

            else if (index == R.styleable.CUpdown_upMinValue)
                MinValue = resAry.getInteger(index, 0);

            else if (index == R.styleable.CUpdown_uValue)
                Value = resAry.getInteger(index, 0);

        }
        resAry.recycle();


    }


    private void initCompoents() {
        Value = 0;
        MaxValue = 100;
        isByPass = false;

        upLeft = (Button) findViewById(R.id.upLeft);
        upRight = (Button) findViewById(R.id.upRight);
        //----------------
        QuickTouchListener qListener = new QuickTouchListener();
        upLeft.setOnTouchListener(qListener);
        upRight.setOnTouchListener(qListener);
    }


    public class QuickTouchListener implements OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            //   qDebug.qLog("reponse ontouch event in cupdown....");
            if (isByPass) {
                //do noting
            } else {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (view.getId() == R.id.upLeft || view.getId() == R.id.upRight) {
                            view.setPressed(true);
                            if (labelView != null && view.isPressed())
                                labelView.setActivated(true);
                            stopShedule();
                            if (view.getId() == R.id.upLeft)
                                startShedule(Up_Decrease);
                            else
                                startShedule(Down_Increase);
                            qDebug.qLog("updown pressed action down..........");
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (view.getId() == R.id.upLeft || view.getId() == R.id.upRight) {
                            view.setPressed(false);
                            if (labelView != null)
                                labelView.setActivated(false);
                            stopShedule();
                            qDebug.qLog("updown pressed action up.........");
                        }
                        //  mHandler.sendEmptyMessageDelayed(UD_Finished, UPFinish_Delay);
                        break;


                }

            }


            return true;
        }
    }

    public void setStrValue(String str) {
        if (labelView != null) {
            labelView.setText(str);
        }
    }

    private ScheduledExecutorService scheduledExecutor;


    private void stopShedule() {
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdownNow();
            scheduledExecutor = null;
        }
    }

    private void startShedule(final int vid) {

        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Message msg = mHandler.obtainMessage();
                msg.what = vid;
                mHandler.sendMessage(msg);
            }
        }, 0, MinDelay, TimeUnit.MILLISECONDS);


    }

    public static final int Down_Increase = 101;
    public static final int Up_Decrease = 100;
    public static final int UD_Finished = 102;
    public static final int UPFinish_Delay = 200;
    public static final int MinDelay = 100;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Up_Decrease:
                    synchronized (this) {

                        if (Value > 0) {
                            Value--;
                            if (updownEventListener != null)
                                updownEventListener.onUpDownValueChange(CUpdown.this, Value);
                        }

                        //qDebug.qLog("decrease value now  is " + Value);
                    }
                    break;
                case Down_Increase:
                    synchronized (this) {

                        if (Value < MaxValue) {
                            Value++;
                            if (updownEventListener != null)
                                updownEventListener.onUpDownValueChange(CUpdown.this, Value);
                        }
                        //qDebug.qLog("decrease value now  is " + Value);
                    }
                    break;
                case UD_Finished: {
                    // qDebug.qLog("has finished now...............,so up");
//                    if (updownEventListener != null)
//                        updownEventListener.onUpDownChangeFinished(this);
                }
                break;

            }

        }
    };

    public interface UpdownEventListener {
        public void onUpDownValueChange(CUpdown sender, int mValue);

    }

}
