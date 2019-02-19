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

package com.github.ont.cyanowallet.view.jptab;

/**
 * Created by jpeng on 16-11-13.
 * 点击回调监听
 */
public interface OnTabSelectListener {
    /**
     *  用户每次点击不同的Tab将会触发这个方法
     * @param index
     * 当前选择的TAB的索引值
     */
    void onTabSelect(int index);

}
