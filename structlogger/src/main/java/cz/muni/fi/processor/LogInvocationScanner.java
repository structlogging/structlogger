package cz.muni.fi.processor;

import static java.lang.String.format;

import com.squareup.javapoet.JavaFile;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.util.TreePathScanner;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;
import cz.muni.fi.EventLogger;
import cz.muni.fi.annotation.LoggerContext;
import cz.muni.fi.processor.service.POJOService;
import cz.muni.fi.utils.GeneratedClassInfo;
import cz.muni.fi.utils.MethodAndParameter;
import cz.muni.fi.utils.ProviderVariables;
import cz.muni.fi.utils.ScannerParams;
import cz.muni.fi.utils.StatementInfo;
import cz.muni.fi.utils.StructLoggerFieldContext;
import cz.muni.fi.utils.Variable;
import cz.muni.fi.utils.VariableAndValue;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * TreePathScanner which takes care of structured log statements replacement
 */
public class LogInvocationScanner extends TreePathScanner<Object, ScannerParams> {


    private final HashMap<TypeMirror, ProviderVariables> varsHashMap;
    private final Map<Name, StructLoggerFieldContext> fields;
    private final TreeMaker treeMaker;
    private final JavacElements elementUtils;
    private final Names names;
    private final POJOService pojoService;
    private final Messager messager;
    private final Set<GeneratedClassInfo> generatedClassesNames;

    public LogInvocationScanner(final HashMap<TypeMirror, ProviderVariables> varsHashMap,
                                final Map<Name, StructLoggerFieldContext> fields,
                                final ProcessingEnvironment processingEnvironment,
                                final Set<GeneratedClassInfo> generatedClassesNames) {
        final Context context = ((JavacProcessingEnvironment) processingEnvironment).getContext();

        this.varsHashMap = varsHashMap;
        this.fields = fields;
        this.treeMaker = TreeMaker.instance(context);
        this.elementUtils = (JavacElements) processingEnvironment.getElementUtils();
        this.pojoService = new POJOService(processingEnvironment.getFiler());
        this.names = Names.instance(context);
        this.messager = processingEnvironment.getMessager();
        this.generatedClassesNames = generatedClassesNames;
    }

