/**
 * Mule S3 Cloud Connector
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.s3.simpleapi;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.StorageClass;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * A Amazon S3 facade roughly based on {@link AmazonS3} interface, but that
 * simplifies it by adding consistent versioning support. Otherwise stated, all
 * messages that take {@link S3ObjectId} are aware of versioning, that is, if
 * {@link S3ObjectId#isVersioned()}, then operations try to affect the specified
 * version.
 * <p>
 * Not all messages of {@link AmazonS3} interface are exposed here. However, those
 * exposed share the same semantics of that interface.
 * </p>
 * Exception handling:
 * <ul>
 * <li>All operations will throw {@link IllegalArgumentException} if a non null or
 * non empty constraint is violated {@link AmazonClientException}</li>
 * <li>All operations will throw {@link AmazonServiceException} if a s3 restriction
 * is violated, like for example, trying to create a bucket without permissions</li>
 * <li>All operation will throw {@link AmazonClientException} if any communication or
 * unexpected error occurs</li>
 * </ul>
 */
public interface SimpleAmazonS3
{
    List<Bucket> listBuckets();

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
    Bucket createBucket(@NotNull String bucketName, String region, CannedAccessControlList acl);

    /**
     * Deletes a Bucket
     * 
     * @see AmazonS3#deleteBucket(com.amazonaws.services.s3.model.DeleteBucketRequest)
     * @param bucketName
     * @throws AmazonClientException
     * @throws AmazonServiceException
     */
    void deleteBucket(@NotNull String bucketName);

    /**
     * Deletes a Bucket, deleting also all its contents if necessary
     * 
     * @see AmazonS3#deleteBucket(com.amazonaws.services.s3.model.DeleteBucketRequest)
     * @param bucketName
     * @throws AmazonClientException
     * @throws AmazonServiceException
     */
    void deleteBucketAndObjects(@NotNull String bucketName);

    ObjectListing listObjects(@NotNull String bucketName, @NotNull String prefix);

    void deleteBucketPolicy(@NotNull String bucketName);

    String getBucketPolicy(@NotNull String bucketName);

    void setBucketPolicy(@NotNull String bucketName, @NotNull String policyText);

    void deleteBucketWebsiteConfiguration(@NotNull String bucketName);

    BucketWebsiteConfiguration getBucketWebsiteConfiguration(@NotNull String bucketName);

    void setBucketWebsiteConfiguration(@NotNull String bucketName,
                                       @NotNull BucketWebsiteConfiguration configuration);

    /**
     * Creates an object, uploading its contents, and optionally setting its
     * {@link CannedAccessControlList} and {@link StorageClass}
     * 
     * @param objectId the id of the object to be created. If its versioned, its
     *            version is ignored
     * @param input
     * @param metadata
     * @param acl TODO
     * @param storageClass TODO
     * @return the version id, if the versioning was enabled
     * @throws AmazonClientException
     * @throws AmazonServiceException
     */
    String createObject(@NotNull S3ObjectId objectId,
                        @NotNull InputStream input,
                        @NotNull ObjectMetadata metadata,
                        CannedAccessControlList acl,
                        StorageClass storageClass);

    void deleteObject(@NotNull S3ObjectId objectId);

    void setObjectStorageClass(@NotNull S3ObjectId objectId, @NotNull StorageClass newStorageClass);

    /**
     * Copies a source object, with optional version, to a destination, with optional
     * destination object acl.
     * 
     * @param source
     * @param destination the destination object. If this id is versioned, its
     *            version is ignored
     * @param acl
     * @return the version id of the destination object, if versioning is enabled
     * @see AmazonS3#copyObject(CopyObjectRequest)
     * @throws AmazonClientException
     * @throws AmazonServiceException
     */
    String copyObject(@NotNull S3ObjectId source,
                      @NotNull S3ObjectId destination,
                      CannedAccessControlList acl,
                      StorageClass storageClass);

    /**
     * Creates a presigned URL for accessing the object of the given id, with an
     * optional http method and date expiration.
     * 
     * @param objectId
     * @param expiration if no expiration is supplied, a default expiration provided
     *            by AmazonS3 will be used
     * @param method if no method is supplied, PUT method is assumed
     * @throws AmazonClientException
     * @see AmazonS3#generatePresignedUrl(com.amazonaws.services.s3.model.
     *      GeneratePresignedUrlRequest)
     */
    URI createPresignedUri(@NotNull S3ObjectId objectId, Date expiration, HttpMethod method);

    /**
     * Answers the ObjectMetadata content a given {@link S3ObjectId}.
     * 
     * @param objectId
     * @return
     * @throws AmazonClientException
     * @see AmazonS3#getObject(com.amazonaws.services.s3.model.GetObjectMetadataRequest)
     */
    InputStream getObjectContent(@NotNull S3ObjectId objectId);

    /**
     * Answers the ObjectMetadata for a given {@link S3ObjectId}
     * 
     * @param objectId
     * @return
     * @throws AmazonClientException
     * @see AmazonS3#getObjectMetadata(com.amazonaws.services.s3.model.GetObjectMetadataRequest)
     */
    ObjectMetadata getObjectMetadata(@NotNull S3ObjectId objectId);

    /**
     * Retrieves an object from S3 given its id. <strong>Warning: use this method
     * with caution</strong>, as the retrieved object has an already open inputStream
     * to the object contents. It should be closed quickly.
     * 
     * @see AmazonS3#getObject(com.amazonaws.services.s3.model.GetObjectRequest)
     * @param objectId
     * @return
     */
    S3Object getObject(@NotNull S3ObjectId objectId);
}
