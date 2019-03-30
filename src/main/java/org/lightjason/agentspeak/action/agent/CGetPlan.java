/*
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the LightJason                                                #
 * # Copyright (c) 2015-19, LightJason (info@lightjason.org)                            #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU Lesser General Public License as                     #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU Lesser General Public License for more details.                                #
 * #                                                                                    #
 * # You should have received a copy of the GNU Lesser General Public License           #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

package org.lightjason.agentspeak.action.agent;

import com.codepoetics.protonpack.StreamUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.lightjason.agentspeak.action.IBaseAction;
import org.lightjason.agentspeak.agent.IAgent;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.instantiable.plan.statistic.IPlanStatistic;
import org.lightjason.agentspeak.language.execution.instantiable.plan.trigger.ITrigger;
import org.lightjason.agentspeak.language.fuzzy.IFuzzyValue;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * action to get plan objects.
 * The actions returns a list of plans of a trigger,
 * the arguments are tuples of a string with the trigger
 * sequence (+!, +, -, -!) and a string or literal, the plan name,
 * for each tuple the plan object will returned within a list, the
 * action fails on non-existing plan
 *
 * {@code [L1|L2] = .agent/getplan( "+!", "myplan(X)", "-!", Literal );}
 */
@SuppressFBWarnings( "GC_UNRELATED_TYPES" )
public final class CGetPlan extends IBaseAction
{
    /**
     * serial id
     */
    private static final long serialVersionUID = 2674034436717184541L;
    /**
     * action name
     */
    private static final IPath NAME = namebyclass( CGetPlan.class, "agent" );

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
        return StreamUtils.windowed( CCommon.flatten( p_argument ), 2, 2 )
                          .allMatch( i -> CGetPlan.query( ITrigger.EType.of( i.get( 0 ).raw() ), i.get( 1 ), p_context.agent(), p_return ) )
               ? Stream.of()
               : p_context.agent().fuzzy().membership().fail();
    }

    /**
     * query tha plan of an agent
     *
     * @param p_trigger trigger type
     * @param p_literal literal as string or literal object
     * @param p_agent agent
     * @param p_return list with return arguments
     * @return flag to query plan successfully
     */
    private static boolean query( @Nonnull final ITrigger.EType p_trigger, @Nonnull final ITerm p_literal,
                                  @Nonnull final IAgent<?> p_agent, @Nonnull final List<ITerm> p_return
    )
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
            return false;
        }

        final Collection<IPlanStatistic> l_plans = p_agent.plans().get( p_trigger.builddefault( l_literal ) );
        if ( l_plans.isEmpty() )
            return false;

        p_return.add(
            CRawTerm.of(
                l_plans.stream().map( IPlanStatistic::plan ).collect( Collectors.toList() )
            )
        );

        return true;
    }
}
