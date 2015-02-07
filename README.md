# make [![Build Status](https://drone.io/github.com/t1/make/status.png)](https://drone.io/github.com/t1/make/latest)

This is an experiment. It takes the concepts of [Maven](http://maven.apache.org) (namely that you specify the _what_, not the _how_) and pushes them to the next level by showing that it's possible to describe builds more concise, more uniformly, and more to-the-point. I.e. if you want e.g. a Java EE 7 WAR, you only specify that this is what you want, and the know-how required to build it, is expressed [OAOO](http://c2.com/cgi/wiki?OnceAndOnlyOnce) in a repository.

In it's current incarnation, it produces a `pom.xml` and then calls Maven to do the build. If this proves to work out well, a second incarnation may do the build directly and much more continuously. It will still be able to produce a `pom.xml` for usage in other tools, e.g. for IDE integration.

The file used to supersede the `pom` is called `product.xml`, or `product.json` if you prefer. Other encodings may be supported in the future; this is just a technical detail.

If you look at the code, you may wonder, why there isn't one unified model that can the be converted to this or that model. This actually was the first approach, but the differences are too big: An xml file can have comments, whitespace, and much more... and we _do_ want to keep those when it comes to reading and writing. But json does not even support attributes in addition to elements; while this one thing can be simulated (with the convention that attribute names start with a minus character), other features are just too different to find a usable common feature set.

# try

After building `make` with Maven (using `mvn package`), `make` can e.g. build itself:

```
java -jar target/make.jar build --repository=target/test-classes/repository
```

Take a look at the `product.xml` to see.