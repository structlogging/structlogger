## Project status

[![Build Status](https://travis-ci.org/structlogging/structlogger.svg?branch=master)](https://travis-ci.org/structlogging/structlogger)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central//com.github.structlogging/structlogger/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/com.github.structlogging/structlogger)
[![Javadocs](http://javadoc.io/badge/com.github.structlogging/structlogger.svg)](http://javadoc.io/doc/com.github.structlogging/structlogger)

## What?
This is master thesis project at Masaryk University [Faculty of Informatics](https://fi.muni.cz/index.html.en) under supervision of Daniel Tovarňák ([xdanos](https://github.com/xdanos)).
This project is based on project [ngmon-structlog-java-fal](https://github.com/lasaris/ngmon-structlog-java-fal), which supports structured logging in Java using so-called Variable Contexts (see [Daniel's dissertation thesis, Chapter 3](https://is.muni.cz/th/172673/fi_d/)).

## How to use this project
add dependency to your project 
```
<dependency>
    <groupId>com.github.structlogging</groupId>
    <artifactId>structlogger</artifactId>
    <version>${structlogger.version}</version>
</dependency>
```

then you should set compiler argument `schemasRoot` in order to set path where schemas are generated, also you can set package (namespace) for auto generated events using compiler argument `generatedEventsPackage`, which takes qualified package (dot notation, e.g. com.github.structlogging). You can set compiler arguments using maven-compiler-plugin

```
<plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.6.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <showWarnings>true</showWarnings>
                    <compilerArgs>
                        <arg>-AschemasRoot=${project.basedir}</arg>
                        <arg>-AgeneratedEventsPackage=${generatedEventsPackage}</arg>
                    </compilerArgs>
                </configuration>
</plugin>
```

in your java code you can then declare fields like this:
```
@LoggerContext(context = DefaultContext.class)
private static StructLogger<DefaultContext> logger = new StructLogger<>(new Slf4jLoggingCallback(LoggerFactory.getLogger("LOGGER")));
```

StructLogger takes implementation of LoggingCallback, which implements basic logging operations, for example here we use [Slf4jLoggingCallback](structlogger/src/main/java/com/github/structlogging/slf4j/Slf4jLoggingCallback.java), which encapsulates SLF4j logger and all it does is it serializes incoming events as string and pass them to SLF4j logger, or you can implement your own [LoggingCallback](structlogger/src/main/java/com/github/structlogging/LoggingCallback.java)

StructLogger field has to be annotated with `@LoggerContext` in order to structured logging to work, you have to also specify extension of [VariableContext](structlogger/src/main/java/com/github/structlogging/VariableContext.java) as annotation parameter (this parameter must be same as generic argument of StructLogger otherwise you will encounter undefined behaviour). Variable context provides logging variables. You can create your own VariableContext like [BlockCacheContext](structlogger-example/src/main/java/com/github/structlogging/BlockCacheContext.java). Please see *Creating your own Variable context* section of README. 

this declared logger can then be used for logging in structured way like this:

```
logger.info("Event with double={} and boolean={}")
      .varDouble(1.2)
      .varBoolean(false)
      .log();
```

this structured log statement will generate json like this:
```json
{
  "type":"auto.Event677947de",
  "timestamp":1524037512388,
  "context":{
    "message":"Event with double=1.2 and boolean=false",
    "sourceFile":"com.github.structlogging.Example",
    "lineNumber":66,
    "sid":1,
    "logLevel":"INFO"
  },
  "varDouble":1.2,
  "varBoolean":false
}
```

or you can choose your own name of generated event by passing String literal as argument to log method like this:
```
logger.info("Event with double={} and boolean={}")
      .varDouble(1.2)
      .varBoolean(false)
      .log("edu.TestEvent");
```
Beware that you cannot pass String containing white spaces or new lines, such String will generate compilation error

this will generate event like this:
```json
{
  "type":"edu.TestEvent",
  "timestamp":1524037512388,
  "context":{
    "message":"Event with double=1.2 and boolean=false",
    "sourceFile":"com.github.structlogging.Example",
    "lineNumber":71,
    "sid":2,
    "logLevel":"INFO"
  },
  "varDouble":1.2,
  "varBoolean":false
}
```

and this json will logged using *slf4j* logging API, implementation of this API should be provided by your project (like logback or log4j)

this structured event is send as message to implementation of *slf4j* logging API
 
## Event json schemas
For each generated structured logging event there is corresponding json schema created during compilation on path specified by compiler argument `schemasRoot` in folder `schemas/events` and each event with namespace is nested in corresponding folder,

For example:
```
logger.info("Event with double={} and boolean={}")
      .varDouble(1.2)
      .varBoolean(false)
      .log("edu.TestEvent");
``` 
will create json schema `${schemasRoot}/schemas/events/edu/TestEvent.json`
```
{
  "type" : "object",
  "id" : "urn:jsonschema:edu:TestEvent",
  "$schema" : "http://json-schema.org/draft-04/schema#",
  "description" : "Event with double={} and boolean={}",
  "title" : "edu.TestEvent",
  "properties" : {
    "type" : {
      "type" : "string"
    },
    "timestamp" : {
      "type" : "integer"
    },
    "context" : {
      "type" : "object",
      "id" : "urn:jsonschema:com:github:structlogging:LoggingEventContext",
      "properties" : {
        "message" : {
          "type" : "string"
        },
        "sourceFile" : {
          "type" : "string"
        },
        "lineNumber" : {
          "type" : "integer"
        },
        "sid" : {
          "type" : "integer"
        },
        "logLevel" : {
          "type" : "string"
        }
      }
    },
    "varDouble" : {
      "type" : "number"
    },
    "varBoolean" : {
      "type" : "boolean"
    }
  }
}
```
see [example](structlogger-example) where schemas are created after compilation in root of this module


If `schemasRoot` compiler argument is not specified, no schemas will be created!
## Creating your own Variable context provider
Variable context is interface which provides parameters to be used in structured logging by [StructLogger](structlogger/src/main/java/com/github/structlogging/StructLogger.java). To implement your own VariableContext,
create new interface which extends [VariableContext](structlogger/src/main/java/com/github/structlogging/VariableContext.java) and only extends this interface, 
annotate your interface with [@VarContextProvider](structlogger/src/main/java/com/github/structlogging/annotation/VarContextProvider.java), then add methods annotated with [@Var](structlogger/src/main/java/com/github/structlogging/annotation/Var.java),
these methods should all have return type your Interface and accept single parameter, please not that method overloading is not supported.
also these method names are prohibited: `info`, `debug`, `error`, `warn`, `trace`, `audit`, `infoEvent`, `debugEvent`, `errorEvent`, `warnEvent`, `traceEvent`, `auditEvent` `log`, 
For example of custom Variable context see [BlockCacheContext](structlogger-example/src/main/java/com/github/structlogging/BlockCacheContext.java).

Also note that your custom VariableContext is checked whether it is valid only checked lazily, when it is used.

### Log message parametrization
[@VarContextProvider](structlogger/src/main/java/com/github/structlogging/annotation/VarContextProvider.java) has parameter called `parametrization`, which when set to `true` forces constraints on log message such that log message (String in `info`,`debug`,... method) must contain `{}` placeholders same count as parameters used in given log statement, for example
```
logger.info("test {} string literal {}")
      .varDouble(1.2)
      .varBoolean(false)
      .log();
``` 

must contain two `{}` placeholders, because we are using two parameters here `varDouble` and `varBoolean`, these placeholders are at runtime replaced with values passed as argument to log parameters, so here it will create event message will look like this `test 1.2 string literal false`.

If `parametrization` is set to false, no placeholder `{}` is replaced in log message and no placeholders are enforced in log message during compilation

## Usage restrictions
There are some restrictions due to the way this library is implemented.
Please note that:
 * **StructLogger should not be declared and cannot be used as local variable** 
 * **StructLogger field should be only referenced within class where it is declared** 
 * **StructLogger field should be referenced directly (not via some access method or something like that)**.
 * **StructLogger field name should not be shadowed by any local variable**


## Structlogger tests
tests of structlogger API and annotation processor are located in [structlogger-tests](structlogger-tests) module

## Structlogger kafka support 
You can use [EventTypeAwareKafkaCallback](structlogger/src/main/java/com/github/structlogging/kafka/EventTypeAwareKafkaCallback.java) to send events asynchronously to topics using provided Producer, just provide in your project dependency on `kafka-clients`, for example like this:

```
 <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka-clients</artifactId>
            <version>1.0.0</version>
 </dependency>
```
Events are send with keys corresponding to system time in milliseconds, events are send to topics based on event type.
