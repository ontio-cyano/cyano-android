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

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by jpeng on 17-9-3.
 */
public class Scale2Animater implements  Animatable {

    @Override
    public void onPressDown(View v, boolean selected) {
        if(!selected) {
            ViewHelper.setScaleX(v,0.75f);
            ViewHelper.setScaleY(v,0.75f);
        }
    }

    @Override
    public void onTouchOut(View v, boolean selected) {
        if(!selected) {
            ViewHelper.setScaleX(v,1f);
            ViewHelper.setScaleY(v,1f);
        }
    }

    @Override
    public void onSelectChanged(View v, boolean selected) {
        if(!selected)return;
        AnimatorSet set= new AnimatorSet();
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(v,"scaleX",0.75f,1.3f,1f,1.2f,1f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(v,"scaleY",0.75f,1.3f,1f,1.2f,1f);
        set.playTogether(animator1,animator2);
        set.setDuration(800);
        set.start();

    }

    @Override
    public void onPageAnimate(View v, float offset) {

    }

    @Override
    public boolean isNeedPageAnimate() {
        return false;
    }
}
