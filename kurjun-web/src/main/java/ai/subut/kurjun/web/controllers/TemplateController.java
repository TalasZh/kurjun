package ai.subut.kurjun.web.controllers;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.metadata.common.subutai.TemplateId;
import ai.subut.kurjun.metadata.common.utils.IdValidators;
import ai.subut.kurjun.model.identity.UserSession;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.metadata.template.TemplateData;
import ai.subut.kurjun.web.handler.SubutaiFileHandler;
import ai.subut.kurjun.web.model.KurjunFileItem;
import ai.subut.kurjun.web.service.RelationManagerService;
import ai.subut.kurjun.web.service.RepositoryService;
import ai.subut.kurjun.web.service.TemplateManagerService;
import ninja.Context;
import ninja.Renderable;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.session.FlashScope;
import ninja.uploads.FileItem;
import ninja.uploads.FileProvider;


@Singleton
public class TemplateController extends BaseController
{

    private static final Logger LOGGER = LoggerFactory.getLogger( TemplateController.class );

    @Inject
    private TemplateManagerService templateManagerService;

    @Inject
    private RepositoryService repositoryService;

    @Inject
    private RelationManagerService relationManagerService;


    public Result listTemplates( Context context, FlashScope flashScope, @Param( "repository" ) String repo,
                                 @Param( "search" ) String search )
    {
        List<SerializableMetadata> defaultTemplateList = new ArrayList<>();
        try
        {
            search = StringUtils.isBlank( search ) ? "all" : search;
            //*****************************************************
            UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
            defaultTemplateList = templateManagerService.list( uSession, repo, search, false );
            //*****************************************************
        }
        catch ( IOException e )
        {
            flashScope.error( "Failed to get list of templates." );
            LOGGER.error( "Failed to get list of templates: " + e.getMessage() );
        }
        List<String> repos = repositoryService.getRepositoryContextList();

        return Results.html().template( "views/templates.ftl" ).render( "templates", defaultTemplateList )
                      .render( "repos", repos ).render( "sel_repo", repo ).render( "owners", null );
    }


    public Result getUploadTemplateForm()
    {
        List<String> repos = repositoryService.getRepositoryContextList();

        return Results.html().template( "views/_popup-upload-templ.ftl" ).render( "repos", repos );
    }


    @FileProvider( SubutaiFileHandler.class )
    public Result uploadTemplate( Context context, @Param( "repository" ) String repository,
                                  @Param( "repo_name" ) String repoName, @Param( "repo_type" ) String repoType,
                                  @Param( "file" ) FileItem file, FlashScope flashScope )
    {
        try
        {
            if ( repoType.equals( "new" ) )
            {
                repository = repoName;
            }

            KurjunFileItem fileItem = ( KurjunFileItem ) file;
            //*****************************************************
            UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
            String id = templateManagerService.upload( uSession, repository, fileItem.getInputStream() );
            //*****************************************************

            if ( Strings.isNullOrEmpty( id ) )
            {
                flashScope.error( "Failed to upload metadata. Access Permission error." );
                return Results.redirect( context.getContextPath() + "/" );
            }
            else
            {
                String[] temp = id.split( "\\." );
                //temp contains [fprint].[md5]
                if ( temp.length == 2 )
                {
                    flashScope.success( "Template uploaded successfully" );
                    return Results.redirect( context.getContextPath() + "/" );
                }
            }
        }
        catch ( IOException e )
        {
            LOGGER.error( "Failed to upload metadata: {}", e.getMessage() );
        }

        flashScope.error( "Failed to upload metadata" );
        return Results.redirect( context.getContextPath() + "/" );
    }


    public Result getTemplateInfo( Context context, @Param( "repository" ) String repoContext,
                                   @Param( "version" ) String version, @Param( "md5" ) String md5,
                                   @Param( "search" ) String search )
    {
        if ( !StringUtils.isBlank( md5 ) )
        {
            //TemplateId tid = IdValidators.Template.validate( id );

            //*****************************************************
            UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
            TemplateData templateData =
                    templateManagerService.getTemplate( uSession, repoContext, md5, version, search );
            //*****************************************************

            if ( templateData != null )
            {
                return Results.html().template( "views/_popup-view-tpl.ftl" ).render( "templ_info", templateData );
            }
        }

        return Results.html().template( "views/_popup-view-tpl.ftl" );
    }


    public Result downloadTemplate( Context context,@Param( "repository" ) String repository, @Param( "md5" ) String md5 )
    {
        try
        {
            //*****************************************************
            UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );
            Renderable renderable =
                    templateManagerService.renderableTemplate( uSession, repository, md5, false );
            //*****************************************************

            return Results.ok().render( renderable ).supportedContentType( Result.APPLICATION_OCTET_STREAM );
        }
        catch ( IOException e )
        {
            LOGGER.error( "Failed to download metadata: " + e.getMessage() );
            return Results.internalServerError().text().render( "Failed to download metadata" );
        }
    }


    public Result deleteTemplate( Context context, @PathParam( "repository" ) String repository, @PathParam( "md5" ) String md5,
                                  FlashScope flashScope )
    {
        try
        {
            UserSession uSession = ( UserSession ) context.getAttribute( "USER_SESSION" );


            // get relations list
            //List<Relation> relations = relationManagerService.getTrustRelationsByObject( relationManagerService
            // .toTrustObject( uSession, id, null, null, null, RelationObjectType.RepositoryContent ) );

            //*****************************************************
            Integer status = templateManagerService.delete( uSession, repository, md5 );
            //*****************************************************

            // remove relations
            // relations.forEach( r -> relationManagerService.removeRelation( r ) );
            switch ( status )
            {
                case 0:
                    flashScope.success( "Template removed successfully" );
                    break;
                case 1:
                    flashScope.success( "Template was not found " );
                case 2:
                    flashScope.success( "Permission denied " );
                    break;
                default:
                    flashScope.success( "Internal Server error " );
                    break;
            }
        }
        catch ( Exception e )
        {
            LOGGER.error( "Failed to remove metadata: " + e.getMessage() );
            flashScope.error( "Failed to remove metadata." );
        }

        return Results.redirect( context.getContextPath() + "/" );
    }
}
