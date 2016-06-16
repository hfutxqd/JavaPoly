function testLambdas() {
  describe('Lambdas', function() {

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

    it('calling Java lambdas should return a promise ', function() {
      return JavaPoly.type('EvalTest').then(function(EvalTest) {
        return EvalTest.javaLambdaFromJSPromise(11).then(function(result) {
          expect(result).toEqual(1100);
        });
      });
    });

    it('calling Java lambdas with exceptions should return a failed promise ', function() {
      return new Promise(function(testResolve, testReject) {
        return JavaPoly.type('EvalTest').then(function(EvalTest) {
          return EvalTest.javaLambdaFromJSFailedPromise(10).then(function(result) {
            testReject(new Error("Not expecting labmda promise to be resolved"));
          }, function(cause) {
            expect(cause.name).toEqual("java.lang.ClassCastException");
            expect(cause.message).toEqual('java.lang.String cannot be cast to java.lang.Double');
            testResolve("Passed");
          });
        });
      });
    });

  });
}
