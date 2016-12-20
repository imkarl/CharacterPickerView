## 仿iOS的PickerView控件

[![](https://jitpack.io/v/ImKarl/CharacterPickerView.svg)](https://jitpack.io/#ImKarl/CharacterPickerView)

可实现单项选择，并支持一二三级联动效果
高仿iOS的滚轮效果，实现字体大小自适应

## Preview
![Preview](./Screenshot/Screenshot_2015-11-13-154813.gif)

## Useage

    //选项选择器
    CharacterPickerWindow mOptions = new CharacterPickerWindow(activity);
    //初始化选项数据
    setPickerData(mOptions.getPickerView());
    //设置默认选中的三级项目
    mOptions.setSelectOptions(0, 0, 0);
    //监听确定选择按钮
    mOptions.setOnoptionsSelectListener(new OnOptionChangedListener() {
        @Override
        public void onOptionChanged(int options1, int option2, int options3) {
            // TODO 处理选择结果
        }
    });
    mOptions.showAtLocation(v, Gravity.BOTTOM, 0, 0);

## How to

- Step 1. 把 JitPack repository 添加到build.gradle文件中 repositories的末尾:
```
repositories {
    maven { url "https://jitpack.io" }
}
```
- Step 2. 在你的app build.gradle 的 dependencies 中添加依赖
```
dependencies {
	compile 'com.github.imkarl:CharacterPickerView:v0.2.4'
}
```

## ChangeLog

#### 0.2.4
- 修复部分情况下选项显示宽度变小的bug
- 修复一二级同时滑动时，出现index=-1导致程序崩溃
- 修复三级同时滑动时，偶然出现的数组越界
- 增加LoopView.items的空数据判断

#### 0.2.0
- 升级工程编译工具版本
- 升级3D滚轮控件
- 完善字体大小自适应
- 微调选中项改变监听器

#### Thanks
- [Android-PickerView](https://github.com/saiwu-bigkoo/Android-PickerView) 一二三级联动选择器
- [androidWheelView](https://github.com/weidongjian/androidWheelView/) 仿iOS滚轮控件
