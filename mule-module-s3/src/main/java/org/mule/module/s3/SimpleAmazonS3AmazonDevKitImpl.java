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
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.StorageClass;

import java.io.InputStream;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.Validate;

public class SimpleAmazonS3AmazonDevKitImpl implements SimpleAmazonS3
{
    AmazonS3 s3;

    public SimpleAmazonS3AmazonDevKitImpl(@NotNull AmazonS3 s3)
    {
        Validate.notNull(s3);
        this.s3 = s3;
    }

    // 1.1
    public List<Bucket> listBuckets() throws AmazonClientException, AmazonServiceException
    {
        return s3.listBuckets();
    }

    // 2.1
    public Bucket createBucket(@NotNull String bucketName, String region, CannedAccessControlList acl)
        throws AmazonClientException, AmazonServiceException
    {
        Validate.notNull(bucketName);
        CreateBucketRequest request = new CreateBucketRequest(bucketName, region);
        request.setCannedAcl(acl);
        return s3.createBucket(request);
    }

    // 2.2
    public void deleteBucket(@NotNull String bucketName) throws AmazonClientException, AmazonServiceException
    {
        Validate.notNull(bucketName);
        s3.deleteBucket(bucketName);
    }

    public void deleteBucketAndObjects(@NotNull String bucketName)
        throws AmazonClientException, AmazonServiceException
    {
        Validate.notNull(bucketName);
        ObjectListing objects = s3.listObjects(bucketName);
        for (S3ObjectSummary summary : objects.getObjectSummaries())
        {
            s3.deleteObject(bucketName, summary.getKey());
        }
        deleteBucket(bucketName);
    }

    // 2.3
    public ObjectListing listObjects(@NotNull String bucketName, @NotNull String prefix)
        throws AmazonClientException, AmazonServiceException
    {
        Validate.notNull(bucketName);
        Validate.notNull(prefix);
        return s3.listObjects(bucketName, prefix);
    }

    // 3.1.1
    public void deleteBucketPolicy(@NotNull String bucketName)
        throws AmazonClientException, AmazonServiceException
    {
        Validate.notNull(bucketName);
        s3.deleteBucketPolicy(bucketName);
    }

    // 3.1.2
    public BucketPolicy getBucketPolicy(@NotNull String bucketName)
        throws AmazonClientException, AmazonServiceException
    {
        Validate.notNull(bucketName);
        return s3.getBucketPolicy(bucketName);
    }

    // 3.1.3
    public void setBucketPolicy(@NotNull String bucketName, @NotNull String policyText)
        throws AmazonClientException, AmazonServiceException
    {
        Validate.notNull(bucketName);
        Validate.notNull(policyText);
        s3.setBucketPolicy(bucketName, policyText);
    }

    // 3.2.1
    public void deleteBucketWebsiteConfiguration(@NotNull String bucketName)
        throws AmazonClientException, AmazonServiceException
    {
        Validate.notNull(bucketName);
        s3.deleteBucketWebsiteConfiguration(bucketName);
    }

    // 3.2.2
    public BucketWebsiteConfiguration getBucketWebsiteConfiguration(@NotNull String bucketName)
        throws AmazonClientException, AmazonServiceException
    {
        Validate.notNull(bucketName);
        return s3.getBucketWebsiteConfiguration(bucketName);
    }

    // 3.2.3
    public void setBucketWebsiteConfiguration(@NotNull String bucketName,
                                              @NotNull BucketWebsiteConfiguration configuration)
        throws AmazonClientException, AmazonServiceException
    {
        Validate.notNull(bucketName);
        Validate.notNull(configuration);
        s3.setBucketWebsiteConfiguration(bucketName, configuration);
    }

    // 4.1
    public String putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata)
        throws AmazonClientException, AmazonServiceException
    {
        return s3.putObject(bucketName, key, input, metadata).getVersionId();
    }

    public void deleteObject(String bucketName, String key)
        throws AmazonClientException, AmazonServiceException
    {
        s3.deleteObject(bucketName, key);
    }

    public void deleteVersion(String bucketName, String key, String versionId)
        throws AmazonClientException, AmazonServiceException
    {
        s3.deleteVersion(bucketName, key, versionId);
    }

    public void changeObjectStorageClass(String bucketName, String key, StorageClass newStorageClass)
        throws AmazonClientException, AmazonServiceException
    {
        s3.changeObjectStorageClass(bucketName, key, newStorageClass);
    }

    public CopyObjectResult copyObject(CopyObjectRequest copyOptions)
        throws AmazonClientException, AmazonServiceException
    {
        return s3.copyObject(copyOptions);
    }

    // 3. Get (full or just metadata, latest or specific version, conditional get)
    // 5. Generate Presigned URL to access an Object

}
