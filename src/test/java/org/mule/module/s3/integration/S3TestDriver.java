/**
 * Mule S3 Cloud Connector
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.s3.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.mule.api.lifecycle.InitialisationException;
import org.mule.module.s3.S3CloudConnector;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.Bucket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class S3TestDriver
{
    private S3CloudConnector connector;
    private String bucketName;

    @Before
    public void setup() throws InitialisationException
    {
        connector = new S3CloudConnector();
        connector.setAccessKey(System.getenv("user.key.access"));
        connector.setSecretKey(System.getenv("user.key.secret"));
        bucketName = System.getenv("bucket.name");
        connector.initialise();
    }

    @After
    public void teardown()
    {
        connector.deleteBucket(bucketName, true);
    }


    @Test(expected = AmazonServiceException.class)
    public void testDeleteNoForce() throws Exception
    {
        connector.createBucket(bucketName, null, "Private");
        connector.createObject(bucketName, "anObject", "hello world", null, null, null, null, null, null);
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
        assertFalse(connector.listObjects(bucketName, "").iterator().hasNext());

        String objectVersion = connector.createObject(bucketName, "anObject", "hello world!", null,
            null, "text/plain", null, "Standard", null);
        // Versioning is not enabled
        assertNull(objectVersion);

        assertTrue(connector.listObjects(bucketName, "").iterator().hasNext());
    }
}
