package ai.subut.kurjun.rest.raw;


import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Hex;

import com.google.inject.Injector;

import ai.subut.kurjun.cfparser.ControlFileParserModule;
import ai.subut.kurjun.common.KurjunBootstrap;
import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.common.service.KurjunProperties;
import ai.subut.kurjun.index.PackagesIndexParserModule;
import ai.subut.kurjun.metadata.common.DefaultMetadata;
import ai.subut.kurjun.metadata.common.raw.RawMetadata;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreFactory;
import ai.subut.kurjun.metadata.factory.PackageMetadataStoreModule;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.repository.UnifiedRepository;
import ai.subut.kurjun.quota.QuotaManagementModule;
import ai.subut.kurjun.repo.LocalRawRepository;
import ai.subut.kurjun.repo.RepositoryFactory;
import ai.subut.kurjun.repo.RepositoryModule;
import ai.subut.kurjun.riparser.ReleaseIndexParserModule;
import ai.subut.kurjun.security.SecurityModule;
import ai.subut.kurjun.snap.SnapMetadataParserModule;
import ai.subut.kurjun.storage.factory.FileStoreFactory;
import ai.subut.kurjun.storage.factory.FileStoreModule;
import ai.subut.kurjun.subutai.SubutaiTemplateParserModule;
import io.subutai.common.protocol.Resource;
import io.subutai.core.kurjun.impl.TrustedWebClientFactoryModule;


public class RawManagerStandalone
{

    private static final Logger LOGGER = LoggerFactory.getLogger( RawManagerStandalone.class );

    private static final KurjunContext RAW_CONTEXT = new KurjunContext( "raw" );

    private Injector injector;


    public void init()
    {
        injector = bootstrapDI();

        KurjunProperties properties = injector.getInstance( KurjunProperties.class );

        setContexts( properties );
    }


    private Injector bootstrapDI()
    {
        KurjunBootstrap bootstrap = new KurjunBootstrap();
        bootstrap.addModule( new ControlFileParserModule() );
        bootstrap.addModule( new ReleaseIndexParserModule() );
        bootstrap.addModule( new PackagesIndexParserModule() );
        bootstrap.addModule( new SubutaiTemplateParserModule() );

        bootstrap.addModule( new FileStoreModule() );
        bootstrap.addModule( new PackageMetadataStoreModule() );
        bootstrap.addModule( new SnapMetadataParserModule() );

        bootstrap.addModule( new RepositoryModule() );
        bootstrap.addModule( new TrustedWebClientFactoryModule() );
        bootstrap.addModule( new SecurityModule() );
        bootstrap.addModule( new QuotaManagementModule() );

        bootstrap.boot();

        return bootstrap.getInjector();
    }


    public void dispose()
    {
    }


    private LocalRawRepository getLocalRepository() throws IOException
    {
        try
        {
            RepositoryFactory repositoryFactory = injector.getInstance( RepositoryFactory.class );
            return repositoryFactory.createLocalRaw( RAW_CONTEXT );
        }
        catch ( IllegalArgumentException ex )
        {
            throw new IOException( ex );
        }
    }


    private UnifiedRepository getRepository( boolean isKurjunClient ) throws IOException
    {
        RepositoryFactory repositoryFactory = injector.getInstance( RepositoryFactory.class );
        UnifiedRepository unifiedRepo = repositoryFactory.createUnifiedRepo();
        unifiedRepo.getRepositories().add( getLocalRepository() );

        return unifiedRepo;
    }


    public Resource getFile( byte[] md5, boolean isKurjunClient ) throws IOException
    {
        if ( md5 == null )
        {
            return null;
        }

        DefaultMetadata m = new DefaultMetadata();
        m.setMd5sum( md5 );

        RawMetadata meta = ( RawMetadata ) getRepository( isKurjunClient ).getPackageInfo( m );
        if ( meta != null )
        {
            return convertToResource( meta );
        }
        return null;
    }


    public Resource getFile( String name, boolean isKurjunClient ) throws IOException
    {

        if ( name == null )
        {
            return null;
        }

        DefaultMetadata m = new DefaultMetadata();
        m.setName( name );

        RawMetadata meta = ( RawMetadata ) getRepository( isKurjunClient ).getPackageInfo( m );
        if ( meta != null )
        {
            return convertToResource( meta );
        }
        return null;
    }


    public InputStream getFileData( byte[] md5, boolean isKurjunClient ) throws IOException
    {
        if ( md5 == null )
        {
            return null;
        }

        DefaultMetadata m = new DefaultMetadata();
        m.setMd5sum( md5 );

        return getRepository( isKurjunClient ).getPackageStream( m );
    }


    public List<Resource> getFileList( boolean isKurjunClient ) throws IOException
    {
        List<SerializableMetadata> metadatas = getRepository( isKurjunClient ).listPackages();
        List<Resource> result = new LinkedList<>();

        for ( SerializableMetadata metadata : metadatas )
        {
            result.add( convertToResource( ( RawMetadata ) metadata ) );
        }

        return result;
    }


    public String uploadFile( InputStream is, String fileName ) throws IOException
    {
        Objects.requireNonNull( is, "The uploaded InputStream cannot be null" );
        Objects.requireNonNull( fileName, "The fileName cannot be null" );
        try
        {
            Metadata meta = getLocalRepository().put( is, fileName );
            return Hex.encodeHexString( meta.getMd5Sum() );
        }
        catch ( IOException ex )
        {
            LOGGER.error( "Failed to upload", ex );
            throw new IllegalArgumentException( ex.getMessage(), ex );
        }
    }


    public boolean deleteFile( byte[] md5 ) throws IOException
    {
        LocalRepository repo = getLocalRepository();
        try
        {
            return repo.delete( md5 );
        }
        catch ( IOException ex )
        {
            LOGGER.error( "Failed to delete the raw file", ex );
            throw ex;
        }
    }


    private void setContexts( KurjunProperties properties )
    {
        // init apt type context
        Properties kcp = properties.getContextProperties( RAW_CONTEXT );
        kcp.setProperty( FileStoreFactory.TYPE, FileStoreFactory.FILE_SYSTEM );
        kcp.setProperty( PackageMetadataStoreModule.PACKAGE_METADATA_STORE_TYPE, PackageMetadataStoreFactory.FILE_DB );
    }


    private Resource convertToResource( RawMetadata meta )
    {
        return new Resource( Hex.encodeHexString( meta.getMd5Sum() ), meta.getName(), meta.getSize() );
    }
}
