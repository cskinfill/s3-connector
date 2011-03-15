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

    public void testSendMessageToFlow() throws Exception
    {
        /*
            This test case tests your Mule integration.
            
            To test your flow directly (i.e. without any inbound endpoints, declare a flow in
            s3-namespace-config.xml and put the element from your
            cloud connector's namespace that you want to test into it.
            A proper example was put into s3-namespace-config.xml

            Now you can send data to your test flow from the unit test:

            String payload = <your input to the flow here>;
            SimpleFlowConstruct flow = lookupFlowConstruct("theFlow");
            MuleEvent event = getTestEvent(payload);
            MuleEvent responseEvent = flow.process(event);
            assertEquals(<expected test output>, responseEvent.getMessage().getPayloadAsString());
        */
    }

    private SimpleFlowConstruct lookupFlowConstruct(String name)
    {
        return (SimpleFlowConstruct) muleContext.getRegistry().lookupFlowConstruct(name);
    }
}