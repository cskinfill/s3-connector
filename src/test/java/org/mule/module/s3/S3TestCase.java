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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mule.module.s3.simpleapi.SimpleAmazonS3AmazonDevKitImpl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.BucketPolicy;
import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.StorageClass;

import java.net.URI;
import java.net.URL;

import org.apache.commons.io.input.NullInputStream;
import org.junit.Before;
import org.junit.Test;

public class S3TestCase
{
    private static final String POLICY_TEXT = "policy1";
    private static final String MY_OBJECT = "myObject";
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
    public void setObjectStorageClass()
    {
        connector.setObjectStorageClass(MY_BUCKET, MY_OBJECT, "Standard");
        verify(client).changeObjectStorageClass(MY_BUCKET, MY_OBJECT, StorageClass.Standard);
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
        when(client.copyObject(refEq(new CopyObjectRequest(MY_BUCKET, MY_OBJECT, MY_BUCKET, "myObject2")))).thenReturn(
            result);

        assertEquals("12", connector.copyObject(MY_BUCKET, MY_OBJECT, null, null, "myObject2", null, null));
    }

    @Test
    public void copyObjectWithVersion()
    {
        CopyObjectResult result = new CopyObjectResult();
        result.setVersionId("12");
        when(
            client.copyObject(refEq(new CopyObjectRequest(MY_BUCKET, MY_OBJECT, "12", MY_BUCKET, "myObject2")))).thenReturn(
            result);

        assertEquals("12", connector.copyObject(MY_BUCKET, MY_OBJECT, "12", null, "myObject2", null, null));
    }

    @Test
    public void copyObjectBucketWithACL()
    {
        CopyObjectRequest request = new CopyObjectRequest(MY_BUCKET, MY_OBJECT, "myBucket2", "myObject2");
        request.setCannedAccessControlList(CannedAccessControlList.Private);
        when(client.copyObject(refEq(request))).thenReturn(new CopyObjectResult());

        assertNull(connector.copyObject(MY_BUCKET, MY_OBJECT, null, "myBucket2", "myObject2", "Private", null));
    }

    @Test
    public void createObjectSimple()
    {
        when(
            client.putObject(refEq(new PutObjectRequest(MY_BUCKET, MY_OBJECT, new NullInputStream(0),
                new ObjectMetadata()), "metadata", "inputStream"))).thenReturn(new PutObjectResult());

        assertNull(connector.createObject(MY_BUCKET, MY_OBJECT, "have a nice release", null, null, null));
    }

    @Test
    public void createObjectWithFullOptions() throws Exception
    {
        PutObjectRequest request = new PutObjectRequest(MY_BUCKET, MY_OBJECT, new NullInputStream(0),
            new ObjectMetadata());
        request.setCannedAcl(CannedAccessControlList.PublicRead);
        request.setStorageClass(StorageClass.Standard);
        when(client.putObject(refEq(request, "metadata", "inputStream"))).thenReturn(new PutObjectResult());
        assertNull(connector.createObject(MY_BUCKET, MY_OBJECT, "have a nice release", "text/plain",
            "PublicRead", "Standard"));
    }

    @Test
    public void getBucketPolicy()
    {
        BucketPolicy policy = new BucketPolicy();
        policy.setPolicyText(POLICY_TEXT);
        when(client.getBucketPolicy(MY_BUCKET)).thenReturn(policy);
        assertSame(POLICY_TEXT, connector.getBucketPolicy(MY_BUCKET));
    }

    @Test
    public void createPresignedUri() throws Exception
    {
        when(client.generatePresignedUrl(MY_BUCKET, MY_OBJECT, null, HttpMethod.GET)).thenReturn(
            new URL("http://www.foo.com"));
        assertEquals(new URI("http://www.foo.com"), connector.createPresignedUri(MY_BUCKET, MY_OBJECT, null,
            null, "GET"));
    }

