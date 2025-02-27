/**
 * Copyright (C) 2019 Bonitasoft S.A.
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
package org.bonitasoft.engine.api.impl;

import static org.bonitasoft.engine.business.application.ApplicationSearchDescriptor.USER_ID;
import static org.bonitasoft.engine.business.application.InternalProfiles.INTERNAL_PROFILE_SUPER_ADMIN;
import static org.bonitasoft.engine.search.descriptor.SearchApplicationDescriptor.APPLICATION_VISIBILITY;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bonitasoft.engine.api.ApplicationAPI;
import org.bonitasoft.engine.api.ImportStatus;
import org.bonitasoft.engine.api.impl.converter.ApplicationMenuModelConverter;
import org.bonitasoft.engine.api.impl.converter.ApplicationModelConverter;
import org.bonitasoft.engine.api.impl.converter.ApplicationPageModelConverter;
import org.bonitasoft.engine.api.impl.livingapplication.LivingApplicationAPIDelegate;
import org.bonitasoft.engine.api.impl.livingapplication.LivingApplicationExporterDelegate;
import org.bonitasoft.engine.api.impl.livingapplication.LivingApplicationMenuAPIDelegate;
import org.bonitasoft.engine.api.impl.livingapplication.LivingApplicationPageAPIDelegate;
import org.bonitasoft.engine.api.impl.transaction.application.SearchApplicationMenus;
import org.bonitasoft.engine.api.impl.transaction.application.SearchApplicationPages;
import org.bonitasoft.engine.api.impl.transaction.application.SearchApplications;
import org.bonitasoft.engine.api.impl.transaction.application.SearchApplicationsOfUser;
import org.bonitasoft.engine.business.application.*;
import org.bonitasoft.engine.business.application.converter.*;
import org.bonitasoft.engine.business.application.exporter.ApplicationContainerExporter;
import org.bonitasoft.engine.business.application.exporter.ApplicationExporter;
import org.bonitasoft.engine.business.application.importer.StrategySelector;
import org.bonitasoft.engine.business.application.importer.validator.ApplicationImportValidator;
import org.bonitasoft.engine.business.application.importer.validator.ApplicationMenuCreatorValidator;
import org.bonitasoft.engine.business.application.importer.validator.ApplicationTokenValidator;
import org.bonitasoft.engine.exception.*;
import org.bonitasoft.engine.page.PageService;
import org.bonitasoft.engine.profile.ProfileService;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.engine.search.descriptor.SearchApplicationDescriptor;
import org.bonitasoft.engine.search.descriptor.SearchApplicationMenuDescriptor;
import org.bonitasoft.engine.search.descriptor.SearchApplicationPageDescriptor;
import org.bonitasoft.engine.search.impl.SearchFilter;
import org.bonitasoft.engine.service.ServiceAccessor;
import org.bonitasoft.engine.service.impl.ServiceAccessorFactory;
import org.bonitasoft.engine.session.SessionService;

/**
 * @author Elias Ricken de Medeiros
 */
