package org.lightjason.agentspeak.action.agent;

import org.lightjason.agentspeak.action.IBaseAction;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.fuzzy.IFuzzyValue;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;


/**
 * sets the agent to the sleep state.
 * The action applys the sleep state
 * of the current agent. The first
 * optional argument can be a sleeping
 * time (in agent cycles)
 *
 * {@code .agent/sleep(3);}
 */
public final class CSleep extends IBaseAction
{
    /**
     * serial id
     */
    private static final long serialVersionUID = -8150278330935392034L;
    /**
     * action name
     */
    private static final IPath NAME = namebyclass( CSleep.class, "agent" );

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
        p_context.agent().sleep(

            p_argument.size() > 0
            ? p_argument.get( 0 ).<Number>raw().longValue()
            : Long.MAX_VALUE,

            p_argument.size() > 1
            ? p_argument.subList( 1, p_argument.size() ).stream()
            : Stream.empty()

        );

        return Stream.of();
    }

}
