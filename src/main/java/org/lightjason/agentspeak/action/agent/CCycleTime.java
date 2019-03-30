package org.lightjason.agentspeak.action.agent;

import org.lightjason.agentspeak.action.IBaseAction;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.fuzzy.IFuzzyValue;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;


/**
 * action to get cycle time.
 * The action returns the time between
 * the last cycle and the current time
 * in nanoseconds
 *
 * {@code T = .agent/cycletime();}
 */
public final class CCycleTime extends IBaseAction
{
    /**
     * serial id
     */
    private static final long serialVersionUID = 3781558534685092083L;
    /**
     * action name
     */
    private static final IPath NAME = namebyclass( CCycleTime.class, "agent" );

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
        p_return.add(
            CRawTerm.of(
                System.nanoTime() - p_context.agent().cycletime()
            )
        );
        return Stream.of();
    }

}
