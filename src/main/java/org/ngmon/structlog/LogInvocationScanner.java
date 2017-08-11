package org.ngmon.structlog;

import static java.lang.String.format;
import static org.ngmon.structlog.POJOService.PACKAGE_NAME;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
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

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;


public class LogInvocationScanner extends TreePathScanner<Object, CompilationUnitTree> {

    private final HashMap<TypeMirror, ProviderVariables> varsHashMap;
    private final Map<Name, TypeMirror> fields;
    private final TreeMaker treeMaker;
    private final JavacElements elementUtils;
    private final Names names;
    private final POJOService pojoService;
    private final Messager messager;

    public LogInvocationScanner(final HashMap<TypeMirror, ProviderVariables> varsHashMap,
                                final Map<Name, TypeMirror> fields,
                                final ProcessingEnvironment processingEnvironment) {
        final Context context = ((JavacProcessingEnvironment) processingEnvironment).getContext();

        this.varsHashMap = varsHashMap;
        this.fields = fields;
        this.treeMaker = TreeMaker.instance(context);
        this.elementUtils = (JavacElements) processingEnvironment.getElementUtils();
        this.pojoService = new POJOService(processingEnvironment.getFiler());
        this.names = Names.instance(context);
        this.messager = processingEnvironment.getMessager();
    }

    @Override
    public Object visitClass(final ClassTree node, final CompilationUnitTree compilationUnitTree) {
        final JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) getCurrentPath().getLeaf();

        generateLoggerField(node, classDecl);

        generateEventLoggerField(classDecl);

