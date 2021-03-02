### **LiveDataBusX的使用**
**（1）基础配置**

在项目的buld.gradle中添加如下代码（jcenter中没有提交审核，所以需要添加这句，之后审核通过，这句话可以去掉）

    allprojects {
        repositories {
            google()
            jcenter()
            maven {
                url 'https://dl.bintray.com/codefarmerguan/LiveDataBusX'
            }
        }
    }

**（2）初始化操作**

在项目Application的onCreate方法中进行如下操作

    public class MyApp extends Application {
        @Override
        public void onCreate() {
            super.onCreate();
            LiveDataBusX.getInstance().init();
        }
    }

**（3）事件监听**

在需要接收事件的类中进行observe，和EventBus的register方法一样，不过不需要解注册，如下

    //无动态key
    LiveDataBusX.getInstance().observe(this);
    //有动态key
    LiveDataBusX.getInstance().observe(this,dynamicKey);

**observe方法**有两种：一种是**无动态key**，一种是**有动态key**。在某些业务场景下，key值可能会随某些属性而变化。

之后在类中写如下的方法来接收事件变化

    @Observe(threadMode = ThreadMode.MAIN,sticky = false,append = false,key = "test1")
    public void test1(Test1Bean test1Bean){

    }
**Observe**注解有四个属性：

**threadMode**：指定线程，目前有MAIN，BACKGROUND，ASYNC三个值，和EventBus中的一样、

**sticky**：是否具有粘性，和EventBus中的粘性相似。究其原理，其实和粘性没有任何关系，执行现象和EventBus的粘性相似，我这里为了好解释，也就使用了sticky这个值

**key**：静态key

**append**：静态key与动态key是否拼接。如果没有设置动态key，这里一定要设置false，否则会出现问题

如果设置了动态key，并且append为true，最后的key形态为  **静态key::动态key**

如果append为false，那么最后的key就是静态key本身。**使用者不需要关心这里的形态问题，这里只是做了解释**

**（4）事件发送**

    LiveDataBusX.getInstance()
            .post("test1",new Test1Bean());

    LiveDataBusX.getInstance()
            .post("test1","动态key",new Test1Bean());

**post**也有两个方法，与**observe**的两个方法相对应。

如果没有动态key的情况，使用两参数的方法**：第一个参数为静态key，第二个参数为事件对象**

如果有静态key的情况，使用三参数的方法：**第一个参数为静态key，第二个参数为动态key，第三个参数为事件对象**