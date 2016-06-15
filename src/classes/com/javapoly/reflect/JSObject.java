package com.javapoly.reflect;

import com.javapoly.Eval;

public interface JSObject extends JSValue {
  public JSValue getProperty(String name);

  public JSValue invoke(JSObject invokeOn, Object... args);

  public default JSValue invoke(Object... args) {
    return invoke(null, args);
  }
}

