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
import static org.junit.Assert.*;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.Bucket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class S3TestDriver
{
    private S3CloudConnector connector;
    private String bucketName;
    private String accessKey;
    private String secretKey;

    @Before
    public void setup()
    {
        connector = new S3CloudConnector();
        connector.setAccessKey(accessKey);
        connector.setSecretKey(secretKey);
    }

    @After
    public void teardown()
    {
        connector.deleteBucket(bucketName, true);
    }

    @Test
    public void testDeleteUnexistent() throws Exception
    {
        connector.deleteBucket("NonExistentBucket", true);
    }

    @Test(expected = AmazonServiceException.class)
    public void testDeleteNoForce() throws Exception
    {
        connector.createBucket(bucketName, null, "Private");
        connector.createObject(bucketName, "anObject", "hello world", null, null, null, null, null);

        connector.deleteBucket(bucketName, false);
    }

    @Test
    public void testCreateBucketAndObjects() throws Exception
    {
        int bucketsCount = connector.listBuckets().size();

        Bucket bucket = connector.createBucket(bucketName, null, null);
        assertNotNull(bucket);
        assertEquals(bucketName, bucket.getName());

        assertEquals(bucketsCount + 1, connector.listBuckets().size());
        assertEquals(0, connector.listObjects(bucketName, "").getObjectSummaries().size());

        String objectVersion = connector.createObject(bucketName, "anObject", "hello world!", null,
            null, "text/plain", null, "Standard");
        // Versioning is not enabled
        assertNull(objectVersion);

        assertEquals(1, connector.listObjects(bucketName, "anObj").getObjectSummaries().size());
    }
}
