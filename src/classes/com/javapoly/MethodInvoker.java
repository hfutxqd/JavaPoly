package com.javapoly;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class MethodInvoker {
  private final Method sam;
  private final Object obj;

  MethodInvoker(Object obj, Method sam) {
    this.sam = sam;
    this.obj = obj;
  }

  void invoke(Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    sam.invoke(obj, args);
  }
}
