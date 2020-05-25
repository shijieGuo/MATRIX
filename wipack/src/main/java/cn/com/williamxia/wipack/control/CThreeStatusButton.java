package cn.com.williamxia.wipack.control;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import cn.com.williamxia.wipack.R;


/**
 * Created by williamXia on 2017/7/31.
 */

public class CThreeStatusButton extends CButton implements View.OnClickListener {

    public interface ThreeStatusChange {
        public void onThreeStatusChange(Object sender, int statusindex);

    }

    public static final int Status_Peak = 0;
    public static final int Status_Low = 1;
    public static final int Status_High = 2;

    private int mParentID = 0;

    public void setmParentID(int sID) {
        mParentID = sID;
    }

    public int getmParentID() {
        return mParentID;
    }


    private int mStatus;

    private ThreeStatusChange onThreeStatusChange;

    public void setOnThreeStatusChange(ThreeStatusChange threeStatusChange) {
        onThreeStatusChange = threeStatusChange;

    }


    public void setmStatus(int status) {
        mStatus = status;
        refreshButton();
    }

    public int getmStatus() {
        return mStatus;
    }

    private Drawable mPic_Peak;
    private Drawable mPic_Low;
    private Drawable mPic_High;

    public void setmPic_Peak(Drawable dwb) {
        mPic_Peak = dwb;
        refreshButton();
    }

    public void setmPic_Low(Drawable dwb) {
        mPic_Low = dwb;
        refreshButton();
    }

    public void setmPic_High(Drawable dwb) {
        mPic_High = dwb;
        refreshButton();
    }

    public void refreshButton() {
        Drawable acDraw = null;
        switch (mStatus) {
            case Status_Peak:
                acDraw = mPic_Peak;
                break;
            case Status_Low:
                acDraw = mPic_Low;
                break;
            case Status_High:
                acDraw = mPic_High;
                break;
            default:
                break;
        }
        this.setBackground(acDraw);

    }

    public CThreeStatusButton(Context context) {
        this(context, null);
    }


    public CThreeStatusButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    private int tmpStatus = 0;

    @Override
    public void onClick(View view) {
        tmpStatus = mStatus;
        tmpStatus++;
        //  mStatus++;

        if (tmpStatus == 3) {
            tmpStatus = 0;
        }
        if (onThreeStatusChange != null) {
            onThreeStatusChange.onThreeStatusChange(this, tmpStatus);
        }

        //  refreshButton();
    }

    public void initParameter() {
        setOnClickListener(this);
    }

    public CThreeStatusButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParameter();
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CThreeStatusButton, defStyleAttr, 0);
        int n = array.getIndexCount();
        try {


            for (int i = 0; i < n; i++) {
                int index = array.getIndex(i);

                if (index == R.styleable.CThreeStatusButton_Image_Peak)
                    mPic_Peak = array.getDrawable(index);

                else if (index == R.styleable.CThreeStatusButton_Image_Low)
                    mPic_Low = array.getDrawable(index);

                else if (index == R.styleable.CThreeStatusButton_Image_High)
                    mPic_High = array.getDrawable(index);

                else if (index == R.styleable.CThreeStatusButton_Status_button)
                    mStatus = array.getInt(index, 0);

            }
            //  qDebug.qLog("Read mstatus" + mStatus);


        } finally {
            array.recycle();
            refreshButton();
        }

    }


}
