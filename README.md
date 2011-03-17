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

     <s3:create-bucket bucketName="myBucket" acl="Private"/> 

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName| mandatory. The bucket to create|no||
|region| optional. The region were to create the bucket. Default is
           US_STANDARD|yes||
|acl| optional. TODO default ACL is not clear enough in the AmazonS3|yes||

Delete Bucket
-------------

Example: 

     <s3:delete-bucket bucketName="myBucket" force="true"/> 

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName| mandatory the bucket to delete|no||
|force| optional {@code true} if the bucket must be deleted even if it is
           not empty, {@code false} if operation should fail in such scenario.
           Default is {@code false}|yes|false|

Delete Bucket Website Configuration
-----------------------------------

Example: 

     <s3:delete-bucket-website-configuration
    bucketName="myBucket"/>

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName| mandatory the bucket whose policy to delete|no||

Get Bucket Policy
-----------------

Example: 

     <s3:get-bucket-policy bucketName="myBucket"/>

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName| mandatory the bucket whose policy to retrieve|no||

Set Bucket Policy
-----------------

Example: 

     <s3:set-bucket-policy bucketName="myBucket"
    policyText="your policy" />

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName| mandatory the bucket name|no||
|policyText| mandatory the policy text|no||

Delete Bucket Policy
--------------------

Example: 

     <s3:delete-bucket-policy bucketName="myBucket"/>

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName| mandatory the bucket whose policy to delete|no||

Set Bucket Website Configuration
--------------------------------

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName||no||
|suffix||no||
|errorPage||yes||

Get Bucket Website Configuration
--------------------------------

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName||no||

List Buckets
------------

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||

List Objects
------------

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName||no||
|prefix||no||

Create Object
-------------

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|bucketName||no||
|key||no||
|content||no||
|contentType||yes||
|acl||yes||
|storageClass||yes||

Delete Object
-------------

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
|newStorageClass||no||

Copy Object
-----------

| attribute | description | optional | default value | possible values |
|:-----------|:-----------|:---------|:--------------|:----------------|
|config-ref|Specify which configuration to use for this invocation|yes||
|sourceBucketName||no||
|sourceKey||no||
|sourceVersionId||yes||
|destinationBucketName||yes||
|destinationKey||no||
|destinationAcl||yes||
|destinationStorageClass||yes||























