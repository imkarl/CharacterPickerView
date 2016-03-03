# CharacterPickerView
可实现三级联动的选择器，高仿iOS的滚轮控件，字体大小自适应

### 仿iOS的PickerView控件
可实现单项选择，并支持一二三级联动效果
高仿iOS的滚轮效果，实现字体大小自适应

### Demo

    //选项选择器
    CharacterPickerWindow mOptions = new CharacterPickerWindow(activity);
    //初始化选项数据
    setPickerData(mOptions.getPickerView());
    //设置默认选中的三级项目
    mOptions.setSelectOptions(0, 0, 0);
    //监听确定选择按钮
    mOptions.setOnoptionsSelectListener(new CharacterPickerWindow.OnOptionsSelectListener() {
        @Override
        public void onOptionsSelect(int options1, int option2, int options3) {
            // TODO 处理选择结果
        }
    });
    mOptions.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        

### 效果图
![效果图](./Screenshot/Screenshot_2015-11-13-154813.gif)

#### Thanks
- [Android-PickerView](https://github.com/saiwu-bigkoo/Android-PickerView) 一二三级联动选择器
- [androidWheelView](https://github.com/weidongjian/androidWheelView/) 仿iOS滚轮控件
