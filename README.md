## Travis build

[![Build Status](https://travis-ci.org/Tantalor93/structlogger.svg?branch=master)](https://travis-ci.org/Tantalor93/structlogger)

## How to use this project
clone this project from github and build this project using maven `mvn clean install`

add dependency to your project 
```
<dependency>
    <groupId>cz.muni.fi</groupId>
    <artifactId>structlogger</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

then you should set compiler argument `schemasRoot` in order to set path where schemas are generated, also you can set package (namespace) for auto generated events using compiler argument `generatedEventsPackage`, which takes qualified package (dot notation, e.g. cz.muni.fi). You can set compiler arguments using maven-compiler-plugin

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
                        <arg>-AschemasRoot=${basedir}</arg>
                        <arg>-AgeneratedEventsPackage=${generatedEventsPackage}</arg>
                    </compilerArgs>
                </configuration>
</plugin>
```

in your java code you can then declare, fields like this:
```
@LoggerContext(context = DefaultContext.class)
private static EventLogger<DefaultContext> logger = new EventLogger<>(new Slf4jLoggingCallback(LoggerFactory.getLogger("LOGGER")));
```

please note that EventLogger should not be declared and cannot be used as local variable!!
EventLogger takes implementation of LoggingCallback, which implements basic logging operations, for example here we use [Slf4jLoggingCallback](structlogger/src/main/java/cz/muni/fi/slf4j/Slf4jLoggingCallback.java), which encapsulates SLF4j logger and all it does is it serializes incoming events as string and pass them to SLF4j logger, or you can implement your own [LoggingCallback](structlogger/src/main/java/cz/muni/fi/LoggingCallback.java)

EventLogger field has to be annotated with `@LoggerContext` in order to structured logging to work, you have to also specify extension of [VariableContext](structlogger/src/main/java/cz/muni/fi/VariableContext.java) as annotation parameter (this parameter must be same as generic argument of EventLogger otherwise you will encounter undefined behaviour). Variable context provides logging variables. You can create your own VariableContext like [BlockCacheContext](structlogger-example/src/main/java/cz/muni/fi/BlockCacheContext.java). Please see *Creating your own Variable context* section of README. 

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
        "message":"Event with double=1.2 and boolean=false",
        "sourceFile":"cz.muni.fi.Example",
        "lineNumber":37,
        "type":"auto.Event677947de",
        "sid":1,
        "logLevel":"INFO",
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
        "message":"Event with double=1.2 and boolean=false",
        "sourceFile":"cz.muni.fi.Example",
        "lineNumber":24,
        "type":"edu.TestEvent",
        "sid":1,
        "varDouble":1.2,
        "varBoolean":false
}
```

and this json will logged using *slf4j* logging API, implementation of this API should be provided by your project (like logback or log4j)

By using for example *logback* implementation of slf4j API and by using [logstash encoder](https://github.com/logstash/logstash-logback-encoder) you can generate full json logs, where each log entry is valid json embedding json generated by structlogger in correct manner. See [example](structlogger-example)

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
    "message" : {
      "type" : "string"
    },
    "sourceFile" : {
      "type" : "string"
    },
    "lineNumber" : {
      "type" : "integer"
    },
    "type" : {
      "type" : "string"
    },
    "sid" : {
      "type" : "integer"
    },
    "logLevel" : {
      "type" : "string"
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
Variable context is interface which provides parameters to be used in structured logging by event logger. To implement your own VariableContext,
create new interface which extends [VariableContext](structlogger/src/main/java/cz/muni/fi/VariableContext.java) and only extends this interface, 
annotate your interface with [@VarContextProvider](structlogger/src/main/java/cz/muni/fi/annotation/VarContextProvider.java), then add methods annotated with [@Var](structlogger/src/main/java/cz/muni/fi/annotation/Var.java),
these methods should all have return type your Interface and accept single parameter, please not that method overloading is not supported.

For example of custom Variable context see [BlockCacheContext](structlogger-example/src/main/java/cz/muni/fi/BlockCacheContext.java).

Also note that your custom VariableContext is checked whether it is valid only checked lazily, when it is used.

### Log message parametrization
[@VarContextProvider](structlogger/src/main/java/cz/muni/fi/annotation/VarContextProvider.java) has parameter called `parametrization`, which when set to true forces constraints on log message such that log message (String in `info`,`debug`,... method) must contain `{}` placeholders same count as parameters used in given log statement, for example
```
logger.info("test {} string literal {}")
      .varDouble(1.2)
      .varBoolean(false)
      .log();
``` 

must contain two `{}` placeholders, because we are using two parameters here `varDouble` and `varBoolean`, these placeholders are at runtime replaced with values passed as argument to log parameters, so here it will create event message will look like this `test 1.2 string literal false`.

If `parametrization` is set to false, no placeholder `{}` is replaced in log message and no placeholders are enforced in log message during compilation

## Structlogger tests
tests of structlogger API and annotation processor are located in [structlogger-tests](structlogger-tests) module

## Structlogger kafka support 
You can use [EventTypeAwareKafkaCallback](structlogger/src/main/java/cz/muni/fi/kafka/EventTypeAwareKafkaCallback.java) to send events asynchronously to topics using provided Producer, just provide in your project dependency on `kafka-clients`, for example like this:

```
 <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka-clients</artifactId>
            <version>1.0.0</version>
 </dependency>
```
Events are send with keys corresponding to system time in milliseconds, events are send to topics based on event type.