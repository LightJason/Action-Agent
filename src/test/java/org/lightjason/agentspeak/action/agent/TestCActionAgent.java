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

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lightjason.agentspeak.beliefbase.CBeliefbase;
import org.lightjason.agentspeak.beliefbase.storage.CMultiStorage;
import org.lightjason.agentspeak.common.CPath;
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.CContext;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.execution.IExecution;
import org.lightjason.agentspeak.language.execution.instantiable.IBaseInstantiable;
import org.lightjason.agentspeak.language.execution.instantiable.plan.IPlan;
import org.lightjason.agentspeak.language.execution.instantiable.plan.annotation.IAnnotation;
import org.lightjason.agentspeak.language.execution.instantiable.plan.statistic.CPlanStatistic;
import org.lightjason.agentspeak.language.execution.instantiable.plan.statistic.IPlanStatistic;
import org.lightjason.agentspeak.language.execution.instantiable.plan.trigger.ITrigger;
import org.lightjason.agentspeak.testing.IBaseTest;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.LogManager;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/**
 * test agent action
 */
public final class TestCActionAgent extends IBaseTest
{
    /**
     * agent context
     */
    private IContext m_context;

    static
    {
        LogManager.getLogManager().reset();
    }


    /**
     * initialize
     *
     * @throws Exception on initialize error
     */
    @BeforeEach
    public void initialize() throws Exception
    {
        m_context = new CContext(
            new CAgentGenerator( new ByteArrayInputStream( "".getBytes( StandardCharsets.UTF_8 ) ) ).generatesingle(),
            new CEmptyPlan( ITrigger.EType.ADDGOAL.builddefault( CLiteral.of( "contextplan" ) ) ),
            Collections.emptyList()
        );
    }


    /**
     * test plan list
     */
    @Test
    public void planlist()
    {
        final ITrigger l_trigger = ITrigger.EType.ADDGOAL.builddefault( CLiteral.of( "testplanlist" ) );
        final IPlan l_plan = new CEmptyPlan( l_trigger );
        final List<ITerm> l_return = new ArrayList<>();

        new CPlanList().execute(
            false, m_context,
            Collections.emptyList(),
            l_return
        );

        Assertions.assertEquals( l_return.size(), 1 );
        Assertions.assertTrue( l_return.get( 0 ).raw() instanceof List<?> );
        Assertions.assertEquals( l_return.get( 0 ).<List<?>>raw().size(), 0 );


        m_context.agent().plans().put( l_plan.trigger(), CPlanStatistic.of( l_plan ) );

        new CPlanList().execute(
            false, m_context,
            Collections.emptyList(),
            l_return
        );

        Assertions.assertEquals( l_return.size(), 2 );
        Assertions.assertTrue( l_return.get( 1 ).raw() instanceof List<?> );
        Assertions.assertEquals( l_return.get( 1 ).<List<?>>raw().size(), 1 );
        Assertions.assertTrue( l_return.get( 1 ).<List<?>>raw().get( 0 ) instanceof AbstractMap.Entry<?, ?> );
        Assertions.assertEquals( l_return.get( 1 ).<List<AbstractMap.Entry<String, ILiteral>>>raw().get( 0 ).getKey(), l_trigger.type().sequence() );
        Assertions.assertEquals( l_return.get( 1 ).<List<AbstractMap.Entry<String, ILiteral>>>raw().get( 0 ).getValue(), l_trigger.literal() );
    }


    /**
     * test add plan
     */
    @Test
    public void addplan()
    {
        final IPlan l_plan = new CEmptyPlan( ITrigger.EType.ADDGOAL.builddefault( CLiteral.of( "testaddplan" ) ) );

        new CAddPlan().execute(
            false, m_context,
            Stream.of( l_plan ).map( CRawTerm::of ).collect( Collectors.toList() ),
            Collections.emptyList()
        );

        Assertions.assertEquals( m_context.agent().plans().size(), 1 );
        Assertions.assertArrayEquals(
            m_context.agent().plans().values().stream().map( IPlanStatistic::plan ).toArray(),
            Stream.of( l_plan ).toArray()
        );
    }


