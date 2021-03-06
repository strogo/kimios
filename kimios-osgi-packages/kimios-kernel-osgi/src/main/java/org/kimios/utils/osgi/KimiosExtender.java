/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kimios.utils.osgi;

import org.kimios.utils.extension.BundleUrlType;
import org.kimios.utils.extension.ClassFinder;
import org.kimios.utils.extension.ExtensionRegistryManager;
import org.kimios.utils.extension.IExtensionRegistryManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Enumeration;

/**
 * Created by farf on 6/20/14.
 */
public class KimiosExtender extends BundleTracker {


    private static Logger logger = LoggerFactory.getLogger(KimiosExtender.class);


    private IExtensionRegistryManager extensionRegistryManager;

    public KimiosExtender(BundleContext context, int stateMask, BundleTrackerCustomizer customizer,
                          IExtensionRegistryManager extensionRegistryManager) {
        super(context, stateMask, customizer);
        this.extensionRegistryManager = extensionRegistryManager;
    }

    @Override
    public Object addingBundle(Bundle bundle, BundleEvent event) {
        analyseBundle(bundle);
        return bundle;
    }

    @Override
    public void modifiedBundle(Bundle bundle, BundleEvent event, Object object) {
        super.modifiedBundle(bundle, event, object);
    }

    @Override
    public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
        super.removedBundle(bundle, event, object);
    }


    private void analyseBundle(Bundle bundle){
        //eturn super.addingBundle(bundle, event);
        Enumeration<String> items = bundle.getHeaders().keys();
        while (items.hasMoreElements()) {
            String k = items.nextElement();
            if (k.startsWith("Kimios-")) {
                // put in registry
                logger.info("found header {} ==> {}", k, bundle.getHeaders().get(k));
                String interfacesClassName =  (String)bundle.getHeaders().get(k);
                String[] iArray = interfacesClassName.split(",");

                for(String interfaceClassName: iArray){
                    logger.info("bundle {} with name {} will provide extension of type {}",
                            bundle.getBundleId(), bundle.getSymbolicName(), interfaceClassName);
                    if (interfaceClassName != null) {
                        Class<?> clazz;
                        try {
                            clazz = bundle.loadClass(interfaceClassName);

                            Class<BundleWiring> wiringClass = BundleWiring.class;
                            BundleWiring bw = (BundleWiring)bundle.adapt(wiringClass);
                            ClassLoader bundleCl = bw.getClassLoader();
                            Collection<Class<?>> classes =
                                    ClassFinder.findImplement(new BundleUrlType(bundle), "org.kimios", clazz, bundleCl);
                            for(Class clItem: classes) {
                                logger.info("Kimios Extender Found item For Type {} ==> {}. will be put in registry", clazz, clItem);
                                extensionRegistryManager.addClass(clItem);
                            }
                        } catch (ClassNotFoundException e) {
                            logger.error("Could not find class " + interfaceClassName, e);
                        }
                        catch (Exception e) {
                            logger.error("extender exception fro " + interfaceClassName, e);
                        }
                    }
                }

            }
        }
    }
}
