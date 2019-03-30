package org.lightjason.agentspeak.action.agent;

import com.codepoetics.protonpack.StreamUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.lightjason.agentspeak.action.IBaseAction;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.error.context.CExecutionIllegealArgumentException;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.instantiable.plan.trigger.ITrigger;
import org.lightjason.agentspeak.language.fuzzy.IFuzzyValue;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;


/**
 * removes a plan by the plan trigger.
 * The action moves all plan based on the
 * input trigger arguments, the action fails
 * on wrong input
 *
 * {@code .agent/removeplan( "+!", "myplan(X)", "-!", Literal );}
 */
@SuppressFBWarnings( "GC_UNRELATED_TYPES" )
public final class CRemovePlan extends IBaseAction
{

    /**
     * serial id
     */
    private static final long serialVersionUID = -2509947123811973880L;
    /**
     * action name
     */
    private static final IPath NAME = namebyclass( CRemovePlan.class, "agent" );

    @Nonnull
    @Override
    public IPath name()
    {
        return NAME;
    }

    @Nonnegative
    @Override
    public int minimalArgumentNumber()
    {
        return 1;
    }

    @Nonnull
    @Override
    public Stream<IFuzzyValue<?>> execute( final boolean p_parallel, @Nonnull final IContext p_context,
                                           @Nonnull final List<ITerm> p_argument, @Nonnull final List<ITerm> p_return )
    {
        return StreamUtils.windowed( CCommon.flatten( p_argument ), 2, 2 )
                          .allMatch( i -> CRemovePlan.remove( ITrigger.EType.of( i.get( 0 ).raw() ), i.get( 1 ), p_context ) )
               ? Stream.of()
               : p_context.agent().fuzzy().membership().fail();
    }

    /**
     * removes the plan based on the trigger
     *
     * @param p_trigger trigger type
     * @param p_literal literal as string or literal object
     * @param p_context execution context
     * @return flag to remove plan successfully
     */
    private static boolean remove( @Nonnull final ITrigger.EType p_trigger, @Nonnull final ITerm p_literal, @Nonnull final IContext p_context )
    {
        final ILiteral l_literal;
        try
        {

            l_literal = CCommon.isssignableto( p_literal, ILiteral.class )
                        ? p_literal.raw()
                        : CLiteral.parse( p_literal.raw() );

        }
        catch ( final Exception l_exception )
        {
            throw new CExecutionIllegealArgumentException( p_context, l_exception );
        }

        return !p_context.agent().plans().removeAll( p_trigger.builddefault( l_literal ) ).isEmpty();
    }
}
