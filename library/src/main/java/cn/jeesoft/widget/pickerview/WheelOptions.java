package cn.jeesoft.widget.pickerview;

import android.view.View;

import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 0.1 king 2015-11
 * @version 0.2 imkarl 2017-9
 */
final class WheelOptions {
    private final CharacterPickerView view;
    private LoopView wvOption1;
    private LoopView wvOption2;
    private LoopView wvOption3;

    private List<String> mOptions1Items;
    private List<List<String>> mOptions2Items;
    private List<List<List<String>>> mOptions3Items;
    private OnOptionChangedListener mOnOptionChangedListener;

    private float maxTextSize;

    public View getView() {
        return view;
    }

    public WheelOptions(CharacterPickerView view) {
        super();
        this.view = view;
    }

    public void setOnOptionChangedListener(OnOptionChangedListener listener) {
        this.mOnOptionChangedListener = listener;
    }

    public void setPicker(ArrayList<String> optionsItems) {
        setPicker(optionsItems, null, null);
    }

    public void setPicker(List<String> options1Items,
                          List<List<String>> options2Items) {
        setPicker(options1Items, options2Items, null);
    }

    public void setPicker(List<String> options1Items,
                          List<List<String>> options2Items,
                          List<List<List<String>>> options3Items) {
        this.mOptions1Items = options1Items == null ? new ArrayList<String>() : options1Items;
        this.mOptions2Items = options2Items == null ? new ArrayList<List<String>>() : options2Items;
        this.mOptions3Items = options3Items == null ? new ArrayList<List<List<String>>>() : options3Items;
        // 选项1
        wvOption1 = view.findViewById(R.id.j_options1);
        wvOption1.setItems(mOptions1Items);// 设置显示数据
        //设置是否循环播放
        wvOption1.setNotLoop();

        //滚动监听
        wvOption1.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                if (index == -1) {
                    return;
                }
                if (mOptions2Items.isEmpty()) {
                    doItemChange();
                    return;
                }

                wvOption2.setItems(mOptions2Items.get(index));
                wvOption2.setCurrentPosition(0);
            }
        });

        // 选项2
        wvOption2 = view.findViewById(R.id.j_options2);
        if (!mOptions2Items.isEmpty()) {
            wvOption2.setItems(mOptions2Items.get(0));// 设置显示数据
            //设置是否循环播放
            wvOption2.setNotLoop();
            //滚动监听
            wvOption2.setListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int index) {
                    if (index == -1) {
                        return;
                    }
                    if (mOptions3Items.isEmpty()) {
                        doItemChange();
                        return;
                    }

                    if (wvOption1.getSelectedItem() < mOptions3Items.size()) {
                        List<List<String>> allItems3 = mOptions3Items.get(wvOption1.getSelectedItem());
                        if (index >= allItems3.size()) {
                            index = 0;
                        }
                        wvOption3.setItems(allItems3.get(index));
                        wvOption3.setCurrentPosition(0);
                    }
                }
            });
        }

        // 选项3
        wvOption3 = view.findViewById(R.id.j_options3);
        if (!mOptions3Items.isEmpty()) {
            wvOption3.setItems(mOptions3Items.get(0).get(0));// 设置显示数据
            wvOption3.setCurrentPosition(0);// 初始化时显示的数据
            //设置是否循环播放
            wvOption3.setNotLoop();
            //滚动监听
            wvOption3.setListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int index) {
                    doItemChange();
                }
            });
        }

        if (mOptions2Items.isEmpty()) {
            view.findViewById(R.id.j_layout2).setVisibility(View.GONE);
        }
        if (mOptions3Items.isEmpty()) {
            view.findViewById(R.id.j_layout3).setVisibility(View.GONE);
        }

        if (maxTextSize > 0) {
            setMaxTextSize(maxTextSize);
        }

        // 初始化时显示的数据
        setCurrentPositions(0, 0, 0);
    }

    /**
     * 选中项改变
     */
    private void doItemChange() {
        if (mOnOptionChangedListener != null) {
            int option1 = wvOption1.getSelectedItem();
            int option2 = wvOption2.getSelectedItem();
            int option3 = wvOption3.getSelectedItem();
            mOnOptionChangedListener.onOptionChanged(option1, option2, option3);
        }
    }

    /**
     * 设置是否循环滚动
     */
    public void setCyclic(boolean cyclic) {
        wvOption1.setLoop(cyclic);
        wvOption2.setLoop(cyclic);
        wvOption3.setLoop(cyclic);
    }

    @Deprecated
    public int[] getCurrentItems() {
        int[] currentItems = new int[3];
        currentItems[0] = wvOption1.getSelectedItem();
        currentItems[1] = wvOption2.getSelectedItem();
        currentItems[2] = wvOption3.getSelectedItem();
        return currentItems;
    }

    /**
     * 返回当前选中的结果对应的位置数组 因为支持三级联动效果，分三个级别索引，0，1，2
     */
    public int[] getCurrentPositions() {
        int[] currentItems = new int[3];
        currentItems[0] = wvOption1.getSelectedItem();
        currentItems[1] = wvOption2.getSelectedItem();
        currentItems[2] = wvOption3.getSelectedItem();
        return currentItems;
    }

    public void setCurrentPositions(int option1, int option2, int option3) {
        if (option1 < 0) {
            option1 = 0;
        }
        if (option2 < 0) {
            option2 = 0;
        }
        if (option3 < 0) {
            option3 = 0;
        }

        if (wvOption1.getSelectedItem() == -1) {
            wvOption1.setInitPosition(option1);
            wvOption2.setInitPosition(option2);
            wvOption3.setInitPosition(option3);
        } else {
            wvOption1.setCurrentPosition(option1);
            wvOption2.setCurrentPosition(option2);
            wvOption3.setCurrentPosition(option3);
        }
    }

    public void setMaxTextSize(float dpValue) {
        maxTextSize = dpValue;
        if (wvOption1 != null) {
            wvOption1.setMaxTextSize(dpValue);
            wvOption2.setMaxTextSize(dpValue);
            wvOption3.setMaxTextSize(dpValue);
        }
    }
}