    /**
     *  Checks expressions, if expression is method call on {@link LoggerContext} field, it is considered structured log statement and is
     *  expression is transformed in such way, that expression is replaced with call to {@link EventLogger} with generated Event for given expression
     */
    @Override
    public Object visitExpressionStatement(final ExpressionStatementTree node, final ScannerParams scannerParams) {

        final JCTree.JCExpressionStatement statement = (JCTree.JCExpressionStatement) getCurrentPath().getLeaf();

        final StatementInfo statementInfo = new StatementInfo(scannerParams.getCompilationUnitTree().getLineMap().getLineNumber(statement.pos),
                scannerParams.getTypeElement().getQualifiedName().toString(),
                statement);

        final TreePathScanner scanner = new TreePathScanner<Object, ScannerParams>() {
            Stack<MethodAndParameter> stack = new Stack<>();

            @Override
            public Object visitMethodInvocation(final MethodInvocationTree node, final ScannerParams o) {
                if (node.getMethodSelect() instanceof JCTree.JCFieldAccess) {
                    try {
                        final JCTree.JCFieldAccess methodSelect = (JCTree.JCFieldAccess) node.getMethodSelect();
                        ExpressionTree parameter = null;
                        if (!node.getArguments().isEmpty()) {
                            parameter = node.getArguments().get(0);
                        }
                        stack.add(new MethodAndParameter(methodSelect.name, parameter));
                        handle(methodSelect, stack, node, statementInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return super.visitMethodInvocation(node, o);
            }

        };

        scanner.scan(getCurrentPath(), scannerParams);

        return super.visitExpressionStatement(node, scannerParams);
    }

    private void handle(final JCTree.JCFieldAccess fieldAccess, final Stack<MethodAndParameter> stack, final MethodInvocationTree node, final StatementInfo statementInfo) throws Exception {
        if (fieldAccess.getExpression() instanceof JCTree.JCFieldAccess) {
            handle((JCTree.JCFieldAccess) fieldAccess.getExpression(), stack, node, statementInfo);
        } else if (fieldAccess.getExpression() instanceof JCTree.JCIdent) {
            final JCTree.JCIdent ident = (JCTree.JCIdent) fieldAccess.getExpression();
            final Name name = ident.getName();
            if (fields.containsKey(name)) {
                handleStructLogExpression(stack, node, name, statementInfo);
            }
        }
    }

    /**
     *
     * @param stack all method calls on one line
     * @param node
     * @param name of field
     * @param statementInfo about whole one line statement
     */
    private void handleStructLogExpression(final Stack<MethodAndParameter> stack, final MethodInvocationTree node, final Name name, final StatementInfo statementInfo) {
        final java.util.List<VariableAndValue> usedVariables = new ArrayList<>();
        JCTree.JCLiteral literal = null;
        String level = null;
        String eventName = null;

        //statement check
        final StructLoggerFieldContext structLoggerFieldContext = fields.get(name);
        final TypeMirror typeMirror = structLoggerFieldContext.getContextProvider();
        final ProviderVariables providerVariables = varsHashMap.get(typeMirror);
        while (!stack.empty()) {
            final MethodAndParameter top = stack.pop();
            for (Variable variable : providerVariables.getVariables()) {
                final Name topMethodName = top.getMethodName();
                if (variable.getName().equals(topMethodName)) {
                    addToUsedVariables(usedVariables, top, variable);
                } else if (topMethodName.contentEquals("info")) {
                    if (!(node.getArguments().get(0) instanceof JCTree.JCLiteral)) {
                        messager.printMessage(Diagnostic.Kind.ERROR, format("method %s in %s statement must have String literal as argument", topMethodName, statementInfo.getStatement()));
                        return;
                    }
                    literal = (JCTree.JCLiteral) node.getArguments().get(0);
                    level = "INFO";
                } else if (topMethodName.contentEquals("error")) {
                    if (!(node.getArguments().get(0) instanceof JCTree.JCLiteral)) {
                        messager.printMessage(Diagnostic.Kind.ERROR, format("method %s in %s statement must have String literal as argument", topMethodName, statementInfo.getStatement()));
                        return;
                    }
                    literal = (JCTree.JCLiteral) node.getArguments().get(0);
                    level = "ERROR";
                } else if (topMethodName.contentEquals("debug")) {
                    if (!(node.getArguments().get(0) instanceof JCTree.JCLiteral)) {
                        messager.printMessage(Diagnostic.Kind.ERROR, format("method %s in %s statement must have String literal as argument", topMethodName, statementInfo.getStatement()));
                        return;
                    }
                    literal = (JCTree.JCLiteral) node.getArguments().get(0);
                    level = "DEBUG";
                } else if (topMethodName.contentEquals("warn")) {
                    if (!(node.getArguments().get(0) instanceof JCTree.JCLiteral)) {
                        messager.printMessage(Diagnostic.Kind.ERROR, format("method %s in %s statement must have String literal as argument", topMethodName, statementInfo.getStatement()));
                        return;
                    }
                    literal = (JCTree.JCLiteral) node.getArguments().get(0);
                    level = "WARN";
                } else if (topMethodName.contentEquals("trace")) {
                    if (!(node.getArguments().get(0) instanceof JCTree.JCLiteral)) {
                        messager.printMessage(Diagnostic.Kind.ERROR, format("method %s in %s statement must have String literal as argument", topMethodName, statementInfo.getStatement()));
                        return;
                    }
                    literal = (JCTree.JCLiteral) node.getArguments().get(0);
                    level = "TRACE";
                } else if (topMethodName.contentEquals("audit")) {
                    if (!(node.getArguments().get(0) instanceof JCTree.JCLiteral)) {
                        messager.printMessage(Diagnostic.Kind.ERROR, format("method %s in %s statement must have String literal as argument", topMethodName, statementInfo.getStatement()));
                        return;
                    }
                    literal = (JCTree.JCLiteral) node.getArguments().get(0);
                    level = "AUDIT";
                }
            }
            if (top.getMethodName().contentEquals("log") && top.getParameter() != null) {
                if (!(top.getParameter() instanceof JCTree.JCLiteral)){
                    messager.printMessage(Diagnostic.Kind.ERROR, format("method %s in %s statement must have String literal as argument", top.getMethodName(), statementInfo.getStatement()));
                    return;
                }
                eventName = ((JCTree.JCLiteral) top.getParameter()).getValue().toString();
                if (eventName.contains(" ") || eventName.contains(System.lineSeparator())) {
                    messager.printMessage(Diagnostic.Kind.ERROR, format("method %s in %s statement must have String literal without spaces and new lines as argument", top.getMethodName(), statementInfo.getStatement()));
                    return;
                }
            }
            if (stack.empty() && !top.getMethodName().contentEquals("log")) {
                messager.printMessage(Diagnostic.Kind.ERROR, format("statement %s must be ended by calling log() method", statementInfo.getStatement()));
                return;
            }
        }

        //parametrization check
        final int countOfStringVariables = StringUtils.countMatches(literal.getValue().toString(), "{}");
        if (countOfStringVariables != usedVariables.size()) {
            messager.printMessage(Diagnostic.Kind.ERROR, format("literal %s contains %d variables, but statement %s uses %d variables",
                    literal.getValue().toString(), countOfStringVariables, statementInfo.getStatement(), usedVariables.size()));
            return;
        }

        //event class generation
        final JavaFile javaFile = pojoService.createPojo(eventName, literal, usedVariables);
        final String className = javaFile.typeSpec.name;
        final GeneratedClassInfo generatedClassInfo = new GeneratedClassInfo(POJOService.PACKAGE_NAME + "." + className, className, (String) literal.getValue(), usedVariables);
        for (GeneratedClassInfo info : generatedClassesNames) {
            if (info.getQualifiedName().equals(generatedClassInfo.getQualifiedName())
                    && !info.getUsedVariables().equals(generatedClassInfo.getUsedVariables())
                    ) {
                messager.printMessage(Diagnostic.Kind.ERROR, format("Statement %s generates different event structure for same event name", statementInfo.getStatement()));
                return;
            }
        }
        generatedClassesNames.add(generatedClassInfo);

        pojoService.writeJavaFile(javaFile);

        //replace statement
        replaceInCode(name.toString(), className, statementInfo, usedVariables, literal, level);
    }

    private void addToUsedVariables(final java.util.List<VariableAndValue> usedVariables, final MethodAndParameter top, final Variable variable) {
        VariableAndValue variableAndValue = new VariableAndValue(variable, top.getParameter());
        if (!usedVariables.contains(variableAndValue)) {
            usedVariables.add(variableAndValue);
        } else {
            int i = 0;
            do {
                i++;
                variableAndValue = new VariableAndValue(new Variable(elementUtils.getName(variable.getName().toString() + i), variable.getType()),
                        top.getParameter());
            } while (usedVariables.contains(variableAndValue));
            usedVariables.add(variableAndValue);
        }
    }

    /**
     * replaces statement with our improved call to {@link EventLogger}
     * @param loggerName
     * @param className
     * @param statementInfo
     * @param usedVariables
     * @param literal
     * @param level
     */
    private void replaceInCode(final String loggerName, final String className, final StatementInfo statementInfo, java.util.List<VariableAndValue> usedVariables, JCTree.JCLiteral literal, String level) {
        final ListBuffer listBuffer = new ListBuffer();

        listBuffer.add(createEventLoggerFormatCall(usedVariables, literal));

        listBuffer.add(treeMaker.Literal(statementInfo.getSourceFileName()));
        listBuffer.add(treeMaker.Literal(statementInfo.getLineNumber()));
        listBuffer.add(treeMaker.Literal(className));
        listBuffer.add(treeMaker.Apply(
                com.sun.tools.javac.util.List.nil(),
                treeMaker.Select(
                        treeMaker.Select(
                                treeMaker.Select(
                                        treeMaker.Ident(names.fromString("cz.muni.fi")), names.fromString("EventLogger")
                                ), names.fromString("SEQ_NUMBER")
                        ), names.fromString("incrementAndGet")
                ),
                List.nil())
        );
        listBuffer.add(treeMaker.Literal(level));
        addVariablesToBuffer(usedVariables, listBuffer);

        final JCTree.JCNewClass jcNewClass = treeMaker.NewClass(null, com.sun.tools.javac.util.List.nil(), treeMaker.Select(treeMaker.Ident(names.fromString(POJOService.PACKAGE_NAME)), names.fromString(className)), listBuffer.toList(), null);
        final JCTree.JCMethodInvocation apply = treeMaker.Apply(
                com.sun.tools.javac.util.List.nil(),
                treeMaker.Select(
                        treeMaker.Ident(
                                elementUtils.getName(loggerName)
                        ),
                        elementUtils.getName(level.toLowerCase() + "Event")
                ),
                com.sun.tools.javac.util.List.of(
                        jcNewClass
                )
        );
        statementInfo.getStatement().expr = apply;
    }

    private JCTree.JCMethodInvocation createEventLoggerFormatCall(final java.util.List<VariableAndValue> usedVariables, final JCTree.JCLiteral literal) {
        final ListBuffer lb = new ListBuffer();
        lb.add(literal);
        addVariablesToBuffer(usedVariables, lb);

        return treeMaker.Apply(List.nil(), treeMaker.Select(
                treeMaker.Select(
                        treeMaker.Ident(names.fromString(EventLogger.class.getPackage().getName())), names.fromString(EventLogger.class.getSimpleName())
                ), names.fromString("format")
        ), lb.toList());
    }

    private void addVariablesToBuffer(final java.util.List<VariableAndValue> usedVariables, final ListBuffer listBuffer) {
        for (VariableAndValue variableAndValue : usedVariables) {
            listBuffer.add(variableAndValue.getValue());
        }
    }

}
