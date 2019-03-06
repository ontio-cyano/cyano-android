# Cyano Wallet

Cayno wallet implement dApi:

* support open dapp in wallet
* support scan qrcode
* support APP wakeup Cyano

Cyano download: http://101.132.193.149/files/app-debug.apk



## App wakeup Cyano

* login
* invoke smartcontract

## [Use of other wallets](#Use_of_other_wallets)

### Login

Request data the same to [Cyano scan qrcode Login](https://github.com/ontio-cyano/CEPs/blob/master/CEPS/CEP1.mediawiki#Login-2)

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

1. check if installed Cyano APP :
```
 public static boolean checkInstallCynoApp(Context context) {
        final PackageManager packageManager = context.getPackageManager();// get packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// get info 
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


2. make request message, start Activity：
```
    String data = "{\"action\":\"login\",\"id\":\"10ba038e-48da-487b-96e8-8d3b99b6d18a\",\"version\":\"v1.0.0\",\"params\":{\"type\":\"ontid or account\",\"dappName\":\"dapp Name\",\"dappIcon\":\"dapp Icon\",\"message\":\"helloworld\",\"expire\":1546415363,\"callback\":\"http://127.0.0.1:80/login/callback\"}}";



    Intent intent = new Intent("android.intent.action.VIEW");
    intent.setData(Uri.parse("ont://com.github.ont?data=" + data ));
    intent.addCategory("android.intent.category.DEFAULT");
    startActivity(intent);
```

### Incoke smartcontract

Request data the same to [Cyano scan qrcode Invoke](https://github.com/ontio-cyano/CEPs/blob/master/CEPS/CEP1.mediawiki#Invoke_a_Smart_Contract-2)
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
1. check if installed, reference [Login](#login)
2.  make request message, start Activity：
```
    String data="{\"action\":\"invoke\",\"version\":\"v1.0.0\",\"id\":\"10ba038e-48da-487b-96e8-8d3b99b6d18a\",\"params\":{\"login\":true,\"qrcodeUrl\":\"http://101.132.193.149:4027/qrcode/AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ\",\"message\":\"will pay 1 ONT in this transaction\",\"callback\":\"http://101.132.193.149:4027/invoke/callback\"}}";


    Intent intent = new Intent("android.intent.action.VIEW");
    intent.setData(Uri.parse("ont://com.github.ont?data=" + data ));
    intent.addCategory("android.intent.category.DEFAULT");
    startActivity(intent);
```



### Use_of_other_wallets
If your wallet also wants to support wake-up, follow the following procedure:

+ Registration Receive Activity
```text
  <activity
            android:name=".wake.WakeInvokeActivity"
            android:launchMode="singleTop">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <data
                    android:host="com.github.ont"
                    android:scheme="ont" />
            </intent-filter>
   </activity>
```

+ intent to different activity according to action，[Reference resources](https://github.com/ontio-cyano/cyano-android/blob/master/app/src/main/java/com/github/ont/cyanowallet/wake/WakeInvokeActivity.java)
