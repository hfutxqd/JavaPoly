package com.javapoly;

import com.javapoly.reflect.*;

class SystemJSObject extends SystemJSValue implements JSObject {
  private final SystemBridge bridge;

  SystemJSObject(final Object rawValue, final SystemBridge bridge) {
    super(rawValue);
    this.bridge = bridge;
  }

  public JSValue getProperty(String name) {
    final JSValue result = bridge.getObjectProperty(name, (Integer) rawValue);
    return result;
  }

  public JSValue invoke(JSObject invokeOn, Object... args) {
    final Object[] unwrappedArgs = new Object[args.length];
    for (int i = 0; i < args.length; i++) {
      final Object e = args[i];
      unwrappedArgs[i] = (e instanceof SystemJSValue) ? ((SystemJSValue) e).rawValue : e;
    }
    return invoke(rawValue, ((SystemJSObject)invokeOn).getRawValue(), unwrappedArgs);
  }

  private JSValue invoke(Object functionObj, Integer invokeOn, Object... args) {
    return bridge.invoke(functionObj, invokeOn, args);
  }

  public int getRawValue() {
    return (Integer) rawValue;
  }
}


