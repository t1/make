# somemake

...it, some don't.

This is an experiment. It takes the concepts of Maven and tries to prove that it's possible to describe builds more concise, more uniformly, more to-the-point.

In it's current incarnation, it produces a `pom.xml` and then calls Maven to do the build. If this proves to work out well, a second incarnation may do the build directly and much more continuously. It will still be able to produce a `pom.xml` for usage in other tools, e.g. IDE integration.

The file used to supersede the `pom` is called `product.xml`. I'm currently happy with XML, but somemake could support JSON or whatever serialization you prefer... that's just a technical detail.
