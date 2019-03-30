package org.lightjason.agentspeak.action.agent;

import org.lightjason.agentspeak.action.IBaseAction;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.fuzzy.IFuzzyValue;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;


/**
 * action to pass data to the fuzzy-membership function.
 * The action passes any data to the agent fuzzy-membership
 * function for any modification of the function
 *
 * {@code .agent/fuzzymembership(5, "hello");}
 */
public final class CFuzzyMembership extends IBaseAction
{
    /**
     * serial id
     */
    private static final long serialVersionUID = 7815588503920853463L;
    /**
     * action name
     */
    private static final IPath NAME = namebyclass( CFuzzyMembership.class, "agent" );

    @Nonnull
    @Override
    public IPath name()
    {
        return NAME;
    }

    @Nonnull
    @Override
    public Stream<IFuzzyValue<?>> execute( final boolean p_parallel, @Nonnull final IContext p_context,
                                           @Nonnull final List<ITerm> p_argument, @Nonnull final List<ITerm> p_return )
    {
        return p_context.agent().fuzzy().membership().modify( CCommon.flatten( p_argument ) );
    }

}
