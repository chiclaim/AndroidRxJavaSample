## [READ English document](https://github.com/chiclaim/awesome_android_rxjava/blob/master/README.md)

## 在Android开发中的一些真实场景如何使用RxJava

该项目介绍了Rxjava一些常用的操作符和在实际场景使用的一些案例.
为了更加贴近真实项目, 项目中使用的网络请求和一些耗时任务都是请求本地服务器的. 服务器端使用Java web+Tomcat来实现的.
如果需要可以把服务器部署在你的本地机器上, 下载地址[下载地址](https://github.com/chiclaim/android_mvvm_server)

##Example Details

### 1. 基础部分

基础部分介绍了 Rxjava的一些常用操作符如:`create` 、`just` 、 `from`  、`map`、`flatMap`、`concatMap vs flatMap`


### 2. 每个HTTP请求都带token给服务器 [如果token过期则获取新token]  
一般请情况下,很多公司的提api接口, 请求的的时候都需要带有token, 该token在用户第一次启动app或者登陆的时候去获取. 以后的所有请求都需要带该Token
如果token过期, 服务器将返回401, 这时候就需要去请求获取token的接口, 如果获取成功接着在请求原来的接口. 
这个时候就两个回调的嵌套了. 实现起来比较费劲, 而且也不够优雅. 代码的可维护性变得很差.
可以使用 `onErrorResumeNext` 来处理这样的业务逻辑.



### 3. 搜索防抖[Search debounce]

现在几乎所有的App都有搜索功能 , 一般情况我们监听EditText控件,当值发生改变去请求搜索接口. 这将导致2个问题:

* 可能导致很多没有意义的请求,耗费用户流量(因为控件的值没更改一次立即就会去请求网络)

* 可能导致最终的结果不是用户想要的. 例如,用户一开始输入关键字'AB' 这个时候出现两个请求, 一个请求是A关键字, 一个请求是AB关键字.
表面上是'A'请求先发出去, 'AB'请求后发出去. 如果后发出去的'AB'请求先返回, 'A'请求后返回,那么'A'请求后的结果将会覆盖'AB'请求的结果.
从而导致搜索结果不正确.

很多文章说使用 `debounce` 操作可以解决这个问题.
但是, RxJava也不能完全解决这个问题, 可以使用 `debounce` 操作符 也只能从一定程度上减少这种情况的出现. 
比如: 一开始用户输入了AB两个字符, 在某个时间段内, 用户没有输入新的关键字, 将会发出搜索请求, 此时用户又输入新的关键字C, 
那就输入框就是ABC了, 在某个时间段内, 用户没有输入新的关键字, 将会发出搜索新的请求. 如果'ABC'的请求返回比'AB'的快, 那么AB请求的结果将会覆盖'ABC'请求的结果,从而导致不正确的结果


### 4. Observable is dependent on another Observable's result
例如, 在我的实际项目中上传图片到又拍云 需要先获取上传的url, 然后在上传图片. 所以上传图片这个任务需要依赖获取url这个任务


### 5. 检查数据缓存
比如获取列表数据, 如果数据库里有使用数据库的, 没有再去请求网络. 
当然也可以应用到其他场景, 比如有多个数据源, 数据源有优先级, 哪个有数据就使用哪个.

### 6. HTTP 请求重试

当请求网络的时候出现错误, 我们需要重试, 如果不停的重试也没有多大意义, 出现错误延迟多少秒然后重试.
所以使用RxJava可以设置最多重试次数和延迟的时间.

### 7. 如何在Android中正确的使用RxJava
如何避免在Activity/Fragment内存泄露.


## Reference documents

1. [danlew posts](http://blog.danlew.net/page/6/)
2. [jianshu posts](http://www.jianshu.com/p/33c548bce571)
3. [myexception posts](http://www.myexception.cn/android/1949467.html)
4. [stackoverflow posts](http://stackoverflow.com/questions/26201420/retrofit-with-rxjava-handling-network-exceptions-globally)