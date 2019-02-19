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
 * Copyright 2013 ZXing authors
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

package core.aztec.encoder;

import core.common.BitMatrix;

/**
 * Aztec 2D code representation
 *
 * @author Rustam Abdullaev
 */
public final class AztecCode {

  private boolean compact;
  private int size;
  private int layers;
  private int codeWords;
  private BitMatrix matrix;

  /**
   * @return {@code true} if compact instead of full mode
   */
  public boolean isCompact() {
    return compact;
  }

  public void setCompact(boolean compact) {
    this.compact = compact;
  }

  /**
   * @return size in pixels (width and height)
   */
  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  /**
   * @return number of levels
   */
  public int getLayers() {
    return layers;
  }

  public void setLayers(int layers) {
    this.layers = layers;
  }

  /**
   * @return number of data codewords
   */
  public int getCodeWords() {
    return codeWords;
  }

  public void setCodeWords(int codeWords) {
    this.codeWords = codeWords;
  }

  /**
   * @return the symbol image
   */
  public BitMatrix getMatrix() {
    return matrix;
  }

  public void setMatrix(BitMatrix matrix) {
    this.matrix = matrix;
  }

}
