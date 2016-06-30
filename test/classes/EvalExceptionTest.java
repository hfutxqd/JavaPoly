import com.javapoly.Eval;
import com.javapoly.reflect.*;

public class EvalExceptionTest {
  public static JSValue test() {
    return Eval.eval("var someUndefinedValue = anotherUndefinedValue.xyz");
  }
}
