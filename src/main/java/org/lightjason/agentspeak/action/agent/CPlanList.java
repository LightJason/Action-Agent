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
