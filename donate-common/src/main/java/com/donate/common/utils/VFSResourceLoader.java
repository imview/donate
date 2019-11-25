package com.donate.common.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * VFS based ResourceLoader.
 *
 * @author <a href="mailto:ales.justin@jboss.com">Ales Justin</a>
 * @author <a href="mailto:mariusb@redhat.com">Marius Bogoevici</a>
 */
public class VFSResourceLoader extends DefaultResourceLoader {

    public VFSResourceLoader(ClassLoader classLoader) {
        super(classLoader);
    }

    public Resource getResource(String location) {
        Assert.notNull(location, "Location must not be null");
        if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            return doGetResourceForLocation(location.substring(CLASSPATH_URL_PREFIX.length()));
        } else {
            return super.getResource(location);
        }
    }

    protected Resource getResourceByPath(String path) {
        return doGetResourceForLocation(path);
    }

    private Resource doGetResourceForLocation(String path) {
        URL url = getClassLoader().getResource(path);
        if (url != null) {
            return new VFSResource(url);
        } else {
            return new InexistentResource(path);
        }
    }

    /* A special type of resource, for the case when the resource does not exit */
    private static class InexistentResource extends AbstractResource {

        private final String path;

        private InexistentResource(String path) {
            this.path = path;
        }

        public String getDescription() {
            return null;
        }

        public InputStream getInputStream() throws IOException {
            throw new FileNotFoundException("Resource does not exist for path " + path);
        }

        public boolean exists() {
            return false;
        }
    }
}