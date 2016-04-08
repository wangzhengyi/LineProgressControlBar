# LineProgressControlBar

LineProgressControlBar是一个带间断的水平进度条.

# DEMO

调节亮度:
![Brightness](https://github.com/wangzhengyi/LineProgressControlBar/raw/master/screenshots/device-2016-04-08-154552.png)

声音调节:
![Volume](https://github.com/wangzhengyi/LineProgressControlBar/raw/master/screenshots/device-2016-04-08-154642.png)

# Usage

## Gradle

```groovy
dependencies {
    compile project(':library')
}
```

## 使用示例

### 自定义属性

```xml
<resources>
    <attr name="first_color" format="color" />
    <attr name="second_color" format="color" />
    <attr name="total_width" format="dimension" />
    <attr name="divide_width" format="dimension" />
    <attr name="progress_height" format="dimension" />
    <attr name="dot_count" format="integer" />

    <declare-styleable name="LineProgressControlBar">
        <attr name="first_color" />
        <attr name="second_color" />
        <attr name="total_width" />
        <attr name="divide_width" />
        <attr name="progress_height" />
        <attr name="dot_count" />

    </declare-styleable>
</resources>
```

属性含义说明:

* first_color: 进度条背景颜色.
* second_color: 当前进度条颜色.
* total_width: 进度条总长度(单位px).
* divide_width: 两个进度块之间的间隔大小(单位px).
* progress_height: 进度条高度.
* dot_count: 进度条中总的进度块数目.

### 示例布局

```xml
<genius.com.wzy.linecontrolbar.LineProgressControlBar
    android:id="@+id/id_line_progress_control_bar"
    android:layout_width="match_parent"
    android:layout_height="10px"
    android:layout_marginTop="30px"
    custom:divide_width="2px"
    custom:dot_count="3"
    custom:first_color="#3e3e3e"
    custom:progress_height="10px"
    custom:second_color="#fd592a"
    custom:total_width="184px" />
```

## 示例初始化代码

```java
mLineProgressControlBar.setOnProgressChangeListener(this);
mLineProgressControlBar.setMaxAndMinProgress(mMaxVolumeValue, DEFAULT_MIN_VOLUME_VALUE);
mLineProgressControlBar.setCurrentProgress(mCurVolumeValue);
```

需要注意的是,setOnProgressChangeListener是为了在外部控制进度条的增大和减少.进度条本身不支持触摸响应.