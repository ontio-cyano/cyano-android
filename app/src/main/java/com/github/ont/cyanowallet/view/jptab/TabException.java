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
 * Tab异常类
 */
public class TabException extends NullPointerException {
    public TabException() {
        super();
    }

    public TabException(String detailMessage) {
        super(detailMessage);
    }
}
