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

import com.github.ont.cyanowallet.network.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by liutao17 on 2016/6/23.
 */
interface ParseTool {

    Result onSuccess(JSONObject jsonObject);

    Result onFail(VolleyError error);
}
