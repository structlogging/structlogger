package org.ngmon.structlog;

import com.sun.tools.javac.tree.JCTree;

public class StatementInfo {

    private long lineNumber;
    private String sourceFileName;
    private JCTree.JCExpressionStatement statement;

    public StatementInfo(final long lineNumber,
                         final String sourceFileName,
                         final JCTree.JCExpressionStatement statement) {
        this.lineNumber = lineNumber;
        this.sourceFileName = sourceFileName;
        this.statement = statement;
    }

    public long getLineNumber() {
        return lineNumber;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public JCTree.JCExpressionStatement getStatement() {
        return statement;
    }
}
