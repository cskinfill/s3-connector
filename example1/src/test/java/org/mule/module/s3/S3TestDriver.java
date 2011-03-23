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

import org.mule.api.MuleEvent;
import org.mule.construct.SimpleFlowConstruct;
import org.mule.tck.FunctionalTestCase;
import org.mule.transport.NullPayload;

public class S3TestDriver extends FunctionalTestCase
{
    /*
     * This test needs the following System properties to be set: s3.accessKey,
     * s3.secetKey and s3.bucketName.
     */

    @Override
    protected String getConfigResources()
    {
        return "mule-config.xml";
    }

    /**
     * Run this test to upload new versions of 
     * http://www.mulesoft.com:80/images/index/front-esb.jpg 
     * to the bucket. 
     */
    public void testUpload() throws Exception
    {
        final MuleEvent event = getTestEvent("");
        final SimpleFlowConstruct flow = lookupFlowConstruct("UploadFlow");
        final MuleEvent responseEvent = flow.process(event);
        assertTrue(responseEvent.getMessage().getPayload() instanceof NullPayload);
    }

    /**
     * Run this test only once in order to create a bucket with versioning 
     * enabled
     */
    public void ignoreTestSetup() throws Exception
    {
        final MuleEvent event = getTestEvent("");
        final SimpleFlowConstruct flow = lookupFlowConstruct("SetupFlow");
        flow.process(event);
    }


    private SimpleFlowConstruct lookupFlowConstruct(final String name)
    {
        return (SimpleFlowConstruct) muleContext.getRegistry().lookupFlowConstruct(name);
    }

}
