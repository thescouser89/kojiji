package com.redhat.red.build.koji;

import com.redhat.red.build.koji.model.xmlrpc.KojiMultiCallValueObj;
import com.redhat.red.build.koji.model.xmlrpc.messages.MultiCallRequest;
import com.redhat.red.build.koji.model.xmlrpc.messages.MultiCallResponse;
import org.commonjava.rwx.core.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.redhat.red.build.koji.model.xmlrpc.messages.MultiCallRequest.getBuilder;

public class KojiClientUtils
{
    private static final Logger logger = LoggerFactory.getLogger( KojiClientUtils.class );

    private static final Registry registry = Registry.getInstance();

    public static <T> MultiCallRequest buildMultiCallRequest( String method, List<T> args )
    {
        MultiCallRequest.Builder builder = getBuilder();
        args.forEach(( arg ) -> {
            if ( arg instanceof List<?> )
            {
                List<Object> rendered = new ArrayList<>();
                for ( Object a : (List<?>) arg )
                {
                    if ( registry.hasRender( a.getClass() ) )
                    {
                        a = registry.renderTo( a );
                    }
                    rendered.add( a );
                }
                builder.addCallObj( method, rendered );
            }
            else
            {
                if ( registry.hasRender( arg.getClass() ) )
                {

                    Object obj = registry.renderTo( arg );
                    builder.addCallObj( method, obj );
                }
                else
                {
                    builder.addCallObj( method, arg );
                }
            }
        });
        return builder.build();
    }

    public static <T> List<T> parseMultiCallResponse( MultiCallResponse response, Class<T> type )
    {
        List<KojiMultiCallValueObj> multiCallValueObjs = response.getValueObjs();

        List<T> ret = new ArrayList<>();

        Registry registry = Registry.getInstance();
        for ( KojiMultiCallValueObj valueObj : multiCallValueObjs )
        {
            Object data = valueObj.getData();
            if ( data != null )
            {
                T obj = registry.parseAs( data, type );
                ret.add( obj );
            }
            else
            {
                if ( valueObj.getFault() != null )
                {
                    logger.warn( "multiCall error: faultCode={}, faultString={}, traceback={}",
                                 valueObj.getFault().getFaultCode(), valueObj.getFault().getFaultString(),
                                 valueObj.getFault().getTraceback() );
                }
                ret.add( null ); // indicate an error
            }
        }
        return ret;
    }

    public static <R> List<List<R>> parseMultiCallResponseToLists( MultiCallResponse response, Class<R> type )
    {
        List<List<R>> ret = new ArrayList<>();

        List<KojiMultiCallValueObj> multiCallValueObjs = response.getValueObjs();

        multiCallValueObjs.forEach( valueObj -> {
            Object data = valueObj.getData();
            if ( data instanceof List )
            {
                List<R> typed = new ArrayList<>();
                List<?> l = (List<?>) data;
                l.forEach( element -> {
                    R obj = registry.parseAs( element, type );
                    typed.add( obj );
                } );
                ret.add( typed );
            }
            else
            {
                logger.debug( "Data object is not List, type: {}", valueObj.getClass() );
                if ( valueObj.getFault() != null )
                {
                    logger.warn( "multiCall error: faultCode={}, faultString={}, traceback={}",
                                 valueObj.getFault().getFaultCode(), valueObj.getFault().getFaultString(),
                                 valueObj.getFault().getTraceback() );
                }
                ret.add( null ); // indicate an error
            }
        } );

        return ret;
    }
}
