
package org.mule.module.s3;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.StorageClass;

import org.junit.Before;
import org.junit.Test;

public class S3TestCase
{
    private S3CloudConnector connector;
    private AmazonS3 client;

    @Before
    public void setup()
    {
        client = mock(AmazonS3.class);
        connector = new S3CloudConnector();
        connector.setClient(new SimpleAmazonS3AmazonDevKitImpl(client));
    }

    @Test
    public void testchangeObjectStorageClass() throws AmazonClientException, AmazonServiceException
    {
        connector.changeObjectStorageClass("myBucket", "myObject", "STANDARD");
        verify(client).changeObjectStorageClass("myBucket", "myObject", StorageClass.Standard);
    }

    @Test
    public void createBucket()
    {
        connector.createBucket("myBucket", "US", null);
        verify(client).createBucket((CreateBucketRequest) anyObject());
    }

}
