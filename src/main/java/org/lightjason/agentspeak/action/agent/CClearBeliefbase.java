package org.lightjason.agentspeak.action.agent;

import org.lightjason.agentspeak.action.IBaseAction;
import org.lightjason.agentspeak.common.CPath;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.fuzzy.IFuzzyValue;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;


/**
 * clears all elements of the beliefbase.
 * The action clear th beliefbase, the arguments
 * are optional and can be string paths to beliefbases
 *
 * {@code .agent/clearbeliefbase( "env", "foo" );}
 */
public final class CClearBeliefbase extends IBaseAction
{
    /**
     * serial id
     */
    private static final long serialVersionUID = 5832369527494082158L;
    /**
     * action name
     */
    private static final IPath NAME = namebyclass( CClearBeliefbase.class, "agent" );

    @Nonnull
    @Override
    public IPath name()
    {
        return NAME;
    }

    @Nonnull
    @Override
    public Stream<IFuzzyValue<?>> execute( final boolean p_parallel, @Nonnull final IContext p_context,
                                           @Nonnull final List<ITerm> p_argument, @Nonnull final List<ITerm> p_return
    )
    {
        p_context.agent()
                 .beliefbase()
                 .clear(
                     p_argument.size() == 0
                     ? null
                     : CCommon.flatten( p_argument )
                              .parallel()
                              .map( i -> CPath.of( i.raw() ) )
                              .toArray( IPath[]::new ) );

        return Stream.of();
    }

}
