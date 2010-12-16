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

/**
 * Contexts are the internal mechanism used to keep track of where a
 * computation has got to (and from where),
 * so that control-flow constructs can work and reasonable information
 * can be produced on error conditions,
 * (such as via traceback) and otherwise (the sys.xxx functions).
 */
public class Context {


  public enum Type {
    /** toplevel context */
    TOP_LEVEL,

    /** target for next */
    NEXT,

    /** target for break */
    BREAK,

    /** break or next target */
    LOOP,

    /** function closure */
    FUNCTION,

    /** other functions that need error cleanup */
    CCODE,

    /** return() from a closure */
    RETURN,

    /** return target on exit from browser */
    BROWSER,

    /** rather, running an S3 method */
    GENERIC,

    /** a call to restart was made from a closure */
    RESTART,

    /** builtin internal function */
    BUILTIN
  }

  private Context nextContext;
  private int evaluationDepth;
  private Type type;
  private Environment environment;

  private Context() {
  }

  public static Context newTopLevelContext() {
    Context context = new Context();
    context.type = Type.TOP_LEVEL;
    context.environment = Environment.createGlobalEnvironment();
    return context;
  }

  public Environment getEnvironment() {
    return environment;
  }

  public int getEvaluationDepth() {
    return evaluationDepth;
  }

  public Context beginFunction(Environment enclosingEnvironment) {
    Context context = new Context();
    context.type = Type.FUNCTION;
    context.nextContext = this;
    context.evaluationDepth = evaluationDepth+1;
    context.environment = Environment.createChildEnvironment(enclosingEnvironment);

    return context;
  }
  
}
