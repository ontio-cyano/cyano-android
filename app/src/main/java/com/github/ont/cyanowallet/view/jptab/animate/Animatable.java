/*
 * **************************************************************************************
 *  Copyright © 2014-2018 Ontology Foundation Ltd.
 *  All rights reserved.
 *
 *  This software is supplied only under the terms of a license agreement,
 *  nondisclosure agreement or other written agreement with Ontology Foundation Ltd.
 *  Use, redistribution or other disclosure of any parts of this
 *  software is prohibited except in accordance with the terms of such written
 *  agreement with Ontology Foundation Ltd. This software is confidential
 *  and proprietary information of Ontology Foundation Ltd.
 *
 * **************************************************************************************
 */

package com.github.ont.cyanowallet.view.jptab.animate;

import android.view.View;

/**
 * Created by jpeng on 16-11-14.
 */
public interface Animatable {
    /**
     * 按下View动画行为
     */
    void onPressDown(View v, boolean selected);

    /**
     * 松开时,手指触摸在View外发生的动作行为
     */
    void onTouchOut(View v, boolean selected);

    /**
     * 当选中状态发生改变时
     */
    void onSelectChanged(View v, boolean selected);

    /**
     * 页面切换动画
     */
    void onPageAnimate(View v, float offset);

    /**
     * 是否需要页面切换动画
     */
    boolean isNeedPageAnimate();

}
