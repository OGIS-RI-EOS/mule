/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.routing.outbound;

import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.message.ExceptionMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class MulticastRouterTestCase extends FunctionalTestCase
{
    private static AtomicInteger errorCounter = new AtomicInteger(0);
    
    
    @Override
    protected String getConfigResources()
    {
        return "org/mule/test/integration/routing/outbound/multicasting-router-config.xml";
    }

    @Test
    public void testAll() throws Exception
    {
        ByteArrayInputStream bis = new ByteArrayInputStream("Hello, world".getBytes("UTF-8"));
        MuleClient client = new MuleClient(muleContext);
        client.dispatch("vm://inbound1", bis, null);
        MuleMessage response = client.request("vm://output1", 2000);
        assertNull(response);
        MuleMessage error = client.request("vm://errors", 2000);
        assertNotNull(error);
        Object payload = error.getPayload();
        assertNotNull(payload);
        assertTrue(payload instanceof ExceptionMessage);
    }

    @Test
    public void testFirstSuccessful() throws Exception
    {
        ByteArrayInputStream bis = new ByteArrayInputStream("Hello, world".getBytes("UTF-8"));
        MuleClient client = new MuleClient(muleContext);
        MuleMessage response = client.send("vm://inbound2", bis, null);
        assertNotNull(response);
        Object payload = response.getPayload();
        assertNotNull(payload);
        assertEquals("Hello, world", response.getPayload());
        assertEquals(errorCounter.get(), 2);
        MuleMessage error = client.request("vm://errors2", 2000);
        assertNull(error);
    }

    public static class Fail implements Callable
    {
        @Override
        public Object onCall(MuleEventContext eventContext) throws Exception
        {
            errorCounter.incrementAndGet();
            eventContext.getMessage().setPayload("Exception was thrown");
            throw new Exception();
        }
    }

    public static class Echo
    {
        public  String process(Object message) throws Exception
        {
            if (message instanceof String)
            {
                return (String) message;
            }
            else if (message instanceof byte[])
            {
                return new String((byte[])message);
            }
            else if (message instanceof InputStream)
            {
                return IOUtils.toString((InputStream) message);
            }
            else
            {
                return message.toString();
            }
        }
    }
}
