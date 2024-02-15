/**
 * Copyright (C) 2022 Bonitasoft S.A.
 * Bonitasoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.web.rest.server.api.deployer;

import org.bonitasoft.web.rest.model.applicationpage.ApplicationPageItem;
import org.bonitasoft.web.rest.server.framework.Deployer;
import org.bonitasoft.web.rest.server.framework.api.DatastoreHasGet;
import org.bonitasoft.web.toolkit.client.data.APIID;
import org.bonitasoft.web.toolkit.client.data.item.IItem;

/**
 * @author Julien Mege
 */
public class ApplicationPageDeployer implements Deployer {

    private final DatastoreHasGet<ApplicationPageItem> getter;

    private final String attribute;

    public ApplicationPageDeployer(final DatastoreHasGet<ApplicationPageItem> getter, final String attribute) {
        this.getter = getter;
        this.attribute = attribute;
    }

    @Override
    public String getDeployedAttribute() {
        return attribute;
    }

    @Override
    public void deployIn(final IItem item) {
        if (isDeployable(attribute, item)) {
            item.setDeploy(attribute, getApplicationPage(getApplicationPageId(item)));
        }
    }

    private APIID getApplicationPageId(final IItem item) {
        return item.getAttributeValueAsAPIID(attribute);
    }

    private ApplicationPageItem getApplicationPage(final APIID applicationPageId) {
        return getter.get(applicationPageId);
    }

}
