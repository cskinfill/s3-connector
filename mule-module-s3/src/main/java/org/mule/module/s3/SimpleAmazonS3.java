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
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketPolicy;
import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.StorageClass;

import java.io.InputStream;
import java.util.List;

import javax.validation.constraints.NotNull;

public interface SimpleAmazonS3
{
    public List<Bucket> listBuckets() throws AmazonClientException, AmazonServiceException;

    public Bucket createBucket(@NotNull String bucketName, String region, CannedAccessControlList acl)
        throws AmazonClientException, AmazonServiceException;

    public void deleteBucket(@NotNull String bucketName) throws AmazonClientException, AmazonServiceException;

    public void deleteBucketAndObjects(@NotNull String bucketName)
        throws AmazonClientException, AmazonServiceException;

    public ObjectListing listObjects(@NotNull String bucketName, @NotNull String prefix)
        throws AmazonClientException, AmazonServiceException;

    public void deleteBucketPolicy(@NotNull String bucketName)
        throws AmazonClientException, AmazonServiceException;

    public BucketPolicy getBucketPolicy(@NotNull String bucketName)
        throws AmazonClientException, AmazonServiceException;

    public void setBucketPolicy(@NotNull String bucketName, @NotNull String policyText)
        throws AmazonClientException, AmazonServiceException;

    public void deleteBucketWebsiteConfiguration(@NotNull String bucketName)
        throws AmazonClientException, AmazonServiceException;

    public BucketWebsiteConfiguration getBucketWebsiteConfiguration(@NotNull String bucketName)
        throws AmazonClientException, AmazonServiceException;

    public void setBucketWebsiteConfiguration(@NotNull String bucketName,
                                              @NotNull BucketWebsiteConfiguration configuration)
        throws AmazonClientException, AmazonServiceException;

    /**
     * @param bucketName
     * @param key
     * @param input
     * @param metadata
     * @return the version id, if the versioning was enabled
     * @throws AmazonClientException
     * @throws AmazonServiceException
     */
    public String putObject(@NotNull String bucketName,
                            @NotNull String key,
                            @NotNull InputStream input,
                            @NotNull ObjectMetadata metadata)
        throws AmazonClientException, AmazonServiceException;

    public void deleteObject(@NotNull String bucketName, @NotNull String key)
        throws AmazonClientException, AmazonServiceException;

    public void deleteVersion(@NotNull String bucketName, @NotNull String key, @NotNull String versionId)
        throws AmazonClientException, AmazonServiceException;

    public void changeObjectStorageClass(@NotNull String bucketName,
                                         @NotNull String key,
                                         @NotNull StorageClass newStorageClass)
        throws AmazonClientException, AmazonServiceException;

    public CopyObjectResult copyObject(@NotNull CopyObjectRequest copyOptions)
        throws AmazonClientException, AmazonServiceException;
}
