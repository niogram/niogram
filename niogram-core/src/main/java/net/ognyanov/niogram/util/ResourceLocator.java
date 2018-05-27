package net.ognyanov.niogram.util;
/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */

import java.io.InputStream;
import java.util.List;

/**
 * An interface for resource locators.
 *
 * @author Nikolay Ognyanov
 */
public interface ResourceLocator
{
    /**
     * Find resources by name pattern.
     * Optional operation. Locators which do not support it will
     * throw an UnsupportedOperationException.
     * 
     * @param pattern a regular expression for the name
     *        of resources to be retrieved.
     * @return a list of resource paths
     */
    public List<String> findResources(String pattern);

    /**
     * Locate resource by name and return it as an input stream.
     * 
     * @param resourceName the name of the resource to be located.
     * @return an input stream or null if the resource is not found.
     */
    public InputStream getResourceAsStream(String resourceName);
}
