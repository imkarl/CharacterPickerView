package cn.jeesoft.demo;

import android.app.Activity;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cn.jeesoft.widget.pickerview.CharacterPickerView;
import cn.jeesoft.widget.pickerview.CharacterPickerWindow;
import cn.jeesoft.widget.pickerview.OnOptionChangedListener;

/**
 * 地址选择器
 *
 * @version 0.1 king 2015-10
 * @version 0.2 imkarl 2017-9
 */
public class OptionsWindowHelper {

    private static List<String> options1Items = null;
    private static List<List<String>> options2Items = null;
    private static List<List<List<String>>> options3Items = null;

    public interface OnOptionsSelectListener {
        void onOptionsSelect(String province, String city, String area);
    }

    private OptionsWindowHelper() {
    }

    public static CharacterPickerWindow builder(Activity activity, final OnOptionsSelectListener listener) {
        //选项选择器
        CharacterPickerWindow mOptions = new CharacterPickerWindow(activity);
        //初始化选项数据
        setPickerData(mOptions.getPickerView());
        //设置默认选中的三级项目
        mOptions.setCurrentPositions(0, 0, 0);
        //监听确定选择按钮
        mOptions.setOnoptionsSelectListener(new OnOptionChangedListener() {
            @Override
            public void onOptionChanged(int option1, int option2, int option3) {
                if (listener != null) {
                    String province = options1Items.get(option1);
                    String city = options2Items.get(option1).get(option2);
                    String area = options3Items.get(option1).get(option2).get(option3);
                    listener.onOptionsSelect(province, city, area);
                }
            }
        });
        return mOptions;
    }

    /**
     * 初始化选项数据
     */
    public static void setPickerData(CharacterPickerView view) {
        if (options1Items == null) {
            options1Items = new ArrayList<>();
            options2Items = new ArrayList<>();
            options3Items = new ArrayList<>();

            final Map<String, Map<String, List<String>>> allCitys = ArrayDataDemo.getAll();
            for (Map.Entry<String, Map<String, List<String>>> entry1 : allCitys.entrySet()) {
                String key1 = entry1.getKey();
                Map<String, List<String>> value1 = entry1.getValue();

                options1Items.add(key1);

                List<String> options2Items01 = new ArrayList<>();
                List<List<String>> options3Items01 = new ArrayList<>();
                for (Map.Entry<String, List<String>> entry2 : value1.entrySet()) {
                    String key2 = entry2.getKey();
                    List<String> value2 = entry2.getValue();

                    options2Items01.add(key2);
                    Collections.sort(value2);
                    options3Items01.add(new ArrayList<>(value2));
                }
                Collections.sort(options2Items01);
                options2Items.add(options2Items01);
                options3Items.add(options3Items01);
            }
            Collections.sort(options1Items);
        }

        //三级联动效果
        view.setPicker(options1Items, options2Items, options3Items);

    }

    public static void setCurrentPositions(CharacterPickerWindow window, String province, String city, String area) {
        int option1 = indexOf(options1Items, province);
        int option2 = option1>=0 ? indexOf(options2Items.get(option1), city) : -1;
        int option3 = option2>=0 ? indexOf(options3Items.get(option1).get(option2), area) : -1;

        window.setCurrentPositions(option1, option2, option3);
    }

    private static int indexOf(List<String> data, String key) {
        if (data == null || data.isEmpty()) {
            return -1;
        }
        if (TextUtils.isEmpty(key)) {
            return -1;
        }
        return data.indexOf(key);
    }

}
