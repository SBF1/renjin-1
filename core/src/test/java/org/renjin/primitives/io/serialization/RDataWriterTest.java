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

package org.renjin.primitives.io.serialization;

import org.junit.Test;
import org.renjin.EvalTestCase;
import org.renjin.sexp.*;
import org.renjin.sexp.PairList.Builder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;


public class RDataWriterTest extends EvalTestCase {


  @Test
  public void test() throws IOException {

    ListVector.NamedBuilder list = new ListVector.NamedBuilder();
    list.add("foo", new StringVector("zefer", "fluuu"));
    list.setAttribute("categories", new IntArrayVector(3));


    PairList.Builder file = new PairList.Builder();
    file.add("a", new StringVector("who", "am", "i", StringVector.NA));
    file.add("b", new IntArrayVector(1, 2, 3, IntVector.NA, 4));
    file.add("c", new LogicalArrayVector(Logical.NA, Logical.FALSE, Logical.TRUE));
    file.add("d", new DoubleArrayVector(3.14, 6.02, DoubleVector.NA));
    file.add("l", list.build());


    assertReRead(file.build());
   // write("test.rdata", file.build());
  }

  @Test
  public void testVerySimple() throws IOException {
    PairList.Builder pl = new PairList.Builder();
    pl.add("serialized", new IntArrayVector(1,2,3,4));

    PairList list = pl.build();

    assertReRead(list);
 //   write("testsimple.rdata", list);
  }
  
  @Test
  public void specialSymbols() throws IOException {
    assertReRead(Symbol.MISSING_ARG);
    assertReRead(Symbol.UNBOUND_VALUE);
  }
  

  @Test
  public void testClosure() throws IOException {
    
    PairList.Builder formals = new Builder();
    formals.add("x", new IntArrayVector(1));
    formals.add("y", new IntArrayVector(2));
    
    FunctionCall body = FunctionCall.newCall(Symbol.get("+"), Symbol.get("x"), Symbol.get("y"));
    
    Closure closure = new Closure(topLevelContext.getGlobalEnvironment(), formals.build(), body);
    
    assertReRead(closure);
    
//    PairList.Builder list = new Builder();
//    list.add("renjin.f", closure);
//    
//    write("closure.rdata", list.build());
  }
  
  @Test
  public void closureWithPromise() throws IOException {
    eval("g <- function(x) x");
    eval("f <- function(x, fn = g) fn(x) ");
    
    assertReRead(topLevelContext.getEnvironment().getVariable("f"));
  } 

  
  private void write(String fileName, SEXP exp) throws IOException {
    FileOutputStream fos = new FileOutputStream(fileName);
    GZIPOutputStream zos = new GZIPOutputStream(fos);
    RDataWriter writer = new RDataWriter(topLevelContext, zos);
    writer.writeFile(exp);
    zos.close();
  }

  private void assertReRead(SEXP exp) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    RDataWriter writer = new RDataWriter(topLevelContext, baos);
    writer.writeFile(exp);

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    RDataReader reader = new RDataReader(topLevelContext, bais);
    SEXP resexp = reader.readFile();

    assertThat(resexp, equalTo(exp));
  }
}
