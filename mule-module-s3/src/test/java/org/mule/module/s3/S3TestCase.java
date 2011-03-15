/**
 * Mule S3 Cloud Connector
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */


package org.mule.module.s3;

import static org.mockito.Matchers.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.*;
import org.mule.module.s3.simpleapi.SimpleAmazonS3AmazonDevKitImpl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
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

    @Test
    public void copyObjectNoDestinationBucket()
    {
        CopyObjectResult result = new CopyObjectResult();
        result.setVersionId("12");
        when(client.copyObject(refEq(new CopyObjectRequest("myBucket", "myObject", "myBucket", "myObject2")))).thenReturn(
            result);

        assertEquals("12", connector.copyObject("myBucket", "myObject", null, "myObject2", null, null));;
    }

    @Test
    public void copyObjectBucketWithACL()
    {
        CopyObjectRequest request = new CopyObjectRequest("myBucket", "myObject", "myBucket2", "myObject2");
        request.setCannedAccessControlList(CannedAccessControlList.Private);
        when(client.copyObject(refEq(request))).thenReturn(new CopyObjectResult());

        assertNull(connector.copyObject("myBucket", "myObject", "myBucket2", "myObject2", "Private", null));
    }

}
