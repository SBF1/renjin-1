/*
 * R : A Computer Language for Statistical Data Analysis
 * Copyright (C) 1995, 1996  Robert Gentleman and Ross Ihaka
 * Copyright (C) 1997-2008  The R Development Core Team
 * Copyright (C) 2003, 2004  The R Foundation
 * Copyright (C) 2010 bedatadriven
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package r.parser;

public class ParseOptions {
  private boolean generateCode = true;
  private boolean keepSource = true;
  private boolean warnEscapes = true;

  public ParseOptions() {
  }

  public boolean isGenerateCode() {
    return generateCode;
  }

  public void setGenerateCode(boolean generateCode) {
    this.generateCode = generateCode;
  }

  public boolean isKeepSource() {
    return keepSource;
  }

  public void setKeepSource(boolean keepSource) {
    this.keepSource = keepSource;
  }

  public boolean isWarnEscapes() {
    return warnEscapes;
  }

  public void setWarnEscapes(boolean warnEscapes) {
    this.warnEscapes = warnEscapes;
  }

  public static ParseOptions defaults() {
    return new ParseOptions();
  }
}
