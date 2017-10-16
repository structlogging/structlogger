package cz.muni.fi.processor;

import static java.lang.String.format;

import com.squareup.javapoet.JavaFile;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.util.TreePathScanner;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;
import cz.muni.fi.EventLogger;
import cz.muni.fi.StructLogger;
import cz.muni.fi.service.POJOService;
import cz.muni.fi.utils.GeneratedClassInfo;
import cz.muni.fi.utils.ProviderVariables;
import cz.muni.fi.utils.ScannerParams;
import cz.muni.fi.utils.StatementInfo;
import cz.muni.fi.utils.Variable;
import cz.muni.fi.utils.VariableAndValue;
import org.apache.commons.lang3.StringUtils;
import cz.muni.fi.utils.MethodAndParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final String EVENT_LOGGER_FIELD_NAME = "_eventLogger";
    private static final String LOGGER_FIELD_NAME = "_logger";

    private final HashMap<TypeMirror, ProviderVariables> varsHashMap;
    private final Map<Name, TypeMirror> fields;
    private final TreeMaker treeMaker;
    private final JavacElements elementUtils;
    private final Names names;
    private final POJOService pojoService;
    private final Messager messager;
    private final Set<GeneratedClassInfo> generatedClassesNames;

    public LogInvocationScanner(final HashMap<TypeMirror, ProviderVariables> varsHashMap,
                                final Map<Name, TypeMirror> fields,
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
     * Generates {@link Logger} field and {@link EventLogger} field static/non-static based on context (non static inner classes cannot have static fields, etc.)
     * fields are preferably generated as static
     */
    @Override
    public Object visitClass(final ClassTree node, final ScannerParams scannerParams) {
        final JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) getCurrentPath().getLeaf();

        // generate static logger fields only in static inner classes or not inner classes
        if (classDecl.getModifiers().getFlags().contains(Flags.STATIC) || scannerParams.getTypeElement().getSimpleName().toString().equals(classDecl.name.toString())) {
            generateLoggerField(node, classDecl, true);
            generateEventLoggerField(classDecl, true);
        }
        else { //generate non static logger fields in inner classes
            generateLoggerField(node, classDecl, false);
            generateEventLoggerField(classDecl, false);
        }

        return super.visitClass(node, scannerParams);
    }

    /**
     *  Checks expressions, if expression is method call on {@link cz.muni.fi.annotation.VarContext} field, it is considered structured log statement and is
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

    private void handleStructLogExpression(final Stack<MethodAndParameter> stack, final MethodInvocationTree node, final Name name, final StatementInfo statementInfo) {
        final java.util.List<VariableAndValue> usedVariables = new ArrayList<>();
        JCTree.JCLiteral literal = null;
        String level = null;

        final TypeMirror typeMirror = fields.get(name);
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
                }
            }
            if (stack.empty() && !top.getMethodName().contentEquals("log")) {
                messager.printMessage(Diagnostic.Kind.ERROR, format("statement %s must be ended by calling log() method", statementInfo.getStatement()));
                return;
            }
        }

        final int countOfStringVariables = StringUtils.countMatches(literal.getValue().toString(), "{}");
        if (countOfStringVariables != usedVariables.size()) {
            messager.printMessage(Diagnostic.Kind.ERROR, format("literal %s contains %d variables, but statement %s uses %d variables",
                    literal.getValue().toString(), countOfStringVariables, statementInfo.getStatement(), usedVariables.size()));
            return;
        }
        final JavaFile javaFile = pojoService.createPojo(literal, usedVariables);
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
        replaceInCode(className, statementInfo, usedVariables, literal, level);
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
     * replaces statement with our call to {@link EventLogger}
     * @param className
     * @param statementInfo
     * @param usedVariables
     * @param literal
     * @param level
     */
    private void replaceInCode(final String className, final StatementInfo statementInfo, java.util.List<VariableAndValue> usedVariables, JCTree.JCLiteral literal, String level) {
        final ListBuffer listBuffer = new ListBuffer();
        listBuffer.add(treeMaker.Literal(level));

        listBuffer.add(createStructLoggerFormatCall(usedVariables, literal));

        listBuffer.add(treeMaker.Literal(statementInfo.getSourceFileName()));
        listBuffer.add(treeMaker.Literal(statementInfo.getLineNumber()));
        addVariablesToBuffer(usedVariables, listBuffer);

        final JCTree.JCNewClass jcNewClass = treeMaker.NewClass(null, com.sun.tools.javac.util.List.nil(), treeMaker.Select(treeMaker.Ident(names.fromString(POJOService.PACKAGE_NAME)), names.fromString(className)), listBuffer.toList(), null);
        final JCTree.JCMethodInvocation apply = treeMaker.Apply(
                com.sun.tools.javac.util.List.nil(),
                treeMaker.Select(
                        treeMaker.Ident(
                                elementUtils.getName(EVENT_LOGGER_FIELD_NAME)
                        ),
                        elementUtils.getName(level.toLowerCase())
                ),
                com.sun.tools.javac.util.List.of(
                        jcNewClass
                )
        );
        statementInfo.getStatement().expr = apply;
    }

    private JCTree.JCMethodInvocation createStructLoggerFormatCall(final java.util.List<VariableAndValue> usedVariables, final JCTree.JCLiteral literal) {
        final ListBuffer lb = new ListBuffer();
        lb.add(literal);
        addVariablesToBuffer(usedVariables, lb);

        return treeMaker.Apply(List.nil(), treeMaker.Select(
                treeMaker.Select(
                        treeMaker.Ident(names.fromString(StructLogger.class.getPackage().getName())), names.fromString(StructLogger.class.getSimpleName())
                ), names.fromString("format")
        ), lb.toList());
    }

    private void addVariablesToBuffer(final java.util.List<VariableAndValue> usedVariables, final ListBuffer listBuffer) {
        for (VariableAndValue variableAndValue : usedVariables) {
            listBuffer.add(variableAndValue.getValue());
        }
    }

    /**
     * generates {@link EventLogger} for given class
     * @param classDecl
     * @param shouldBeStatic
     */
    private void generateEventLoggerField(final JCTree.JCClassDecl classDecl, final boolean shouldBeStatic) {
        Symbol.ClassSymbol typeElement = elementUtils.getTypeElement(EventLogger.class.getName());

        final JCTree.JCNewClass jcNewClass = treeMaker.NewClass(
                null,
                List.nil(),
                treeMaker.Select(
                        treeMaker.Ident(names.fromString(EventLogger.class.getPackage().getName())), names.fromString(EventLogger.class.getSimpleName())
                ),
                List.of(
                        treeMaker.Ident(names.fromString(LOGGER_FIELD_NAME))
                ),
                null);

        Symbol.VarSymbol varSymbol;

        if(shouldBeStatic) {
            varSymbol = new Symbol.VarSymbol(Flags.STATIC | Flags.FINAL, elementUtils.getName(EVENT_LOGGER_FIELD_NAME), typeElement.asType(), null);
        }
        else {
            varSymbol = new Symbol.VarSymbol(Flags.FINAL, elementUtils.getName(EVENT_LOGGER_FIELD_NAME), typeElement.asType(), null);
        }

        JCTree.JCVariableDecl logger = treeMaker.VarDef(varSymbol,
                jcNewClass);

        classDecl.defs = classDecl.defs.append(logger);
    }

    /**
     * generates {@link Logger} for given class
     * @param node
     * @param classDecl
     * @param shouldBeStatic
     */
    private void generateLoggerField(final ClassTree node, final JCTree.JCClassDecl classDecl, final boolean shouldBeStatic) {
        Symbol.ClassSymbol typeElement = elementUtils.getTypeElement(Logger.class.getName());
        Symbol.VarSymbol varSymbol;

        if (shouldBeStatic) {
            varSymbol = new Symbol.VarSymbol(Flags.STATIC | Flags.FINAL, elementUtils.getName(LOGGER_FIELD_NAME), typeElement.asType(), null);
        } else {
            varSymbol = new Symbol.VarSymbol(Flags.FINAL, elementUtils.getName(LOGGER_FIELD_NAME), typeElement.asType(), null);
        }

        JCTree.JCVariableDecl logger = treeMaker.VarDef(varSymbol,
                treeMaker.Apply(
                        com.sun.tools.javac.util.List.nil(),
                        treeMaker.Select(
                                treeMaker.Type(
                                        elementUtils.getTypeElement(LoggerFactory.class.getName()).type
                                ),
                                elementUtils.getName("getLogger")
                        ),
                        com.sun.tools.javac.util.List.of(
                                treeMaker.Literal(
                                        node.getSimpleName().toString()
                                )
                        )
                ));

        classDecl.defs = classDecl.defs.append(logger);
    }

}
