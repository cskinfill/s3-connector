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

Example: 

     <s3:create-bucket bucketName="my-bucket" acl="Private"/> 

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName| . The bucket to create|no||
|region| optional|yes|US_Standard|
|acl| optional|yes|Private|

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
|bucketName||no||
|prefix| the prefix of the objects to be listed. If absent, all objects
           are listed|yes||

Create Object
-------------

Uploads an object to S3. Supported contents are InputStreams, Strings, byte
arrays and Files. Example: 

     <s3:create-object bucketName="my-bucket"
    key="helloWorld.txt" content="#[hello world]" contentType="text/plain" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName||no||
|key||no||
|content||no||
|contentLength| the content length. If content is a InputStream or byte
           arrays, this parameter should be passed, as not doing so will
           introduce a severe performance loss, otherwise, it is ignored. A
           content length of 0 is interpreted as an absent (null) content
           length|yes||
|contentMd5| the content md5, encoded in base 64. If content is a file,
           it is ignored.|yes||
|contentType||yes||
|acl||yes|Private|
|storageClass||yes|Standard|
|userMetadata| TODO|yes||

Delete Object
-------------

Example: 

     <s3:delete-object bucketName="my-bucket" key="foo.gzip"/> 

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName||no||
|key||no||
|versionId||yes||

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
|sourceBucketName||no||
|sourceKey||no||
|sourceVersionId||yes||
|destinationBucketName| the destination object's bucket. If none
           provided, a local copy is performed, that is, it is copied within
           the same bucket.|yes||
|destinationKey||no||
|destinationAcl| the acl of the destination object.|yes|Private|
|destinationStorageClass||yes|Standard|

Create Presigned Uri
--------------------

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName||no||
|key||no||
|versionId||yes||
|expiration||yes||
|method||yes|PUT|

Get Object Content
------------------

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName||no||
|key||no||
|versionId||yes||
|modifiedSince||yes||
|unmodifiedSince||yes||

Get Object Metadata
-------------------

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName||no||
|key||no||
|versionId||yes||

Get Object
----------

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName||no||
|key||no||
|versionId||yes||

Set Bucket Versioning Status
----------------------------

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName||no||
|versioningStatus||no||



















