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

import static org.mule.module.s3.util.InternalUtils.coalesce;

import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.module.s3.simpleapi.ConditionalConstraints;
import org.mule.module.s3.simpleapi.Region;
import org.mule.module.s3.simpleapi.S3ObjectId;
import org.mule.module.s3.simpleapi.SimpleAmazonS3;
import org.mule.module.s3.simpleapi.SimpleAmazonS3AmazonDevKitImpl;
import org.mule.module.s3.simpleapi.VersioningStatus;
import org.mule.tools.cloudconnect.annotations.Connector;
import org.mule.tools.cloudconnect.annotations.Operation;
import org.mule.tools.cloudconnect.annotations.Parameter;
import org.mule.tools.cloudconnect.annotations.Property;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

/**
 * A cloud connector wrapper on {@link SimpleAmazonS3} api. Same exception handling
 * policies applies. Documentation is based in that of {@link com.amazonaws.services.s3.AmazonS3}
 */
@Connector(namespacePrefix = "s3", namespaceUri = "http://www.mulesoft.org/schema/mule/s3")
public class S3CloudConnector implements Initialisable
{
    @Property
    private String accessKey;
    @Property
    private String secretKey;

    @Property(optional = true)
    private String proxyUsername;
    @Property(optional = true)
    private Integer proxyPort;
    @Property(optional = true)
    private String proxyPassword;
    @Property(optional = true)
    private String proxyHost;
    
    private SimpleAmazonS3 client;

    /**
     * Creates a new bucket; connector must not be configured as anonymous for this
     * operation to succeed. Bucket names must be unique across all of Amazon S3,
     * that is, among all their users. Bucket ownership is similar to the ownership
     * of Internet domain names. Within Amazon S3, only a single user owns each
     * bucket. Once a uniquely named bucket is created in Amazon S3, organize and
     * name the objects within the bucket in any way. Ownership of the bucket is
     * retained as long as the owner has an Amazon S3 account. To conform with DNS
     * requirements, buckets names must: not contain underscores, be between 3 and 63
     * characters long, not end with a dash, not contain adjacent periods, not
     * contain dashes next to periods and not contain uppercase characters. Do not
     * make bucket create or delete calls in the high availability code path of an
     * application. Create or delete buckets in a separate initialization or setup.
     * Example: {@code <s3:create-bucket bucketName="my-bucket" acl="Private"/> }
     * 
     * @param bucketName The bucket to create. It must not exist yet.
     * @param region the region where to create the new bucket
     * @param acl the access control list of the new bucket
     * @return the non null, new Bucket
     */
    @Operation
    public Bucket createBucket(@Parameter(optional = false) String bucketName,
                               @Parameter(optional = true, defaultValue = "US_STANDARD") Region region,
                               @Parameter(optional = true, defaultValue = "PRIVATE") AccessControlList acl)
    {
        return client.createBucket(bucketName, region, acl.toS3Equivalent());
    }

    /**
     * Deletes the specified bucket. All objects (and all object versions, if
     * versioning was ever enabled) in the bucket must be deleted before the bucket
     * itself can be deleted; this restriction can be relaxed by specifying the 
     * attribute  force="true". Example: 
     * {@code <s3:delete-bucket bucketName="my-bucket" force="true"/> }
     * 
     * @param bucketName the bucket to delete
     * @param force optional true if the bucket must be deleted even if it is not empty, false if operation should fail in such scenario.
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
     * Removes the website configuration for a bucket; this operation requires the
     * DeleteBucketWebsite permission. By default, only the bucket owner can delete
     * the website configuration attached to a bucket. However, bucket owners can
     * grant other users permission to delete the website configuration by writing a
     * bucket policy granting them the <code>S3:DeleteBucketWebsite</code>
     * permission. Calling this operation on a bucket with no website configuration
     * does not fail, but calling this operation a bucket that does not exist does.
     * Example: 
     * {@code <s3:delete-bucket-website-configuration bucketName="my-bucket"/>}
     * 
     * @param bucketName the bucket whose policy to delete
     */
    @Operation
    public void deleteBucketWebsiteConfiguration(@Parameter(optional = false) String bucketName)
    {
        client.deleteBucketWebsiteConfiguration(bucketName);
    }

