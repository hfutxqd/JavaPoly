package com.javapoly;

import com.javapoly.Main;
import com.javapoly.reflect.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;

class DoppioJSObject extends DoppioJSValue implements JSObject {
  DoppioJSObject(final Object rawValue) {
    super(rawValue);
  }

  public JSValue getProperty(String name) {
    return Main.wrapValue(getProperty(rawValue, name));
  }

  private Method getSAM(Object obj) {
    final Class cls = obj.getClass();
    final Class fc = getFunctionalInterface(cls);
    if (fc != null) {
      Method[] methods = fc.getMethods();
      for (Method m : methods) {
        if (Modifier.isAbstract(m.getModifiers())) {
          return m;
        }
      }
    }
    return null;
  }

  private Class getFunctionalInterface(Class cls) {
    final Class sc = cls.getSuperclass();
    if (sc != null) {
      if (sc.isAnnotationPresent(FunctionalInterface.class)) {
        return sc;
      }
    }

    final Class[] classes = cls.getInterfaces();
    for (final Class c : classes) {
      if (c.isAnnotationPresent(FunctionalInterface.class)) {
        return c;
      } else {
        Class p = getFunctionalInterface(c);
        if (p != null) {
          return p;
        }
      }
    }
    return null;
  }

  public JSValue invoke(Object... args) {
    final Object[] unwrappedArgs = new Object[args.length];
    for (int i = 0; i < args.length; i++) {
      final Object e = args[i];
      if (e instanceof DoppioJSValue) {
        unwrappedArgs[i] = ((DoppioJSValue) e).rawValue;
      } else {
        final Method sam = getSAM(e);
        if (sam != null) {
          System.out.println("SAM: " + sam);
          unwrappedArgs[i] = new MethodInvoker(e, sam);
        } else {
          unwrappedArgs[i] = e;
        }
      }
    }
    return Main.wrapValue(invoke(rawValue, unwrappedArgs));
  }

  private static native Object[] invoke(Object functionObj, Object... args);

  private static native Object[] getProperty(Object obj, String name);
}

