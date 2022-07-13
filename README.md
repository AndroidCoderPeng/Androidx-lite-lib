# Androidx-lite-lib
Android依赖库，Android-library升级兼容到Androidx

[![](https://jitpack.io/v/AndroidCoderPeng/Androidx-lite-lib.svg)](https://jitpack.io/#AndroidCoderPeng/Androidx-lite-lib)

使用方式

1、在项目的build.gradle里面添加如下代码块：

```
allprojects {
    repositories {
        maven { url 'http://maven.aliyun.com/nexus/content/groups/public/' }
        //依赖库
        maven { url 'https://jitpack.io' }
        google()
    }
}
```

2、在APP的build.gradle中添加如下代码块：

```
dependencies {
    implementation fileTree(include: ['*.jar'], dir: 'libs')
    //基础依赖库
    implementation 'com.github.AndroidCoderPeng:Androidx-lite-lib:1.0.4'
}
```

3、最后点击Sync同步一下就可以用了，例如要跳转页面，一行代码解决：
```
ContextUtil.navigatePageTo(context, BluetoothActivity.class);
```

又或者要使用对话框
```
new AlertControlDialog.Builder()
    .setContext(this)
    .setTitle("这里是标题")
    .setMessage("这里是对话框内容")
    .setNegativeButton("取消")
    .setPositiveButton("确定")
    .setOnDialogButtonClickListener(new AlertControlDialog.OnDialogButtonClickListener() {
        @Override
        public void onConfirmClick() {

        }

        @Override
        public void onCancelClick() {

        }
    }).build().show();
```

其他的就不一一列举了，有用得到的可以自己试试~~~

详细功能架构图如下所示：
![框架功能图](https://github.com/AndroidCoderPeng/Androidx-lite-lib/blob/main/lite/src/main/assets/Androidx-lite-lib.png)