    /**
     * Answers the policy for the given bucket. Only the owner of the bucket can
     * retrieve it. If no policy has been set for the bucket, then a null policy text
     * field will be returned. Example: 
     * {@code <s3:get-bucket-policy bucketName="my-bucket"/>}
     * 
     * @param bucketName the bucket whose policy to retrieve
     * @return the bucket policy, or null, if not set
     */
    @Operation
    public String getBucketPolicy(@Parameter(optional = false) String bucketName)
    {
        return client.getBucketPolicy(bucketName);
    }

    /**
     * Sets the bucket's policy, overriding any previously set. Only the owner of the
     * bucket can set a bucket policy. Bucket policies provide access control
     * management at the bucket level for both the bucket resource and contained
     * object resources. Only one policy can be specified per-bucket. Example:
     * {@code <s3:set-bucket-policy bucketName="my-bucket" policyText="your policy" />}
     * 
     * @param bucketName the bucket name
     * @param policyText the policy text
     */
    @Operation
    public void setBucketPolicy(@Parameter(optional = false) String bucketName,
                                @Parameter(optional = false) String policyText)
    {
        client.setBucketPolicy(bucketName, policyText);
    }

    /**
     * Deletes the bucket's policy. Only the owner of the bucket can delete the
     * bucket policy. Bucket policies provide access control management at the bucket
     * level for both the bucket resource and contained object resources. Example:
     * {@code <s3:delete-bucket-policy bucketName="my-bucket"/>}
     * 
     * @param bucketName the bucket whose policy to delete
     */
    @Operation
    public void deleteBucketPolicy(@Parameter(optional = false) String bucketName)
    {
        client.deleteBucketPolicy(bucketName);
    }

    /**
     * Sets the given bucket's website configuration. This operation requires the
     * PutBucketWebsite permission. By default, only the bucket owner can configure
     * the website attached to a bucket. However, bucket owners can allow other users
     * to set the website configuration by writing a bucket policy granting them the
     * S3:PutBucketWebsite permission. Example: {@code
     * <s3:set-bucket-website-configuration bucketName="my-bucket" suffix="index.html" 
     *                                      errorDocument="errorDocument.html" />}
     * 
     * @param bucketName the target bucket's name
     * @param suffix The document to serve when a directory is specified, relative to
     *            the requested resource
     * @param errorDocument the full path to error document the bucket will use as
     *            error page for 4XX errors
     */
    @Operation
    public void setBucketWebsiteConfiguration(@Parameter(optional = false) String bucketName,
                                              @Parameter(optional = false) String suffix,
                                              @Parameter(optional = true) String errorDocument)
    {
        client.setBucketWebsiteConfiguration(bucketName,
            errorDocument != null
                                 ? new BucketWebsiteConfiguration(suffix, errorDocument)
                                 : new BucketWebsiteConfiguration(suffix));
    }

    /**
     * Answers the website of the given bucket. This operation requires the
     * GetBucketWebsite permission. By default, only the bucket owner can read the
     * bucket website configuration. However, bucket owners can allow other users to
     * read the website configuration by writing a bucket policy granting them the
     * GetBucketWebsite permission. Example: 
     * {@code <s3:get-bucket-website-configuration bucketName="my-bucket" />}
     * 
     * @param bucketName
     * @return a non null com.amazonaws.services.s3.model.BucketWebsiteConfiguration
     */
    @Operation
    public BucketWebsiteConfiguration getBucketWebsiteConfiguration(@Parameter(optional = false) String bucketName)
    {
        return client.getBucketWebsiteConfiguration(bucketName);
    }

