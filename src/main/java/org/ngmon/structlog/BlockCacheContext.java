package org.ngmon.structlog;

import org.ngmon.structlog.annotation.Var;
import org.ngmon.structlog.annotation.VarContext;

@VarContext
public interface BlockCacheContext extends VariableContext {

    @Var
    BlockCacheContext blockId(long blockId);

    @Var
    BlockCacheContext dataNodeUuid(long dataNodeUuid);
}
