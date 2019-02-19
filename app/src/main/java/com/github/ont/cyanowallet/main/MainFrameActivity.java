package com.github.ont.cyanowallet.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener;
import com.github.ont.cyano.Constant;
import com.github.ont.cyanowallet.R;
import com.github.ont.cyanowallet.base.BaseActivity;
import com.github.ont.cyanowallet.base.BaseFragment;
import com.github.ont.cyanowallet.game.GameFragment;
import com.github.ont.cyanowallet.mine.MineFragment;
import com.github.ont.cyanowallet.scan.activity.CaremaPermissionActivity;
import com.github.ont.cyanowallet.view.jptab.JPTabBar;
import com.github.ont.cyanowallet.view.jptab.anno.NorIcons;
import com.github.ont.cyanowallet.view.jptab.anno.SeleIcons;
import com.github.ont.cyanowallet.view.jptab.anno.Titles;
import com.github.ont.cyanowallet.wallet.WalletFragment;

import java.util.ArrayList;
import java.util.List;

public class MainFrameActivity extends BaseActivity {

//    @SeleIcons
//    private static final int[] SELE_ICONS = {R.drawable.tab_asset_selected, R.drawable.tab_id_selected, R.drawable.tab_game_select, R.drawable.tab_me_selected};
//
//    @NorIcons
//    private static final int[] NORMAL_ICONS = {R.drawable.tab_asset_un_selected, R.drawable.tab_id_un_selected, R.drawable.tab_game_unselect, R.drawable.tab_me_un_selected};
//
//    @Titles
//    private static final int[] TITLES = {R.string.title_wallet,R.string.title_ontid,R.string.title_app,R.string.title_mine,};

    @SeleIcons
    private static final int[] SELE_ICONS = {R.drawable.tab_asset_selected, R.drawable.tab_game_select, R.drawable.tab_me_selected};

    @NorIcons
    private static final int[] NORMAL_ICONS = {R.drawable.tab_asset_un_selected, R.drawable.tab_game_unselect, R.drawable.tab_me_un_selected};

    @Titles
    private static final int[] TITLES = {R.string.title_wallet, R.string.title_app, R.string.title_mine,};
    private List<Fragment> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_frame);
        initView();
        initVersion();
    }

    private void initVersion() {
        AllenVersionChecker.getInstance().requestVersion().setRequestUrl(Constant.CYANO_VERSION_URL).request(new RequestVersionListener() {
            @Nullable
            @Override
            public UIData onRequestVersionSuccess(String result) {
                //拿到服务器返回的数据，解析，拿到downloadUrl和一些其他的UI数据
//                        ...
                //如果是最新版本直接return null
                JSONObject json = (JSONObject) JSONObject.parse(result);
                int versionCode = json.getJSONObject("result").getIntValue("versionCode");
                try {
                    long longVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
                    if (versionCode > longVersionCode) {
                        UIData uiData = UIData.create().setDownloadUrl(Constant.CYANO_DOWNLOAD_URL);
                        uiData.setTitle("Update");
                        uiData.setContent("Version Update");
                        return uiData;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
                return null;
            }

            @Override
            public void onRequestVersionFailure(String message) {

            }
        }).executeMission(this);
    }


    private void initView() {
        JPTabBar mTabbar = findViewById(R.id.tabbar);
        ViewPager mPager = findViewById(R.id.view_pager);
        ImageView imgScan = findViewById(R.id.img_scan);

        BaseFragment mTab1 = new WalletFragment();
//        BaseFragment mTab2 = new IdentityFragment();
//        OntIdFragment mTab2 = new OntIdFragment();
        BaseFragment mTab3 = new GameFragment();
        BaseFragment mTab4 = new MineFragment();

        list.add(mTab1);
        list.add(mTab3);
        list.add(mTab4);
        mPager.setAdapter(new MainAdapter(getSupportFragmentManager(), list));
        mTabbar.setContainer(mPager);
        mPager.setOffscreenPageLimit(3);

        imgScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(baseActivity, CaremaPermissionActivity.class));
            }
        });
    }
}
