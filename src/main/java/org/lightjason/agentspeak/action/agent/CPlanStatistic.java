package org.lightjason.agentspeak.action.agent;

import org.lightjason.agentspeak.action.IBaseAction;
import org.lightjason.agentspeak.agent.IAgent;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.instantiable.plan.IPlan;
import org.lightjason.agentspeak.language.execution.instantiable.plan.statistic.IPlanStatistic;
import org.lightjason.agentspeak.language.execution.instantiable.plan.trigger.ITrigger;
import org.lightjason.agentspeak.language.fuzzy.IFuzzyValue;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;


/**
 * action to get plan statistic.
 * The actions returns for each plan the statistic information,
 * for each plan argument, succesfull, fail and sum rate is returned,
 * the action fails if the plan does not exist within the plan-base
 *
 * {@code [Successful1|Fail1|Sum1|Successful2|Fail2|Sum2] = .agent/planstatistic( Plan1, Plan2 );}
 */
public final class CPlanStatistic extends IBaseAction
{

    /**
     * serial id
     */
    private static final long serialVersionUID = 432941607230785685L;
    /**
     * action name
     */
    private static final IPath NAME = namebyclass( CPlanStatistic.class, "agent" );

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
                                           @Nonnull final List<ITerm> p_argument, @Nonnull final List<ITerm> p_return
    )
    {
        return CCommon.flatten( p_argument ).allMatch( i -> CPlanStatistic.statistic( i.<IPlan>raw().trigger(), p_context.agent(), p_return ) )
               ? Stream.of()
               : p_context.agent().fuzzy().membership().fail();
    }

    /**
     * creates the plan statistic
     *
     * @param p_trigger plan trigger
     * @param p_agent agent
     * @param p_return return arguments
     * @return successfull flag
     */
    private static boolean statistic( @Nonnull final ITrigger p_trigger, @Nonnull final IAgent<?> p_agent, @Nonnull final List<ITerm> p_return )
    {
        final Collection<IPlanStatistic> l_plans = p_agent.plans().get( p_trigger );
        if ( l_plans.isEmpty() )
            return false;

        final double l_success = l_plans.parallelStream().mapToDouble( IPlanStatistic::successful ).sum();
        final double l_fail = l_plans.parallelStream().mapToDouble( IPlanStatistic::fail ).sum();

        p_return.add( CRawTerm.of( l_success ) );
        p_return.add( CRawTerm.of( l_fail ) );
        p_return.add( CRawTerm.of( l_success + l_fail ) );

        return true;
    }
}
