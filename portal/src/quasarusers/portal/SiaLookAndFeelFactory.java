package quasarusers.portal;

/*
 * Copyright (c) 2001, 2002. software design & management AG
 * All rights reserved.
 * This file is made available under the terms of the license
 * agreement that accompanies this distribution.
 *
 * $Revision: 1.1 $, last modified $Date: 2003/03/26 19:58:36 $ by $Author: schmickler $
 */

import org.wings.plaf.LookAndFeelFactory;
import org.wings.plaf.LookAndFeel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//todo dpk 28.01.03 -> MC kommentieren

public class SiaLookAndFeelFactory extends LookAndFeelFactory {
    protected LookAndFeel create() throws IOException {
        Properties properties = new Properties();

        String name = "default.properties";

        InputStream in = getClass().getResourceAsStream(name);
        properties.load(in);

        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return new LookAndFeel(properties) {
            public ClassLoader getClassLoader() {
                return contextClassLoader;
            }
        };
    }
}
