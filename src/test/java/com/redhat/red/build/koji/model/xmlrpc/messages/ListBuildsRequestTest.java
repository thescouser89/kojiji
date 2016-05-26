package com.redhat.red.build.koji.model.xmlrpc.messages;

import org.commonjava.maven.atlas.ident.ref.ProjectVersionRef;
import org.commonjava.maven.atlas.ident.ref.SimpleProjectVersionRef;
import org.commonjava.rwx.estream.model.Event;
import org.commonjava.rwx.impl.estream.EventStreamGeneratorImpl;
import org.commonjava.rwx.impl.estream.EventStreamParserImpl;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by jdcasey on 5/9/16.
 */
public class ListBuildsRequestTest
    extends AbstractKojiMessageTest
{

    @Test
    public void verifyVsCapturedHttpRequest()
            throws Exception
    {
        EventStreamParserImpl eventParser = new EventStreamParserImpl();
        bindery.render( eventParser, new ListBuildsRequest( new SimpleProjectVersionRef( "commons-io", "commons-io", "2.4.0.redhat-1" ) ) );

        List<Event<?>> objectEvents = eventParser.getEvents();
        eventParser.clearEvents();

        List<Event<?>> capturedEvents = parseEvents( "listBuilds-byGAV-request.xml" );

        assertEquals( objectEvents, capturedEvents );
    }

    @Test
    public void roundTrip()
            throws Exception
    {
        EventStreamParserImpl eventParser = new EventStreamParserImpl();
        ProjectVersionRef gav = new SimpleProjectVersionRef( "commons-io", "commons-io", "2.4.0.redhat-1" );
        bindery.render( eventParser, new ListBuildsRequest( gav ) );

        List<Event<?>> objectEvents = eventParser.getEvents();
        EventStreamGeneratorImpl generator = new EventStreamGeneratorImpl( objectEvents );

        ListBuildsRequest parsed = bindery.parse( generator, ListBuildsRequest.class );
        assertNotNull( parsed );

        assertThat( parsed.getQuery().getGav(), equalTo( gav ) );
    }

    @Test
    public void renderXML()
            throws Exception
    {
        ProjectVersionRef gav = new SimpleProjectVersionRef( "commons-io", "commons-io", "2.4.0.redhat-1" );
        String xml = bindery.renderString( new ListBuildsRequest( gav ) );
        System.out.println( xml );
    }
}