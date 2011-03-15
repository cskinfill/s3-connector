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
