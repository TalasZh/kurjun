package ai.subut.kurjun.repo;


import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.codec.binary.Hex;

import ai.subut.kurjun.ar.CompressionType;
import ai.subut.kurjun.model.metadata.Metadata;
import ai.subut.kurjun.model.metadata.MetadataListing;
import ai.subut.kurjun.model.metadata.PackageMetadataStore;
import ai.subut.kurjun.model.metadata.SerializableMetadata;
import ai.subut.kurjun.model.repository.LocalRepository;
import ai.subut.kurjun.model.storage.FileStore;


/**
 * Abstract base class for local repositories.
 */
abstract class LocalRepositoryBase extends RepositoryBase implements LocalRepository
{


    @Override
    public SerializableMetadata getPackageInfo( Metadata metadata )
    {
        PackageMetadataStore metadataStore = getMetadataStore();
        try
        {
            if ( metadata.getId() != null || metadata.getMd5Sum() != null )
            {
                return metadataStore.get( metadata.getId() != null ? metadata.getId() : metadata.getMd5Sum() );
            }
            if ( metadata.getName() != null )
            {
                return getMetadataByName( metadata.getName(), metadata.getVersion() );
            }
        }
        catch ( IOException ex )
        {
            getLogger().error( "Failed to get package info", ex );
        }
        return null;
    }


    @Override
    public InputStream getPackageStream( Metadata metadata )
    {
        SerializableMetadata m = getPackageInfo( metadata );
        if ( m == null )
        {
            return null;
        }
        try
        {
            FileStore fileStore = getFileStore();
            if ( fileStore.contains( m.getMd5Sum() ) )
            {
                return fileStore.get( m.getMd5Sum() );
            }
            else
            {
                throw new IllegalStateException( "File not found for metadata" );
            }
        }
        catch ( IOException ex )
        {
            getLogger().error( "Failed to get package", ex );
        }
        return null;
    }

    private static Logger LOGGER = LoggerFactory.getLogger( LocalRepositoryBase.class );
    @Override
    public List<SerializableMetadata> listPackages()
    {
        LOGGER.info( "listing packages " );
        PackageMetadataStore metadataStore = getMetadataStore();
        List<SerializableMetadata> result = new LinkedList<>();
        try
        {
            MetadataListing list = metadataStore.list();
            result.addAll( list.getPackageMetadata() );

            while ( list.isTruncated() )
            {
                MetadataListing next = metadataStore.listNextBatch( list );
                result.addAll( next.getPackageMetadata() );
            }
        }
        catch ( IOException ex )
        {
            getLogger().error( "Failed to list package in metadata store", ex );
        }
        LOGGER.info( " returning listing packages " );
        return result;
    }


    @Override
    public Metadata put( InputStream is ) throws IOException
    {
        return put( is, CompressionType.NONE );
    }


    @Override
    public boolean delete( String md5 ) throws IOException
    {
        return delete( md5, md5 );
    }


    @Override
    public boolean delete( Object id, String md5 ) throws IOException
    {
        PackageMetadataStore metadataStore = getMetadataStore();
        FileStore fileStore = getFileStore();

        if ( metadataStore.contains( id ) )
        {
            fileStore.remove( md5 );  // TODO
            metadataStore.remove( id );
            return true;
        }
        return false;
    }


    /**
     * Gets logger instance associated classes that extend this abstract class.
     *
     * @return logger instance
     */
    protected abstract Logger getLogger();


    /**
     * Gets meta data store to be used in implementations classes of this abstract class.
     *
     * @return meta data store
     */
    protected abstract PackageMetadataStore getMetadataStore();


    /**
     * Gets file store to be used in implementations classes of this abstract class.
     *
     * @return file store
     */
    protected abstract FileStore getFileStore();


    private SerializableMetadata getMetadataByName( String name, String version ) throws IOException
    {
        List<SerializableMetadata> items = getMetadataStore().get( name );
        if ( items.isEmpty() )
        {
            return null;
        }

        if ( version != null )
        {
            return items.stream().filter( m -> version.equals( m.getVersion() ) ).findFirst().orElse( null );
        }
        else
        {
            // sort by version in descending fasion and get the first item which is will be the latest version
            items.sort( ( m1, m2 ) -> -1 * m1.getVersion().compareTo( m2.getVersion() ) );
            return items.get( 0 );
        }
    }


    @Override
    public String md5()
    {
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance( "MD5" );
            List<SerializableMetadata> list = listPackages();

            if ( list.size() != 0 )
            {
                messageDigest.update( list.toString().getBytes() );

                byte[] md5 = messageDigest.digest();
                return Hex.encodeHexString( md5 );
            }
        }
        catch ( NoSuchAlgorithmException e )
        {
            e.printStackTrace();
        }

        return "";
    }
}

