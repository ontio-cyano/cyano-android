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
 * Copyright 2007 ZXing authors
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
 * @author Sean Owen
 */
public final class BookmarkDoCoMoResultParser extends AbstractDoCoMoResultParser {

  @Override
  public URIParsedResult parse(Result result) {
    String rawText = result.getText();
    if (!rawText.startsWith("MEBKM:")) {
      return null;
    }
    String title = matchSingleDoCoMoPrefixedField("TITLE:", rawText, true);
    String[] rawUri = matchDoCoMoPrefixedField("URL:", rawText, true);
    if (rawUri == null) {
      return null;
    }
    String uri = rawUri[0];
    return URIResultParser.isBasicallyValidURI(uri) ? new URIParsedResult(uri, title) : null;
  }

}