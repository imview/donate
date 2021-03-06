package com.donate.common.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;

import org.jboss.logging.Logger;
import org.springframework.core.NestedIOException;
import org.springframework.util.ReflectionUtils;

public class VFSUtil {

    private static final Logger logger = Logger.getLogger(VFSUtil.class);

    protected static final String VFS2_PACKAGE_NAME = "org.jboss.virtual";

    protected static final String VFS3_PACKAGE_NAME = "org.jboss.vfs";

    private static String[] VFS_PACKAGE_NAME_CANDIDATES = {VFS2_PACKAGE_NAME, VFS3_PACKAGE_NAME};

    public static Class<?> VFS_CLASS = null;

    public static Method VFS_METHOD_GET_ROOT_URL = null;

    public static Method VFS_METHOD_GET_ROOT_URI = null;

    public static Class<?> VIRTUAL_FILE_CLASS = null;

    public static Method VIRTUAL_FILE_METHOD_EXISTS = null;

    public static Method VIRTUAL_FILE_METHOD_GET_SIZE;

    public static Method VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED;

    public static Method VIRTUAL_FILE_METHOD_GET_CHILD;

    public static Method VIRTUAL_FILE_METHOD_GET_INPUT_STREAM;

    public static Method VIRTUAL_FILE_METHOD_TO_URL;

    public static Method VIRTUAL_FILE_METHOD_TO_URI;

    public static Method VIRTUAL_FILE_METHOD_GET_NAME;

    public static Method VIRTUAL_FILE_METHOD_GET_PATH_NAME;

    public static Method VIRTUAL_FILE_METHOD_VISIT;

    public static Class<?> VFS_UTILS_CLASS = null;

    public static Method VFS_UTILS_METHOD_IS_NESTED_FILE = null;

    public static Method VFS_UTILS_METHOD_GET_COMPATIBLE_URI = null;


    public static Class<?> VIRTUAL_FILE_VISITOR_CLASS = null;

    public static Class<?> VISITOR_ATTRIBUTES_CLASS = null;

    public static Field VISITOR_ATTRIBUTES_FIELD_RECURSE = null;

    public static String vfsPackageName = null;

    // initialize VFS classes


