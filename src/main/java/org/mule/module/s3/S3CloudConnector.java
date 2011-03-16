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

import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.module.s3.simpleapi.S3ObjectId;
import org.mule.module.s3.simpleapi.SimpleAmazonS3;
import org.mule.module.s3.simpleapi.SimpleAmazonS3AmazonDevKitImpl;
import org.mule.tools.cloudconnect.annotations.Connector;
import org.mule.tools.cloudconnect.annotations.Operation;
import org.mule.tools.cloudconnect.annotations.Parameter;
import org.mule.tools.cloudconnect.annotations.Property;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.StorageClass;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;

@Connector(namespacePrefix = "s3", namespaceUri = "http://www.mulesoft.org/schema/mule/s3")
public class S3CloudConnector implements Initialisable
{

    @Property
    private String accessKey;
    @Property
    private String secretKey;

    private SimpleAmazonS3 client;

    // TODO defaults

    /**
     * Example: {@code <s3:create-bucket bucketName="myBucket" acl="Private"/> }
     * 
     * @param bucketName mandatory. The bucket to create
     * @param region optional. The region were to create the bucket. Default is
     *            US_STANDARD
     * @param acl optional. TODO default ACL is not clear enough in the AmazonS3
     * @return the new Bucket
     * @throws AmazonClientException
     */
    @Operation
    public Bucket createBucket(@Parameter(optional = false) String bucketName,
                               @Parameter(optional = true) String region,
                               @Parameter(optional = true) String acl)
    {
        return client.createBucket(bucketName, region, toAcl(acl));
    }

    private CannedAccessControlList toAcl(String acl)
    {
        return acl != null ? CannedAccessControlList.valueOf(acl) : null;
    }

    /**
     * Example: {@code <s3:delete-bucket bucketName="myBucket" force="true"/> }
     * 
     * @param bucketName mandatory the bucket to delete
     * @param force optional {@code true} if the bucket must be deleted even if it is
     *            not empty, {@code false} if operation should fail in such scenario.
     *            Default is {@code false}
     * @throws AmazonClientException
     * @throws AmazonServiceException
     */
    @Operation
    public void deleteBucket(@Parameter(optional = false) String bucketName,
                             @Parameter(optional = true, defaultValue = "false") boolean force)
    {
        if (force)
        {
            client.deleteBucketAndObjects(bucketName);
        }
        else
        {
            client.deleteBucket(bucketName);
        }
    }

    /**
     * Example: {@code <s3:delete-bucket-website-configuration
     * bucketName="myBucket"/>}
     * 
     * @param bucketName mandatory the bucket whose policy to delete
     * @throws AmazonClientException
     * @throws AmazonServiceException
     */
    @Operation
    public void deleteBucketWebsiteConfiguration(@Parameter(optional = false) String bucketName)
    {
        client.deleteBucketWebsiteConfiguration(bucketName);
    }

    /**
     * Example: {@code <s3:get-bucket-policy bucketName="myBucket"/>}
     * 
     * @param bucketName mandatory the bucket whose policy to retrieve
     * @return the bucket policy TODO could be policy absent?
     * @throws AmazonClientException
     * @throws AmazonServiceException
     */
    @Operation
    public String getBucketPolicy(@Parameter(optional = false) String bucketName)
    {
        return client.getBucketPolicy(bucketName);
    }

    /**
     * Example: {@code <s3:set-bucket-policy bucketName="myBucket"
     * policyText="your policy" />}
     * 
     * @param bucketName mandatory the bucket name
     * @param policyText mandatory the policy text
     */
    @Operation
    public void setBucketPolicy(@Parameter(optional = false) String bucketName,
                                @Parameter(optional = false) String policyText)
    {
        client.setBucketPolicy(bucketName, policyText);
    }

    /**
     * Example: {@code <s3:delete-bucket-policy bucketName="myBucket"/>}
     * 
     * @param bucketName mandatory the bucket whose policy to delete
     * @throws AmazonClientException
     * @throws AmazonServiceException
     */
    @Operation
    public void deleteBucketPolicy(@Parameter(optional = false) String bucketName)
    {
        client.deleteBucketPolicy(bucketName);
    }

    @Operation
    public void setBucketWebsiteConfiguration(@Parameter(optional = false) String bucketName,
                                              @Parameter(optional = false) String suffix,
                                              @Parameter(optional = true) String errorPage)
    {
        client.setBucketWebsiteConfiguration(bucketName, errorPage != null ? new BucketWebsiteConfiguration(
            suffix, errorPage) : new BucketWebsiteConfiguration(suffix));
    }

    @Operation
    public BucketWebsiteConfiguration getBucketWebsiteConfiguration(@Parameter(optional = false) String bucketName)
    {
        return client.getBucketWebsiteConfiguration(bucketName);
    }

    @Operation
    public List<Bucket> listBuckets()
    {
        return client.listBuckets();
    }

