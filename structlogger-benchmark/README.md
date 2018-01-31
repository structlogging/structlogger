# Structlogger-benchmark
This benchmark tests performance of structlogger with Slf4j callback against SLF4j logger non structured logging. Logback is used as
implementation of Slf4j API and logstash encoder is used for serializing events coming from structlogger into json, logback outputs logs into files benchmark*.log.  
## Build
`mvn clean install`
## Run
`java -jar target/benchmarks.jar`