    /**
     * Answers a list of all Amazon S3 buckets that the authenticated sender of the
     * request owns. Users must authenticate with a valid AWS Access Key ID that is
     * registered with Amazon S3. Anonymous requests cannot list buckets, and users
     * cannot list buckets that they did not create. Example {@code <s3:list-buckets />}
     * 
     * @return a non null list of com.amazonaws.services.s3.model.Bucket
     */
    @Operation
    public List<Bucket> listBuckets()
    {
        return client.listBuckets();
    }

    /**
     * Lazily lists all objects for a given prefix. As S3 does not limit in any way
     * the number of objects, such listing can retrieve an arbitrary amount of
     * objects, and may need to perform extra calls to the api while it is iterated.
     * Example: {@code <s3:list-objects bucketName="my-bucket" prefix="mk" />}
     * 
     * @param bucketName the target bucket's name
     * @param prefix the prefix of the objects to be listed. If unspecified, all
     *            objects are listed
     * @return An iterable
     */
    @Operation
    public Iterable<S3ObjectSummary> listObjects(@Parameter(optional = false) String bucketName,
                                                 @Parameter(optional = true) String prefix)
    {
        return client.listObjects(bucketName, prefix);
    }

    /**
     * Uploads an object to S3. Supported contents are InputStreams, Strings, byte
     * arrays and Files. 
     * 
     * Example: 
     * {@code <s3:create-object bucketName="my-bucket" key="helloWorld.txt" 
     *                              content="#[hello world]" contentType="text/plain" />}
     * 
     * @param bucketName the object's bucket
     * @param key the object's key
     * @param content
     * @param contentLength the content length. If content is a InputStream,
     *            this parameter should be specified, as not doing so will
     *            introduce a performance loss as the contents will have to be persisted on disk before being uploaded. 
     *            Otherwise, it is ignored. An exception to this 
     *            rule are InputStreams returned by Mule Http Connector: if stream has Content-Length 
     *            information, it will be used. 
     *            In any case a content length of 0 is interpreted as an unspecified content length
     * @param contentMd5 the content md5, encoded in base 64. If content is a file,
     *            it is ignored.
     * @param contentType the content type of the new object.
     * @param acl the access control list of the new object
     * @param storageClass the storage class of the new object
     * @param userMetadata a map of arbitrary object properties keys and values
     * @return the id of the created object, or null, if versioning is not enabled
     */
    @Operation
    public String createObject(@Parameter(optional = false) String bucketName,
                               @Parameter(optional = false) String key,
                               @Parameter(optional = false) Object content,
                               @Parameter(optional = true) Long contentLength,
                               @Parameter(optional = true) String contentMd5,
                               @Parameter(optional = true) String contentType,
                               @Parameter(optional = true, defaultValue = "PRIVATE") AccessControlList acl,
                               @Parameter(optional = true, defaultValue = "STANDARD") StorageClass storageClass,
                               @Parameter(optional = true) Map<String, String> userMetadata)
    {
        return client.createObject(new S3ObjectId(bucketName, key), S3ContentUtils.createContent(content,
            contentLength, contentMd5), contentType, acl.toS3Equivalent(), storageClass.toS3Equivalent(),
            userMetadata);
    }

    /**
     * Deletes a given object, only the owner of the bucket containing the version
     * can perform this operation. If version is specified, versioning must be
     * enabled, and once deleted, there is no method to restore such version.
     * Otherwise, once deleted, the object can only be restored if versioning was
     * enabled when the object was deleted. If attempting to delete an object that
     * does not exist, Amazon S3 will return a success message instead of an error
     * message. Example: 
     * {@code <s3:delete-object bucketName="my-bucket" key="foo.gzip"/> }
     * 
     * @param bucketName the object's bucket
     * @param key the object's key
     * @param versionId the specific version of the object to delete, if versioning
     *            is enabled. Left unspecified if the latest version is desired, or
     *            versioning is not enabled.
     */
    @Operation
    public void deleteObject(@Parameter(optional = false) String bucketName,
                             @Parameter(optional = false) String key,
                             @Parameter(optional = true) String versionId)
    {
        client.deleteObject(new S3ObjectId(bucketName, key, versionId));
    }

