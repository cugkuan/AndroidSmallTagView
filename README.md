
# 1.概述
在Android中，关于TagView，有许多优秀的开源控件，如下面两个，star都非常高的。

https://github.com/whilu/AndroidTagView

https://github.com/2dxgujun/AndroidTagGroup

在一般的场景下这些开源的控件完全够用了。但是，在某些场景下，这些TagView 并不能满足我的需求。如在ListView 或者RecyclerView 的场景中，这些TagView显得异常笨重，损耗性能，造成页面卡顿，我需要一个轻量级的TagView用在ListView和RecyclerView的场景中，如下图：

 ![image](https://github.com/cugkuan/smallTagView/blob/master/pic/WechatIMG1.jpeg)

那么 SmallTagView 就是用来满足这些场景的。

SmallTagView 足够的轻，足够的小，核心的代码不过100行左右；SmallTagView专门对ListView和RecyclerView中View复用的情况进行了优化。

如果碰到上面的场景，需要使用Tag,那么还等什么呢，赶快使用SmallTagView吧。

# 2.SmallTagView 支持的属性

| 编号 | 属性名称 | 说明 |
| ------ | ------ | ------ |
| 1 | tagBackground | Tag的背景颜色 |
| 2 | tagRadius | Tag圆角半径 |
| 3 | textPaddingLeft | Tag文字左间距 |
| 4 | textPaddingRight |  |
| 5 | textPaddingTop |  |
| 6 | textPaddingBottom |  |
| 7 | tagHorizontalDivider | 水平方向上，Tag之间的间隔距离 |
| 8 | tagVerticalDivider | 竖直方向上，Tag之间的间隔距离 |
| 9 | maxTagNum | 显示Tag的最大个数 |
| 10 | android:textSize | Tag文字的大小 |
| 11 | android:textColor | Tag文字颜色 |
| 12 | android:maxLines | 显示最大行数 |
# 3.简单使用

在布局xml中这样使用。
```
<com.cugkuan.smalltag.SmallTagView
    android:layout_width="wrap_content"
    android:id="@+id/tag"
    app:tagBackground="#f2f2f2"
    app:textPaddingLeft="4dp"
    app:textPaddingRight="4dp"
    app:tagRadius="3dp"
    app:tagHorizontalDivider="2dp"
    app:tagVerticalDivider="2dp"
    android:textSize="11sp"
    android:textColor="#2eb1ff"
    android:paddingLeft="5dp"
    android:paddingTop="5dp"
    android:paddingBottom="5dp"
    android:background="#ccc"
    android:layout_height="wrap_content" />
    
 ```

Java代码

```
SmallTagView mSmallTagView = findViewById(R.id.tag);

List<String>  tags = new ArrayList<>();

tags.add("数学");
tags.add("这是一个很长很长很长很长很长很长很长的Tag");
tags.add("语文");
tags.add("化学");
tags.add("生物");
tags.add("化学");
tags.add("历史");
mSmallTagView.setTags(tags);
```

# 4.后记
因为SmallTagView 是针对ListView 和RecyclerView的使用场景而生的，因此，暂不支持Tag的点击等事件，后续根据需要添加更多的支持。
