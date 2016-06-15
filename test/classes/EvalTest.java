import com.javapoly.Eval;
import com.javapoly.reflect.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

public class EvalTest {
  public static boolean test() {
    // First define a function
    final JSObject squareFunc = (JSObject) Eval.eval("(function(x){return x*x;})");

    // Invoke it with 7 as argument
    final JSPrimitive square7 = (JSPrimitive) squareFunc.invoke(7);
    final boolean test1Pass = square7.asInteger() == 49;

    // Invoke it with 13 as argument
    final JSPrimitive square13 = (JSPrimitive) squareFunc.invoke(13);
    final boolean test2Pass = square13.asDouble() == 169.0;

    final JSPrimitive square169 = (JSPrimitive) squareFunc.invoke(square13);
    final boolean test3Pass = square169.asLong() == 28561;

    // define a string manipulation function
    final JSObject firstPartFunc = (JSObject) Eval.eval("(function(str, delim, n){return str.split(delim)[n];})");

    // Invoke it with two string arguments and an integer
    final JSPrimitive firstString = (JSPrimitive) firstPartFunc.invoke("a,b,c,d", ",", 0);
    final boolean test4Pass = firstString.asString().equals("a");

    final JSPrimitive secondString = (JSPrimitive) firstPartFunc.invoke("a,b,c,d", ",", 1);
    final boolean test5Pass = secondString.asString().equals("b");

    return test1Pass && test2Pass && test3Pass && test4Pass && test5Pass;
  }

  public static JSValue getProperty(final JSObject jsObj, final String name) {
    return jsObj.getProperty(name);
  }

  public static double javaLambdaFromJS(int a) throws InterruptedException, ExecutionException {
    // Define a function which accepts another function 'f' as paramenter, and
    // then calls f() after a timeout.
    final JSObject func1 = (JSObject) Eval.eval("( function(f){ setTimeout(function() {f(20)}, 100); } )");

    CompletableFuture<Double> future = new CompletableFuture<>();

    func1.invoke((Consumer<Double>) (x) -> {future.complete(23 + x);});

    System.out.println("waiting for the future");
    return future.get();
  }

  // Check that the JS function gets a failed promise when there is an exception in Java invocation
  public static double javaLambdaFromJSWithException(int a) throws InterruptedException, ExecutionException {
    // Define a function which accepts another function 'f' as paramenter, and
    // then calls f() after a timeout.
    final JSObject func1 =
      (JSObject) Eval.eval("( function(f) { return new Promise((resolve, reject) => {setTimeout(() => {f('xyz').then(resolve, reject);}, 100)}) } )");

    final JSObject promise = (JSObject) func1.invoke((Consumer<Double>) (x) -> { /* Do nothing, this should fail anyway */});

    CompletableFuture<Double> future = new CompletableFuture<>();
    ((JSObject) promise.getProperty("catch")).invoke(promise, (Consumer<JSObject>) (e) -> future.complete(23.0));

    return future.get();
  }

  // Return the promise returned by a lambda invocation
  public static JSObject javaLambdaFromJSPromise(int a) throws InterruptedException, ExecutionException {
    final JSObject func1 =
      (JSObject) Eval.eval("( function(f) { return new Promise((resolve, reject) => {setTimeout(() => {f(100).then(resolve, reject);}, 100)}) } )");

    final JSObject promise = (JSObject) func1.invoke((Function<Double, Double>) (x) -> x * a);

    return promise;
  }

  // Return the failed promise returned by a lambda invocation with exception
  public static JSObject javaLambdaFromJSFailedPromise(int a) throws InterruptedException, ExecutionException {
    final JSObject func1 =
      (JSObject) Eval.eval("( function(f) { return new Promise((resolve, reject) => {setTimeout(() => {f('xyz').then(resolve, reject);}, 100)}) } )");

    final JSObject promise = (JSObject) func1.invoke((Consumer<Double>) (x) -> { /* Do nothing, this should fail anyway */});

    return promise;
  }
}
