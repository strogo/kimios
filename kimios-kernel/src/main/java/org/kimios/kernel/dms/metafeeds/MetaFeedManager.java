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
package org.kimios.kernel.dms.metafeeds;

import org.kimios.kernel.dms.model.MetaFeedImpl;
import org.kimios.utils.extension.ClassFinder;
import org.kimios.utils.extension.ExtensionRegistry;
import org.kimios.utils.extension.ExtensionRegistryManager;
import org.kimios.utils.extension.IExtensionRegistryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MetaFeedManager extends ExtensionRegistry<MetaFeedImpl>
{
    public MetaFeedManager(IExtensionRegistryManager extensionRegistryManager) {
        super(extensionRegistryManager);
    }

}

