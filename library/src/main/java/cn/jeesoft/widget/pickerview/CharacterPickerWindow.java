package cn.jeesoft.widget.pickerview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import java.util.List;

/**
 * @version 0.1 king 2015-11
 * @version 0.2 imkarl 2017-9
 */
public class CharacterPickerWindow extends PopupWindow implements View.OnClickListener {
    private final View rootView; // 总的布局
    private final View btnSubmit, btnCancel;
    private final CharacterPickerView pickerView;
    private OnOptionChangedListener optionsSelectListener;
    private static final String TAG_SUBMIT = "submit";
    private static final String TAG_CANCEL = "cancel";

    public CharacterPickerWindow(Context context) {
        super(context);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(dp2Px(280));
        this.setBackgroundDrawable(new BitmapDrawable());// 这样设置才能点击屏幕外dismiss窗口
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.j_timepopwindow_anim_style);

        LayoutInflater mLayoutInflater = LayoutInflater.from(context);
        rootView = mLayoutInflater.inflate(R.layout.j_picker_dialog, null);
        // -----确定和取消按钮
        btnSubmit = rootView.findViewById(R.id.j_btnSubmit);
        btnSubmit.setTag(TAG_SUBMIT);
        btnCancel = rootView.findViewById(R.id.j_btnCancel);
        btnCancel.setTag(TAG_CANCEL);
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        // ----转轮
        pickerView = (CharacterPickerView) rootView.findViewById(R.id.j_optionspicker);
        setContentView(rootView);
    }

    private static int dp2Px(float dp) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public CharacterPickerView getPickerView() {
        return pickerView;
    }

    public void setPicker(List<String> optionsItems) {
        pickerView.setPicker(optionsItems, null, null);
    }

    public void setPicker(List<String> options1Items,
                          List<List<String>> options2Items) {
        pickerView.setPicker(options1Items, options2Items, null);
    }

    public void setPicker(List<String> options1Items,
                          List<List<String>> options2Items,
                          List<List<List<String>>> options3Items) {
        pickerView.setPicker(options1Items, options2Items, options3Items);
    }

    /**
     * 设置选中的item位置
     */
    @Deprecated
    public void setSelectOptions(int option1) {
        pickerView.setCurrentItems(option1, 0, 0);
    }

    /**
     * 设置选中的item位置
     */
    @Deprecated
    public void setSelectOptions(int option1, int option2) {
        pickerView.setCurrentItems(option1, option2, 0);
    }

    /**
     * 设置选中的item位置
     */
    @Deprecated
    public void setSelectOptions(int option1, int option2, int option3) {
        pickerView.setCurrentItems(option1, option2, option3);
    }

    /**
     * 设置选中的item位置
     */
    public void setCurrentPositions(int option1) {
        pickerView.setCurrentPositions(option1, 0, 0);
    }

    /**
     * 设置选中的item位置
     */
    public void setCurrentPositions(int option1, int option2) {
        pickerView.setCurrentPositions(option1, option2, 0);
    }

    /**
     * 设置选中的item位置
     */
    public void setCurrentPositions(int option1, int option2, int option3) {
        pickerView.setCurrentPositions(option1, option2, option3);
    }

    /**
     * 设置是否循环滚动
     */
    public void setCyclic(boolean cyclic) {
        pickerView.setCyclic(cyclic);
    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        if (!tag.equals(TAG_CANCEL)) {
            if (optionsSelectListener != null) {
                int[] optionsCurrentItems = pickerView.getCurrentPositions();
                optionsSelectListener.onOptionChanged(optionsCurrentItems[0], optionsCurrentItems[1], optionsCurrentItems[2]);
            }
        }
        dismiss();
    }

    public void setOnoptionsSelectListener(
            OnOptionChangedListener optionsSelectListener) {
        this.optionsSelectListener = optionsSelectListener;
    }
}
