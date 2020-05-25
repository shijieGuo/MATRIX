package comcontrol;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import cn.com.williamxia.matrixa8dante.R;


public class CButton extends android.support.v7.widget.AppCompatButton {


    public static final int tEQ_freq = 0;
    public static final int tEQ_Qvalue = 1;
    public static final int tEQ_Gain = 2;
    public static final int tEQ_filter = 3;
    public static final int tHL_Filter = 4;

    //---------
    public static final int t_bypas = 5;
    public static final int t_fader = 6;
    public static final int t_thres = 7;
    public static final int t_ratio = 8;
    public static final int t_Attack = 9;
    public static final int t_Release = 10;
    public static final int t_comp_gain=11;


    //exposed items
    protected int iTag;
    protected int typeValue;//EQFreq,Qvalue,Gain
    protected int parentID;


    //region geter and setter define
    public int getiTag() {
        return iTag;
    }

    public int getTypeValue() {
        return typeValue;
    }

    public void setiTag(int iTag) {
        this.iTag = iTag;
    }

    public void setTypeValue(int typeValue) {
        this.typeValue = typeValue;
    }

    public int getParentID() {
        return parentID;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }
    //endregion


    public CButton(Context context) {
        this(context, null);
    }

    public CButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public CButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray resAry=null;
        try {
             resAry = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CButton, defStyleAttr, 0);
            int atrCount = resAry.getIndexCount();
            for (int i = 0; i < atrCount; i++) {
                int index = resAry.getIndex(i);
                switch (index) {
                    case R.styleable.CButton_iTag:
                        iTag = resAry.getInteger(index, 0);
                        break;
                    case R.styleable.CButton_typeValue:
                        typeValue = resAry.getInteger(index, 0);
                        break;
                    case R.styleable.CButton_parentID:
                        parentID = resAry.getInteger(index, 0);
                        break;
                }
            }
        } finally {
            resAry.recycle();
        }

    }


}
