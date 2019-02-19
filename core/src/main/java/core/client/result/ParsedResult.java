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

/**
 * <p>Abstract class representing the result of decoding a barcode, as more than
 * a String -- as some type of structured data. This might be a subclass which represents
 * a URL, or an e-mail address. {@link ResultParser#parseResult(com.github.ontio.onto.www.core.Result)} will turn a raw
 * decoded string into the most appropriate type of structured representation.</p>
 *
 * <p>Thanks to Jeff Griffin for proposing rewrite of these classes that relies less
 * on exception-based mechanisms during parsing.</p>
 *
 * @author Sean Owen
 */
public abstract class ParsedResult {

  private final ParsedResultType type;

  protected ParsedResult(ParsedResultType type) {
    this.type = type;
  }

  public final ParsedResultType getType() {
    return type;
  }

  public abstract String getDisplayResult();

  @Override
  public final String toString() {
    return getDisplayResult();
  }

  public static void maybeAppend(String value, StringBuilder result) {
    if (value != null && !value.isEmpty()) {
      // Don't add a newline before the first value
      if (result.length() > 0) {
        result.append('\n');
      }
      result.append(value);
    }
  }

  public static void maybeAppend(String[] values, StringBuilder result) {
    if (values != null) {
      for (String value : values) {
        maybeAppend(value, result);
      }
    }
  }

}
