
package org.mule.module.s3.config;

import org.mule.api.MuleEvent;
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
