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

/*
 * Copyright 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package core.client.result;

import core.Result;

/**
 * Parses a "tel:" URI result, which specifies a phone number.
 *
 * @author Sean Owen
 */
public final class TelResultParser extends ResultParser {

  @Override
  public TelParsedResult parse(Result result) {
    String rawText = getMassagedText(result);
    if (!rawText.startsWith("tel:") && !rawText.startsWith("TEL:")) {
      return null;
    }
    // Normalize "TEL:" to "tel:"
    String telURI = rawText.startsWith("TEL:") ? "tel:" + rawText.substring(4) : rawText;
    // Drop tel, query portion
    int queryStart = rawText.indexOf('?', 4);
    String number = queryStart < 0 ? rawText.substring(4) : rawText.substring(4, queryStart);
    return new TelParsedResult(number, telURI, null);
  }

}