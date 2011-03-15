
package org.mule.module.s3;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.mule.module.s3.simpleapi.SimpleAmazonS3AmazonDevKitImpl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.StorageClass;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
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
        connector.changeObjectStorageClass("myBucket", "myObject", "Standard");
        verify(client).changeObjectStorageClass("myBucket", "myObject", StorageClass.Standard);
    }

    @Test
    public void createBucket()
    {
        connector.createBucket("myBucket", "US", null);
        verify(client).createBucket(refEq(new CreateBucketRequest("myBucket", "US")));
    }

    @Test
    public void createBucketWithAcl()
    {
        connector.createBucket("myBucket", "US", "Private");
        CreateBucketRequest request = new CreateBucketRequest("myBucket", "US");
        request.setCannedAcl(CannedAccessControlList.Private);
        verify(client).createBucket(refEq(request));
    }

}
