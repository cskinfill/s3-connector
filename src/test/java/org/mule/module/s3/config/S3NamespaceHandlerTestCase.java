/**
 * Mule S3 Cloud Connector
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.s3.config;

import org.mule.api.MuleMessage;
import org.mule.construct.SimpleFlowConstruct;
import org.mule.tck.FunctionalTestCase;

import com.amazonaws.services.s3.model.ObjectMetadata;

import java.util.HashMap;

public class S3NamespaceHandlerTestCase extends FunctionalTestCase
{

    @Override
    protected String getConfigResources()
    {
        return "s3-namespace-config.xml";
    }

    @Override
    protected void doSetUp() throws Exception
    {
        getConfigResources();
    }

    public void testGetBucketMetadata() throws Exception
    {
        SimpleFlowConstruct flow = lookupFlowConstruct("GetObjectMetadata");
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("bucketName", "codinghorrorimg");
        properties.put("key", "codinghorror-bandwidth-usage.png");
        MuleMessage message = flow.process(getTestEvent(properties)).getMessage();
        assertTrue(message.getPayload() instanceof ObjectMetadata);
    }

    private SimpleFlowConstruct lookupFlowConstruct(String name)
    {
        return (SimpleFlowConstruct) muleContext.getRegistry().lookupFlowConstruct(name);
    }

}