    // TODO return keys instead of an object listing?
    // TODO empty prefix for listing all objects?
    // TODO support pagination
    @Operation
    public ObjectListing listObjects(@Parameter(optional = false) String bucketName,
                                     @Parameter(optional = false) String prefix)
    {
        return client.listObjects(bucketName, prefix);
    }

    // TODO add support for usermetadata
    @Operation
    public String createObject(@Parameter(optional = false) String bucketName,
                               @Parameter(optional = false) String key,
                               @Parameter(optional = false) Object content,
                               @Parameter(optional = true) String contentType,
                               @Parameter(optional = true) String acl,
                               @Parameter(optional = true) String storageClass)
    {

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        return client.createObject(new S3ObjectId(bucketName, key), createContent(content), metadata,
            toAcl(acl), toStorageClass(storageClass));
    }

    @Operation
    public void deleteObject(@Parameter(optional = false) String bucketName,
                             @Parameter(optional = false) String key,
                             @Parameter(optional = true) String versionId)
    {
        client.deleteObject(new S3ObjectId(bucketName, key, versionId));
    }

    @Operation
    public void setObjectStorageClass(@Parameter(optional = false) String bucketName,
                                      @Parameter(optional = false) String key,
                                      @Parameter(optional = false) String newStorageClass)
    {
        Validate.notNull(newStorageClass);
        client.setObjectStorageClass(new S3ObjectId(bucketName, key), toStorageClass(newStorageClass));
    }

    private StorageClass toStorageClass(String storageClass)
    {
        return storageClass != null ? StorageClass.valueOf(storageClass) : null;
    }

    @Operation
    public String copyObject(@Parameter(optional = false) String sourceBucketName,
                             @Parameter(optional = false) String sourceKey,
                             @Parameter(optional = true) String sourceVersionId,
                             @Parameter(optional = true) String destinationBucketName,
                             @Parameter(optional = false) String destinationKey,
                             @Parameter(optional = true) String destinationAcl,
                             @Parameter(optional = true) String destinationStorageClass)
    {
        return client.copyObject(new S3ObjectId(sourceBucketName, sourceKey, sourceVersionId),
            new S3ObjectId(coalesce(destinationBucketName, sourceBucketName), destinationKey),
            toAcl(destinationAcl), toStorageClass(destinationStorageClass));
    }

    public URI createPresignedUri(@Parameter(optional = false) String bucketName,
                                  @Parameter(optional = false) String key,
                                  @Parameter(optional = true) String versionId,
                                  @Parameter(optional = true) Date expiration,
                                  @Parameter(optional = true) String method)
    {
        return client.createPresignedUri(new S3ObjectId(bucketName, key, versionId), expiration,
            toHttpMethod(method));
    }

    private HttpMethod toHttpMethod(String method)
    {
        return method != null ? HttpMethod.valueOf(method) : null;
    }

    public InputStream getObjectContent(@Parameter(optional = false) String bucketName,
                                        @Parameter(optional = false) String key,
                                        @Parameter(optional = true) String versionId)

    {
        return client.getObjectContent(new S3ObjectId(bucketName, key, versionId));
    }

    public ObjectMetadata getObjectMetadata(@Parameter(optional = false) String bucketName,
                                            @Parameter(optional = false) String key,
                                            @Parameter(optional = true) String versionId)

    {
        return client.getObjectMetadata(new S3ObjectId(bucketName, key, versionId));
    }

    public S3Object getObject(@Parameter(optional = false) String bucketName,
                              @Parameter(optional = false) String key,
                              @Parameter(optional = true) String versionId)
    {
        return client.getObject(new S3ObjectId(bucketName, key, versionId));
    }

    public void initialise() throws InitialisationException
    {
        if (client == null)
        {
            client = new SimpleAmazonS3AmazonDevKitImpl(createAmazonS3());
        }
    }

    /**
     * Creates an {@link AmazonS3} client. If accessKey and secretKey are not set,
     * the resulting client is annonymous
     * 
     * @return a new {@link AmazonS3}
     */
    private AmazonS3Client createAmazonS3()
    {
        if (accessKey == null && secretKey == null)
        {
            return new AmazonS3Client();
        }
        return new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
    }

    public String getAccessKey()
    {
        return accessKey;
    }

    public void setAccessKey(String accessKey)
    {
        this.accessKey = accessKey;
    }

    public String getSecretKey()
    {
        return secretKey;
    }

    public void setSecretKey(String secretKey)
    {
        this.secretKey = secretKey;
    }

    public void setClient(SimpleAmazonS3 client)
    {
        this.client = client;
    }

    private static InputStream createContent(Object content)
    {
        if (content instanceof InputStream)
        {
            return (InputStream) content;
        }
        if (content instanceof String)
        {
            return new ByteArrayInputStream(((String) content).getBytes());
        }
        if (content instanceof byte[])
        {
            return new ByteArrayInputStream((byte[]) content);
        }
        throw new IllegalArgumentException("Wrong input");
    }

    private <T> T coalesce(T o0, T o1)
    {
        return o0 != null ? o0 : o1;
    }

}
