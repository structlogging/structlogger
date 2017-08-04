package org.ngmon.structlog;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.util.TreePathScanner;
import com.sun.tools.javac.tree.JCTree;

import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


public class LogInvocationScanner extends TreePathScanner<Object, CompilationUnitTree> {
    HashMap<TypeMirror, VarContextProviderVars> varsHashMap;
    Map<Name, TypeMirror> fields;


    public LogInvocationScanner(HashMap<TypeMirror, VarContextProviderVars> varsHashMap, Map<Name, TypeMirror> fields) {
        this.varsHashMap = varsHashMap;
        this.fields = fields;
    }

    @Override
    public Object visitMethodInvocation(final MethodInvocationTree node, final CompilationUnitTree compilationUnitTree) {

        return super.visitMethodInvocation(node, compilationUnitTree);
    }


    @Override
    public Object visitMemberSelect(final MemberSelectTree node, final CompilationUnitTree compilationUnitTree) {
        return super.visitMemberSelect(node, compilationUnitTree);
    }

    @Override
    public Object visitExpressionStatement(final ExpressionStatementTree node, final CompilationUnitTree compilationUnitTree) {
        System.out.println("--------------------------------------------");

        System.out.println("EXPRESSION: \n" + node.getExpression());

        TreePathScanner scanner = new TreePathScanner<Object, CompilationUnitTree>() {
            Stack<Name> stack = new Stack<>();

            @Override
            public Object visitMethodInvocation(final MethodInvocationTree node, final CompilationUnitTree o) {
                if (node.getMethodSelect() instanceof JCTree.JCFieldAccess) {
                    try {
                        stack.add(((JCTree.JCFieldAccess) node.getMethodSelect()).name);
                        handle((JCTree.JCFieldAccess) node.getMethodSelect(), node, stack);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return super.visitMethodInvocation(node, o);
            }

        };

        scanner.scan(getCurrentPath(), compilationUnitTree);


        System.out.println("--------------------------------------------");

        return super.visitExpressionStatement(node, compilationUnitTree);
    }

    private void handle(final JCTree.JCFieldAccess fieldAccess, final MethodInvocationTree node, final Stack<Name> stack) throws Exception {
        if (fieldAccess.getExpression() instanceof JCTree.JCFieldAccess) {
            handle((JCTree.JCFieldAccess) fieldAccess.getExpression(), node, stack);
        } else if (fieldAccess.getExpression() instanceof JCTree.JCIdent) {
            final JCTree.JCIdent ident = (JCTree.JCIdent) fieldAccess.getExpression();
            final Name name = ident.getName();
            if(fields.containsKey(name)) {
                final TypeMirror typeMirror = fields.get(name);
                final VarContextProviderVars varContextProviderVars = varsHashMap.get(typeMirror);
                System.out.print("Stack: ");
                while (!stack.empty()) {
                    Name pop = stack.pop();
                    System.out.print(pop + " ");
                }
                System.out.println("\nNAME:" + name + "  Provider:" + typeMirror + " Vars:" + varContextProviderVars);
            }
        }
    }

}
