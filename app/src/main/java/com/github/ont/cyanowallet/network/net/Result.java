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

package com.github.ont.cyanowallet.network.net;

/**
 * Created by liutao17 on 2016/6/22.
 */
public class Result {
    public boolean isSuccess;
    public Object info;
    public String tag;

    public Result(boolean isSuccess)
    {
        this(isSuccess, null);
    }

    public Result(Object obj)
    {
        this(true, obj);
    }

    public Result(boolean isSuccess, Object obj)
    {
        this.isSuccess =isSuccess;
        info = obj;
    }
}
