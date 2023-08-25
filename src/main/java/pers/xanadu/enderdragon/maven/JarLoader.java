package pers.xanadu.enderdragon.maven;

import pers.xanadu.enderdragon.EnderDragon;
import sun.misc.Unsafe;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

/**
 * @author sky
 * @since 2020-04-12 22:39
 */
public class JarLoader {

    private static MethodHandles.Lookup lookup;
    private static Unsafe unsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            Field lookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            Object lookupBase = unsafe.staticFieldBase(lookupField);
            long lookupOffset = unsafe.staticFieldOffset(lookupField);
            lookup = (MethodHandles.Lookup) unsafe.getObject(lookupBase, lookupOffset);
        } catch (Throwable ignore) {
        }
    }

    JarLoader() {
    }

    public static ClassLoader addPath(Path path) {
        try {
            File file = new File(path.toUri().getPath());

            ClassLoader loader = EnderDragon.class.getClassLoader();
            // Bukkit
            Field ucpField;
            try {
                ucpField = URLClassLoader.class.getDeclaredField("ucp");
            } catch (NoSuchFieldError | NoSuchFieldException ignored) {
                ucpField = ucp(loader.getClass());
            }
            addURL(loader, ucpField, file);
            return loader;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    private static void addURL(ClassLoader loader, Field ucpField, File file) throws Throwable {
        if (ucpField == null) {
            throw new IllegalStateException("ucp field not found");
        }
        Object ucp = unsafe.getObject(loader, unsafe.objectFieldOffset(ucpField));
        try {
            MethodHandle methodHandle = lookup.findVirtual(ucp.getClass(), "addURL", MethodType.methodType(void.class, URL.class));
            methodHandle.invoke(ucp, file.toURI().toURL());
        } catch (NoSuchMethodError e) {
            throw new IllegalStateException("Unsupported (classloader: " + loader.getClass().getName() + ", ucp: " + ucp.getClass().getName() + ")", e);
        }
    }

    private static Field ucp(Class<?> loader) {
        try {
            return loader.getDeclaredField("ucp");
        } catch (NoSuchFieldError | NoSuchFieldException e2) {
            Class<?> superclass = loader.getSuperclass();
            if (superclass == Object.class) {
                return null;
            }
            return ucp(superclass);
        }
    }
}
