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

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketPolicy;
import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.List;

public class SimpleAmazonS3Impl implements SimpleAmazonS3
{
    private AmazonS3 s3;

    public SimpleAmazonS3Impl(AmazonS3 s3)
    {
        this.s3 = s3;
    }

    // 1.1
    public List<Bucket> listBuckets() throws AmazonClientException, AmazonServiceException
    {
        return s3.listBuckets();
    }

    // 2.1
    public Bucket createBucket(String bucketName, String region, CannedAccessControlList acl)
        throws AmazonClientException, AmazonServiceException
    {
        Bucket bucket = s3.createBucket(bucketName, region);
        s3.setBucketAcl(bucketName, acl);
        return bucket;
    }

    // 2.2
    public void deleteBucket(String bucketName) throws AmazonClientException, AmazonServiceException
    {
        s3.deleteBucket(bucketName);
    }

    public void deleteBucketAndObjects(String bucketName)
        throws AmazonClientException, AmazonServiceException
    {
        ObjectListing objects = s3.listObjects(bucketName);
        for (S3ObjectSummary summary : objects.getObjectSummaries())
        {
            s3.deleteObject(bucketName, summary.getKey());
        }
        deleteBucket(bucketName);
    }

    // 2.3
    public ObjectListing listObjects(String bucketName, String prefix)
        throws AmazonClientException, AmazonServiceException
    {
        return s3.listObjects(bucketName, prefix);
    }

    // 3.1.1
    public void deleteBucketPolicy(String bucketName) throws AmazonClientException, AmazonServiceException
    {
        s3.deleteBucketPolicy(bucketName);
    }

    // 3.1.2
    public BucketPolicy getBucketPolicy(String bucketName)
        throws AmazonClientException, AmazonServiceException
    {
        return s3.getBucketPolicy(bucketName);
    }

    // 3.1.3
    public void setBucketPolicy(String bucketName, String policyText)
        throws AmazonClientException, AmazonServiceException
    {
        s3.setBucketPolicy(bucketName, policyText);
    }

    // 3.2.1
    public void deleteBucketWebsiteConfiguration(String bucketName)
        throws AmazonClientException, AmazonServiceException
    {
        s3.deleteBucketWebsiteConfiguration(bucketName);
    }

    // 3.2.2
    public BucketWebsiteConfiguration getBucketWebsiteConfiguration(String bucketName)
        throws AmazonClientException, AmazonServiceException
    {
        return s3.getBucketWebsiteConfiguration(bucketName);
    }

    // 3.2.3
    public void setBucketWebsiteConfiguration(String bucketName, BucketWebsiteConfiguration configuration)
        throws AmazonClientException, AmazonServiceException
    {
        s3.setBucketWebsiteConfiguration(bucketName, configuration);
    }

}