@AvailableInMaintenanceMode
public class ApplicationAPIImpl implements ApplicationAPI {

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated(since = "9.0.0")
    public Application createApplication(final ApplicationCreator applicationCreator)
            throws CreationException {
        return getLivingApplicationAPIDelegate().createApplication(applicationCreator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated(since = "10.2.0")
    public ApplicationLink createApplicationLink(final ApplicationLinkCreator applicationLinkCreator)
            throws CreationException {
        return getLivingApplicationAPIDelegate().createApplicationLink(applicationLinkCreator);
    }

    private LivingApplicationAPIDelegate getLivingApplicationAPIDelegate() {
        return new LivingApplicationAPIDelegate(getServiceAccessor(),
                getApplicationModelConverter(getServiceAccessor().getPageService()),
                SessionInfos.getUserIdFromSession(), new ApplicationTokenValidator());
    }

    protected ApplicationModelConverter getApplicationModelConverter(final PageService pageService) {
        return new ApplicationModelConverter(pageService);
    }

    private LivingApplicationPageAPIDelegate getApplicationPageAPIDelegate() {
        return new LivingApplicationPageAPIDelegate(getServiceAccessor(), new ApplicationPageModelConverter(),
                SessionInfos.getUserIdFromSession(),
                new ApplicationTokenValidator());
    }

    private LivingApplicationMenuAPIDelegate getApplicationMenuAPIDelegate() {
        return new LivingApplicationMenuAPIDelegate(getServiceAccessor(), new ApplicationMenuModelConverter(),
                new ApplicationMenuCreatorValidator(),
                SessionInfos.getUserIdFromSession());
    }

    private LivingApplicationExporterDelegate getLivingApplicationExporterDelegate() {
        final ServiceAccessor serviceAccessor = getServiceAccessor();
        final ApplicationService applicationService = serviceAccessor.getApplicationService();
        final PageService pageService = serviceAccessor.getPageService();
        ApplicationToNodeConverter applicationToNodeConverter = new ApplicationToNodeConverter(
                serviceAccessor.getProfileService(), applicationService,
                new ApplicationPageToNodeConverter(pageService), new ApplicationMenuToNodeConverter(applicationService),
                pageService);
        final ApplicationsToNodeContainerConverter applicationsToNodeContainerConverter = new ApplicationsToNodeContainerConverter(
                applicationToNodeConverter);
        final ApplicationContainerExporter applicationContainerExporter = new ApplicationContainerExporter();
        final ApplicationExporter applicationExporter = new ApplicationExporter(applicationsToNodeContainerConverter,
                applicationContainerExporter);
        return new LivingApplicationExporterDelegate(serviceAccessor.getApplicationService(), applicationExporter);
    }

    protected NodeToApplicationConverter getNodeToApplicationConverter(final PageService pageService,
            final ProfileService profileService, final ApplicationImportValidator importValidator) {
        return new NodeToApplicationConverter(profileService, pageService, importValidator);

    }

    @Override
    public IApplication getIApplication(final long applicationId) throws ApplicationNotFoundException {
        return getLivingApplicationAPIDelegate().getIApplication(applicationId);
    }

    @Override
    public IApplication getIApplicationByToken(final String applicationToken) throws ApplicationNotFoundException {
        return getLivingApplicationAPIDelegate().getIApplicationByToken(applicationToken);
    }

    @Override
    public void deleteApplication(final long applicationId) throws DeletionException {
        getLivingApplicationAPIDelegate().deleteApplication(applicationId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated(since = "9.0.0")
    public Application updateApplication(final long applicationId, final ApplicationUpdater updater)
            throws ApplicationNotFoundException, UpdateException,
            AlreadyExistsException {
        return getLivingApplicationAPIDelegate().updateApplication(applicationId, updater);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated(since = "10.2.0")
    public ApplicationLink updateApplicationLink(final long applicationId, final ApplicationLinkUpdater updater)
            throws ApplicationNotFoundException, UpdateException,
            AlreadyExistsException {
        return getLivingApplicationAPIDelegate().updateApplicationLink(applicationId, updater);
    }

    protected ServiceAccessor getServiceAccessor() {
        try {
            return ServiceAccessorFactory.getInstance().createServiceAccessor();
        } catch (final Exception e) {
            throw new BonitaRuntimeException(e);
        }
    }

    /**
     * @deprecated as of 10.2.0, use {@link #searchIApplications(SearchOptions)} instead to include application links.
     */
    @Deprecated(since = "10.2.0")
    @Override
    public SearchResult<Application> searchApplications(final SearchOptions searchOptions) throws SearchException {
        final ServiceAccessor serviceAccessor = getServiceAccessor();
        final SearchApplicationDescriptor appSearchDescriptor = serviceAccessor.getSearchEntitiesDescriptor()
                .getSearchApplicationDescriptor();
        final ApplicationModelConverter converter = getApplicationModelConverter(serviceAccessor.getPageService());
        final ApplicationService applicationService = serviceAccessor.getApplicationService();
        final Optional<SearchFilter> filterOnUserId = searchOptions.getFilters().stream()
                .filter(s -> s.getField().equals(USER_ID)).findFirst();
        if (filterOnUserId.isPresent()) {
            final SearchOptionsBuilder searchOptionsWithoutUserId = new SearchOptionsBuilder(searchOptions)
                    .setFilters(searchOptions.getFilters().stream().filter(s -> !s.getField().equals(USER_ID))
                            .collect(Collectors.toList()));
            final String userId = String.valueOf(filterOnUserId.get().getValue());
            if (String.valueOf(SessionService.SYSTEM_ID).equals(userId)) { // It's the tenant admin user
                return getLivingApplicationAPIDelegate().searchIApplications(new SearchApplications(
                        Application.class,
                        applicationService, appSearchDescriptor,
                        searchOptionsWithoutUserId
                                .filter(APPLICATION_VISIBILITY, INTERNAL_PROFILE_SUPER_ADMIN.getProfileName()).done(),
                        converter));
            } else {
                return getLivingApplicationAPIDelegate().searchIApplications(new SearchApplicationsOfUser(
                        Application.class,
                        Long.parseLong(userId), applicationService, appSearchDescriptor,
                        searchOptionsWithoutUserId.done(), converter));
            }
        }
        return getLivingApplicationAPIDelegate()
                .searchIApplications(new SearchApplications(Application.class, applicationService, appSearchDescriptor,
                        searchOptions, converter));
    }

    @Override
    public SearchResult<IApplication> searchIApplications(final SearchOptions searchOptions) throws SearchException {
        final ServiceAccessor serviceAccessor = getServiceAccessor();
        final SearchApplicationDescriptor appSearchDescriptor = serviceAccessor.getSearchEntitiesDescriptor()
                .getSearchApplicationDescriptor();
        return internalSearchIApplications(serviceAccessor, appSearchDescriptor, searchOptions);
    }

    protected SearchResult<IApplication> internalSearchIApplications(ServiceAccessor serviceAccessor,
            SearchApplicationDescriptor appSearchDescriptor, SearchOptions searchOptions) throws SearchException {
        final ApplicationModelConverter converter = getApplicationModelConverter(serviceAccessor.getPageService());
        final ApplicationService applicationService = serviceAccessor.getApplicationService();
        final Optional<SearchFilter> filterOnUserId = searchOptions.getFilters().stream()
                .filter(s -> s.getField().equals(USER_ID)).findFirst();
        if (filterOnUserId.isPresent()) {
            final SearchOptionsBuilder searchOptionsWithoutUserId = new SearchOptionsBuilder(searchOptions)
                    .setFilters(searchOptions.getFilters().stream().filter(s -> !s.getField().equals(USER_ID))
                            .collect(Collectors.toList()));
            final String userId = String.valueOf(filterOnUserId.get().getValue());
            if (String.valueOf(SessionService.SYSTEM_ID).equals(userId)) { // It's the tenant admin user
                return getLivingApplicationAPIDelegate()
                        .searchIApplications(SearchApplications.defaultSearchApplications(
                                applicationService, appSearchDescriptor,
                                searchOptionsWithoutUserId
                                        .filter(APPLICATION_VISIBILITY, INTERNAL_PROFILE_SUPER_ADMIN.getProfileName())
                                        .done(),
                                converter));
            } else {
                return getLivingApplicationAPIDelegate()
                        .searchIApplications(SearchApplicationsOfUser.defaultSearchApplicationsOfUser(
                                Long.parseLong(userId), applicationService, appSearchDescriptor,
                                searchOptionsWithoutUserId.done(), converter));
            }
        }
        return getLivingApplicationAPIDelegate()
                .searchIApplications(
                        SearchApplications.defaultSearchApplications(applicationService, appSearchDescriptor,
                                searchOptions, converter));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated(since = "9.0.0")
    public ApplicationPage createApplicationPage(final long applicationId, final long pageId, final String token)
            throws CreationException {
        return getApplicationPageAPIDelegate().createApplicationPage(applicationId, pageId, token);
    }

    @Override
    public ApplicationPage getApplicationPage(final String applicationName, final String applicationPageToken)
            throws ApplicationPageNotFoundException {
        return getApplicationPageAPIDelegate().getApplicationPage(applicationName, applicationPageToken);
    }

    @Override
    public SearchResult<ApplicationPage> searchApplicationPages(final SearchOptions searchOptions)
            throws SearchException {
        final ServiceAccessor serviceAccessor = getServiceAccessor();
        final SearchApplicationPageDescriptor appPageSearchDescriptor = serviceAccessor.getSearchEntitiesDescriptor()
                .getSearchApplicationPageDescriptor();
        final ApplicationPageModelConverter converter = new ApplicationPageModelConverter();
        final ApplicationService applicationService = serviceAccessor.getApplicationService();
        final SearchApplicationPages searchApplicationPages = new SearchApplicationPages(applicationService, converter,
                appPageSearchDescriptor, searchOptions);
        return getApplicationPageAPIDelegate().searchApplicationPages(searchApplicationPages);
    }

    @Override
    public ApplicationPage getApplicationPage(final long applicationPageId) throws ApplicationPageNotFoundException {
        return getApplicationPageAPIDelegate().getApplicationPage(applicationPageId);
    }

    @Override
    public void deleteApplicationPage(final long applicationPageId) throws DeletionException {
        getApplicationPageAPIDelegate().deleteApplicationPage(applicationPageId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated(since = "9.0.0")
    public void setApplicationHomePage(final long applicationId, final long applicationPageId)
            throws UpdateException, ApplicationNotFoundException {
        getApplicationPageAPIDelegate().setApplicationHomePage(applicationId, applicationPageId);
    }

    @Override
    public ApplicationPage getApplicationHomePage(final long applicationId) throws ApplicationPageNotFoundException {
        return getApplicationPageAPIDelegate().getApplicationHomePage(applicationId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated(since = "9.0.0")
    public ApplicationMenu createApplicationMenu(final ApplicationMenuCreator applicationMenuCreator)
            throws CreationException {
        return getApplicationMenuAPIDelegate().createApplicationMenu(applicationMenuCreator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated(since = "9.0.0")
    public ApplicationMenu updateApplicationMenu(final long applicationMenuId, final ApplicationMenuUpdater updater)
            throws ApplicationMenuNotFoundException,
            UpdateException {
        return getApplicationMenuAPIDelegate().updateApplicationMenu(applicationMenuId, updater);
    }

    @Override
    public ApplicationMenu getApplicationMenu(final long applicationMenuId) throws ApplicationMenuNotFoundException {
        return getApplicationMenuAPIDelegate().getApplicationMenu(applicationMenuId);
    }

    @Override
    public void deleteApplicationMenu(final long applicationMenuId) throws DeletionException {
        getApplicationMenuAPIDelegate().deleteApplicationMenu(applicationMenuId);
    }

    @Override
    public SearchResult<ApplicationMenu> searchApplicationMenus(final SearchOptions searchOptions)
            throws SearchException {
        final ServiceAccessor serviceAccessor = getServiceAccessor();
        final ApplicationService applicationService = serviceAccessor.getApplicationService();
        final ApplicationMenuModelConverter converter = new ApplicationMenuModelConverter();
        final SearchApplicationMenuDescriptor searchDescriptor = serviceAccessor.getSearchEntitiesDescriptor()
                .getSearchApplicationMenuDescriptor();
        final SearchApplicationMenus searchApplicationMenus = new SearchApplicationMenus(applicationService, converter,
                searchDescriptor, searchOptions);
        return getApplicationMenuAPIDelegate().searchApplicationMenus(searchApplicationMenus);
    }

    @Override
    public List<String> getAllPagesForProfile(final long profileId) {
        return getApplicationPageAPIDelegate().getAllPagesForProfile(profileId);
    }

    @Override
    public List<String> getAllPagesForProfile(String profile) {
        return getApplicationPageAPIDelegate().getAllPagesForProfile(profile);
    }

    @Override
    public byte[] exportApplications(final long... applicationIds) throws ExportException {
        return getLivingApplicationExporterDelegate().exportApplications(applicationIds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated(since = "9.0.0")
    public List<ImportStatus> importApplications(final byte[] xmlContent, final ApplicationImportPolicy policy)
            throws ImportException, AlreadyExistsException {
        return getServiceAccessor().getApplicationImporter().importApplications(xmlContent, null, null,
                SessionInfos.getUserIdFromSession(),
                new StrategySelector().selectStrategy(policy));
    }

    @Override
    public Icon getIconOfApplication(long applicationId) throws ApplicationNotFoundException {
        return getLivingApplicationAPIDelegate().getIconOfApplication(applicationId);
    }
}