    /**
     * Sets the Amazon S3 storage class for the given object. Changing the storage
     * class of an object in a bucket that has enabled versioning creates a new
     * version of the object with the new storage class. The existing version of the
     * object preservers the previous storage class.
     * 
     * @param bucketName the object's bucket name
     * @param key the object's key
     * @param storageClass the storage class to set
     */
    @Operation
    public void setObjectStorageClass(@Parameter(optional = false) String bucketName,
                                      @Parameter(optional = false) String key,
                                      @Parameter(optional = false) StorageClass storageClass)
    {
        Validate.notNull(storageClass);
        client.setObjectStorageClass(new S3ObjectId(bucketName, key), storageClass.toS3Equivalent());
    }

    /**
     * Copies a source object to a new destination; to copy an object, the caller's
     * account must have read access to the source object and write access to the
     * destination bucket. By default, all object metadata for the source object are
     * copied to the new destination object, unless new object metadata in the
     * specified is provided. The AccesControlList is not copied to the new object,
     * and, unless another ACL specified, PRIVATE is assumed. If no destination
     * bucket is specified, the same that the source bucket is used - local copy.
     * Example: 
     * {@code <s3:copy-object sourceBucketName="my-bucket" sourceKey="foo.gzip" destinationKey="bar.gzip"
     *                                  destinationStorageClass="Private" /> }
     * 
     * @param sourceBucketName the source object's bucket
     * @param sourcekey the source object's key
     * @param sourceVersionId the specific version of the source object to copy, if
     *            versioning is enabled. Left unspecified if the latest version is
     *            desired, or versioning is not enabled.
     * @param destinationBucketName the destination object's bucket. If none
     *            provided, a local copy is performed, that is, it is copied within
     *            the same bucket.
     * @param destinationKey the destination object's key
     * @param destinationAcl the acl of the destination object.
     * @param destinationStorageClass
     * @param destinationUserMetadata the new metadata of the destination object,
     *            that if specified, overrides that copied from the source object
     * @param modifiedSince The modified constraint that restricts this request to
     *            executing only if the object has been modified after the specified
     *            date. This constraint is specified but does not match, no copy is performed
     * @param unmodifiedSince The unmodified constraint that restricts this request
     *            to executing only if the object has not been modified after this
     *            date. This constraint is specified but does not match, no copy is performed
     * @return the version id of the new object, or null, if versioning is not
     *         enabled
     */
    @Operation
    public String copyObject(@Parameter(optional = false) String sourceBucketName,
                             @Parameter(optional = false) String sourceKey,
                             @Parameter(optional = true) String sourceVersionId,
                             @Parameter(optional = true) String destinationBucketName,
                             @Parameter(optional = false) String destinationKey,
                             @Parameter(optional = true, defaultValue = "PRIVATE") AccessControlList destinationAcl,
                             @Parameter(optional = true, defaultValue = "STANDARD") StorageClass destinationStorageClass,
                             @Parameter(optional = true) Map<String, String> destinationUserMetadata,  
                             @Parameter(optional = true) Date modifiedSince, 
                             @Parameter(optional = true) Date unmodifiedSince)
    {
        return client.copyObject(
            new S3ObjectId(sourceBucketName, sourceKey, sourceVersionId),
            new S3ObjectId(coalesce(destinationBucketName, sourceBucketName), destinationKey),
            ConditionalConstraints.from(modifiedSince, unmodifiedSince), 
            destinationAcl.toS3Equivalent(),
            destinationStorageClass.toS3Equivalent(), 
            destinationUserMetadata);
    }