    static {
        ClassLoader loader = VFSUtil.class.getClassLoader();
        for (String vfsPackageNameCandidate : VFS_PACKAGE_NAME_CANDIDATES) {
            try {
                VFS_CLASS = loader.loadClass(vfsPackageNameCandidate + "." + "VFS");
                vfsPackageName = vfsPackageNameCandidate;
            } catch (ClassNotFoundException e) {
                // ignore, package does not exist
            }
        }
        if (vfsPackageName == null) {
            // unexpected error, no known VFS package found
            throw new IllegalStateException("No known VFS package found while trying to process VFS resource");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("VFS package name is:" + vfsPackageName);
        }
        // extract the rest of the reflective information that we need for VFS invocation
        try {
            if (VFS2_PACKAGE_NAME.equals(vfsPackageName)) {
                VFS_METHOD_GET_ROOT_URL = ReflectionUtils.findMethod(VFS_CLASS, "getRoot", new Class[]{URL.class});
                VFS_METHOD_GET_ROOT_URI = ReflectionUtils.findMethod(VFS_CLASS, "getRoot", new Class[]{URI.class});
            } else {
                VFS_METHOD_GET_ROOT_URL = ReflectionUtils.findMethod(VFS_CLASS, "getChild", new Class[]{URL.class});
                VFS_METHOD_GET_ROOT_URI = ReflectionUtils.findMethod(VFS_CLASS, "getChild", new Class[]{URI.class});
            }
            VIRTUAL_FILE_CLASS = loader.loadClass(vfsPackageName + ".VirtualFile");
            VIRTUAL_FILE_METHOD_EXISTS = ReflectionUtils.findMethod(VIRTUAL_FILE_CLASS, "exists");
            VIRTUAL_FILE_METHOD_GET_SIZE = ReflectionUtils.findMethod(VIRTUAL_FILE_CLASS, "getSize");
            VIRTUAL_FILE_METHOD_GET_INPUT_STREAM = ReflectionUtils.findMethod(VIRTUAL_FILE_CLASS, "openStream");
            VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED = ReflectionUtils.findMethod(VIRTUAL_FILE_CLASS, "getLastModified");
            VIRTUAL_FILE_METHOD_TO_URI = ReflectionUtils.findMethod(VIRTUAL_FILE_CLASS, "toURI");
            VIRTUAL_FILE_METHOD_TO_URL = ReflectionUtils.findMethod(VIRTUAL_FILE_CLASS, "toURL");
            VIRTUAL_FILE_METHOD_GET_NAME = ReflectionUtils.findMethod(VIRTUAL_FILE_CLASS, "getName");
            VIRTUAL_FILE_METHOD_GET_PATH_NAME = ReflectionUtils.findMethod(VIRTUAL_FILE_CLASS, "getPathName");

            if (VFS2_PACKAGE_NAME.equals(vfsPackageName)) {
                VIRTUAL_FILE_METHOD_GET_CHILD = ReflectionUtils.findMethod(VIRTUAL_FILE_CLASS, "findChild", new Class[]{String.class});
            } else {
                VIRTUAL_FILE_METHOD_GET_CHILD = ReflectionUtils.findMethod(VIRTUAL_FILE_CLASS, "getChild", new Class[]{String.class});

            }

            VFS_UTILS_CLASS = loader.loadClass(vfsPackageName + ".VFSUtils");
            VFS_UTILS_METHOD_GET_COMPATIBLE_URI = ReflectionUtils.findMethod(VFS_UTILS_CLASS, "getCompatibleURI", new Class<?>[]{VIRTUAL_FILE_CLASS});
            VFS_UTILS_METHOD_IS_NESTED_FILE = ReflectionUtils.findMethod(VFS_UTILS_CLASS, "isNestedFile", new Class<?>[]{VIRTUAL_FILE_CLASS});

            VIRTUAL_FILE_VISITOR_CLASS = loader.loadClass(vfsPackageName + ".VirtualFileVisitor");
            VIRTUAL_FILE_METHOD_VISIT = ReflectionUtils.findMethod(VIRTUAL_FILE_CLASS, "visit", new Class<?>[]{VIRTUAL_FILE_VISITOR_CLASS});

            VISITOR_ATTRIBUTES_CLASS = loader.loadClass(vfsPackageName + ".VisitorAttributes");
            VISITOR_ATTRIBUTES_FIELD_RECURSE = ReflectionUtils.findField(VISITOR_ATTRIBUTES_CLASS, "RECURSE");

        } catch (ClassNotFoundException e) {
            // unexpected error, a VFS class hasn't been found although others have
            throw new IllegalStateException("A VFS class hasn't been found, although others exist:", e);
        }
    }


    public static <T, E extends Exception> T invokeMethodWithExpectedExceptionType(Method method, Object target, Class<E> expectedExceptionType, Object... args) throws E {
        try {
            return (T) method.invoke(target, args);
        } catch (IllegalAccessException ex) {
            ReflectionUtils.handleReflectionException(ex);
        } catch (InvocationTargetException ex) {
            if (expectedExceptionType.isAssignableFrom(ex.getTargetException().getClass())) {
                throw (E) ex.getTargetException();
            }
            ReflectionUtils.handleInvocationTargetException(ex);
        }
        throw new IllegalStateException("Should never get here");
    }

    public static <T> T invokeVfsMethod(Method method, Object target, Object... arguments) throws IOException {
        return (T) invokeMethodWithExpectedExceptionType(method, target, IOException.class, arguments);
    }

    public static File getPhysicalFile(Object virtualFile) throws IOException {
        if (VFS2_PACKAGE_NAME.equals(vfsPackageName)) {
            if ((Boolean) invokeVfsMethod(VFS_UTILS_METHOD_IS_NESTED_FILE, null, virtualFile)) {
                throw new IOException("File resolution not supported for nested resource: " + virtualFile);
            }
            try {
                return new File((URI) invokeVfsMethod(VFS_UTILS_METHOD_GET_COMPATIBLE_URI, null, virtualFile));
            } catch (Exception ex) {
                throw new NestedIOException("Failed to obtain File reference for " + virtualFile, ex);
            }
        } else {
            return (File) invokeVfsMethod(ReflectionUtils.findMethod(VIRTUAL_FILE_CLASS, "getPhysicalFile"), virtualFile);
        }
    }
}
