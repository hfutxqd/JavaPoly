function testEval() {
  describe('Eval', function() {

    it('integer result', function() {
      return JavaPoly.type('com.javapoly.Eval').then(function(Eval) {
        Eval.eval("40 + 2").then(function(result) {
          expect(result).toEqual(42);
        });
      });
    });

    it('array result', function() {
      return JavaPoly.type('com.javapoly.Eval').then(function(Eval) {
        Eval.eval("'a,b,c'.split(',')").then(function(result) {
          expect(result).toEqual(["a", "b", "c"]);
        });
      });
    });

    it('should pass all tests from EvalTest.java', function() {
      return JavaPoly.type('EvalTest').then(function(EvalTest) {
        return EvalTest.test().then(function(result) {
          expect(result).toEqual(true);
        }, (error) => console.log(error));
      });
    });

    it('function definition', function() {
      return JavaPoly.type('com.javapoly.Eval').then(function(Eval) {
        return Eval.eval("(function(x){return x*x})").then(function(result) {
          expect(result(7)).toEqual(49);
        });
      });
    });

    it('calling Java lambdas', function() {
      return JavaPoly.type('EvalTest').then(function(EvalTest) {
        return EvalTest.javaLambdaFromJS(10).then(function(result) {
          expect(result).toEqual(43);
        });
      });
    });

    it('calling Java lambdas with exception', function() {
      return JavaPoly.type('EvalTest').then(function(EvalTest) {
        return EvalTest.javaLambdaFromJSWithException(10).then(function(result) {
          expect(result).toEqual(23);
        });
      });
    });

  });
}

