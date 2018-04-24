### Tic tac toe in Clojurescript!

Running app at https://hungry-nobel-23ee76.netlify.com/

This little demo was inspired by the "ReasonDojo - Learn ReasonML by Making Things" on April 23, 1028, where we were tasked to do the same thing, only in ReasonReact :)

### Development mode

To start the Figwheel compiler, navigate to the project folder and run the following command in the terminal:

```
lein figwheel
```

Figwheel will automatically push cljs changes to the browser.
Once Figwheel starts up, you should be able to open the `public/index.html` page in the browser.


### Building for production

```
lein clean
lein package
```
