package org.renjin.compiler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.renjin.compiler.cfg.BasicBlock;
import org.renjin.compiler.cfg.ControlFlowGraph;
import org.renjin.compiler.ir.tac.IRBody;
import org.renjin.compiler.ir.tac.IRBodyBuilder;
import org.renjin.compiler.ir.tac.IRFunctionTable;
import org.renjin.parser.RParser;
import org.renjin.sexp.ExpressionVector;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class CompilerTestCase {

  protected IRFunctionTable functionTable = new IRFunctionTable();

  protected IRBody buildScope(String rcode) {
    ExpressionVector ast = RParser.parseSource(rcode + "\n");
    return new IRBodyBuilder(functionTable).build(ast);
  }  
  

  protected final IRBody parseCytron() throws IOException {
    return buildScope(Resources.toString(Resources.getResource(ControlFlowGraph.class, "cytron.R"), Charsets.UTF_8));
  }
  
  protected final Matcher<Collection<BasicBlock>> itemsEqualTo(final BasicBlock... blocks) {
    return new TypeSafeMatcher<Collection<BasicBlock>>() {

      @Override
      public void describeTo(Description d) {
        d.appendValue(Arrays.toString(blocks));
      }

      @Override
      public boolean matchesSafely(Collection<BasicBlock> collection) {
        if(collection.size() != blocks.length) {
          return false;
        }
        for(BasicBlock bb : blocks) {
          if(!collection.contains(bb)) {
            return false;
          }
        }
        return true;
      }
    };
  }
}
