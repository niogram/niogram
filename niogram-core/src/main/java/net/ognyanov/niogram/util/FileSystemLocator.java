package net.ognyanov.niogram.util;
/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * A locator for resources residing on the file system path.
 *
 * @author Nikolay Ognyanov
 */
public class FileSystemLocator
    implements ResourceLocator
{
    /**
     * Unsupported by this class.
     */
    @Override
    public List<String> findResources(String pattern)
    {
        // TODO implement?
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getResourceAsStream(String resourceName)
    {
        InputStream is = null;
        try {
            is = new FileInputStream(resourceName);
        }
        catch (FileNotFoundException e) {
        }
        return is;
    }
}
