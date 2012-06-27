package org.renjin.primitives.annotations.processor;


import com.google.common.collect.Lists;
import com.sun.codemodel.*;
import org.renjin.eval.EvalException;
import org.renjin.sexp.SEXP;

import java.util.List;

import static com.sun.codemodel.JExpr._new;
import static com.sun.codemodel.JExpr.invoke;

/**
 * Implements the apply() method of {@code PrimitiveFunction} for methods
 * that do not have var arguments, but have one or more overloads of fixed arity.
 */
public class FixedArityApplyBuilder extends ApplyMethodBuilder {

  public FixedArityApplyBuilder(JCodeModel codeModel, JDefinedClass invoker, PrimitiveModel primitive) {
    super(codeModel, invoker, primitive);
  }

  @Override
  protected void apply(JBlock parent) {

    List<JExpression> arguments = Lists.newArrayList();

    for(int i=0; i<primitive.maxPositionalArgs();++i) {

      List<JvmMethod> overloads = primitive.overloadsWithPosArgCountOf(i);
      if(!overloads.isEmpty()) {

        // if this is the last argument, then we call the wrapper
        // for this overload

        parent._if(lastArgument())._then()._return(invokeWrapper(arguments));
      }

      JVar argument = parent.decl(classRef(SEXP.class), "s" + i, nextArgAsSexp(primitive.isEvaluated(i)));
      arguments.add(argument);
    }

    parent._if(lastArgument())._then()._return(invokeWrapper(arguments));

    // if we still have arguments left, there are too many
    parent._throw(_new(classRef(EvalException.class)));
  }

  private JExpression invokeWrapper(List<JExpression> arguments) {
    JInvocation invocation = JExpr.invoke(JExpr._this(), "doApply");
    invocation.arg(context);
    invocation.arg(environment);
    for(JExpression argument : arguments) {
      invocation.arg(argument);
    }
    return invocation;
  }

  private JExpression lastArgument() {
    return invoke(argumentIterator, "hasNext").not();
  }
}
