Mule S3 Cloud Connector
=======================

Mule Cloud connector to S3

Installation
------------

The connector can either be installed for all applications running within the Mule instance or can be setup to be used
for a single application.

*All Applications*

Download the connector from the link above and place the resulting jar file in
/lib/user directory of the Mule installation folder.

*Single Application*

To make the connector available only to single application then place it in the
lib directory of the application otherwise if using Maven to compile and deploy
your application the following can be done:

Add the connector's maven repo to your pom.xml:

    <repositories>
        <repository>
            <id>muleforge-releases</id>
            <name>MuleForge Snapshot Repository</name>
            <url>https://repository.muleforge.org/release/</url>
            <layout>default</layout>
        </repsitory>
    </repositories>

Add the connector as a dependency to your project. This can be done by adding
the following under the dependencies element in the pom.xml file of the
application:

    <dependency>
        <groupId>org.mule.modules</groupId>
        <artifactId>mule-module-s3</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

Configuration
-------------

You can configure the connector as follows:

    <s3:config accessKey="value" secretKey="value"/>

Here is detailed list of all the configuration attributes:

| attribute | description | optional | default value |
|:-----------|:-----------|:---------|:--------------|
|name|Give a name to this configuration so it can be later referenced by config-ref.|yes||
|accessKey||no|
|secretKey||no|


Create Bucket
-------------

Creates a new bucket; connector must not be configured as anonymous for this
operation to succeed. Bucket names must be unique across all of Amazon S3,
that is, among all their users. Bucket ownership is similar to the ownership
of Internet domain names. Within Amazon S3, only a single user owns each
bucket. Once a uniquely named bucket is created in Amazon S3, organize and
name the objects within the bucket in any way. Ownership of the bucket is
retained as long as the owner has an Amazon S3 account. To conform with DNS
requirements, buckets names must: not contain underscores, be between 3 and 63
characters long, not end with a dash, not contain adjacent periods, not
contain dashes next to periods and not contain uppercase characters. Do not
make bucket create or delete calls in the high availability code path of an
application. Create or delete buckets in a separate initialization or setup.
Example: 

     <s3:create-bucket bucketName="my-bucket" acl="Private"/> 

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName|The bucket to create. It must not exist yet.|no||
|region|the region where to create the new bucket|yes|US_STANDARD|*US_STANDARD*, *US_WEST*, *EU_IRELAND*, *AP_SINGAPORE*, *s3Equivalent*, *domain*
|acl|the access control list of the new bucket|yes|PRIVATE|*PRIVATE*, *PUBLIC_READ*, *PUBLIC_READ_WRITE*, *AUTHENTICATED_READ*, *LOG_DELIVERY_WRITE*, *BUCKET_OWNER_READ*, *BUCKET_OWNER_FULL_CONTROL*, *s3Equivalent*

Delete Bucket
-------------

Deletes the specified bucket. All objects (and all object versions, if
versioning was ever enabled) in the bucket must be deleted before the bucket
itself can be deleted; this restriction can be relaxed by specifying the 
attribute  force="true". Example: 


     <s3:delete-bucket bucketName="my-bucket" force="true"/> 

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName|the bucket to delete|no||
|force|optional true if the bucket must be deleted even if it is not empty, false if operation should fail in such scenario.|yes|false|

Delete Bucket Website Configuration
-----------------------------------

Removes the website configuration for a bucket; this operation requires the
DeleteBucketWebsite permission. By default, only the bucket owner can delete
the website configuration attached to a bucket. However, bucket owners can
grant other users permission to delete the website configuration by writing a
bucket policy granting them the <code>S3:DeleteBucketWebsite</code>
permission. Calling this operation on a bucket with no website configuration
does not fail, but calling this operation a bucket that does not exist does.
Example: 


     <s3:delete-bucket-website-configuration bucketName="my-bucket"/>

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName|the bucket whose policy to delete|no||

Get Bucket Policy
-----------------

Answers the policy for the given bucket. Only the owner of the bucket can
retrieve it. If no policy has been set for the bucket, then a null policy text
field will be returned. Example: 


     <s3:get-bucket-policy bucketName="my-bucket"/>

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName|the bucket whose policy to retrieve|no||

Set Bucket Policy
-----------------

Sets the bucket's policy, overriding any previously set. Only the owner of the
bucket can set a bucket policy. Bucket policies provide access control
management at the bucket level for both the bucket resource and contained
object resources. Only one policy can be specified per-bucket. Example:


     <s3:set-bucket-policy bucketName="my-bucket" policyText="your policy" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName|the bucket name|no||
|policyText|the policy text|no||

Delete Bucket Policy
--------------------

Deletes the bucket's policy. Only the owner of the bucket can delete the
bucket policy. Bucket policies provide access control management at the bucket
level for both the bucket resource and contained object resources. Example:


     <s3:delete-bucket-policy bucketName="my-bucket"/>

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName|the bucket whose policy to delete|no||