    /**
     * Returns a pre-signed URL for accessing an Amazon S3 object. The pre-signed URL
     * can be shared to other users, allowing access to the resource without
     * providing an account's AWS security credentials. Example: {@code
     * <s3:create-presigned-uri bucketName="my-bucket" key="bar.xml" method="GET" />}
     * 
     * @param bucketName the object's bucket
     * @param key the object's key
     * @param versionId the specific version of the object to create the URI, if
     *            versioning is enabled. Left unspecified if the latest version is
     *            desired, or versioning is not enabled.
     * @param expiration The time at which the returned pre-signed URL will expire.
     * @param method The HTTP method verb to use for this URL
     * @return A non null pre-signed URI that can be used to access an Amazon S3
     *         resource without requiring the user of the URL to know the account's
     *         AWS security credentials.
     */
    @Operation
    public URI createObjectPresignedUri(@Parameter(optional = false) String bucketName,
                                        @Parameter(optional = false) String key,
                                        @Parameter(optional = true) String versionId,
                                        @Parameter(optional = true) Date expiration,
                                        @Parameter(optional = true, defaultValue = "PUT") String method)
    {
        return client.createObjectPresignedUri(new S3ObjectId(bucketName, key, versionId), expiration,
            toHttpMethod(method));
    }

    private HttpMethod toHttpMethod(String method)
    {
        return method != null ? HttpMethod.valueOf(method) : null;
    }

    /**
     * Gets the content of an object stored in Amazon S3 under the specified bucket
     * and key. Returns null if the specified constraints weren't met. To get an
     * object's content from Amazon S3, the caller must have {@link Permission#Read}
     * access to the object. Regarding conditional get constraints, Amazon S3 will
     * ignore any dates occurring in the future.
     * 
     * @param bucketName the object's bucket
     * @param key the object's key
     * @param versionId the specific version of the object to get its contents, if
     *            versioning is enabled, left unspecified if the latest version is
     *            desired, or versioning is not enabled.
     * @param modifiedSince The modified constraint that restricts this request to
     *            executing only if the object has been modified after the specified
     *            date.
     * @param unmodifiedSince The unmodified constraint that restricts this request
     *            to executing only if the object has not been modified after this
     *            date.
     * @return an input stream to the objects contents
     */
    @Operation
    public InputStream getObjectContent(@Parameter(optional = false) String bucketName,
                                        @Parameter(optional = false) String key,
                                        @Parameter(optional = true) String versionId,
                                        @Parameter(optional = true) Date modifiedSince,
                                        @Parameter(optional = true) Date unmodifiedSince)
    {
        return client.getObjectContent(new S3ObjectId(bucketName, key, versionId), 
            ConditionalConstraints.from(modifiedSince, unmodifiedSince));
    }

    /**
     * Gets the object stored in Amazon S3 under the specified bucket and key.
     * Returns null if the specified constraints weren't met. To get an object from
     * Amazon S3, the caller must have {@link Permission#Read} access to the object.
     * Callers should be very careful when using this method; the returned Amazon S3
     * object contains a direct stream of data from the HTTP connection. The
     * underlying HTTP connection cannot be closed until the user finishes reading
     * the data and closes the stream. Regarding conditional get constraints, Amazon
     * S3 will ignore any dates occurring in the future.
     * 
     * @param bucketName the object's bucket
     * @param key the object's key
     * @param versionId the specific version of the object to get its contents, if
     *            versioning is enabled. Left unspecified if the latest version is
     *            desired, or versioning is not enabled.
     * @param modifiedSince The modified constraint that restricts this request to
     *            executing only if the object has been modified after the specified
     *            date.
     * @param unmodifiedSince The unmodified constraint that restricts this request
     *            to executing only if the object has not been modified after this
     *            date.
     * @return the S3Object, or null, if conditional get constraints did not match
     */
    @Operation
    public S3Object getObject(@Parameter(optional = false) String bucketName,
                              @Parameter(optional = false) String key,
                              @Parameter(optional = true) String versionId,
                              @Parameter(optional = true) Date modifiedSince,
                              @Parameter(optional = true) Date unmodifiedSince)
    {
        return client.getObject(new S3ObjectId(bucketName, key, versionId), 
            ConditionalConstraints.from(modifiedSince, unmodifiedSince));
    }

