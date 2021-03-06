/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.http;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.cert.Certificate;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSocket;

import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
@SmallTest
public class HttpServerConnectionTestCase extends AbstractMuleTestCase
{

    public static final String ENCODING = "UTF-8";
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Socket mockSocket;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SSLSocket mockSslSocket;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HttpConnector mockHttpConnector;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HandshakeCompletedEvent mockHandshakeCompleteEvent;
    private Certificate[] mockLocalCertificate = new Certificate[2];
    private Certificate[] mockPeerCertificates = new Certificate[2];

    @Test
    public void createHttpServerConnectionWithSslSocket() throws IOException
    {
        when(mockHandshakeCompleteEvent.getLocalCertificates()).thenReturn(mockLocalCertificate);
        when(mockHandshakeCompleteEvent.getPeerCertificates()).thenReturn(mockPeerCertificates);
        doAnswer(new Answer<Void>()
        {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable
            {
                ((HandshakeCompletedListener) invocationOnMock.getArguments()[0]).handshakeCompleted(mockHandshakeCompleteEvent);
                return null;
            }
        }).when(mockSslSocket).addHandshakeCompletedListener(any(HandshakeCompletedListener.class));
        HttpServerConnection httpServerConnection = new HttpServerConnection(mockSslSocket, ENCODING, mockHttpConnector);
        verify(mockSslSocket, times(1)).addHandshakeCompletedListener(httpServerConnection);
        assertThat(httpServerConnection.getLocalCertificateChain(), is(mockLocalCertificate));
        assertThat(httpServerConnection.getPeerCertificateChain(), is(mockPeerCertificates));
    }

    @Test(expected = IllegalStateException.class)
    public void createHttpServerConnectionWithSocketAndFailForLocalCertificates() throws Exception
    {
        HttpServerConnection httpServerConnection = new HttpServerConnection(mockSocket, ENCODING, mockHttpConnector);
        httpServerConnection.getLocalCertificateChain();
    }

    @Test(expected = IllegalStateException.class)
    public void createHttpServerConnectionWithSocketAndFailForPeerCertificates() throws Exception
    {
        HttpServerConnection httpServerConnection = new HttpServerConnection(mockSocket, ENCODING, mockHttpConnector);
        httpServerConnection.getPeerCertificateChain();
    }

    @Test(expected = IllegalStateException.class)
    public void createHttpServerConnectionWithSocketAndFailForHandshakeLatch() throws Exception
    {
        HttpServerConnection httpServerConnection = new HttpServerConnection(mockSocket, ENCODING, mockHttpConnector);
        httpServerConnection.getSslSocketHandshakeCompleteLatch();
    }

    @Test
    public void resetConnectionReadNextRequest() throws Exception
    {
        when(mockSocket.getInputStream()).thenReturn(new ByteArrayInputStream(String.format("GET %s HTTP/1.1\n\nGET %s HTTP/1.1\n", "/service/order?param1=value1&param2=value2", "/?param1=value1&param2=value2").getBytes()));
        HttpServerConnection httpServerConnection = new HttpServerConnection(mockSocket, ENCODING, mockHttpConnector);
        assertThat(httpServerConnection.getUrlWithoutRequestParams(), is("/service/order"));
        httpServerConnection.reset();
        assertThat(httpServerConnection.getUrlWithoutRequestParams(), is("/"));
    }

    @Test
    public void getRemoteSocketAddressWithNullSocketAddress() throws Exception
    {
        HttpServerConnection httpServerConnection = new HttpServerConnection(mockSocket, ENCODING, mockHttpConnector);
        when(mockSocket.getRemoteSocketAddress()).thenReturn(null);
        assertThat(httpServerConnection.getRemoteClientAddress(), IsNull.nullValue());
    }

    @Test
    public void getRemoteSocketAddress() throws Exception
    {
        HttpServerConnection httpServerConnection = new HttpServerConnection(mockSocket, ENCODING, mockHttpConnector);
        when(mockSocket.getRemoteSocketAddress()).thenReturn(new InetSocketAddress("host", 1000));
        assertThat(httpServerConnection.getRemoteClientAddress(), is("host:1000"));
    }

    @Test
    public void getUrlWithoutRequestParams() throws Exception
    {
        testUrlWithoutParams("/service/order?param1=value1&param2=value2", "/service/order");
        testUrlWithoutParams("/service/order", "/service/order");
        testUrlWithoutParams("/service?param1=value1&param2=value2", "/service");
        testUrlWithoutParams("/?param1=value1&param2=value2", "/");
        testUrlWithoutParams("/", "/");
    }

    private void testUrlWithoutParams(String requestUrl, String expectedUrlWithoutParams) throws IOException
    {
        when(mockSocket.getInputStream()).thenReturn(new ByteArrayInputStream(String.format("GET %s HTTP/1.0\n", requestUrl).getBytes()));
        HttpServerConnection httpServerConnection = new HttpServerConnection(mockSocket, ENCODING, mockHttpConnector);
        String urlWithoutParams = httpServerConnection.getUrlWithoutRequestParams();
        assertThat(urlWithoutParams, is(expectedUrlWithoutParams));
    }

}


