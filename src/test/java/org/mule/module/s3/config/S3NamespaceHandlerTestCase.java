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

import org.mule.construct.SimpleFlowConstruct;
import org.mule.tck.FunctionalTestCase;

public class S3NamespaceHandlerTestCase extends FunctionalTestCase
{
    @Override
    protected String getConfigResources()
    {
        return "s3-namespace-config.xml";
    }

    public void testListBuckets() throws Exception
    {
//        SimpleFlowConstruct flow = lookupFlowConstruct("ListBucketsFlow");
//        MuleEvent event = getTestEvent("foobar");
//        MuleEvent responseEvent = flow.process(event);
//        assertEquals("[]", responseEvent.getMessage().getPayloadAsString());
    }

    private SimpleFlowConstruct lookupFlowConstruct(String name)
    {
        return (SimpleFlowConstruct) muleContext.getRegistry().lookupFlowConstruct(name);
    }
}