Set Bucket Website Configuration
--------------------------------

Sets the given bucket's website configuration. This operation requires the
PutBucketWebsite permission. By default, only the bucket owner can configure
the website attached to a bucket. However, bucket owners can allow other users
to set the website configuration by writing a bucket policy granting them the
S3:PutBucketWebsite permission. Example: 

    
    <s3:set-bucket-website-configuration bucketName="my-bucket" suffix="index.html" 
                                         errorDocument="errorDocument.html" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName|the target bucket's name|no||
|suffix|The document to serve when a directory is specified, relative to the requested resource|no||
|errorDocument|the full path to error document the bucket will use as error page for 4XX errors|yes||

Get Bucket Website Configuration
--------------------------------

Answers the website of the given bucket. This operation requires the
GetBucketWebsite permission. By default, only the bucket owner can read the
bucket website configuration. However, bucket owners can allow other users to
read the website configuration by writing a bucket policy granting them the
GetBucketWebsite permission. Example: 


     <s3:get-bucket-website-configuration bucketName="my-bucket" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName||no||

List Buckets
------------

Answers a list of all Amazon S3 buckets that the authenticated sender of the
request owns. Users must authenticate with a valid AWS Access Key ID that is
registered with Amazon S3. Anonymous requests cannot list buckets, and users
cannot list buckets that they did not create. Example 

     <s3:list-buckets />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||

List Objects
------------

Lazily lists all objects for a given prefix. As S3 does not limit in any way
the number of objects, such listing can retrieve an arbitrary amount of
objects, and may need to perform extra calls to the api while it is iterated.
Example: 

     <s3:list-objects bucketName="my-bucket" prefix="mk" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName|the target bucket's name|no||
|prefix|the prefix of the objects to be listed. If unspecified, all objects are listed|yes||

Create Object
-------------

Uploads an object to S3. Supported contents are InputStreams, Strings, byte
arrays and Files. Example: 


     <s3:create-object bucketName="my-bucket" key="helloWorld.txt" 
                                 content="#[hello world]" contentType="text/plain" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName|the object's bucket|no||
|key|the object's key|no||
|content||no||
|contentLength|the content length. If content is a InputStream or byte arrays, this parameter should be specified, as not doing so will introduce a severe performance loss, otherwise, it is ignored. A content length of 0 is interpreted as an unspecified content length|yes||
|contentMd5|the content md5, encoded in base 64. If content is a file, it is ignored.|yes||
|contentType|the content type of the new object.|yes||
|acl|the access control list of the new object|yes|PRIVATE|*PRIVATE*, *PUBLIC_READ*, *PUBLIC_READ_WRITE*, *AUTHENTICATED_READ*, *LOG_DELIVERY_WRITE*, *BUCKET_OWNER_READ*, *BUCKET_OWNER_FULL_CONTROL*, *s3Equivalent*
|storageClass|the storaga class of the new object|yes|STANDARD|*STANDARD*, *REDUCED_REDUNDANCY*, *s3Equivalent*
|userMetadata|a map of arbitrary object properties keys and values|yes||

Delete Object
-------------

Deletes a given object, only the owner of the bucket containing the version
can perform this operation. If version is specified, versioning must be
enabled, and once deleted, there is no method to restore such version.
Otherwise, once deleted, the object can only be restored if versioning was
enabled when the object was deleted. If attempting to delete an object that
does not exist, Amazon S3 will return a success message instead of an error
message. Example: 


     <s3:delete-object bucketName="my-bucket" key="foo.gzip"/> 

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName|the object's bucket|no||
|key|the object's key|no||
|versionId|the specific version of the object to delete, if versioning is enabled. Left unspecified if the latest version is desired, or versioning is not enabled.|yes||

Set Object Storage Class
------------------------

Sets the Amazon S3 storage class for the given object. Changing the storage
class of an object in a bucket that has enabled versioning creates a new
version of the object with the new storage class. The existing version of the
object preservers the previous storage class.

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName|the object's bucket name|no||
|key|the object's key|no||
|storageClass|the storage class to set|no||*STANDARD*, *REDUCED_REDUNDANCY*, *s3Equivalent*

Copy Object
-----------

