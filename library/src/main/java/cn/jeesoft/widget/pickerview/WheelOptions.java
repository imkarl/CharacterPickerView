package cn.jeesoft.widget.pickerview;

import android.view.View;

import com.weidongjian.meitu.wheelviewdemo.view.LoopView;
import com.weidongjian.meitu.wheelviewdemo.view.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 0.1 king 2015-11
 */
final class WheelOptions {
    private final CharacterPickerView view;
    private LoopView wv_option1;
    private LoopView wv_option2;
    private LoopView wv_option3;

    private List<String> mOptions1Items;
    private List<List<String>> mOptions2Items;
    private List<List<List<String>>> mOptions3Items;
    private OnOptionChangedListener mOnOptionChangedListener;

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
        wv_option1 = (LoopView) view.findViewById(R.id.j_options1);
        wv_option1.setItems(mOptions1Items);// 设置显示数据
        wv_option1.setCurrentItem(0);// 初始化时显示的数据
        //设置是否循环播放
        wv_option1.setNotLoop();

        //滚动监听
        wv_option1.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                if (index == -1) {
                    return;
                }
                if (mOptions2Items.isEmpty()) {
                    doItemChange();
                    return;
                }

                wv_option2.setItems(mOptions2Items.get(index));
                wv_option2.setCurrentItem(0);

                if (mOptions3Items.isEmpty()) {
                    doItemChange();
                    return;
                }

                wv_option3.setItems(mOptions3Items.get(index).get(0));
                wv_option3.setCurrentItem(0);
            }
        });

        // 选项2
        wv_option2 = (LoopView) view.findViewById(R.id.j_options2);
        if (!mOptions2Items.isEmpty()) {
            wv_option2.setItems(mOptions2Items.get(0));// 设置显示数据
            wv_option2.setCurrentItem(0);// 初始化时显示的数据
            //设置是否循环播放
            wv_option2.setNotLoop();
            //滚动监听
            wv_option2.setListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int index) {
                    if (index == -1) {
                        return;
                    }
                    if (mOptions3Items.isEmpty()) {
                        doItemChange();
                        return;
                    }

                    if (wv_option1.getSelectedItem() < mOptions3Items.size()) {
                        List<List<String>> allItems3 = mOptions3Items.get(wv_option1.getSelectedItem());
                        if (index >= allItems3.size()) {
                            index = 0;
                        }
                        wv_option3.setItems(allItems3.get(index));
                        wv_option3.setCurrentItem(0);
                    }
                }
            });
        }

        // 选项3
        wv_option3 = (LoopView) view.findViewById(R.id.j_options3);
        if (!mOptions3Items.isEmpty()) {
            wv_option3.setItems(mOptions3Items.get(0).get(0));// 设置显示数据
            wv_option3.setCurrentItem(0);// 初始化时显示的数据
            //设置是否循环播放
            wv_option3.setNotLoop();
            //滚动监听
            wv_option3.setListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int index) {
                    doItemChange();
                }
            });
        }

        if (mOptions2Items.isEmpty())
            view.findViewById(R.id.j_layout2).setVisibility(View.GONE);
        if (mOptions3Items.isEmpty())
            view.findViewById(R.id.j_layout3).setVisibility(View.GONE);

        setCurrentItems(0, 0, 0);
    }

    /**
     * 选中项改变
     */
    private void doItemChange() {
        if (mOnOptionChangedListener != null) {
            int option1 = wv_option1.getSelectedItem();
            int option2 = wv_option2.getSelectedItem();
            int option3 = wv_option3.getSelectedItem();
            mOnOptionChangedListener.onOptionChanged(option1, option2, option3);
        }
    }

    /**
     * 设置是否循环滚动
     *
     * @param cyclic
     */
    public void setCyclic(boolean cyclic) {
        wv_option1.setLoop(cyclic);
        wv_option2.setLoop(cyclic);
        wv_option3.setLoop(cyclic);
    }

    /**
     * 返回当前选中的结果对应的位置数组 因为支持三级联动效果，分三个级别索引，0，1，2
     *
     * @return
     */
    public int[] getCurrentItems() {
        int[] currentItems = new int[3];
        currentItems[0] = wv_option1.getSelectedItem();
        currentItems[1] = wv_option2.getSelectedItem();
        currentItems[2] = wv_option3.getSelectedItem();
        return currentItems;
    }

    public void setCurrentItems(int option1, int option2, int option3) {
        wv_option1.setCurrentItem(option1);
        wv_option2.setCurrentItem(option2);
        wv_option3.setCurrentItem(option3);
    }
}
