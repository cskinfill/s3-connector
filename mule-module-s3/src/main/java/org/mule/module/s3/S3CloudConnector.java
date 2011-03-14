
package org.mule.module.s3;

import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
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
import com.amazonaws.services.s3.model.StorageClass;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

@Connector(namespacePrefix = "s3", namespaceUri = "http://www.mulesoft.org/schema/mule/s3")
public class S3CloudConnector implements Initialisable
{

    @Property
    private String accessKey;
    @Property
    private String secretKey;

    private SimpleAmazonS3 client;

    public Bucket createBucket(String bucketName, String region, String acl)
        throws AmazonClientException, AmazonServiceException
    {
        return client.createBucket(bucketName, region, toAcl(acl));
    }

    private CannedAccessControlList toAcl(String acl)
    {
        return CannedAccessControlList.valueOf(acl);
    }

    @Operation
    public void deleteBucketAndObjects(String bucketName,
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
    public void deleteBucketPolicy(String bucketName) throws AmazonClientException, AmazonServiceException
    {
        client.deleteBucketPolicy(bucketName);
    }

    @Operation
    public void deleteBucketWebsiteConfiguration(String bucketName)
        throws AmazonClientException, AmazonServiceException
    {
        client.deleteBucketWebsiteConfiguration(bucketName);
    }

    @Operation
    public BucketPolicy getBucketPolicy(String bucketName)
        throws AmazonClientException, AmazonServiceException
    {
        return client.getBucketPolicy(bucketName);
    }

    @Operation
    public BucketWebsiteConfiguration getBucketWebsiteConfiguration(String bucketName)
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
    // @Operation
    // public String putObject(String bucketName, String key, Object input)
    // throws AmazonClientException, AmazonServiceException
    // {
    // Validate.notNull(input, "Input must not ne null");
    // return client.putObject(bucketName, key, createContent(input), new
    // ObjectMetadata());
    // }

    @Operation
    public void deleteObject(@Parameter(optional = false) String bucketName,
                             @Parameter(optional = false) String key,
                             @Parameter(optional = true) String versionId)
        throws AmazonClientException, AmazonServiceException
    {

        if (versionId == null)
        {
            client.deleteObject(bucketName, key);
        }
        else
        {
            client.deleteVersion(bucketName, key, versionId);
        }
    }

    @Operation
    public void changeObjectStorageClass(@Parameter(optional = false) String bucketName,
                                         @Parameter(optional = false) String key,
                                         @Parameter(optional = false) String newStorageClass)
        throws AmazonClientException, AmazonServiceException
    {
        client.changeObjectStorageClass(bucketName, key, StorageClass.fromValue(newStorageClass));
    }

//    public CopyObjectResult copyObject(CopyObjectRequest copyOptions)
//        throws AmazonClientException, AmazonServiceException
//    {
//        return client.copyObject(copyOptions);
//    }

    public void initialise() throws InitialisationException
    {
        if (client == null)
        {
            client = new SimpleAmazonS3AmazonDevKitImpl(new AmazonS3Client(new BasicAWSCredentials(accessKey,
                secretKey)));
        }
    }

    public String getUsername()
    {
        return accessKey;
    }

    public void setUsername(String username)
    {
        this.accessKey = username;
    }

    public String getPassword()
    {
        return secretKey;
    }

    public void setPassword(String password)
    {
        this.secretKey = password;
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

}
