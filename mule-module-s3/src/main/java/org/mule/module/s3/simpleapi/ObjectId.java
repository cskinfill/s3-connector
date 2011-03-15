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

import org.apache.commons.lang.Validate;

public class ObjectId
{
    private String bucketName;
    private String key;

    public ObjectId(String bucketName, String key)
    {
        Validate.notEmpty(bucketName);
        Validate.notEmpty(key);
        this.bucketName = bucketName;
        this.key = key;
    }

    public String getBucketName()
    {
        return bucketName;
    }

    public String getKey()
    {
        return key;
    }
}

