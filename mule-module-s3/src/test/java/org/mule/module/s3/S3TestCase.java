/**
 * Mule S3 Cloud Connector
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import com.amazonaws.services.s3.model.BucketPolicy;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.StorageClass;

import org.apache.commons.io.input.NullInputStream;
import org.junit.Before;
import org.junit.Test;

public class S3TestCase
{
    private static final String MY_BUCKET = "myBucket";
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
    public void changeObjectStorageClass() throws AmazonClientException, AmazonServiceException
    {
        connector.changeObjectStorageClass(MY_BUCKET, "myObject", "Standard");
        verify(client).changeObjectStorageClass(MY_BUCKET, "myObject", StorageClass.Standard);
    }

    @Test
    public void createBucket()
    {
        connector.createBucket(MY_BUCKET, "US", null);
        verify(client).createBucket(refEq(new CreateBucketRequest(MY_BUCKET, "US")));
    }

    @Test
    public void createBucketWithAcl()
    {
        connector.createBucket(MY_BUCKET, "US", "Private");
        CreateBucketRequest request = new CreateBucketRequest(MY_BUCKET, "US");
        request.setCannedAcl(CannedAccessControlList.Private);
        verify(client).createBucket(refEq(request));
    }

    @Test
    public void copyObjectNoDestinationBucket()
    {
        CopyObjectResult result = new CopyObjectResult();
        result.setVersionId("12");
        when(client.copyObject(refEq(new CopyObjectRequest(MY_BUCKET, "myObject", MY_BUCKET, "myObject2")))).thenReturn(
            result);

        assertEquals("12", connector.copyObject(MY_BUCKET, "myObject", null, "myObject2", null, null));;
    }

    @Test
    public void copyObjectBucketWithACL()
    {
        CopyObjectRequest request = new CopyObjectRequest(MY_BUCKET, "myObject", "myBucket2", "myObject2");
        request.setCannedAccessControlList(CannedAccessControlList.Private);
        when(client.copyObject(refEq(request))).thenReturn(new CopyObjectResult());

        assertNull(connector.copyObject(MY_BUCKET, "myObject", "myBucket2", "myObject2", "Private", null));
    }

    @Test
    public void createObject()
    {
        when(
            client.putObject(refEq(new PutObjectRequest(MY_BUCKET, "myObject", new NullInputStream(0),
                new ObjectMetadata()), "metadata", "inputStream"))).thenReturn(new PutObjectResult());

        assertNull(connector.createObject(MY_BUCKET, "myObject", "have a nice release", null, null, null));
    }

    @Test
    public void getBucketPolicy()
    {
        BucketPolicy policy = new BucketPolicy();
        when(client.getBucketPolicy(MY_BUCKET)).thenReturn(policy);
        assertSame(policy, connector.getBucketPolicy(MY_BUCKET));
    }

}
