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
package org.bonitasoft.web.rest.model.bpm.cases;

import org.bonitasoft.web.toolkit.client.data.item.Definitions;
import org.bonitasoft.web.toolkit.client.data.item.IItem;
import org.bonitasoft.web.toolkit.client.data.item.ItemDefinition;
import org.bonitasoft.web.toolkit.client.data.item.attribute.ItemAttribute;

/**
 * Process instance definition
 *
 * @author Séverin Moussel *
 */
public class CaseDefinition extends ItemDefinition {

    /**
     * Singleton
     */
    public static CaseDefinition get() {
        return (CaseDefinition) Definitions.get(TOKEN);
    }

    public static final String TOKEN = "cases";

    /**
     * the URL of user resource
     */
    protected static final String API_URL = "../API/bpm/case";

    @Override
    public String defineToken() {
        return TOKEN;
    }

    @Override
    protected void definePrimaryKeys() {
        setPrimaryKeys(CaseItem.ATTRIBUTE_ID);
    }

    @Override
    protected String defineAPIUrl() {
        return API_URL;
    }

    @Override
    protected void defineAttributes() {
        createAttribute(CaseItem.ATTRIBUTE_ID, ItemAttribute.TYPE.ITEM_ID);
        createAttribute(CaseItem.ATTRIBUTE_STATE, ItemAttribute.TYPE.ENUM);
        createAttribute(CaseItem.ATTRIBUTE_PROCESS_ID, ItemAttribute.TYPE.ITEM_ID);
        createAttribute(CaseItem.ATTRIBUTE_STARTED_BY_USER_ID, ItemAttribute.TYPE.ITEM_ID);
        createAttribute(CaseItem.ATTRIBUTE_STARTED_BY_SUBSTITUTE_USER_ID, ItemAttribute.TYPE.ITEM_ID);
        createAttribute(CaseItem.ATTRIBUTE_START_DATE, ItemAttribute.TYPE.DATETIME);
        createAttribute(CaseItem.ATTRIBUTE_LAST_UPDATE_DATE, ItemAttribute.TYPE.DATETIME);
        createAttribute(CaseItem.ATTRIBUTE_END_DATE, ItemAttribute.TYPE.DATETIME);
        createAttribute(CaseItem.ATTRIBUTE_SEARCH_INDEX_1_LABEL, ItemAttribute.TYPE.STRING);
        createAttribute(CaseItem.ATTRIBUTE_SEARCH_INDEX_2_LABEL, ItemAttribute.TYPE.STRING);
        createAttribute(CaseItem.ATTRIBUTE_SEARCH_INDEX_3_LABEL, ItemAttribute.TYPE.STRING);
        createAttribute(CaseItem.ATTRIBUTE_SEARCH_INDEX_4_LABEL, ItemAttribute.TYPE.STRING);
        createAttribute(CaseItem.ATTRIBUTE_SEARCH_INDEX_5_LABEL, ItemAttribute.TYPE.STRING);
        createAttribute(CaseItem.ATTRIBUTE_SEARCH_INDEX_1_VALUE, ItemAttribute.TYPE.STRING);
        createAttribute(CaseItem.ATTRIBUTE_SEARCH_INDEX_2_VALUE, ItemAttribute.TYPE.STRING);
        createAttribute(CaseItem.ATTRIBUTE_SEARCH_INDEX_3_VALUE, ItemAttribute.TYPE.STRING);
        createAttribute(CaseItem.ATTRIBUTE_SEARCH_INDEX_4_VALUE, ItemAttribute.TYPE.STRING);
        createAttribute(CaseItem.ATTRIBUTE_SEARCH_INDEX_5_VALUE, ItemAttribute.TYPE.STRING);
    }

    @Override
    public IItem _createItem() {
        return new CaseItem();
    }

}
