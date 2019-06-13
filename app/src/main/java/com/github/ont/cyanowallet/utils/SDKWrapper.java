package com.github.ont.cyanowallet.utils;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.github.ont.cyanowallet.network.net.BaseRequest;
import com.github.ont.cyanowallet.network.net.Result;
import com.github.ont.cyanowallet.request.ScanGetTransactionReq;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.core.DataSignature;
import com.github.ontio.core.ontid.Attribute;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.MnemonicCode;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.smartcontract.nativevm.Ong;
import com.github.ontio.smartcontract.nativevm.Ont;
import com.github.ontio.smartcontract.neovm.Oep4;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SDKWrapper {
    private static final String TAG = "SDKWrapper";
    private static final long GAS_LIMIT = 20000;
    private static final long GAS_PRICE = 500;
    private static OntSdk ontSdk = OntSdk.getInstance();


    public static void initOntSDK(final SDKCallback callback, final String tag, final String restUrl, final SharedPreferences path) {
        try {
            ontSdk.setRestful(restUrl);
            ontSdk.setDefaultConnect(ontSdk.getRestful());
            ontSdk.openWalletFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SDKException e) {
            e.printStackTrace();
        }
    }

    public static void createIdentity(final SDKCallback callback, final String tag, final String password, final String walletPwd) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                Identity identity = ontSdk.getWalletMgr().createIdentity(password);
                Account account = ontSdk.getWalletMgr().getWallet().getAccount(SPWrapper.getDefaultAddress());
                com.github.ontio.account.Account account1 = OntSdk.getInstance().getWalletMgr().getAccount(account.address, walletPwd, account.getSalt());

                ontSdk.nativevm().ontId().sendRegister(identity, password, account1, GAS_LIMIT, GAS_PRICE);
                Transaction transaction = ontSdk.nativevm().ontId().makeRegister(identity.ontid, password, identity.controls.get(0).getSalt(), "ATGJSGzm2poCB8N44BgrAccJcZ64MFf187", GAS_LIMIT, GAS_PRICE);
                ontSdk.signTx(transaction, identity.ontid, password, identity.controls.get(0).getSalt());
                String s = transaction.toHexString();
                ontSdk.getWalletMgr().writeWallet();
                emitter.onNext(identity.ontid);
                emitter.onComplete();
            }

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String res) {
                callback.onSDKSuccess(tag, res);
            }

            @Override
            public void onError(Throwable e) {
//                try {
//                    SharedPreferences sp = OCApplication.getMyApplicationContext().getSharedPreferences(Constant.WALLET_FILE, Context.MODE_PRIVATE);
//                    OntSdk.getInstance().openWalletFile(sp);
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void importIdentity(final SDKCallback callback, final String tag, final String key, final String password) {
        final OntSdk ontSdk = OntSdk.getInstance();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Identity identity;
                if (key.length() == 52) {
                    //wif
                    byte[] bytes = com.github.ontio.account.Account.getPrivateKeyFromWIF(key);
                    identity = ontSdk.getWalletMgr().createIdentityFromPriKey(password, Helper.toHexString(bytes));
                } else {
                    identity = ontSdk.getWalletMgr().createIdentityFromPriKey(password, key);
                }
                String s = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
                if (TextUtils.isEmpty(s)) {
                    emitter.onError(new Throwable(""));
                }
                ontSdk.getWalletMgr().writeWallet();
                emitter.onNext(identity.ontid);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String res) {


                callback.onSDKSuccess(tag, res);
            }

            @Override
            public void onError(Throwable e) {
//                try {
//                    SharedPreferences sp = OCApplication.getMyApplicationContext().getSharedPreferences(Constant.WALLET_FILE, Context.MODE_PRIVATE);
//                    OntSdk.getInstance().openWalletFile(sp);
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void createWallet(final SDKCallback callback, final String tag, final String password) {
        Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String mnemonicCodes = MnemonicCode.generateMnemonicCodesStr();
                byte[] prikeyFromMnemonicCodesStr = MnemonicCode.getPrikeyFromMnemonicCodesStrBip44(mnemonicCodes);
                String hexString = Helper.toHexString(prikeyFromMnemonicCodesStr);
                Account account = ontSdk.getWalletMgr().createAccountFromPriKey(password, hexString);
                String encryptedMnemonicCodesStr = MnemonicCode.encryptMnemonicCodesStr(mnemonicCodes, password, account.address);
                ontSdk.getWalletMgr().writeWallet();
                emitter.onNext(account.address);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String key) {
                callback.onSDKSuccess(tag, key);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void importWallet(final SDKCallback callback, final String tag, final String key, final String password) {
        Observable.create(new ObservableOnSubscribe<String>() {
            final OntSdk ontSdk = OntSdk.getInstance();

            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Account account;
                if (key.length() == 52) {
                    //wif
                    byte[] bytes = com.github.ontio.account.Account.getPrivateKeyFromWIF(key);
                    account = ontSdk.getWalletMgr().createAccountFromPriKey(password, Helper.toHexString(bytes));
                } else {
                    account = ontSdk.getWalletMgr().createAccountFromPriKey(password, key);
                }
                ontSdk.getWalletMgr().writeWallet();
                emitter.onNext(account.address);
                emitter.onComplete();

            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String key) {
                callback.onSDKSuccess(tag, key);
            }

            @Override
            public void onError(Throwable e) {

                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void getSendAddress(final SDKCallback callback, final String tag, final String data, final String password, final String address) {
        Observable.create(new ObservableOnSubscribe<ArrayList<String>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<String>> emitter) throws Exception {
                OntSdk ontSdk = OntSdk.getInstance();
                String invokeData = data.replaceAll("%address", SPWrapper.getDefaultAddress());
                Transaction[] transactions = ontSdk.makeTransactionByJson(invokeData);
                Transaction transaction = transactions[0];
                if (transaction.payer.equals(new Address())) {
                    transaction.payer = Address.decodeBase58(address);
                }
                Account account = ontSdk.getWalletMgr().getWallet().getAccount(address);
                com.github.ontio.account.Account account1 = OntSdk.getInstance().getWalletMgr().getAccount(account.address, password, account.getSalt());
                ontSdk.signTx(transaction, new com.github.ontio.account.Account[][]{{account1}});
//                ontSdk.setRestful("http://139.219.136.147:20334");
//                ontSdk.setDefaultConnect(ontSdk.getRestful());
                Object o = ontSdk.getConnect().sendRawTransactionPreExec(transaction.toHexString());
                ArrayList<String> result = new ArrayList<>();
                result.add(JSON.toJSONString(o));
                result.add(transaction.toHexString());
                emitter.onNext(result);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ArrayList<String>>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(ArrayList<String> s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void getGameLogin(final SDKCallback callback, final String tag, final String password, final String data) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String address = SPWrapper.getDefaultAddress();
                Account account = OntSdk.getInstance().getWalletMgr().getWallet().getAccount(address);
                com.github.ontio.account.Account account1 = OntSdk.getInstance().getWalletMgr().getAccount(account.address, password, account.getSalt());
                DataSignature sign1 = new DataSignature(OntSdk.getInstance().defaultSignScheme, account1, data.getBytes());
                String signature = Helper.toHexString(sign1.signature());
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("publickey", Helper.toHexString(account1.serializePublicKey()));
                jsonObject1.put("type", "account");
                jsonObject1.put("user", address);
                jsonObject1.put("message", data);
                jsonObject1.put("signature", signature);
                emitter.onNext(jsonObject1.toString());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void transfer(final SDKCallback callback, final String tag, final String sendAddress, final String receiveAddress, final String password, final long amount, final String type) {
        Observable.create(new ObservableOnSubscribe<String>() {
            final OntSdk ontSdk = OntSdk.getInstance();

            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Transaction tx = null;
                Account sendAccount = ontSdk.getWalletMgr().getWallet().getAccount(sendAddress);
                byte[] salt = sendAccount.getSalt();
                if (type.equalsIgnoreCase(Constant.ONT)) {
                    Ont ontAssetTx = ontSdk.nativevm().ont();
                    Transaction transaction = ontAssetTx.makeTransfer(sendAddress, receiveAddress, amount, sendAddress, GAS_LIMIT, GAS_PRICE);
                    tx = ontSdk.signTx(transaction, sendAddress, password, salt);
                } else if (type.equalsIgnoreCase(Constant.ONG)) {
                    Ong ong = ontSdk.nativevm().ong();
                    Transaction transaction = ong.makeTransfer(sendAddress, receiveAddress, amount, sendAddress, GAS_LIMIT, GAS_PRICE);
                    tx = ontSdk.signTx(transaction, sendAddress, password, salt);
                } else {
                    throw new Exception("not valid asset");
                }
                ontSdk.getConnect().sendRawTransaction(tx);
                emitter.onNext(tx.hash().toHexString());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String key) {
                callback.onSDKSuccess(tag, key);
            }

            @Override
            public void onError(Throwable e) {

                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void sendTransactionHex(final SDKCallback callback, final String tag, final String data) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Transaction transaction = Transaction.deserializeFrom(Helper.hexToBytes(data));
                boolean b = OntSdk.getInstance().getConnect().sendRawTransaction(transaction);
                Log.i(TAG, "subscribe: "+transaction.hash().toString());
//                Object smartCodeEvent = OntSdk.getInstance().getConnect().getSmartCodeEvent(transaction.hash().toString());
                if (b) {
                    emitter.onNext(transaction.hash().toString());
                    emitter.onComplete();
                } else {
                    emitter.onError(new Throwable(""));
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void verifyTX(final SDKCallback callback, final String tag, final String url) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                ScanGetTransactionReq scanGetTransactionReq = new ScanGetTransactionReq(url);
                scanGetTransactionReq.setOnResultListener(new BaseRequest.ResultListener() {
                    @Override
                    public void onResult(Result result) {
                        emitter.onNext((String) result.info);
                    }

                    @Override
                    public void onResultFail(Result error) {
                        emitter.onError(new Throwable(""));
                    }
                });
                scanGetTransactionReq.excute();
            }
        }).flatMap(new Function<String, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(final String s) throws Exception {
                return Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                        final OntSdk ontSdk = OntSdk.getInstance();
                        Transaction[] transactions = ontSdk.makeTransactionByJson(s);
                        Transaction transaction = transactions[0];
//                        Transaction transaction = Transaction.deserializeFrom(Helper.hexToBytes(s));
//                        boolean b = ontSdk.verifyTransaction(transaction);
                        if (transaction.payer.equals(new Address())) {
                            emitter.onNext("");
                            emitter.onComplete();
                            return;
                        }
                        emitter.onNext(transaction.payer.toBase58());
                        emitter.onComplete();
                    }
                }).subscribeOn(Schedulers.io());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void scanLoginSign(final SDKCallback callback, final String tag, final String data, final String address, final String password, final String type) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                com.github.ontio.account.Account accountSign;
                if (TextUtils.equals(type, "ontid")) {
                    Identity identity = OntSdk.getInstance().getWalletMgr().getWallet().getIdentity(address);
                    accountSign = OntSdk.getInstance().getWalletMgr().getAccount(identity.ontid, password, identity.controls.get(0).getSalt());
                } else {
                    Account account = OntSdk.getInstance().getWalletMgr().getWallet().getAccount(address);
                    accountSign = OntSdk.getInstance().getWalletMgr().getAccount(account.address, password, account.getSalt());
                }
//                DataSignature sign1 = new DataSignature(OntSdk.getInstance().defaultSignScheme, accountSign, data.getBytes());
                byte[] sign = accountSign.generateSignature(data.getBytes(), SignatureScheme.SHA256WITHECDSA, null);
                String publicKey = Helper.toHexString(accountSign.serializePublicKey());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", type);
                jsonObject.put("user", address);
                jsonObject.put("message", data);
                jsonObject.put("publickey", publicKey);
                jsonObject.put("signature", Helper.toHexString(sign));
                emitter.onNext(jsonObject.toString());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void scanAddSign(final SDKCallback callback, final String tag, final String qrcodeUrl, final String address, final String password) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                ScanGetTransactionReq scanGetTransactionReq = new ScanGetTransactionReq(qrcodeUrl);
                scanGetTransactionReq.setOnResultListener(new BaseRequest.ResultListener() {
                    @Override
                    public void onResult(Result result) {
                        emitter.onNext((String) result.info);
                    }

                    @Override
                    public void onResultFail(Result error) {
                        emitter.onError(new Throwable(""));
                    }
                });
                scanGetTransactionReq.excute();

            }
        }).flatMap(new Function<String, ObservableSource<ArrayList<String>>>() {
            @Override
            public ObservableSource<ArrayList<String>> apply(final String s) throws Exception {
                return Observable.create(new ObservableOnSubscribe<ArrayList<String>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ArrayList<String>> emitter) throws Exception {
                        OntSdk instance = OntSdk.getInstance();
                        Transaction[] transactions = instance.makeTransactionByJson(s);
                        Transaction transaction = transactions[0];
                        if (transaction.payer.equals(new Address())) {
                            transaction.payer = Address.decodeBase58(SPWrapper.getDefaultAddress());
                        }
                        Account account = instance.getWalletMgr().getWallet().getAccount(address);
                        com.github.ontio.account.Account account1 = OntSdk.getInstance().getWalletMgr().getAccount(account.address, password, account.getSalt());
                        instance.signTx(transaction, new com.github.ontio.account.Account[][]{{account1}});
                        Object o = ontSdk.getConnect().sendRawTransactionPreExec(transaction.toHexString());
                        ArrayList<String> result = new ArrayList<>();
                        result.add(JSON.toJSONString(o));
                        result.add(transaction.toHexString());
                        emitter.onNext(result);
                        emitter.onComplete();
                    }
                }).subscribeOn(Schedulers.io());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ArrayList<String>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ArrayList<String> s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void getWalletKey(final SDKCallback callback, final String tag, final String password, final String defaultAddress) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Account account = OntSdk.getInstance().getWalletMgr().getWallet().getAccount(defaultAddress);
                byte[] salt = account.getSalt();
                SignatureScheme scheme = OntSdk.getInstance().getWalletMgr().getSignatureScheme();
                String gcmDecodedPrivateKey = com.github.ontio.account.Account.getGcmDecodedPrivateKey(account.key, password, defaultAddress, salt, 4096, scheme);
                emitter.onNext(gcmDecodedPrivateKey);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() != null) {
                    callback.onSDKFail(tag, e.getMessage());
                } else {
                    callback.onSDKFail(tag, "");
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void getIdentityKey(final SDKCallback callback, final String tag, final String password) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String ontId = com.github.ont.connector.utils.SPWrapper.getDefaultOntId();
                Identity identity = OntSdk.getInstance().getWalletMgr().getWallet().getIdentity(ontId);
                byte[] salt = Base64.decode(identity.controls.get(0).salt, Base64.NO_WRAP);
                SignatureScheme scheme = OntSdk.getInstance().getWalletMgr().getSignatureScheme();
                ontSdk.getWalletMgr().getWalletFile().getScrypt().getN();
                String gcmDecodedPrivateKey = com.github.ontio.account.Account.getGcmDecodedPrivateKey(identity.controls.get(0).key, password, ontId.replace("did:ont:", ""), salt, 4096, scheme);
                emitter.onNext(gcmDecodedPrivateKey);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() != null) {
                    callback.onSDKFail(tag, e.getMessage());
                } else {
                    callback.onSDKFail(tag, "");
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void claimOng(final SDKCallback callback, final String tag

            , final String password, final Long amount) {
        Observable.create(new ObservableOnSubscribe<String>() {
            final OntSdk ontSdk = OntSdk.getInstance();

            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                Ong ong = ontSdk.nativevm().ong();
                String sendAddress = SPWrapper.getDefaultAddress();
                Account sendAccount = ontSdk.getWalletMgr().getWallet().getAccount(sendAddress);
                com.github.ontio.account.Account account1 = OntSdk.getInstance().getWalletMgr().getAccount(sendAccount.address, password, sendAccount.getSalt());
                String s = ong.claimOng(account1, sendAddress, amount, account1, GAS_LIMIT, GAS_PRICE);
                emitter.onNext(s);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String key) {
                callback.onSDKSuccess(tag, key);
            }

            @Override
            public void onError(Throwable e) {

                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
//                onError: {"Desc":"WalletManager Error,getAccountByAddress err","Error":58018}
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void handleInvokeRead(final SDKCallback callback, final String tag, final String data) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                OntSdk ontSdk = OntSdk.getInstance();
                Transaction[] transactions = ontSdk.makeTransactionByJson(data);
                Transaction transaction = transactions[0];
//                Account account = ontSdk.getWalletMgr().getWallet().getAccount(SPWrapper.getDefaultAddress());
//                com.github.ontio.account.Account account1 = OntSdk.getInstance().getWalletMgr().getAccount(account.address, password, account.getSalt());
//                ontSdk.signTx(transaction, new com.github.ontio.account.Account[][]{{account1}});
//                ontSdk.setRestful("http://139.219.136.147:20334");
//                ontSdk.setDefaultConnect(ontSdk.getRestful());
                if (transaction.payer.equals(new Address())) {
                    transaction.payer = Address.decodeBase58(SPWrapper.getDefaultAddress());
                }
                Object o = ontSdk.getConnect().sendRawTransactionPreExec(transaction.toHexString());
                emitter.onNext(o);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void getOep4Balance(final SDKCallback callback, final String tag, final String contractHash) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Oep4 oep4 = new Oep4(ontSdk);
                oep4.setContractAddress(contractHash);
                String balance = oep4.queryBalanceOf(SPWrapper.getDefaultAddress());
                emitter.onNext(balance);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void sendOep4Amount(final SDKCallback callback, final String tag, final String contractHash, final String password, final String receiveAddress, final long amount) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Oep4 oep4 = new Oep4(ontSdk);
                oep4.setContractAddress(contractHash);
                String defaultAddress = SPWrapper.getDefaultAddress();
                Account account = ontSdk.getWalletMgr().getWallet().getAccount(defaultAddress);
                com.github.ontio.account.Account signAccount = ontSdk.getWalletMgr().getAccount(defaultAddress, password, account.getSalt());
                String s = oep4.sendTransfer(signAccount, receiveAddress, amount, signAccount, GAS_LIMIT, GAS_PRICE);
                emitter.onNext(s);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void getDDO(final SDKCallback callback, final String tag) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String s = ontSdk.nativevm().ontId().sendGetDDO(com.github.ont.connector.utils.SPWrapper.getDefaultOntId());
                emitter.onNext(s);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void addDDOController(final SDKCallback callback, final String tag, final String publicKey, final String pwd, final String walletPwd) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String defaultOntId = com.github.ont.connector.utils.SPWrapper.getDefaultOntId();
                Identity identity = ontSdk.getWalletMgr().getWallet().getIdentity(defaultOntId);

                Account account = ontSdk.getWalletMgr().getWallet().getAccount(SPWrapper.getDefaultAddress());
                com.github.ontio.account.Account payer = ontSdk.getWalletMgr().getAccount(account.address, walletPwd, account.getSalt());

                String s = ontSdk.nativevm().ontId().sendAddPubKey(defaultOntId, pwd, identity.controls.get(0).getSalt(), publicKey, payer, GAS_LIMIT, GAS_PRICE);
                emitter.onNext(s);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void addDDORecover(final SDKCallback callback, final String tag, final String address, final String pwd, final String walletPwd) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String defaultOntId = com.github.ont.connector.utils.SPWrapper.getDefaultOntId();
                Identity identity = ontSdk.getWalletMgr().getWallet().getIdentity(defaultOntId);

                Account account = ontSdk.getWalletMgr().getWallet().getAccount(SPWrapper.getDefaultAddress());
                com.github.ontio.account.Account payer = ontSdk.getWalletMgr().getAccount(account.address, walletPwd, account.getSalt());

                String s = ontSdk.nativevm().ontId().sendAddRecovery(defaultOntId, pwd, identity.controls.get(0).getSalt(), address, payer, GAS_LIMIT, GAS_PRICE);
                emitter.onNext(s);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void updateDDORecover(final SDKCallback callback, final String tag, final String address, final String oldAddress, final String pwd, final String walletPwd) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                String defaultOntId = com.github.ont.connector.utils.SPWrapper.getDefaultOntId();
                Identity identity = ontSdk.getWalletMgr().getWallet().getIdentity(defaultOntId);

                Account account = ontSdk.getWalletMgr().getWallet().getAccount(SPWrapper.getDefaultAddress());
                com.github.ontio.account.Account payer = ontSdk.getWalletMgr().getAccount(account.address, walletPwd, account.getSalt());
                String s = ontSdk.nativevm().ontId().sendChangeRecovery(defaultOntId, address, oldAddress, pwd, identity.controls.get(0).getSalt(), payer, GAS_LIMIT, GAS_PRICE);
                emitter.onNext(s);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void updateDDOAttr(final SDKCallback callback, final String tag, final String ontidPwd, final String walletPwd, final String key, final String type, final String value) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Account account = ontSdk.getWalletMgr().getWallet().getAccount(SPWrapper.getDefaultAddress());
                com.github.ontio.account.Account accountPay = ontSdk.getWalletMgr().getAccount(account.address, walletPwd, account.getSalt());

                Attribute attribute = new Attribute(key.getBytes(), type.getBytes(), value.getBytes());
                Identity identity = ontSdk.getWalletMgr().getWallet().getIdentity(com.github.ont.connector.utils.SPWrapper.getDefaultOntId());

                String result = ontSdk.nativevm().ontId().sendAddAttributes(com.github.ont.connector.utils.SPWrapper.getDefaultOntId(), ontidPwd, identity.controls.get(0).getSalt(), new Attribute[]{attribute}, accountPay, GAS_LIMIT, GAS_PRICE);
                emitter.onNext(result);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                callback.onSDKSuccess(tag, s);
            }

            @Override
            public void onError(Throwable e) {
                if (e.getMessage() == null) {
                    callback.onSDKFail(tag, "");
                } else {
                    callback.onSDKFail(tag, e.getMessage());
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
