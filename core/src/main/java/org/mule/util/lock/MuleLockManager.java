/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.util.lock;

import org.mule.api.MuleContext;
import org.mule.api.config.MuleProperties;
import org.mule.api.context.MuleContextAware;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;

public class MuleLockManager implements LockManager, MuleContextAware, Initialisable, Disposable
{
    private LockGroup lockGroup;
    private MuleContext muleContext;

    public synchronized Lock getLock(String lockId)
    {
        return new LockAdapter(lockId,lockGroup);
    }
    
    @Override
    public void dispose()
    {
        lockGroup.dispose();
    }

    @Override
    public void initialise() throws InitialisationException
    {
        LockProvider lockProvider = muleContext.getRegistry().get(MuleProperties.OBJECT_LOCK_PROVIDER);
        lockGroup = new InstanceLockGroup(lockProvider);

    }

    @Override
    public void setMuleContext(MuleContext context)
    {
        this.muleContext = context;
    }
}
