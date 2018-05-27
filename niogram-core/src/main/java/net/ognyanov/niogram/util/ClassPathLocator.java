/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * A locator for resources residing on the claspath.
 *
 * @author Nikolay Ognyanov
 */
public class ClassPathLocator
    implements ResourceLocator
{
    private static final Object lock         = new Object();
    private static Set<String>  allResources = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> findResources(String pattern)
    {
        return lookForResources(pattern);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getResourceAsStream(String resourceName)
    {
        return retrieveResourceAsStream(resourceName);
    }

    private static void findFileResources(File root, String startPath)
    {
        File[] fileList = root.listFiles();
        if (fileList == null) {
            return;
        }
        try {
            for (File f : fileList) {
                if (f.isDirectory()) {
                    findFileResources(f, startPath);
                }
                else {
                    String entry =
                        f.getCanonicalPath().substring(startPath.length());
                    allResources.add(entry);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Find resources residing on the classpath.
     * 
     * @param pattern a regular expression for the name
     *        of resources to be retrieved.
     * @return a list of resource paths
     */
    private static List<String> lookForResources(String pattern)
    {
        List<String> resources = new ArrayList<String>();
        if (allResources == null) {
            synchronized (lock) {
                if (allResources == null) {
                    findAllResources();
                }
            }
        }
        Pattern regex = Pattern.compile(pattern);
        for (String item : allResources) {
            if (regex.matcher(item).matches()) {
                resources.add(item);
            }
        }

        return resources;
    }

    /**
     * Retrieve by name a resource residing on the classpath
     * and return it as a stream.
     * 
     * @param resourceName the name of the resource
     * @return the stream or null if the resource is not found
     */
    private static InputStream retrieveResourceAsStream(String resourceName)
    {
        return ClassPathLocator.class.getResourceAsStream(resourceName);

    }

    private static void findAllResources()
    {
        allResources = new HashSet<String>();
        String classpath = System.getProperty("java.class.path");
        String[] classpathEntries = classpath.split(File.pathSeparator);

        for (String entry : classpathEntries) {
            File file = new File(entry);
            if (file.exists() && file.canRead()) {
                if (file.isFile()) {
                    findJarResources(file);
                }
                else if (file.isDirectory()) {
                    String startPath;
                    try {
                        startPath = file.getCanonicalPath();
                        findFileResources(file, startPath);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private static void findJarResources(File file)
    {
        ZipFile zf;
        try {
            zf = new ZipFile(file);
        }
        catch (final ZipException e) {
            return;
        }
        catch (final IOException e) {
            return;
        }
        final Enumeration<? extends ZipEntry> e = zf.entries();
        while (e.hasMoreElements()) {
            final ZipEntry ze = (ZipEntry) e.nextElement();
            final String fileName = ze.getName();
            if (!ze.isDirectory()) {
                allResources.add(File.separator + fileName);
            }
        }
        try {
            zf.close();
        }
        catch (final IOException e1) {
        }
    }
}
