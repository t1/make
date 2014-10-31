# make

This is an experiment. It takes the concepts of [Maven](http://maven.apache.org) and tries to prove that it's possible to describe builds more concise, more uniformly, and more to-the-point.

In it's current incarnation, it produces a `pom.xml` and then calls Maven to do the build. If this proves to work out well, a second incarnation may do the build directly and much more continuously. It will still be able to produce a `pom.xml` for usage in other tools, e.g. IDE integration.

The file used to supersede the `pom` is called `product.xml`, or `product.json` if you prefer.
