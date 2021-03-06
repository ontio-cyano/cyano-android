
# Cyano 钱包

Cyano 钱包集成了dApi功能：

* 支持钱包内打开DAPP
* 支持扫描
* 支持APP唤醒钱包

钱包APK下载地址为 http://101.132.193.149/files/app-debug.apk

## App 唤醒 Cyano App

此文档为原生DApp唤醒Cyano App的流程设计和接口定义。

## [其他钱包使用](#其他钱包使用)

### 流程设计

#### 登录

数据内容仿造[Cyano二维码 Login](https://github.com/ontio-cyano/CEPs/blob/master/CEPS/CEP1.mediawiki#Login-2)
```
{
	"action": "login",
	"id": "10ba038e-48da-487b-96e8-8d3b99b6d18a",
	"version": "v1.0.0",
	"params": {
		"type": "ontid or account",
		"dappName": "dapp Name",
		"dappIcon": "dapp Icon",
		"message": "helloworld",
		"expire": 1546415363,
		"callback": "http://127.0.0.1:80/login/callback"
	}
}
```

1. 判断本地是否安装Cyano App，例如:
```
 public static boolean checkInstallCynoApp(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName.toLowerCase(Locale.ENGLISH);
                if (pn.equals("com.github.ont.cyanowallet")) {
                    return true;
                }
            }
        }
        return false;
    }
```


2. 拼接传递内容，启动，例如：
```
    String data = "{\"action\":\"login\",\"id\":\"10ba038e-48da-487b-96e8-8d3b99b6d18a\",\"version\":\"v1.0.0\",\"params\":{\"type\":\"ontid or account\",\"dappName\":\"dapp Name\",\"dappIcon\":\"dapp Icon\",\"message\":\"helloworld\",\"expire\":1546415363,\"callback\":\"http://127.0.0.1:80/login/callback\"}}";

    String sendData = Base64.encodeToString(Uri.encode(data).getBytes(), Base64.NO_WRAP);
    Intent intent = new Intent("android.intent.action.VIEW");
    intent.setData(Uri.parse("ontprovider://ont.io?param=" + sendData ));
    intent.addCategory("android.intent.category.DEFAULT");
    startActivity(intent);
```

#### 调用合约

数据内容仿造[Cyano二维码 Invoke](https://github.com/ontio-cyano/CEPs/blob/master/CEPS/CEP1.mediawiki#Invoke_a_Smart_Contract-2)
```
{
	"action": "invoke",
	"version": "v1.0.0",
	"id": "10ba038e-48da-487b-96e8-8d3b99b6d18a",
	"params": {
		"login": true,
		"qrcodeUrl": "http://101.132.193.149:4027/qrcode/AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ",
		"message": "will pay 1 ONT in this transaction",
		"callback": "http://101.132.193.149:4027/invoke/callback"
	}
}
```
1. 检查是否安装，参考[登录](#登录)
2. 将交易内容放在qrcodeUrl链接中，参考[Cyano二维码](https://github.com/ontio-cyano/CEPs/blob/master/CEPS/CEP1.mediawiki#Invoke_a_Smart_Contract-2)
3. 拼接传递内容，启动交易,例如：
```
    String data="{\"action\":\"invoke\",\"version\":\"v1.0.0\",\"id\":\"10ba038e-48da-487b-96e8-8d3b99b6d18a\",\"params\":{\"login\":true,\"qrcodeUrl\":\"http://101.132.193.149:4027/qrcode/AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ\",\"message\":\"will pay 1 ONT in this transaction\",\"callback\":\"http://101.132.193.149:4027/invoke/callback\"}}";


    String sendData = Base64.encodeToString(Uri.encode(data).getBytes(), Base64.NO_WRAP);
    Intent intent = new Intent("android.intent.action.VIEW");
    intent.setData(Uri.parse("ontprovider://ont.io?param=" + sendData ));
    intent.addCategory("android.intent.category.DEFAULT");
    startActivity(intent);
```

### 其他钱包使用
如果你的钱包也想支持唤醒功能，可以按照以下流程:

+ 注册接收页面
```text
  <activity
            android:name=".wake.WakeInvokeActivity"
            android:launchMode="singleTop">
            <!--注册scheme-->
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <!--BROWSABLE指定该Activity能被浏览器安全调用-->
                <!--<category android:name="android.intent.category.BROWSABLE"/>-->
                <!--声明自定义scheme，类似于http, https-->
                <data
                   android:host="ont.io"
                   android:scheme="ontprovider" />
            </intent-filter>
   </activity>
```

+ 根据action跳转到不同页面，[参考](https://github.com/ontio-cyano/cyano-android/blob/master/app/src/main/java/com/github/ont/cyanowallet/wake/WakeInvokeActivity.java)
