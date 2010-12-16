/*
 * R : A Computer Language for Statistical Data Analysis
 * Copyright (C) 1995, 1996  Robert Gentleman and Ross Ihaka
 * Copyright (C) 1997--2008  The R Development Core Team
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

package r.lang;

import r.lang.exception.EvalException;

/**
 *  a pairlist of promises (as used for matched arguments) but distinguished by the SEXPTYPE.
 *
 */
public class DotExp extends AbstractSEXP {

  private PairList promises;

  public DotExp(PairList promises) {
    this.promises = promises;
  }

  @Override
  public int getTypeCode() {
    return 0;
  }

  @Override
  public String getTypeName() {
    return "...";
  }

  @Override
  public EvalResult evaluate(Context context, Environment rho) {
    throw new EvalException("'...' used in an incorrect context");
  }

  @Override
  public int length() {
    return promises.length();
  }

  public PairList getPromises() {
    return promises;
  }

  @Override
  public void accept(SexpVisitor visitor) {
    throw new UnsupportedOperationException();
  }
}
