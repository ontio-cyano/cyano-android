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
import android.view.animation.AnticipateInterpolator;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by jpeng on 16-11-15.
 * 实现旋转的动画类
 */
public class RotateAnimater implements Animatable {

    @Override
    public void onPressDown(View v, boolean selected) {

    }

    @Override
    public void onTouchOut(View v, boolean selected) {

    }

    @Override
    public void onSelectChanged(View v, boolean selected) {
        int end = selected ? 360 : 0;
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(v, "rotation",  end);
        rotateAnimator.setDuration(400);
        rotateAnimator.setInterpolator(new AnticipateInterpolator());
        rotateAnimator.start();
    }

    @Override
    public void onPageAnimate(View v, float offset) {
        ViewHelper.setRotation(v, offset * 360);
    }

    @Override
    public boolean isNeedPageAnimate() {
        return true;
    }

}
