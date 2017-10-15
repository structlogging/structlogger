package cz.muni.fi;

import cz.muni.fi.annotation.Var;
import cz.muni.fi.annotation.VarContextProvider;

@VarContextProvider
public interface BlockCacheContext extends VariableContext {

    @Var
    BlockCacheContext blockId(long blockId);

    @Var
    BlockCacheContext dataNodeUuid(long dataNodeUuid);

    @Var
    BlockCacheContext numCached(int numCached);

    @Var
    BlockCacheContext neededCached(int neededCached);

    @Var
    BlockCacheContext reason(String reason);

    @Var
    BlockCacheContext context(int context);

    @Var
    BlockCacheContext object(Test test);

}
