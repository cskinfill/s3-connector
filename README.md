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
the following under the <dependencies> element in the pom.xml file of the
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

Creates a new bucket; connector must not be configured as annonyomus for this
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
application. Create or delete buckets in a separate initialization or setup
Example: 

     <s3:create-bucket bucketName="my-bucket" acl="Private"/> 

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName| The bucket to create|no||
|region| the region where to create the new bucket|yes|US_Standard|
|acl| the acces control list of the new bucket|yes|Private|

Delete Bucket
-------------

Example: 

     <s3:delete-bucket bucketName="my-bucket" force="true"/> 

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName| the bucket to delete|no||
|force| optional true if the bucket must be deleted even if it is not
           empty, false if operation should fail in such scenario.|yes|false|

Delete Bucket Website Configuration
-----------------------------------

Example: 

     <s3:delete-bucket-website-configuration
    bucketName="my-bucket"/>

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName| the bucket whose policy to delete|no||

Get Bucket Policy
-----------------

Example: 

     <s3:get-bucket-policy bucketName="my-bucket"/>

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName| the bucket whose policy to retrieve|no||

Set Bucket Policy
-----------------

Example: 

     <s3:set-bucket-policy bucketName="my-bucket"
    policyText="your policy" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName| the bucket name|no||
|policyText| the policy text|no||

Delete Bucket Policy
--------------------

Example: 

     <s3:delete-bucket-policy bucketName="my-bucket"/>

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName| the bucket whose policy to delete|no||

Set Bucket Website Configuration
--------------------------------

Example: 

     <s3:set-bucket-website-configuration bucketName="my-bucket"
    suffix="index.html" errorDocument="errorDocument.html" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName||no||
|suffix| The document to serve when a directory is specified (ex:
           index.html). This path is relative to the requested resource|no||
|errorDocument| the full path to error document the bucket will use as
           error page for 4XX errors|yes||

Get Bucket Website Configuration
--------------------------------

Example: 

     <s3:get-bucket-website-configuration bucketName="my-bucket"
    />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName||no||

List Buckets
------------

Example 

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
|bucketName| the target bucket's name|no||
|prefix| the prefix of the objects to be listed. If unspecified, all
           objects are listed|yes||

Create Object
-------------

Uploads an object to S3. Supported contents are InputStreams, Strings, byte
arrays and Files. Example: 

     <s3:create-object bucketName="my-bucket"
    key="helloWorld.txt" content="#[hello world]" contentType="text/plain" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName| the object's bucket|no||
|key| the object's key|no||
|content||no||
|contentLength| the content length. If content is a InputStream or byte
           arrays, this parameter should be specified, as not doing so will
           introduce a severe performance loss, otherwise, it is ignored. A
           content length of 0 is interpreted as an unspecified content length|yes||
|contentMd5| the content md5, encoded in base 64. If content is a file,
           it is ignored.|yes||
|contentType| the content type of the new object.|yes||
|acl| the access control list of the new object|yes|Private|
|storageClass| the storaga class of the new object|yes|Standard|
|userMetadata| a map of arbitrary object properties keys and values|yes||

Delete Object
-------------

Example: 

     <s3:delete-object bucketName="my-bucket" key="foo.gzip"/> 

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName| the object's bucket|no||
|key| the object's key|no||
|versionId| the specific version of the object to delete, if versioning
           is enabled. Left unspecified if the latest version is desired, or
           versioning is not enabled.|yes||

Set Object Storage Class
------------------------

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName||no||
|key||no||
|storageClass||no||

Copy Object
-----------

Example: 

     <s3:copy-object sourceBucketName="my-bucket"
    sourceKey="foo.gzip" destinationKey="bar.gzip"
    destinationStorageClass="Private" /> 

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|sourceBucketName| the source object's bucket|no||
|sourceKey||no||
|sourceVersionId| the specific version of the source object to copy, if
           versioning is enabled. Left unspecified if the latest version is
           desired, or versioning is not enabled.|yes||
|destinationBucketName| the destination object's bucket. If none
           provided, a local copy is performed, that is, it is copied within
           the same bucket.|yes||
|destinationKey| the destination object's key|no||
|destinationAcl| the acl of the destination object.|yes|Private|
|destinationStorageClass||yes|Standard|

Create Presigned Uri
--------------------

Returns a pre-signed URL for accessing an Amazon S3 object. The pre-signed URL
can be shared to other users, allowing access to the resource without
providing an account's AWS security credentials. Example: 

    
    <s3:create-presigned-uri bucketName="my-bucket" key="bar.xml" method="GET" />
    * 

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName| the object's bucket|no||
|key| the object's key|no||
|versionId| the specific version of the object to create the URI, if
           versioning is enabled. Left unspecified if the latest version is
           desired, or versioning is not enabled.|yes||
|expiration| The time at which the returned pre-signed URL will expire.|yes||
|method| The HTTP method verb to use for this URL|yes|PUT|

Get Object Content
------------------

Gets the content of an object stored in Amazon S3 under the specified bucket
and key. Returns null if the specified constraints weren't met. To get an
object's content from Amazon S3, the caller must have {@link Permission#Read}
access to the object.

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName| the object's bucket|no||
|key| the object's key|no||
|versionId| the specific version of the object to get its contents, if
           versioning is enabled. Left unspecified if the latest version is
           desired, or versioning is not enabled.|yes||
|modifiedSince| The modified constraint that restricts this request to
           executing only if the object has been modified after the specified
           date. Amazon S3 will ignore any dates occurring in the future.|yes||
|unmodifiedSince| The unmodified constraint that restricts this request
           to executing only if the object has not been modified after this
           date. Amazon S3 will ignore any dates occurring in the future.|yes||

Get Object
----------

Gets the object stored in Amazon S3 under the specified bucket and key.
Returns null if the specified constraints weren't met. To get an object from
Amazon S3, the caller must have {@link Permission#Read} access to the object.
Callers should be very careful when using this method; the returned Amazon S3
object contains a direct stream of data from the HTTP connection. The
underlying HTTP connection cannot be closed until the user finishes reading
the data and closes the stream. * @param bucketName the object's bucket

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName||no||
|key| the object's key|no||
|versionId| the specific version of the object to get its contents, if
           versioning is enabled. Left unspecified if the latest version is
           desired, or versioning is not enabled.|yes||
|modifiedSince| The modified constraint that restricts this request to
           executing only if the object has been modified after the specified
           date. Amazon S3 will ignore any dates occurring in the future.|yes||
|unmodifiedSince| The unmodified constraint that restricts this request
           to executing only if the object has not been modified after this
           date. Amazon S3 will ignore any dates occurring in the future.|yes||

Get Object Metadata
-------------------

Gets the metadata for the specified Amazon S3 object without actually fetching
the object itself. This is useful in obtaining only the object metadata, and
avoids wasting bandwidth on fetching the object data. Example: 

    
    <s3:get-object-metadata bucketName="my-bucket" key="baz.bin" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName| the object's bucket|no||
|key| the object's key|no||
|versionId| the object metadata for the given bucketName and key|yes||

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
|bucketName| the target bucket name|no||
|versioningStatus| the version status to set|no||*Off*, *Enabled*, *Suspended*



















