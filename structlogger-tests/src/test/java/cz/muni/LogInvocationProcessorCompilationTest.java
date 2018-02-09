package cz.muni;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import cz.muni.fi.processor.LogInvocationProcessor;
import org.junit.Test;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

public class LogInvocationProcessorCompilationTest {

    @Test
    public void shouldNotCompileInsufficientParametrization() {
        final Compilation compilation =
                javac()
                        .withProcessors(new LogInvocationProcessor())
                        .compile(JavaFileObjects.forResource("InsufficientParametrization.java"));

        assertThat(compilation).hadErrorContaining(
                "literal Should not compile {} contains 1 variables, but statement defaultLog.info(\"Should not compile {}\").varDouble(1.2).varBoolean(false).log(); uses 2 variables [InsufficientParametrization:16]"
        );
    }

    @Test
    public void shouldNotCompileLogMethodNotCalled() {
        final Compilation compilation =
                javac()
                        .withProcessors(new LogInvocationProcessor())
                        .compile(JavaFileObjects.forResource("LogMethodNotCalled.java"));

        assertThat(compilation).hadErrorContaining(
                "statement defaultLog.info(\"Should not compile {} {}\").varDouble(1.2).varBoolean(false); must be ended by calling log() method [LogMethodNotCalled:16]"
        );
    }
}
