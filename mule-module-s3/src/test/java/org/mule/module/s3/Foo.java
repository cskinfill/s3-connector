/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.s3;

import static org.junit.Assert.*;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class Foo
{
    private AmazonS3Client s3;

    @Before
    public void setup()
    {
        AWSCredentials c = new BasicAWSCredentials("",
            "");
        s3 = new AmazonS3Client(c);
    }

    @Test
    public void testList() throws Exception
    {
        List<Bucket> buckets = s3.listBuckets();
        assertNotNull(buckets);

        System.out.println(buckets);
        for (Bucket bucket : buckets)
        {
            System.out.println(bucket);
        }

    }

    @Test
    public void testDeleteUnexistent() throws Exception
    {
        s3.deleteBucket("nonExistentBucket");
    }
    
    @Test
    public void testOverride() throws Exception
    {
        s3.createBucket("bucket1");
        s3.createBucket("bucket1");
    }
}
