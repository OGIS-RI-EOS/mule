/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.servlet.jetty;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.transport.MessageReceiver;
import org.mule.transport.http.HttpConnector;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.util.ajax.Continuation;
import org.mortbay.util.ajax.ContinuationSupport;

public class JettyContinuationsReceiverServlet extends JettyReceiverServlet
{
    private Object mutex = new Object();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            final Continuation continuation = ContinuationSupport.getContinuation(request, mutex);
            synchronized (mutex)
            {
                MessageReceiver receiver = getReceiverForURI(request);

                MuleMessage requestMessage = receiver.createMuleMessage(request);
                requestMessage.setOutboundProperty(HttpConnector.HTTP_METHOD_PROPERTY, request.getMethod());

                //This will allow Mule to continue the response once the service has do its processing
                requestMessage.setReplyTo(continuation);
                setupRequestMessage(request, requestMessage, receiver);

                //we force asynchronous in the {@link #routeMessage} method
                routeMessage(receiver, requestMessage, request);

                continuation.suspend(10000);
            }

            writeResponse(response, (MuleMessage) continuation.getObject());
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
    }

    @Override
    protected MuleEvent routeMessage(MessageReceiver receiver, MuleMessage requestMessage, HttpServletRequest request)
            throws MuleException
    {
        //Force asynchronous processing since we are using continuations
        return receiver.routeMessage(requestMessage);
    }
}
