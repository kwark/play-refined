# Play-refined

[![Build Status](https://travis-ci.org/kwark/play-refined.svg?branch=master)](https://travis-ci.org/kwark/play-refined)
[![Maven version](https://img.shields.io/maven-central/v/be.venneborg/play26-refined_2.12.svg)](https://maven-badges.herokuapp.com/maven-central/be.venneborg/play26-refined_2.12)

Play-refined is a small scala library that enables boilerplate-free integration of refinement types 
using the [Refined library](https://github.com/fthomas/refined) with Lightbend's [Play framework](https://www.playframework.com/)

It allows you to easily use refined types with Play.  

Both scala 2.11 & 2.12 and Play 2.5 & 2.6 are supported.
You'll also need to use Java8.

The library provides the following functionality:

* boilerplate-free JSON serialization/deserialization of refined types
* boilerplate-free form binding/unbinding of refined types
* Path/Query binding for refined types
* Translation of Refined error messages to standard Play error codes


## Usage

Versions: The table below lists the versions and their main dependencies

|Artifact to use |Version | Scala  |Play  |
|----------------|--------|--------|------|
|play25-refined  |0.1.0   |2.11.x  |2.5.x |
|play26-refined  |0.1.0   |2.11.x  |2.6.x |
|play26-refined  |0.1.0   |2.12.x  |2.6.x |

Depending on the artifact and version you need to add the correct dependency to your SBT dependencies:

```libraryDependencies += "be.venneborg" %% "play26-refined" % 0.1.0```

### Json Formatters

Suppose we have a case class which uses Refined types:

```
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.types.numeric.PosInt

case class FooBar(foo: NonEmptyString, bar: PosInt)

```

To [automatically serialize/deserialize](https://www.playframework.com/documentation/2.6.x/ScalaJsonAutomated) this case class containing refined types, 
you just need an additional import and derive a JSON `Formatter`

```
import be.venneborg.refined.play.RefinedJsonFormats._
import play.api.libs.json.Json

implicit val fooBarFormat = Json.format[FooBar]

```

### Form binding

You can just as easily [bind/unbind to/from a Form](https://www.playframework.com/documentation/2.6.x/ScalaForms#Putting-it-all-together)

```
import play.api.data.{Form, Forms}
import play.api.data.Forms.mapping

import be.venneborg.refined.play.RefinedForms._

val fooBarForm: Form[FooBar] = Form(
  mapping(
    "foo"  -> Forms.of[NonEmptyString],
    "bar"  -> Forms.of[PosInt]
  )(FooBar.apply)(FooBar.unapply)
) 

fooBarForm.bind(Map("foo" -> "myfoo", "bar" -> 5)).value
``` 

### Path/Query binding

If you want to use a refined type as a query/path parameter in your `routes` file,
you have a bit more work to do.

First you need to adapt your `build.sbt` file and extend the `routesImport`:

```
routesGenerator := InjectedRoutesGenerator

routesImport ++= Seq(
  "be.venneborg.refined.play.RefinedPathBinders._",
  "be.venneborg.refined.play.RefinedQueryBinders._",
  "eu.timepit.refined.types.numeric.PosInt" //This depends on the refined types you want to use
)
```

And now you can simple use the `PosInt` refined type either as a path or query param in your `routes` file:

```
GET /foobars/:bar     controllers.MyController.foobar(bar: PosInt)
```

Of course you'll also need a `MyController` with the appropriate `Action` defined:

```
class MyController extends .... {

    def foobar(bar: PosInt) = Action { Ok(v.value) }

}
```

### Example project

This repository also contains an `example` project, which demonstrates all of the above.
Please refer to it for more details.