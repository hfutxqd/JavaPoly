function wrapObject(thread, obj) {
  var objectType = typeof obj;
  if (Array.isArray(obj)) {
    return wrapArray(thread, obj);
  } else if (objectType === 'string') {
    return wrapString(thread, obj);
  } else if (objectType === 'number') {
    return wrapNumber(thread, obj);
  } else {
    var possibleJavaObj = javapoly.unwrapJavaObject(obj);
    return possibleJavaObj == null ? obj : possibleJavaObj;
  }
}

function wrapString(thread, obj) {
  return javapoly.jvm.internString(obj);
}

function wrapArray(thread, obj) {
  var wrappedArr = [];
  for (var i = 0; i < obj.length; i++) {
    wrappedArr.push(wrapObject(thread, obj[i]));
  }
  return util.newArrayFromData(thread, thread.getBsCl(), '[Ljava/lang/Object;', wrappedArr);
}

function wrapNumber(thread, obj) {
  return util.boxPrimitiveValue(thread, 'D', obj)
}

function getPublicInstanceMethods(obj) {
  // Mimicking a set, based on http://stackoverflow.com/a/18890005
  var methodNamesSet = Object.create(null);
  var methods = obj.constructor.cls.getMethods();
  for (var i in methods) {
    var method = methods[i];
    methodNamesSet[method.name] = true;
  }
  return Object.keys(methodNamesSet);
}

/* Converts a Java object to a JS friendly object. Primitive numbers, primitive booleans, strings and arrays are
 * converted to their JS counter-parts. Others are wrapped with JavaObjectWrapper */
function javaObjToJS(thread, obj) {
  if (obj === null)
    return null;
  if (obj['getClass']) {
    var cls = obj.getClass();
    if (cls.className === 'Ljava/lang/String;') {
      return obj.toString();
    } else if (cls.className === 'Ljava/lang/Boolean;') {
      return obj['java/lang/Boolean/value'] == 1;
    } else if (cls.className.charAt(0) === '[') {
      var nativeArray = [];
      for (var i = 0; i < obj.array.length; i++) {
        nativeArray.push(javaObjToJS(thread, obj.array[i]));
      }
      return nativeArray;
    } else {
      if (obj.unbox) {
        return obj.unbox();
      } else {
        return javapoly.wrapJavaObject(obj, getPublicInstanceMethods(obj));
      }
    }
  }
}

registerNatives({
  'com/javapoly/Main': {

    'println(Ljava/lang/String;)V': function(thread, text) {
       console.log("JVM>", text.toString());
     },

    'dispatchMessage(Ljava/lang/String;)V': function(thread, msgId) {
      var callback = javapoly.dispatcher.getMessageCallback(msgId);
      thread.setStatus(6); // ASYNC_WAITING
      callback(thread, function() {
        thread.asyncReturn();
      });
    },

    'returnResult(Ljava/lang/String;Ljava/lang/Object;)V': function(thread, msgId, returnValue) {
      javapoly.dispatcher.callbackMessage(msgId,javaObjToJS(thread, returnValue));
     },

    'installListener()V': function(thread) {
      javapoly.dispatcher.installListener();
      if (window.javaPolyInitialisedCallback) {
        var callback = window.javaPolyInitialisedCallback;
        delete window.javaPolyInitialisedCallback;
        callback();
      }
    },

    'getMessageId()Ljava/lang/String;': function(thread) {
       if (javapoly.dispatcher.getJavaPolyEventsLength() > 0) {
         return wrapObject(thread, javapoly.dispatcher.getMessageId());
       } else {
         thread.setStatus(6); // ASYNC_WAITING
         window.javaPolyCallback = function() {
           delete window.javaPolyCallback;
           thread.asyncReturn( wrapObject(thread, javapoly.dispatcher.getMessageId()));
         }
       }
    },

    'getMessageType(Ljava/lang/String;)Ljava/lang/String;': function(thread, msgId) {
      var unwrappedData = javapoly.dispatcher.getMessageType(msgId);
      if (typeof unwrappedData !== 'undefined') {
        return wrapObject(thread, unwrappedData);
      } else {
        return null;
      }
    },

    'getData(Ljava/lang/String;)[Ljava/lang/Object;': function(thread, msgId) {
      var unwrappedData = javapoly.dispatcher.getMessageData(msgId);
      if (typeof unwrappedData !== 'undefined') {
        return wrapObject(thread, unwrappedData);
      } else {
        return null;
      }
    }
  }
});
