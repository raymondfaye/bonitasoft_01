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
package org.bonitasoft.web.rest.server.api.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.bonitasoft.engine.session.APISession;
import org.bonitasoft.web.rest.model.ModelFactory;
import org.bonitasoft.web.rest.model.application.AbstractApplicationDefinition;
import org.bonitasoft.web.rest.model.application.AbstractApplicationItem;
import org.bonitasoft.web.rest.model.application.ApplicationItem;
import org.bonitasoft.web.rest.model.application.ApplicationLinkItem;
import org.bonitasoft.web.rest.model.portal.profile.ProfileItem;
import org.bonitasoft.web.rest.server.api.applicationpage.APIApplicationDataStoreFactory;
import org.bonitasoft.web.rest.server.api.deployer.DeployerFactory;
import org.bonitasoft.web.rest.server.api.deployer.GenericDeployer;
import org.bonitasoft.web.rest.server.api.deployer.PageDeployer;
import org.bonitasoft.web.rest.server.api.deployer.UserDeployer;
import org.bonitasoft.web.rest.server.datastore.application.ApplicationDataStore;
import org.bonitasoft.web.rest.server.datastore.application.ApplicationDataStoreCreator;
import org.bonitasoft.web.rest.server.datastore.page.PageDatastore;
import org.bonitasoft.web.rest.server.framework.APIServletCall;
import org.bonitasoft.web.rest.server.framework.Deployer;
import org.bonitasoft.web.rest.server.framework.search.ItemSearchResult;
import org.bonitasoft.web.toolkit.client.ItemDefinitionFactory;
import org.bonitasoft.web.toolkit.client.data.APIID;
import org.bonitasoft.web.toolkit.client.data.item.ItemDefinition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class APIApplicationTest {

    static {
        ItemDefinitionFactory.setDefaultFactory(new ModelFactory());
    }

    @Mock
    private ApplicationDataStore dataStore;

    @Mock
    private APIServletCall caller;

    @Mock
    private APIApplicationDataStoreFactory applicationDataStoreFactory;

    @Mock
    private ApplicationDataStoreCreator creator;

    @Mock
    private PageDatastore pageDatastore;

    @InjectMocks
    @Spy
    private APIApplication apiApplication;

    @Mock
    private HttpSession httpSession;

    @Mock
    private APISession apiSession;

    @Mock
    private DeployerFactory deployerFactory;

    @Before
    public void setUp() throws Exception {
        apiApplication.setCaller(caller);
        given(caller.getHttpSession()).willReturn(httpSession);
        given(httpSession.getAttribute("apiSession")).willReturn(apiSession);
        given(applicationDataStoreFactory.createPageDataStore(apiSession)).willReturn(pageDatastore);
        given(creator.create(apiSession)).willReturn(dataStore);
        doReturn(deployerFactory).when(apiApplication).getDeployerFactory();
    }

    @Test
    public void add_legacy_should_return_the_result_of_dataStore_add() throws Exception {
        //given
        final ApplicationItem itemToCreate = mock(ApplicationItem.class);
        final ApplicationItem createdItem = mock(ApplicationItem.class);
        given(dataStore.add((AbstractApplicationItem) itemToCreate)).willReturn(createdItem);
        //when
        final ApplicationItem retrievedItem = (ApplicationItem) apiApplication.add(itemToCreate);

        //then
        assertThat(retrievedItem).isEqualTo(createdItem);
    }

    @Test
    public void add_link_should_return_the_result_of_dataStore_add() throws Exception {
        //given
        final ApplicationLinkItem itemToCreate = mock(ApplicationLinkItem.class);
        final ApplicationLinkItem createdItem = mock(ApplicationLinkItem.class);
        given(dataStore.add((AbstractApplicationItem) itemToCreate)).willReturn(createdItem);
        //when
        final ApplicationLinkItem retrievedItem = (ApplicationLinkItem) apiApplication.add(itemToCreate);

        //then
        assertThat(retrievedItem).isEqualTo(createdItem);
    }

    @Test
    public void search_should_return_the_result_of_dataStore_search() throws Exception {
        //given
        @SuppressWarnings("unchecked")
        final ItemSearchResult<AbstractApplicationItem> result = mock(ItemSearchResult.class);
        given(dataStore.search(0, 10, "request", "default", Collections.singletonMap("name", "hr"))).willReturn(result);

        //when
        final ItemSearchResult<AbstractApplicationItem> retrievedResult = apiApplication.search(0, 10, "request",
                "default",
                Collections.singletonMap("name", "hr"));

        //then
        assertThat(retrievedResult).isEqualTo(result);
    }

    @Test
    public void defineDefaultSearchOrder_should_return_attribute_name() throws Exception {
        //when
        final String defaultSearchOrder = apiApplication.defineDefaultSearchOrder();

        //then
        assertThat(defaultSearchOrder).isEqualTo("displayName");
    }

    @Test
    public void defineItemDefinition_return_an_instance_of_AbstractApplicationDefinition() throws Exception {
        //when
        final ItemDefinition<AbstractApplicationItem> itemDefinition = apiApplication.defineItemDefinition();

        //then
        assertThat(itemDefinition).isExactlyInstanceOf(AbstractApplicationDefinition.class);
    }

    @Test
    public void fillDeploys_should_add_deployers_for_createdBy_updatedBy_ProfileId_and_LayoutId() throws Exception {
        //given
        final ApplicationItem item = mock(ApplicationItem.class);
        doReturn(new GenericDeployer<ProfileItem>(null, ApplicationItem.ATTRIBUTE_PROFILE_ID)).when(deployerFactory)
                .createProfileDeployer(ApplicationItem.ATTRIBUTE_PROFILE_ID);
        //when
        apiApplication.fillDeploys(item, Collections.<String> emptyList());

        //then
        final Map<String, Deployer> deployers = apiApplication.getDeployers();
        assertThat(deployers).hasSize(5);
        assertThat(deployers.keySet()).contains(ApplicationItem.ATTRIBUTE_CREATED_BY,
                ApplicationItem.ATTRIBUTE_UPDATED_BY,
                ApplicationItem.ATTRIBUTE_PROFILE_ID);
        assertThat(deployers.get(ApplicationItem.ATTRIBUTE_CREATED_BY)).isExactlyInstanceOf(UserDeployer.class);
        assertThat(deployers.get(ApplicationItem.ATTRIBUTE_UPDATED_BY)).isExactlyInstanceOf(UserDeployer.class);
        assertThat(deployers.get(ApplicationItem.ATTRIBUTE_LAYOUT_ID)).isExactlyInstanceOf(PageDeployer.class);
        assertThat(deployers.get(ApplicationItem.ATTRIBUTE_THEME_ID)).isExactlyInstanceOf(PageDeployer.class);
    }

    @Test
    public void update_should_return_the_appropriate_application_kind() throws Exception {
        //given
        final APIID idToUpdate = mock(APIID.class);
        final ApplicationItem updatedItem = mock(ApplicationItem.class);
        Map<String, String> attributes = Collections.emptyMap();
        given(dataStore.update(idToUpdate, attributes)).willReturn(updatedItem);

        //when
        final AbstractApplicationItem retrievedItem = apiApplication.update(idToUpdate, attributes);

        //then
        assertThat(retrievedItem).isEqualTo(updatedItem);
    }

}