    /**
     * test cycle-time
     */
    @Test
    public void cycletime()
    {
        this.next();

        final List<ITerm> l_return = new ArrayList<>();
        new CCycleTime().execute(
            false, m_context,
            Collections.emptyList(),
            l_return
        );

        Assertions.assertEquals( l_return.size(), 1 );
        Assertions.assertTrue( l_return.get( 0 ).<Number>raw().longValue() > 0 );
    }


    /**
     * test get plan
     */
    @Test
    public void getplan()
    {
        final IPlan l_plan = new CEmptyPlan( ITrigger.EType.ADDGOAL.builddefault( CLiteral.of( "testgetplan" ) ) );
        final List<ITerm> l_return = new ArrayList<>();


        new CGetPlan().execute(
            false, m_context,
            Collections.emptyList(),
            l_return
        );

        Assertions.assertTrue( l_return.isEmpty() );


        m_context.agent().plans().put( l_plan.trigger(), CPlanStatistic.of( l_plan ) );

        new CGetPlan().execute(
            false, m_context,
            Stream.of( "+!", "testgetplan" ).map( CRawTerm::of ).collect( Collectors.toList() ),
            l_return
        );

        Assertions.assertEquals( 1, l_return.size() );
        Assertions.assertTrue( l_return.get( 0 ).raw() instanceof List<?> );
        Assertions.assertEquals( 1, l_return.get( 0 ).<List<?>>raw().size() );
        Assertions.assertArrayEquals( Stream.of( l_plan ).toArray(), l_return.get( 0 ).<List<?>>raw().toArray() );
    }


    /**
     * test remove plan
     */
    @Test
    public void removeplan()
    {
        final IPlan l_plan = new CEmptyPlan( ITrigger.EType.ADDGOAL.builddefault( CLiteral.of( "testremoveplan" ) ) );
        m_context.agent().plans().put( l_plan.trigger(), CPlanStatistic.of( l_plan ) );

        Assertions.assertTrue(
            execute(
                new CRemovePlan(),
                false,
                Stream.of( "+!", "testremoveplan" ).map( CRawTerm::of ).collect( Collectors.toList() ),
                Collections.emptyList(),
                m_context
            )
        );
    }