    @Test
    public void getObjectContent() throws Exception
    {
        S3Object s3Object = new S3Object();
        NullInputStream content = new NullInputStream(0);
        s3Object.setObjectContent(content);

        when(client.getObject(refEq(new GetObjectRequest(MY_BUCKET, MY_OBJECT)))).thenReturn(s3Object);
        assertSame(content, connector.getObjectContent(MY_BUCKET, MY_OBJECT, null));
    }

    @Test
    public void getObjectContentWithVersion() throws Exception
    {
        S3Object s3Object = new S3Object();
        NullInputStream content = new NullInputStream(0);
        s3Object.setObjectContent(content);

        when(client.getObject(refEq(new GetObjectRequest(MY_BUCKET, MY_OBJECT, "9")))).thenReturn(s3Object);
        assertSame(content, connector.getObjectContent(MY_BUCKET, MY_OBJECT, "9"));
    }

    @Test
    public void getObjectMetadata() throws Exception
    {
        ObjectMetadata meta = new ObjectMetadata();
        when(client.getObjectMetadata(refEq(new GetObjectMetadataRequest(MY_BUCKET, MY_OBJECT)))).thenReturn(
            meta);
        assertSame(meta, connector.getObjectMetadata(MY_BUCKET, MY_OBJECT, null));
    }

    @Test
    public void getObject() throws Exception
    {
        S3Object s3Object = new S3Object();

        when(client.getObject(refEq(new GetObjectRequest(MY_BUCKET, MY_OBJECT)))).thenReturn(s3Object);
        assertSame(s3Object, connector.getObject(MY_BUCKET, MY_OBJECT, null));
    }

    @Test
    public void deleteObject() throws Exception
    {
        connector.deleteObject(MY_BUCKET, MY_OBJECT, null);
        verify(client).deleteObject(MY_BUCKET, MY_OBJECT);
    }

    @Test
    public void deleteObjectWithVersion() throws Exception
    {
        connector.deleteObject(MY_BUCKET, MY_OBJECT, "25");
        verify(client).deleteVersion(MY_BUCKET, MY_OBJECT, "25");
    }

    @Test
    public void setBucketPolicy() throws Exception
    {
        connector.setBucketPolicy(MY_BUCKET, POLICY_TEXT);
        verify(client).setBucketPolicy(MY_BUCKET, POLICY_TEXT);
    }

    @Test
    public void listBuckets() throws Exception
    {
        connector.listBuckets();
        verify(client).listBuckets();
    }

    @Test
    public void setBucketWebsiteConfiguration() throws Exception
    {
        connector.setBucketWebsiteConfiguration(MY_BUCKET, "suffix1", "error.do");
        verify(client).setBucketWebsiteConfiguration(eq(MY_BUCKET), refEq(new BucketWebsiteConfiguration("suffix1", "error.do")));
    }
    
    @Test
    public void getBucketWebsiteConfiguration() throws Exception
    {
        BucketWebsiteConfiguration config = new BucketWebsiteConfiguration("suffix");
        when(client.getBucketWebsiteConfiguration(MY_BUCKET)).thenReturn(config);
        assertSame(config, connector.getBucketWebsiteConfiguration(MY_BUCKET));
    }
    
    @Test
    public void deleteBucketWebsiteConfiguration() throws Exception
    {
        connector.deleteBucketWebsiteConfiguration(MY_BUCKET);
        verify(client).deleteBucketWebsiteConfiguration(MY_BUCKET);
    }


    @Test(expected = IllegalArgumentException.class)
    public void setBucketWebsiteConfigurationNoSuffix() throws Exception
    {
        connector.setBucketWebsiteConfiguration(MY_BUCKET, null, "error.jsp");
    }

    @Test
    public void deleteBucketPolicy() throws Exception
    {
        connector.deleteBucketPolicy(MY_BUCKET);
        verify(client).deleteBucketPolicy(MY_BUCKET);
    }
    
    

}
