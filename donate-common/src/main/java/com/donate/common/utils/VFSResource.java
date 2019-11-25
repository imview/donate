package com.donate.common.utils;

import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.AbstractResource;

/**
 * VFS based Resource.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 */
public class VFSResource extends AbstractResource {

    private Object file;

    public VFSResource(Object file) {
        if (file == null) {
            throw new IllegalArgumentException("Null file");
        }
        this.file = file;
    }

    public VFSResource(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("Null url");
        }
        try {
            this.file = VFSUtil.invokeVfsMethod(VFSUtil.VFS_METHOD_GET_ROOT_URL, null, url);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot retrieve file from URL: ", e);
        }
    }

    public boolean exists() {
        try {
            return (Boolean) VFSUtil.invokeVfsMethod(VFSUtil.VIRTUAL_FILE_METHOD_EXISTS, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isOpen() {
        return false;
    }

    public boolean isReadable() {
        try {
            return ((Long) VFSUtil.invokeVfsMethod(VFSUtil.VIRTUAL_FILE_METHOD_GET_SIZE, file)) > 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long lastModified() {
        try {
            return ((Long) VFSUtil.invokeVfsMethod(VFSUtil.VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED, file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public URL getURL() throws IOException {
        return VFSUtil.invokeVfsMethod(VFSUtil.VIRTUAL_FILE_METHOD_TO_URL, file);
    }

    public URI getURI() throws IOException {
        try {
            return VFSUtil.invokeMethodWithExpectedExceptionType(VFSUtil.VIRTUAL_FILE_METHOD_TO_URI, file, URISyntaxException.class);
        } catch (URISyntaxException e) {
            IOException ioe = new IOException(e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }
    }

    public File getFile() throws IOException {
        return VFSUtil.getPhysicalFile(file);
    }

    @SuppressWarnings("deprecation")
    public Resource createRelative(String relativePath) throws IOException {
        //VirtualFile.findChild will not scan the parent or current directory
        if (relativePath.startsWith(".") || relativePath.indexOf("/") == -1) {
            return new VFSResource(getChild(new URL(getURL(), relativePath)));
        } else {
            return new VFSResource(VFSUtil.invokeVfsMethod(VFSUtil.VIRTUAL_FILE_METHOD_GET_CHILD, file, relativePath));
        }
    }

    public String getFilename() {
        try {
            return VFSUtil.invokeVfsMethod(VFSUtil.VIRTUAL_FILE_METHOD_GET_NAME, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getDescription() {
        return file.toString();
    }

    public InputStream getInputStream() throws IOException {
        return VFSUtil.invokeVfsMethod(VFSUtil.VIRTUAL_FILE_METHOD_GET_INPUT_STREAM, file);
    }

    public String toString() {
        return getDescription();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof VFSResource) {
            return file.equals(((VFSResource) other).file);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }

    static Object getChild(URL url) throws IOException {
        try {
            return VFSUtil.invokeMethodWithExpectedExceptionType(VFSUtil.VFS_METHOD_GET_ROOT_URL, null, URISyntaxException.class, url);
        } catch (URISyntaxException e) {
            IOException ioe = new IOException(e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }
    }
}