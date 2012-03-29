package org.renjin.jvminterop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.renjin.eval.Context;
import org.renjin.eval.EvalException;
import org.renjin.jvminterop.converters.Converter;
import org.renjin.jvminterop.converters.Converters;
import org.renjin.primitives.annotations.processor.ArgumentIterator;
import org.renjin.sexp.Environment;
import org.renjin.sexp.PairList;
import org.renjin.sexp.SEXP;


import com.google.common.collect.Lists;

public class FunctionBinding {

  private List<Overload> overloads = Lists.newArrayList();
  private int maxArgCount;
  
  public FunctionBinding(Iterable<Method> overloads) {
    for(Method method : overloads) {
      addOverload(method);
    }
    AbstractOverload.sortOverloads(this.overloads);
  }

  private void addOverload(Method method) {
    Overload overload = new Overload(method);
    if(overload.getArgCount() > maxArgCount) {
      maxArgCount = overload.getArgCount();
    }
    this.overloads.add(overload);
  }
  
  public FunctionBinding(Method[] methods) {
    this(Arrays.asList(methods));
  }

  public static class Overload extends AbstractOverload {
    private Method method;
    private Converter returnValueConverter;
    
    public Overload(Method method) {
      super(method.getParameterTypes(),
            method.getParameterAnnotations(), 
            method.isVarArgs());
      this.method = method;
      this.returnValueConverter = Converters.get(method.getReturnType());    
    
      // workaround reflection problem calling 
      // public methods on private subclasses
      // see http://download.oracle.com/javase/tutorial/reflect/member/methodTrouble.html
      this.method.setAccessible(true);
    }
    
    
    public SEXP invoke(Context context, Object instance, List<SEXP> args) {
      Object[] converted = convertArguments(context, args);
      try {
        Object result = method.invoke(instance, converted);
        return returnValueConverter.convertToR(result);
        
      } catch (IllegalArgumentException e) {
        throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException("Exception invoking " + method, e);
      } catch (InvocationTargetException e) {
        if(e.getCause() instanceof RuntimeException) {
          throw (RuntimeException)e.getCause();
        } else {
          throw new RuntimeException(e);
        }
      }
    }
    
    @Override
    public String toString() {
      return method.toString();
    }
  }
  
  public SEXP invoke(Object instance, Context context, Environment rho, PairList arguments) {
    
    // eval arguments
    List<SEXP> args = Lists.newArrayListWithCapacity(maxArgCount);
    ArgumentIterator it = new ArgumentIterator(context, rho, arguments);
    int nargs = 0;
    while(it.hasNext()) {
      args.add(context.evaluate( it.next(), rho));
    }
    
    // find overload
    for(Overload overload : overloads) {
      if(overload.accept(args)) {
        
        return overload.invoke(context, instance, args);
        
      }
    }
    throw new EvalException("Cannot match arguments (%s) to any JVM method overload:\n%s", 
        ExceptionUtil.toString(args), ExceptionUtil.overloadListToString(overloads));
  }
}
