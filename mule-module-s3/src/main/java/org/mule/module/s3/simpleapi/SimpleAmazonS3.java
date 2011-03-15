/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.s3.simpleapi;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketPolicy;
import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.StorageClass;

import java.io.InputStream;
import java.util.List;

import javax.validation.constraints.NotNull;

public interface SimpleAmazonS3
{
    public List<Bucket> listBuckets() throws AmazonClientException, AmazonServiceException;

    /**
     * Creates a {@link Bucket}.
     * 
     * @see AmazonS3#createBucket(CreateBucketRequest)
     * @param bucketName mandatory
     * @param region optional
     * @param acl optional
     * @return the new Bucket
     * @throws AmazonClientException
     * @throws AmazonServiceException
     */
    public Bucket createBucket(@NotNull String bucketName, String region, CannedAccessControlList acl)
        throws AmazonClientException, AmazonServiceException;

    /**
     * Deletes a Bucket
     * 
     * @see AmazonS3#deleteBucket(com.amazonaws.services.s3.model.DeleteBucketRequest)
     * @param bucketName
     * @throws AmazonClientException
     * @throws AmazonServiceException
     */
    public void deleteBucket(@NotNull String bucketName) throws AmazonClientException, AmazonServiceException;

    /**
     * Deletes a Bucket, deleting also all its contents if necessary
     * 
     * @see AmazonS3#deleteBucket(com.amazonaws.services.s3.model.DeleteBucketRequest)
     * @param bucketName
     * @throws AmazonClientException
     * @throws AmazonServiceException
     */
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
     * @param input
     * @param metadata
     * @param acl TODO
     * @param storageClass TODO
     * @param bucketName
     * @param key
     * @return the version id, if the versioning was enabled
     * @throws AmazonClientException
     * @throws AmazonServiceException
     */
    public String createObject(@NotNull ObjectId objectId,
                               @NotNull InputStream input,
                               @NotNull ObjectMetadata metadata, CannedAccessControlList acl, StorageClass storageClass)
        throws AmazonClientException, AmazonServiceException;

    public void deleteObject(@NotNull ObjectId objectId) throws AmazonClientException, AmazonServiceException;

    public void deleteVersion(@NotNull ObjectId objectId, @NotNull String versionId)
        throws AmazonClientException, AmazonServiceException;

    public void changeObjectStorageClass(@NotNull ObjectId objectId, @NotNull StorageClass newStorageClass)
        throws AmazonClientException, AmazonServiceException;

    /**
     * Copies a source object to a destination, with optional destination object acl
     * 
     * @param source
     * @param destination
     * @param acl
     * @return the version id of the destination object, if versioning is enabled
     * @see AmazonS3#copyObject(CopyObjectRequest)
     * @throws AmazonClientException
     * @throws AmazonServiceException
     */
    public String copyObject(@NotNull ObjectId source,
                             @NotNull ObjectId destination,
                             CannedAccessControlList acl, 
                             StorageClass storageClass)
        throws AmazonClientException, AmazonServiceException;

}