Copies a source object to a new destination; to copy an object, the caller's
account must have read access to the source object and write access to the
destination bucket. By default, all object metadata for the source object are
copied to the new destination object, unless new object metadata in the
specified is provided. The AccesControlList is not copied to the new object,
and, unless another ACL specified, PRIVATE is assumed. If no destination
bucket is specified, the same that the source bucket is used - local copy.
Example: 


     <s3:copy-object sourceBucketName="my-bucket" sourceKey="foo.gzip" destinationKey="bar.gzip"
                                     destinationStorageClass="Private" /> 

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|sourceBucketName|the source object's bucket|no||
|sourceKey||no||
|sourceVersionId|the specific version of the source object to copy, if versioning is enabled. Left unspecified if the latest version is desired, or versioning is not enabled.|yes||
|destinationBucketName|the destination object's bucket. If none provided, a local copy is performed, that is, it is copied within the same bucket.|yes||
|destinationKey|the destination object's key|no||
|destinationAcl|the acl of the destination object.|yes|PRIVATE|*PRIVATE*, *PUBLIC_READ*, *PUBLIC_READ_WRITE*, *AUTHENTICATED_READ*, *LOG_DELIVERY_WRITE*, *BUCKET_OWNER_READ*, *BUCKET_OWNER_FULL_CONTROL*, *s3Equivalent*
|destinationStorageClass||yes|STANDARD|*STANDARD*, *REDUCED_REDUNDANCY*, *s3Equivalent*
|destinationUserMetadata||yes||

Create Object Presigned Uri
---------------------------

Returns a pre-signed URL for accessing an Amazon S3 object. The pre-signed URL
can be shared to other users, allowing access to the resource without
providing an account's AWS security credentials. Example: 

    
    <s3:create-presigned-uri bucketName="my-bucket" key="bar.xml" method="GET" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName|the object's bucket|no||
|key|the object's key|no||
|versionId|the specific version of the object to create the URI, if versioning is enabled. Left unspecified if the latest version is desired, or versioning is not enabled.|yes||
|expiration|The time at which the returned pre-signed URL will expire.|yes||
|method|The HTTP method verb to use for this URL|yes|PUT|

Get Object Content
------------------

Gets the content of an object stored in Amazon S3 under the specified bucket
and key. Returns null if the specified constraints weren't met. To get an
object's content from Amazon S3, the caller must have {@link Permission#Read}
access to the object. Regarding conditional get constraints, Amazon S3 will
ignore any dates occurring in the future.

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName|the object's bucket|no||
|key|the object's key|no||
|versionId|the specific version of the object to get its contents, if versioning is enabled, left unspecified if the latest version is desired, or versioning is not enabled.|yes||
|modifiedSince|The modified constraint that restricts this request to executing only if the object has been modified after the specified date.|yes||
|unmodifiedSince|The unmodified constraint that restricts this request to executing only if the object has not been modified after this date.|yes||

Get Object
----------

Gets the object stored in Amazon S3 under the specified bucket and key.
Returns null if the specified constraints weren't met. To get an object from
Amazon S3, the caller must have {@link Permission#Read} access to the object.
Callers should be very careful when using this method; the returned Amazon S3
object contains a direct stream of data from the HTTP connection. The
underlying HTTP connection cannot be closed until the user finishes reading
the data and closes the stream. Regarding conditional get constraints, Amazon
S3 will ignore any dates occurring in the future.

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName|the object's bucket|no||
|key|the object's key|no||
|versionId|the specific version of the object to get its contents, if versioning is enabled. Left unspecified if the latest version is desired, or versioning is not enabled.|yes||
|modifiedSince|The modified constraint that restricts this request to executing only if the object has been modified after the specified date.|yes||
|unmodifiedSince|The unmodified constraint that restricts this request to executing only if the object has not been modified after this date.|yes||

Get Object Metadata
-------------------

Gets the metadata for the specified Amazon S3 object without actually fetching
the object itself. This is useful in obtaining only the object metadata, and
avoids wasting bandwidth on fetching the object data. Example: 

    
    <s3:get-object-metadata bucketName="my-bucket" key="baz.bin" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName|the object's bucket|no||
|key|the object's key|no||
|versionId|the object metadata for the given bucketName and key|yes||

Set Bucket Versioning Status
----------------------------

Sets the versioning status for the given bucket. A bucket's versioning
configuration can be in one of three possible states: Off, Enabled and
Suspended. By default, new buckets are in the Off state. Once versioning is
enabled for a bucket the status can never be reverted to Off. Example: 

    
    <s3:set-bucket-versioning-status bucketName="my-bucket"
    versioningStatus="Suspended" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName|the target bucket name|no||
|versioningStatus|the version status to set|no||*OFF*, *ENABLED*, *SUSPENDED*, *versioningStatusString*

Create Object Uri
-----------------

Creates an http URI for the given object id. The useDefaultServer option
enables using default US Amazon server subdomain in the URI regardless of the
region. The main benefit of such feature is that this operation does not need
to hit the Amazon servers, but the drawback is that using the given URI as an
URL to the resource have unnecessary latency penalties for standard regions
other than US_STANDARD.

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName||no||
|key||no||
|useDefaultServer|if the default US Amazon server subdomain should be used in the URI regardless of the region.|yes|false|



