        return super.visitClass(node, compilationUnitTree);
    }

    @Override
    public Object visitExpressionStatement(final ExpressionStatementTree node, final CompilationUnitTree compilationUnitTree) {

        final JCTree.JCExpressionStatement statement = (JCTree.JCExpressionStatement) getCurrentPath().getLeaf();

        final TreePathScanner scanner = new TreePathScanner<Object, CompilationUnitTree>() {
            Stack<MethodAndParameter> stack = new Stack<>();

            @Override
            public Object visitMethodInvocation(final MethodInvocationTree node, final CompilationUnitTree o) {
                if (node.getMethodSelect() instanceof JCTree.JCFieldAccess) {
                    try {
                        final JCTree.JCFieldAccess methodSelect = (JCTree.JCFieldAccess) node.getMethodSelect();
                        ExpressionTree parameter = null;
                        if (!node.getArguments().isEmpty()) {
                            parameter = node.getArguments().get(0);
                        }
                        stack.add(new MethodAndParameter(methodSelect.name, parameter));
                        handle(methodSelect, stack, node, statement);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return super.visitMethodInvocation(node, o);
            }

        };

        scanner.scan(getCurrentPath(), compilationUnitTree);

        return super.visitExpressionStatement(node, compilationUnitTree);
    }

    private void handle(final JCTree.JCFieldAccess fieldAccess, final Stack<MethodAndParameter> stack, final MethodInvocationTree node, final JCTree.JCExpressionStatement statement) throws Exception {
        if (fieldAccess.getExpression() instanceof JCTree.JCFieldAccess) {
            handle((JCTree.JCFieldAccess) fieldAccess.getExpression(), stack, node, statement);
        } else if (fieldAccess.getExpression() instanceof JCTree.JCIdent) {
            final JCTree.JCIdent ident = (JCTree.JCIdent) fieldAccess.getExpression();
            final Name name = ident.getName();
            if (fields.containsKey(name)) {
                handleStructLogExpression(stack, node, name, statement);
            }
        }
    }

    private void handleStructLogExpression(final Stack<MethodAndParameter> stack, final MethodInvocationTree node, final Name name, JCTree.JCExpressionStatement statement) {
        final SortedSet<VariableAndValue> usedVariables = new TreeSet<>();
        JCTree.JCLiteral literal = null;
        String level = null;

        final TypeMirror typeMirror = fields.get(name);
        final ProviderVariables providerVariables = varsHashMap.get(typeMirror);
        while (!stack.empty()) {
            final MethodAndParameter top = stack.pop();
            for (Variable variable : providerVariables.getVariables()) {
                final Name topMethodName = top.getMethodName();
                if (variable.getName().equals(topMethodName)) {
                    usedVariables.add(new VariableAndValue(variable, top.getParameter()));
                } else if (topMethodName.contentEquals("info")) {
                    if (!(node.getArguments().get(0) instanceof JCTree.JCLiteral)) {
                        messager.printMessage(Diagnostic.Kind.ERROR, format("method %s in %s statement must have String literal as argument", topMethodName, statement));
                        return;
                    }
                    literal = (JCTree.JCLiteral) node.getArguments().get(0);
                    level = "INFO";
                } else if (topMethodName.contentEquals("error")) {
                    if (!(node.getArguments().get(0) instanceof JCTree.JCLiteral)) {
                        messager.printMessage(Diagnostic.Kind.ERROR, format("method %s in %s statement must have String literal as argument", topMethodName, statement));
                        return;
                    }
                    literal = (JCTree.JCLiteral) node.getArguments().get(0);
                    level = "ERROR";
                } else if (topMethodName.contentEquals("debug")) {
                    if (!(node.getArguments().get(0) instanceof JCTree.JCLiteral)) {
                        messager.printMessage(Diagnostic.Kind.ERROR, format("method %s in %s statement must have String literal as argument", topMethodName, statement));
                        return;
                    }
                    literal = (JCTree.JCLiteral) node.getArguments().get(0);
                    level = "DEBUG";
                } else if (topMethodName.contentEquals("warn")) {
                    if (!(node.getArguments().get(0) instanceof JCTree.JCLiteral)) {
                        messager.printMessage(Diagnostic.Kind.ERROR, format("method %s in %s statement must have String literal as argument", topMethodName, statement));
                        return;
                    }
                    literal = (JCTree.JCLiteral) node.getArguments().get(0);
                    level = "WARN";
                }
            }
            if (stack.empty() && !top.getMethodName().contentEquals("log")) {
                messager.printMessage(Diagnostic.Kind.ERROR, format("statement %s must be ended by calling log() method", statement));
                return;
            }
        }

        final String className = pojoService.createPojo(literal, usedVariables);
        replaceInCode(className, statement, usedVariables, literal, level);
    }

    private void replaceInCode(final String className, final JCTree.JCExpressionStatement statement, SortedSet<VariableAndValue> usedVariables, JCTree.JCLiteral literal, String level) {
        final ListBuffer listBuffer = new ListBuffer();

        listBuffer.add(treeMaker.Literal(level));
        listBuffer.add(literal);
        for (VariableAndValue variableAndValue : usedVariables) {
            listBuffer.add(variableAndValue.getValue());
        }

        final JCTree.JCNewClass jcNewClass = treeMaker.NewClass(null, com.sun.tools.javac.util.List.nil(), treeMaker.Select(treeMaker.Ident(names.fromString(PACKAGE_NAME)), names.fromString(className)), listBuffer.toList(), null);
        final JCTree.JCMethodInvocation apply = treeMaker.Apply(
                com.sun.tools.javac.util.List.nil(),
                treeMaker.Select(
                        treeMaker.Ident(
                                elementUtils.getName("_eventLogger")
                        ),
                        elementUtils.getName(level.toLowerCase())
                ),
                com.sun.tools.javac.util.List.of(
                        jcNewClass
                )
        );
        statement.expr = apply;
    }

    private void generateEventLoggerField(final JCTree.JCClassDecl classDecl) {
        Symbol.ClassSymbol typeElement = elementUtils.getTypeElement("org.ngmon.structlog.EventLogger");

        final JCTree.JCNewClass jcNewClass = treeMaker.NewClass(
                null,
                List.nil(),
                treeMaker.Select(
                        treeMaker.Ident(names.fromString("org.ngmon.structlog")), names.fromString("EventLogger")
                ),
                List.of(
                        treeMaker.Ident(names.fromString("_logger"))
                ),
                null);

        JCTree.JCVariableDecl logger = treeMaker.VarDef(new Symbol.VarSymbol(Flags.STATIC | Flags.FINAL, elementUtils.getName("_eventLogger"), typeElement.asType(), null),
                jcNewClass);

        classDecl.defs = classDecl.defs.append(logger);
    }

    private void generateLoggerField(final ClassTree node, final JCTree.JCClassDecl classDecl) {
        Symbol.ClassSymbol typeElement = elementUtils.getTypeElement("org.slf4j.Logger");

        JCTree.JCVariableDecl logger = treeMaker.VarDef(new Symbol.VarSymbol(Flags.STATIC | Flags.FINAL, elementUtils.getName("_logger"), typeElement.asType(), null),
                treeMaker.Apply(
                        com.sun.tools.javac.util.List.nil(),
                        treeMaker.Select(
                                treeMaker.Type(
                                        elementUtils.getTypeElement("org.slf4j.LoggerFactory").type
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
