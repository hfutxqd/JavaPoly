# JavaPoly

## Building

There is a Grunt script for building JavaPoly. So for building you have to install `grunt-cli`. Just type command:
```sh
$ npm install -g grunt-cli
```

Now install all required packages by typing:
```sh
$ npm install
```

For building JavaPoly "browser version" the command is:
```sh
$ grunt build:browser
```

This creates `build/javapoly.js` file. This file when included in a web-page, will auto-load some js libraries
(browserjs.js, doppio lib and so on) from external site(eg, www.javapoly.com).

You can create complete package (including JavaPoly-Node-Doppio, JavaPoly-Node-SystemJVM) by running command:
```sh
$ grunt build
```

For testing JavaPoly-Node-Doppio or JavaPoly-Node-SystemJVM you can run:
```sh
$ ./node_modules/mocha/bin/mocha test/TestJavaPolyNodeDoppio.js
or
$ ./node_modules/mocha/bin/mocha test/TestJavaPolyNodeSystem.js
```

### Developing JavaPoly

When developing JavaPoly, you can compile the code with:
```sh
$ grunt watch
```

This command starts a watching process that updates the build when you change any js-file in src-folder.

### Testing JavaPoly

Tests need to be run in atleast the following two browsers: Firefox and Chromium. (different
browser capabilities affect the number and kind of tests that are run).

There is a folder `test` which should contain a simple build of Doppio and JavaPoly.

Make a build of Doppio and copy / link it into the `test/doppio` folder.

Note that **watch**ing-process updates JavaPoly build in `test` folder.

You can also rebuild JavaPoly for testing without watching by command:
```sh
$ grunt build:test
```

**note, the final javapoly.js file which build:test task generates is a little different from the file which build:browser task generate.
the javapoly.js which build:test task generates will load the doppio and browserjs library in local folders.
and the javapoly.js which build:browser(or build) task generates will load the doppio and browserjs library from external web site.** 

To test JavaPoly you should run HTTP server for folder `test` and open `index.html` in browser.

We use a custom http server for this. To run it:
```sh
$ cd test
$ node server.js
```

After this, open browser and navigate to http://localhost:8080/index.html. The page contains tests written in Mocha environment.

You can also test JavaPoly via Mocha in nodejs. Install it by typing:
```sh
$ npm install -g mocha
```

And type `mocha` in projects directory.

### Using JavaPoly Core(including JVM) in WebWorker

To avoid blocking the browser's main thread we can use webworkers if the browser supports it (most current browsers do).

This can be enabled by adding a `worker` option in javapoly. 

The value should be set to the url that points to the `javapoly_worker.js`. (default value is null which disables webworkers)

eg.

```js
new JavaPoly(worker : 'build/javapoly_worker.js');
``` 
