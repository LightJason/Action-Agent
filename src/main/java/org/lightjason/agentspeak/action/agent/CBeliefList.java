package org.lightjason.agentspeak.action.agent;

import org.lightjason.agentspeak.action.IBaseAction;
import org.lightjason.agentspeak.common.CPath;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.fuzzy.IFuzzyValue;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * returns a list of all belief literals.
 * The action creates a list of literals,
 * the arguments are optional, the first
 * argument is a boolean for the negation
 * definition, the second argument is the
 * belief functor, if no arguments are give,
 * the full belieflist is returned
 *
 * {@code L = .agent/belieflist( true, "path/subpath/literalfunctor" );}
 */
public final class CBeliefList extends IBaseAction
{
    /**
     * serial id
     */
    private static final long serialVersionUID = 6884092740048107959L;
    /**
     * action name
     */
    private static final IPath NAME = namebyclass( CBeliefList.class, "agent" );

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
        final List<ILiteral> l_literal = (
            p_argument.isEmpty()
            ? p_context.agent().beliefbase().stream()
            : p_argument.size() == 1
              ? p_context.agent().beliefbase().stream( CPath.of( p_argument.get( 0 ).raw() ) )
              : p_context.agent().beliefbase().stream( p_argument.get( 1 ).<Boolean>raw(), CPath.of( p_argument.get( 0 ).raw() ) )
        ).collect( Collectors.toList() );

        p_return.add(
            CRawTerm.of( p_parallel
                         ? Collections.synchronizedList( l_literal )
                         : l_literal
            )
        );

        return Stream.of();
    }

}