    /**
     * test remove plan error
     */
    @Test
    public void removeplanerror()
    {
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> execute(
                new CRemovePlan(),
                false,
                Stream.of( "+!", "testremoveerrorplan" ).map( CRawTerm::of ).collect( Collectors.toList() ),
                Collections.emptyList()
            )
        );
    }


    /**
     * test clear-beliefbase empty call
     */
    @Test
    public void clearbeliefbaseempty()
    {
        IntStream.range( 0, 100 )
                 .mapToObj( i -> RandomStringUtils.random( 12, "abcdefghijklmnop" ) )
                 .map( i -> CLiteral.of( i ) )
                 .forEach( i -> m_context.agent().beliefbase().add( i ) );

        Assertions.assertEquals( 100, m_context.agent().beliefbase().size() );

        new CClearBeliefbase().execute(
            false, m_context,
            Collections.emptyList(),
            Collections.emptyList()
        );

        Assertions.assertEquals( 0, m_context.agent().beliefbase().size() );
    }

    /**
     * test clear-beliefbase single call
     */
    @Test
    public void clearbeliefbasepath()
    {
        m_context.agent()
                 .beliefbase()
                 .generate(
                     ( p_name, p_view ) -> new CBeliefbase( new CMultiStorage<>() ).create( p_name, p_view ),
                     CPath.of( "sub1" ),
                     CPath.of( "sub2" )
                 )
                 .add(
                     CLiteral.of( "sub1/test" ),
                     CLiteral.of( "sub1/test", CRawTerm.of( 1 ) ),
                     CLiteral.of( "sub1/test", CRawTerm.of( "foobar" ) ),
                     CLiteral.of( "sub2/foobar" ) );

        Assertions.assertEquals( 4, m_context.agent().beliefbase().size() );

        new CClearBeliefbase().execute(
            false, m_context,
            Stream.of( "sub1" ).map( CRawTerm::of ).collect( Collectors.toList() ),
            Collections.emptyList()
        );

        Assertions.assertEquals( 1, m_context.agent().beliefbase().size() );
    }

    /**
     * test belieflist all
     */
    @Test
    public void belieflistall()
    {
        final List<ITerm> l_return = new ArrayList<>();
        final Set<String> l_list = IntStream.range( 0, 100 )
                                            .mapToObj( i -> RandomStringUtils.random( 12, "abcdefghijklmnop" ) )
                                            .peek( i -> m_context.agent().beliefbase().add( CLiteral.of( i ) ) )
                                            .collect( Collectors.toSet() );

        Assertions.assertEquals( m_context.agent().beliefbase().size(), 100 );

        new CBeliefList().execute(
            false, m_context,
            Collections.emptyList(),
            l_return
        );

        Assertions.assertEquals( 1, l_return.size() );
        Assertions.assertTrue( l_return.get( 0 ).raw() instanceof List<?> );

        Assertions.assertTrue(
            l_return.get( 0 )
                    .<List<ILiteral>>raw()
                    .stream()
                    .map( i -> i.fqnfunctor().toString() )
                    .allMatch( l_list::contains )
        );
    }

    /**
     * test belieflist single element
     */
    @Test
    public void belieflistsingle()
    {
        final String l_names = "foo";

        m_context.agent().beliefbase().add(
            CLiteral.of( l_names ),
            CLiteral.of( l_names, CRawTerm.of( 1 ) ),
            CLiteral.of( l_names, CRawTerm.of( 2 ) ),
            CLiteral.of( "y", CRawTerm.of( 1 ) ),
            CLiteral.of( "y", CRawTerm.of( 2 ) )
        );

        final List<ITerm> l_return = new ArrayList<>();

        new CBeliefList().execute(
            false, m_context,
            Stream.of( l_names ).map( CRawTerm::of ).collect( Collectors.toList() ),
            l_return
        );

        Assertions.assertEquals( 1, l_return.size() );
        Assertions.assertEquals( 3, l_return.get( 0 ).<Collection<?>>raw().size() );

        Assertions.assertTrue(
            Stream.of(
                CLiteral.of( l_names ),
                CLiteral.of( l_names, CRawTerm.of( 1 ) ),
                CLiteral.of( l_names, CRawTerm.of( 2 ) )
            ).allMatch( i -> l_return.get( 0 ).<Collection<?>>raw().contains( i ) )
        );
    }

    /**
     * test sleep unlimited
     */
    @Test
    public void sleepunlimited()
    {
        new CSleep().execute(
            false, m_context,
            Collections.emptyList(),
            Collections.emptyList()
        );

        Assertions.assertTrue( m_context.agent().sleeping() );
    }

    /**
     * test sleep single
     */
    @Test
    public void sleepone()
    {
        new CSleep().execute(
            false, m_context,
            Stream.of( 1 ).map( CRawTerm::of ).collect( Collectors.toList() ),
            Collections.emptyList()
        );

        Assertions.assertTrue( m_context.agent().sleeping() );
    }

    /**
     * test fuzzy-membership
     */
    @Test
    public void fuzzymembership()
    {
        Assertions.assertTrue(
            new CFuzzyMembership().execute(
                false, m_context,
                Stream.of( 3, "hello" ).map( CRawTerm::of ).collect( Collectors.toList() ),
                Collections.emptyList()
            ).collect( Collectors.toList() ).isEmpty()
        );
    }

    /**
     * plan statistic
     */
    @Test
    public void planstatistic()
    {
        final IPlan l_plan = new CEmptyPlan( ITrigger.EType.ADDGOAL.builddefault( CLiteral.of( "testgetplan" ) ) );
        m_context.agent().plans().put( l_plan.trigger(), CPlanStatistic.of( l_plan ) );

        final List<ITerm> l_return = new ArrayList<>();

        Assertions.assertTrue(
            execute(
                new org.lightjason.agentspeak.action.agent.CPlanStatistic(),
                false,
                Stream.of( l_plan ).map( CRawTerm::of ).collect( Collectors.toList() ),
                l_return,
                m_context
            )
        );

        Assertions.assertEquals( 3, l_return.size() );
        Assertions.assertArrayEquals(
            Stream.of( 0D, 0D, 0D ).toArray(),
            l_return.stream().map( i -> i.raw() ).toArray()
        );
    }

    /**
     * test name and arguments
     */
    @Test
    public void namearguments()
    {
        Assertions.assertEquals( CPath.of( "agent/planstatistic" ), new org.lightjason.agentspeak.action.agent.CPlanStatistic().name() );
        Assertions.assertEquals( 1, new org.lightjason.agentspeak.action.agent.CPlanStatistic().minimalArgumentNumber() );

        Assertions.assertEquals( CPath.of( "agent/belieflist" ), new CBeliefList().name() );
        Assertions.assertEquals( 0, new CBeliefList().minimalArgumentNumber() );

        Assertions.assertEquals( CPath.of( "agent/clearbeliefbase" ), new CClearBeliefbase().name() );
        Assertions.assertEquals( 0, new CClearBeliefbase().minimalArgumentNumber() );

        Assertions.assertEquals( CPath.of( "agent/cycletime" ), new CCycleTime().name() );
        Assertions.assertEquals( 0, new CClearBeliefbase().minimalArgumentNumber() );

        Assertions.assertEquals( CPath.of( "agent/fuzzymembership" ), new CFuzzyMembership().name() );
        Assertions.assertEquals( 0, new CFuzzyMembership().minimalArgumentNumber() );

        Assertions.assertEquals( CPath.of( "agent/getplan" ), new CGetPlan().name() );
        Assertions.assertEquals( 1, new CGetPlan().minimalArgumentNumber() );

        Assertions.assertEquals( CPath.of( "agent/planlist" ), new CPlanList().name() );
        Assertions.assertEquals( 0, new CPlanList().minimalArgumentNumber() );

        Assertions.assertEquals( CPath.of( "agent/removeplan" ), new CRemovePlan().name() );
        Assertions.assertEquals( 1, new CRemovePlan().minimalArgumentNumber() );

        Assertions.assertEquals( CPath.of( "agent/sleep" ), new CSleep().name() );
        Assertions.assertEquals( 0, new CSleep().minimalArgumentNumber() );
    }


    /**
     * execute agent cycle
     *
     * @return execute context
     */
    private IContext next()
    {
        agentcycleassert( m_context.agent() );
        return m_context;
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * empty plan
     */
    private static final class CEmptyPlan extends IBaseInstantiable implements IPlan
    {
        /**
         * serial id
         */
        private static final long serialVersionUID = 6885053756134284862L;
        /**
         * trigger
         */
        private final ITrigger m_trigger;

        /**
         * ctor
         *
         * @param p_trigger trigger
         */
        CEmptyPlan( final ITrigger p_trigger )
        {
            super( new IAnnotation<?>[0], new IExecution[0], 0 );
            m_trigger = p_trigger;
        }

        @Nonnull
        @Override
        public ITrigger trigger()
        {
            return m_trigger;
        }

        @Override
        public boolean condition( @Nonnull final IContext p_context )
        {
            return true;
        }

        @Override
        public ILiteral literal()
        {
            return m_trigger.literal();
        }
    }

}
