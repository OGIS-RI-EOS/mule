/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.launcher;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

public abstract class AbstractFileWatcher implements Runnable
{

    private long timeStamp;
    private Collection<File> files;

    public AbstractFileWatcher(File file)
    {
        this(Arrays.asList(file));
    }

    public AbstractFileWatcher(Collection<File> files)
    {
        this.files = files;
        this.timeStamp = System.currentTimeMillis();
    }
    
    public final void run()
    {
        long lastTimeStamp = timeStamp;
        File latestFile = null;
        
        for (File file : files)
        {
            long timestamp = file.lastModified();
            if (timestamp > lastTimeStamp)
            {
                lastTimeStamp = timeStamp;
                latestFile = file;
            }
        }
        
        if (latestFile != null)
        {
            this.timeStamp = lastTimeStamp;
            onChange(latestFile);
        }
    }

    protected abstract void onChange(File file);
}