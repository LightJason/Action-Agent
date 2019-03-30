package org.lightjason.agentspeak.action.agent;

import org.lightjason.agentspeak.action.IBaseAction;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.fuzzy.IFuzzyValue;

import javax.annotation.Nonnull;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * action to get plan-information as list.
 * The action returns a list of tuples with
 * the a string (trigger definition) and the
 * plan literal
 *
 * {@code L = .agent/planlist();}
 */
public final class CPlanList extends IBaseAction
{
    /**
     * serial id
     */
    private static final long serialVersionUID = 4584573308355332034L;
    /**
     * action name
     */
    private static final IPath NAME = namebyclass( CPlanList.class, "agent" );

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
        final List<?> l_list = p_context.agent()
                                        .plans()
                                        .values()
                                        .stream()
                                        .map( i -> i.plan().trigger() )
                                        .sorted()
                                        .distinct()
                                        .map( i -> new AbstractMap.SimpleImmutableEntry<>( i.type().toString(), i.literal() ) )
                                        .collect( Collectors.toList() );

        p_return.add(
            CRawTerm.of(
                p_parallel
                ? Collections.synchronizedList( l_list )
                : l_list
            )
        );

        return Stream.of();
    }
}
