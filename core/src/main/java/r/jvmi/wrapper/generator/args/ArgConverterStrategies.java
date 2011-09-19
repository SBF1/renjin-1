package r.jvmi.wrapper.generator.args;

import r.jvmi.binding.JvmMethod;
import r.jvmi.binding.JvmMethod.Argument;
import r.jvmi.wrapper.GeneratorDefinitionException;

public class ArgConverterStrategies {

  public static ArgConverterStrategy findArgConverterStrategy(JvmMethod.Argument formal) {
    if(Recyclable.accept(formal)) {
      return new Recyclable(formal);
    
    } else if(UsingAsCharacter.accept(formal)) {
      return new UsingAsCharacter(formal);
    
    } else if(SexpSubclass.accept(formal)) {
      return new SexpSubclass(formal);
    
    } else if(ToScalar.accept(formal)) {
      return new ToScalar(formal);
    
    } else if(UnwrapExternalObject.accept(formal)) {
      return new UnwrapExternalObject(formal);
    }
    
    throw new GeneratorDefinitionException("Could not find a strategy for converting to argument " + formal.getIndex() + " of type " + formal.getClazz());
  }

}