    /**
     * Gets the metadata for the specified Amazon S3 object without actually fetching
     * the object itself. This is useful in obtaining only the object metadata, and
     * avoids wasting bandwidth on fetching the object data. Example: {@code
     * <s3:get-object-metadata bucketName="my-bucket" key="baz.bin" />}
     * 
     * @param bucketName the object's bucket
     * @param key the object's key
     * @param versionId the object metadata for the given bucketName and key
     * @return the non null object metadata
     */
    @Operation
    public ObjectMetadata getObjectMetadata(@Parameter(optional = false) String bucketName,
                                            @Parameter(optional = false) String key,
                                            @Parameter(optional = true) String versionId)
    {
        return client.getObjectMetadata(new S3ObjectId(bucketName, key, versionId));
    }

    /**
     * Sets the versioning status for the given bucket. A bucket's versioning
     * configuration can be in one of three possible states: Off, Enabled and
     * Suspended. By default, new buckets are in the Off state. Once versioning is
     * enabled for a bucket the status can never be reverted to Off. Example: {@code
     * <s3:set-bucket-versioning-status bucketName="my-bucket" versioningStatus="Suspended" />}
     * 
     * @param bucketName the target bucket name
     * @param versioningStatus the version status to set
     */
    @Operation
    public void setBucketVersioningStatus(@Parameter(optional = false) String bucketName,
                                          @Parameter(optional = false) VersioningStatus versioningStatus)
    {
        client.setBucketVersioningStatus(bucketName, versioningStatus);
    }

    /**
     * Creates an http URI for the given object id. The useDefaultServer option
     * enables using default US Amazon server subdomain in the URI regardless of the
     * region. The main benefit of such feature is that this operation does not need
     * to hit the Amazon servers, but the drawback is that using the given URI as an
     * URL to the resource have unnecessary latency penalties for standard regions
     * other than US_STANDARD.
     * 
     * @param bucketName
     * @param key
     * @param useDefaultServer if the default US Amazon server subdomain should be
     *            used in the URI regardless of the region.
     * @return a non secure http URI to the object. Unlike the presigned URI, object
     *         must have PUBLIC_READ or PUBLIC_READ_WRITE permission
     */
    @Operation
    public URI createObjectUri(@Parameter(optional = false) String bucketName,
                               @Parameter(optional = false) String key,
                               @Parameter(optional = true, defaultValue = "false") boolean useDefaultServer)
    {
        if (useDefaultServer)
        {
            return client.createObjectUriUsingDefaultServer(new S3ObjectId(bucketName, key));
        }
        else
        {
            return client.createObjectUri(new S3ObjectId(bucketName, key));
        }
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
    private AmazonS3  createAmazonS3()
    {
        ClientConfiguration clientConfig = new ClientConfiguration();
        if (proxyUsername != null)
        {
            clientConfig.setProxyUsername(proxyUsername);
        }
        if (proxyPort != null)
        {
            clientConfig.setProxyPort(proxyPort);
        }
        if (proxyPassword != null)
        {
            clientConfig.setProxyPassword(proxyPassword);
        }
        if (proxyHost != null)
        {
            clientConfig.setProxyHost(proxyHost);
        }
        return new AmazonS3Client(createCredentials(), 
            clientConfig);
    }

    private AWSCredentials createCredentials()
    {
        if (StringUtils.isEmpty(accessKey) && StringUtils.isEmpty(secretKey))
        {
            return null;
        }
        return new BasicAWSCredentials(accessKey, secretKey);
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

    public String getProxyUsername()
    {
        return proxyUsername;
    }

    public void setProxyUsername(String proxyUsername)
    {
        this.proxyUsername = proxyUsername;
    }

    public int getProxyPort()
    {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort)
    {
        this.proxyPort = proxyPort;
    }

    public String getProxyPassword()
    {
        return proxyPassword;
    }

    public void setProxyPassword(String proxyPassword)
    {
        this.proxyPassword = proxyPassword;
    }

    public String getProxyHost()
    {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost)
    {
        this.proxyHost = proxyHost;
    }

}
