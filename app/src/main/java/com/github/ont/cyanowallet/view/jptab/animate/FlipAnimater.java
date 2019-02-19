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

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.nineoldandroids.view.ViewHelper;


/**
 * Created by jpeng on 16-11-15.
 * 实现翻转的动画类
 */
public class FlipAnimater implements  Animatable{

    @Override
    public void onPressDown(View v, boolean selected) {
        ViewHelper.setRotationY(v,selected?54f:126f);
    }

    @Override
    public void onTouchOut(View v, boolean selected) {
        ViewHelper.setRotationY(v, selected ? 180f : 0f);
    }

    @Override
    public void onSelectChanged(View v, boolean selected) {
        float end = selected?180f:0f;
        ObjectAnimator flipAnimator = ObjectAnimator.ofFloat(v,"rotationY",end);
        flipAnimator.setDuration(400);
        flipAnimator.setInterpolator(new DecelerateInterpolator());
        flipAnimator.start();
    }

    @Override
    public void onPageAnimate(View v, float offset) {
        ViewHelper.setRotationY(v, 180*offset);
    }

    @Override
    public boolean isNeedPageAnimate() {
        return true ;
    }

}
