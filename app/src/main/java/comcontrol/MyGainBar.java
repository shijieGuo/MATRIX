package comcontrol;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import Datas.DStatic;
import Datas.XData;
import Events.MatrixRecall;
import cn.com.williamxia.matrixa8dante.MainActivity;
import cn.com.williamxia.wipack.socket.TCPClient;
import cn.com.williamxia.wipack.utils.IpManager;

import static Datas.XData.gInstance;

public class MyGainBar extends View {
    private int iTag;
    private int progress;
    private int bgWidth=0,bgHeight=0;
    private String text;
    private Rect barRect;
    private Rect textRect;
    private Paint barpaint;
    private Paint textpain;
    private Context context;
    private float downX;
    private ViewGroup parent;

    public MyGainBar(Context context) {
        this(context, null);
        this.context=context;
        init();
    }

    public MyGainBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.context=context;
        init();
    }

    public MyGainBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }
    public int getiTag() {
        return iTag;
    }

    public void setiTag(int iTag) {
        this.iTag = iTag;
    }

    public void setProgress(final int progress) {
        this.progress = progress;
        if(bgHeight==0&&bgWidth==0){
            post(new Runnable() {
                @Override
                public void run() {
                    bgHeight=getHeight();
                    bgWidth=getWidth();
                    barRect.top=0;
                    barRect.bottom=bgHeight;
                    barRect.left=0;
                    barRect.right=progress*bgWidth/160;
                    text=getText(progress);
                    textpain.setTextSize(bgHeight/4);//setfontsize
                    textpain.getTextBounds(text,0,text.length(),textRect);
                    invalidate();
                }
            });
        }
        else {
            barRect.right=progress*bgWidth/160;
            text=getText(progress);
            textpain.getTextBounds(text,0,text.length(),textRect);
            invalidate();
        }
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
    }
    private String getText(int pro){
        String text;
        if(pro==0){
            text="Mute";
        }
        else {
            Double gain=new Double( pro-160) /2;
            text=String.format("%.1f", gain)+"db";
        }
        return text;
    }

    private void init() {
        barRect=new Rect();
        barpaint = new Paint();
        textRect=new Rect();
        textpain = new Paint();
        barRect.top=0;
        barRect.bottom=bgHeight;
        barRect.left=0;
        barRect.right=progress*bgWidth/160;
        barpaint.setColor(Color.GREEN);
        barpaint.setStyle(Paint.Style.FILL);
        text=getText(progress);
        textpain.setColor(Color.WHITE);
        textpain.setTextSize(bgHeight/2);
        textpain.getTextBounds(text,0,text.length(),textRect);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            bgWidth = widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            bgHeight = heightSize;
        }


        setMeasuredDimension((int) (bgWidth + 0.5),
                (int) (bgHeight + 0.5));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(barRect,barpaint);
        canvas.drawText(text, getWidth() / 2 - textRect.width() / 2, (float) (getHeight()*0.7), textpain);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                parent.requestDisallowInterceptTouchEvent(true);
                downX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                downX=event.getX();
                int Gain= (int) (downX/(float) bgWidth*160);
                if(Gain<0)Gain=0;
                if(Gain>160)Gain=160;
//                XData.gInstance().m_matrixAryGain[XData.gInstance().mRoutChanel][getiTag()]=Gain;
                if ((DStatic.Model&&TCPClient.getInstance().isConnected())||(!DStatic.Model&& !MainActivity.cuurentIp.isEmpty())) {
//                    setProgress(Gain);
                    gInstance().sendCMD_matrixWithChanel(gInstance().mRoutChanel,getiTag(), Gain,IpManager.getInstance().getSelDevID());
                } else {
//                    setProgress(Gain);
//                    Log.d("refreshGain","refresh");
                    EventBus.getDefault().post(new MatrixRecall(78, XData.gInstance().mRoutChanel));
                }
                break;
            case MotionEvent.ACTION_UP:
                parent.requestDisallowInterceptTouchEvent(false);
                break;
        }

        return true;
    }




    public void setParent(ViewGroup parent){
        this.parent = parent;
    }

}
