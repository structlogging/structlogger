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

in your java code you can then declare, fields like this:
```
@VarContext(context = DefaultContext.class)
private static StructLogger<DefaultContext> logger = StructLogger.instance();
```

(please note that StructLogger should not be declared and cannot be used as local variable)

this declared logger can then be used for logging in structured way like this:

```
logger.info("test {} string literal {}")
      .varDouble(1.2)
      .varBoolean(false)
      .log();
```

this structured log statement will generate json like this:
```json
{ 
  "event":
      {   
        "message":"test 1.2 string literal false",
        "sourceFile":"cz.muni.fi.Example",
        "lineNumber":24,
        "varDouble":1.2,
        "varBoolean":false},
  "sid":1,
  "logLevel":"INFO",
  "eventIdentifier":"Event853e32ae"
}
```
and this json will logged using *slf4j* logging API, implementation of this API should be provided by your project (like logback or log4j)