package ai.subut.kurjun.web.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;


/**
 *
 */
public class ProtocolFilter implements Filter
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AuthorizedFilter.class );

    @Override
    public Result filter( FilterChain filterChain, Context context )
    {
        LOGGER.info( "***** ProtocolFilter called " );

        if(context.getScheme().equals( "https" ))
        {
            return filterChain.next( context );
        }
        else
        {
            return Results.forbidden().render( "Not allowed" ).text();
        }
    }
}
