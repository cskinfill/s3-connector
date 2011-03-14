
package org.mule.module.s3;

import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.tools.cloudconnect.annotations.Connector;
import org.mule.tools.cloudconnect.annotations.Operation;
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
    public void deleteBucketAndObjects(String bucketName, boolean force)
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
    public ObjectListing listObjects(String bucketName, String prefix)
        throws AmazonClientException, AmazonServiceException
    {
        return client.listObjects(bucketName, prefix);
    }

    @Operation
    public void setBucketPolicy(String bucketName, String policyText)
        throws AmazonClientException, AmazonServiceException
    {
        client.setBucketPolicy(bucketName, policyText);
    }

    @Operation
    public void setBucketWebsiteConfiguration(String bucketName, String suffix, String errorPage)
        throws AmazonClientException, AmazonServiceException
    {
        client.setBucketWebsiteConfiguration(bucketName, errorPage != null ? new BucketWebsiteConfiguration(
            suffix, errorPage) : new BucketWebsiteConfiguration(suffix));
    }

    public void initialise() throws InitialisationException
    {
        if (client == null)
        {
            client = new SimpleAmazonS3Impl(new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey)));
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
}
