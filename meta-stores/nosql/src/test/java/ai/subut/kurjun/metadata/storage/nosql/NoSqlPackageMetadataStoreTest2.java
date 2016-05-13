package ai.subut.kurjun.metadata.storage.nosql;


import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.subut.kurjun.metadata.common.apt.DefaultPackageMetadata;
import ai.subut.kurjun.model.metadata.SerializableMetadata;


public class NoSqlPackageMetadataStoreTest2
{

    private static final Logger LOGGER = LoggerFactory.getLogger( NoSqlPackageMetadataStoreTest2.class );
    private static NoSqlPackageMetadataStore store;
    private static CassandraSessionProvider sessionProvider;

    private SerializableMetadata meta;
    private List<SerializableMetadata> extraItems;
    private byte[] otherMd5;


    @BeforeClass
    public static void setUpClass()
    {
//        Properties prop = new Properties();
//        try ( InputStream is = ClassLoader.getSystemResourceAsStream( "cassandra.properties" ) )
//        {
//            prop.load( is );
//
//            String node = prop.getProperty( "test.cassandra.node" );
//            int port = Integer.parseInt( prop.getProperty( "test.cassandra.port" ) );
//            sessionProvider = new CassandraSessionProvider( node, port );
//
//            KurjunContext defaultContext = new KurjunContext( "" );
//            store = new NoSqlPackageMetadataStore( sessionProvider, defaultContext );
//        }
//        catch ( Exception ex )
//        {
//            LOGGER.error( "Failed to initialize Cassandra connection", ex );
//        }
    }


    @AfterClass
    public static void tearDownClass() throws IOException
    {
//        if ( sessionProvider != null )
//        {
//            sessionProvider.close();
//        }
    }


    @Before
    public void setUp() throws IOException
    {
//        Assume.assumeNotNull( store );
//
//        meta = createPackageMetadata();
//        store.put( meta );
//
//        extraItems = new ArrayList<>();
//        otherMd5 = DigestUtils.md5( "other content" );
    }


    @After
    public void tearDown() throws IOException
    {
//        if ( store != null )
//        {
//            store.remove( meta.getMd5Sum() );
//            for ( SerializableMetadata item : extraItems )
//            {
//                store.remove( item.getMd5Sum() );
//            }
//        }
    }


    @Test
    public void testContains() throws Exception
    {
//        Assert.assertTrue( store.contains( meta.getMd5Sum() ) );
//        Assert.assertFalse( store.contains( otherMd5 ) );
    }


    @Test
    public void testGet() throws Exception
    {
//        Metadata res = store.get( meta.getMd5Sum() );
//        Assert.assertEquals( meta, res );
//        Assert.assertNull( store.get( otherMd5 ) );
//
//        // test get by name
//        List<SerializableMetadata> ls = store.get( meta.getName() );
//        Assert.assertEquals( 1, ls.size() );
//        Assert.assertTrue( ls.contains( meta ) );
//
//        Assert.assertTrue( store.get( "non-existing-name" ).isEmpty() );
    }


    @Test
    public void testPut() throws Exception
    {
        // already exists
//        Assert.assertFalse( store.put( meta ) );
    }


    @Test
    public void testRemove() throws Exception
    {
        // does not exist
//        Assert.assertFalse( store.remove( otherMd5 ) );

        // removed first then does not exist anymore
//        Assert.assertTrue( store.remove( meta.getMd5Sum() ) );
//        Assert.assertFalse( store.remove( meta.getMd5Sum() ) );
    }


    @Test
    public void testList() throws Exception
    {
//        store.batchSize = 10;
//
//        // put twice of the batch size
//        for ( int i = 0; i < store.batchSize * 2; i++ )
//        {
//            SerializableMetadata pm = createPackageMetadata();
//            store.put( pm );
//            extraItems.add( pm );
//        }
//
//        MetadataListing ls = store.list();
//
//        Assert.assertNotNull( ls );
//        Assert.assertTrue( ls.isTruncated() );
//        Assert.assertEquals( store.batchSize, ls.getPackageMetadata().size() );
//
//        MetadataListing next = store.listNextBatch( ls );
//        Assert.assertNotNull( next );
    }


    @Test//( expected = IllegalStateException.class )
    public void testListNextBatchWithInvalidInput() throws Exception
    {
//        MetadataListingImpl listing = new MetadataListingImpl();
//        listing.setTruncated( false );
//
//        store.listNextBatch( listing );
    }


    @Test//( expected = IllegalStateException.class )
    public void testListNextBatchWithoutMarker() throws IOException
    {
//        store.listNextBatch( new MetadataListingImpl() );
    }


    private DefaultPackageMetadata createPackageMetadata()
    {
//        DefaultPackageMetadata pm = new DefaultPackageMetadata();
//        pm.setPackage( "package-name" + UUID.randomUUID().toString() );
//        pm.setVersion( "1.2.3" );
//        pm.setArchitecture( Architecture.AMD64 );
//        pm.setDescription( "Description here" );
//        pm.setFilename( pm.getPackage() + "-ver-arch.deb" );
//        pm.setInstalledSize( 1234 );
//        pm.setMaintainer( "Maintainer" );
//        pm.setMd5( DigestUtils.md5( pm.getFilename() ) );
//        pm.setPriority( Priority.important );
//
//        DefaultDependency dep = new DefaultDependency();
//        dep.setPackage( "Package" );
//        dep.setVersion( "1.0.0" );
//        dep.setDependencyOperator( RelationOperator.StrictlyLater );
//
//        List<Dependency> ls = new ArrayList<>();
//        ls.add( dep );
//        pm.setDependencies( ls );
//
//        return pm;
        return null;
    }
}
