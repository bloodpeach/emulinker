package org.emulinker.util;

import java.io.*;

import org.nanocontainer.script.xml.XMLContainerBuilder;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.*;

public class PicoUtil {
    public static PicoContainer buildContainer(PicoContainer parentContainer,
            Object scope, String resourceName) throws Exception {
    	FileReader reader = new FileReader(resourceName);
        XMLContainerBuilder builder = new XMLContainerBuilder(reader,
                PicoUtil.class.getClassLoader());
        ObjectReference containerRef = new SimpleReference();
        ObjectReference parentContainerRef = new SimpleReference();
        parentContainerRef.set(parentContainer);
        builder.buildContainer(containerRef, parentContainerRef, scope, true);
        return (PicoContainer) containerRef.get();
    }
}
