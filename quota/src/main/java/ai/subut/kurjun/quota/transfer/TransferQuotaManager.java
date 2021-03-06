package ai.subut.kurjun.quota.transfer;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.inject.Inject;
import com.google.inject.ProvisionException;
import com.google.inject.assistedinject.Assisted;

import ai.subut.kurjun.common.service.KurjunContext;
import ai.subut.kurjun.quota.QuotaException;
import ai.subut.kurjun.quota.QuotaInfoStore;


/**
 * Transfer quota manager.
 */
public class TransferQuotaManager
{
    public static final int BUFFER_SIZE = 1024 * 4;


    @Inject
    private TransferredDataCounterFactory dataCounterFactory;

    private KurjunContext context;
    private TransferQuota quota;


    @Inject
    public TransferQuotaManager( QuotaInfoStore quotaInfoStore, @Assisted KurjunContext context )
    {
        this.context = context;
        try
        {
            quota = quotaInfoStore.getTransferQuota( context );
            if ( quota == null )
            {
                quota = TransferQuota.UNLIMITED;
            }
        }
        catch ( IOException ex )
        {
            throw new ProvisionException( "Failed to get transfer quota to be applied.", ex );
        }
    }


    /**
     * Copies stream of data from source to output stream. While copying quota threshold is checked by a step of {@link
     * TransferQuotaManager#BUFFER_SIZE} bytes. If threshold exceeds during the copy operation {@link QuotaException} is
     * thrown.
     *
     * @param source source stream of data to be copied (transferred)
     * @param sink sink for the data
     *
     * @throws QuotaException if quota threshold is exceeded
     */
    public void copy( InputStream source, OutputStream sink ) throws IOException, QuotaException
    {
        TransferredDataCounter counter = dataCounterFactory.get( context );

        int n;
        byte[] buf = new byte[BUFFER_SIZE];
        while ( ( n = source.read( buf ) ) != -1 )
        {
            if ( isAllowedToTransfer( n, counter ) )
            {
                sink.write( buf, 0, n );
                counter.increment( n );
            }
            else
            {
                throw new QuotaException( "Transfer quota threshold exceeded." );
            }
        }
    }


    /**
     * Creates a managed stream whose read operations perform quota checks. Returned steam wraps the supplied stream.
     * When reading next data exceeds the quota threshold an exception is thrown.
     *
     * @param stream stream that is to be managed by quota management
     *
     * @return wrapper stream that is checked for quota threshold
     */
    public InputStream createManagedStream( InputStream stream )
    {
        TransferredDataCounter counter = dataCounterFactory.get( context );
        checkTimeFrame( counter );

        long max = getThresholdInBytes( quota ) - counter.get();
        return new TransferQuotaManagedStream( stream, counter, max );
    }


    /**
     * Gets size of data allowed to transfer at the time of calling this method. Allowed size to transfer is the data
     * left to reach threshold value. <p> Returned value is expected to be used immediately as the transfer quota is
     * bound to time and is subject to be reset at its time frame.
     *
     * @return allowed size that can be transferred at this time
     */
    public long allowedSizeToTransfer()
    {
        TransferredDataCounter counter = dataCounterFactory.get( context );
        checkTimeFrame( counter );
        return getThresholdInBytes( quota ) - counter.get();
    }


    /**
     * Checks if the supplied data size can be transferred without exceeding quota threshold.
     *
     * @param size data size in bytes
     *
     * @return {@code true} if transferring supplied data size would not exceed quota threshold; {@code false}
     * otherwise.
     */
    public boolean isAllowedToTransfer( long size )
    {
        TransferredDataCounter dataCounter = dataCounterFactory.get( context );
        return isAllowedToTransfer( size, dataCounter );
    }


    private boolean isAllowedToTransfer( long size, TransferredDataCounter counter )
    {
        checkTimeFrame( counter );
        return counter.get() + size < getThresholdInBytes( quota );
    }


    private void checkTimeFrame( TransferredDataCounter counter )
    {
        long timeFrameMillis = quota.getTimeUnit().toMillis( quota.getTime() );
        if ( counter.getUpdatedTimestamp() + timeFrameMillis < System.currentTimeMillis() )
        {
            counter.reset();
        }
    }


    private long getThresholdInBytes( TransferQuota quota )
    {
        return quota.getThreshold() * quota.getUnit().toBytes();
    }


    public void setDataCounterFactory( final TransferredDataCounterFactory dataCounterFactory )
    {
        this.dataCounterFactory = dataCounterFactory;
    }
}

