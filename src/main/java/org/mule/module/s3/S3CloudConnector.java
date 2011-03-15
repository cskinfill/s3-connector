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
import org.mule.module.s3.simpleapi.ObjectId;
import org.mule.module.s3.simpleapi.SimpleAmazonS3;
import org.mule.module.s3.simpleapi.SimpleAmazonS3AmazonDevKitImpl;
import org.mule.tools.cloudconnect.annotations.Connector;
import org.mule.tools.cloudconnect.annotations.Operation;
import org.mule.tools.cloudconnect.annotations.Parameter;
import org.mule.tools.cloudconnect.annotations.Property;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketPolicy;
import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.StorageClass;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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

    /**
     * Example: {@code <s3:create-bucket bucketName="myBucket" acl="Private" }
     * 
     * @param bucketName
     * @param region
     * @param acl
     * @return the new Bucket
     * @throws AmazonClientException
     * @throws AmazonServiceException
     */
    @Operation
    public Bucket createBucket(@Parameter(optional = false) String bucketName,
                               @Parameter(optional = true) String region,
                               @Parameter(optional = true) String acl)
        throws AmazonClientException, AmazonServiceException
    {
        return client.createBucket(bucketName, region, toAcl(acl));
    }

    private CannedAccessControlList toAcl(String acl)
    {
        return acl != null ? CannedAccessControlList.valueOf(acl) : null;
    }

    @Operation
    public void deleteBucketAndObjects(@Parameter(optional = false) String bucketName,
                                       @Parameter(optional = true, defaultValue = "false") boolean force)
        throws AmazonClientException, AmazonServiceException
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

    @Operation
    public void deleteBucketPolicy(@Parameter(optional = false) String bucketName)
        throws AmazonClientException, AmazonServiceException
    {
        client.deleteBucketPolicy(bucketName);
    }

    @Operation
    public void deleteBucketWebsiteConfiguration(@Parameter(optional = false) String bucketName)
        throws AmazonClientException, AmazonServiceException
    {
        client.deleteBucketWebsiteConfiguration(bucketName);
    }

    @Operation
    public BucketPolicy getBucketPolicy(@Parameter(optional = false) String bucketName)
        throws AmazonClientException, AmazonServiceException
    {
        return client.getBucketPolicy(bucketName);
    }

    @Operation
    public BucketWebsiteConfiguration getBucketWebsiteConfiguration(@Parameter(optional = false) String bucketName)
        throws AmazonClientException, AmazonServiceException
    {
        return client.getBucketWebsiteConfiguration(bucketName);
    }

    @Operation
    public List<Bucket> listBuckets() throws AmazonClientException, AmazonServiceException
    {
        return client.listBuckets();
    }

    @Operation
    public ObjectListing listObjects(@Parameter(optional = false) String bucketName,
                                     @Parameter(optional = false) String prefix)
        throws AmazonClientException, AmazonServiceException
    {
        return client.listObjects(bucketName, prefix);
    }

    @Operation
    public void setBucketPolicy(@Parameter(optional = false) String bucketName,
                                @Parameter(optional = false) String policyText)
        throws AmazonClientException, AmazonServiceException
    {
        client.setBucketPolicy(bucketName, policyText);
    }

    @Operation
    public void setBucketWebsiteConfiguration(@Parameter(optional = false) String bucketName,
                                              @Parameter(optional = false) String suffix,
                                              @Parameter(optional = true) String errorPage)
        throws AmazonClientException, AmazonServiceException
    {
        client.setBucketWebsiteConfiguration(bucketName, errorPage != null ? new BucketWebsiteConfiguration(
            suffix, errorPage) : new BucketWebsiteConfiguration(suffix));
    }

    // 1. Upload (set content, content-type, canned acl, storage class, user metadata
    // map)
    // TODO add support for usermetadata
    @Operation
    public String createObject(@Parameter(optional = false) String bucketName,
                               @Parameter(optional = false) String key,
                               @Parameter(optional = false) Object input,
                               @Parameter(optional = true) String contentType,
                               @Parameter(optional = true) String acl,
                               @Parameter(optional = true) String storageClass)
        throws AmazonClientException, AmazonServiceException
    {

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        return client.createObject(new ObjectId(bucketName, key), createContent(input), metadata, toAcl(acl),
            toStorageClass(storageClass));
    }

    @Operation
    public void deleteObject(@Parameter(optional = false) String bucketName,
                             @Parameter(optional = false) String key,
                             @Parameter(optional = true) String versionId)
        throws AmazonClientException, AmazonServiceException
    {
        if (versionId == null)
        {
            client.deleteObject(new ObjectId(bucketName, key));
        }
        else
        {
            client.deleteVersion(new ObjectId(bucketName, key), versionId);
        }
    }

    @Operation
    public void changeObjectStorageClass(@Parameter(optional = false) String bucketName,
                                         @Parameter(optional = false) String key,
                                         @Parameter(optional = false) String newStorageClass)
        throws AmazonClientException, AmazonServiceException
    {
        Validate.notNull(newStorageClass);
        client.changeObjectStorageClass(new ObjectId(bucketName, key), toStorageClass(newStorageClass));
    }

    private StorageClass toStorageClass(String storageClass)
    {
        return storageClass != null ? StorageClass.valueOf(storageClass) : null;
    }

    public String copyObject(@Parameter(optional = false) String bucketSourceName,
                             @Parameter(optional = false) String sourceKey,
                             @Parameter(optional = false) String bucketDestinationName,
                             @Parameter(optional = false) String destinationKey,
                             @Parameter(optional = true) String acl,
                             @Parameter(optional = true) String storageClass)
        throws AmazonClientException, AmazonServiceException
    {
        return client.copyObject(new ObjectId(bucketSourceName, sourceKey), new ObjectId(coalesce(
            bucketSourceName, bucketDestinationName), destinationKey), toAcl(acl),
            toStorageClass(storageClass));
    }

    public void initialise() throws InitialisationException
    {
        if (client == null)
        {
            client = new SimpleAmazonS3AmazonDevKitImpl(new AmazonS3Client(new BasicAWSCredentials(accessKey,
                secretKey)));
        }
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
        return o1 != null ? o1 : o0;
    }
}
