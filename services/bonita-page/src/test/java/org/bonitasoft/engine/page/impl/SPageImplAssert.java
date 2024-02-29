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
package org.bonitasoft.engine.page.impl;

import static java.lang.String.format;

import org.assertj.core.api.AbstractAssert;
import org.bonitasoft.engine.page.SPage;

/**
 * {@link SPage} specific assertions - Generated by CustomAssertionGenerator.
 */
public class SPageImplAssert extends AbstractAssert<SPageImplAssert, SPage> {

    /**
     * Creates a new </code>{@link SPageImplAssert}</code> to make assertions on actual SPageImpl.
     *
     * @param actual the SPageImpl we want to make assertions on.
     */
    public SPageImplAssert(SPage actual) {
        super(actual, SPageImplAssert.class);
    }

    /**
     * An entry point for SPageImplAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
     * With a static import, one's can write directly : <code>assertThat(mySPageImpl)</code> and get specific assertion
     * with code completion.
     *
     * @param actual the SPageImpl we want to make assertions on.
     * @return a new </code>{@link SPageImplAssert}</code>
     */
    public static SPageImplAssert assertThat(SPage actual) {
        return new SPageImplAssert(actual);
    }

    /**
     * Verifies that the actual SPageImpl's contentName is equal to the given one.
     *
     * @param contentName the given contentName to compare the actual SPageImpl's contentName to.
     * @return this assertion object.
     * @throws AssertionError - if the actual SPageImpl's contentName is not equal to the given one.
     */
    public SPageImplAssert hasContentName(String contentName) {
        // check that actual SPageImpl we want to make assertions on is not null.
        isNotNull();

        // we overrides the default error message with a more explicit one
        String errorMessage = format("\nExpected <%s> contentName to be:\n  <%s>\n but was:\n  <%s>", actual,
                contentName, actual.getContentName());

        // check
        if (!actual.getContentName().equals(contentName)) {
            throw new AssertionError(errorMessage);
        }

        // return the current assertion for method chaining
        return this;
    }

    /**
     * Verifies that the actual SPageImpl's contentType is equal to the given one.
     *
     * @param contentType the given contentType to compare the actual SPageImpl's contentType to.
     * @return this assertion object.
     * @throws AssertionError - if the actual SPageImpl's contentType is not equal to the given one.
     */
    public SPageImplAssert hasContentType(String contentType) {
        // check that actual SPageImpl we want to make assertions on is not null.
        isNotNull();

        // we overrides the default error message with a more explicit one
        String errorMessage = format("\nExpected <%s> contentType to be:\n  <%s>\n but was:\n  <%s>", actual,
                contentType, actual.getContentType());

        // check
        if (!actual.getContentType().equals(contentType)) {
            throw new AssertionError(errorMessage);
        }

        // return the current assertion for method chaining
        return this;
    }

    /**
     * Verifies that the actual SPageImpl's description is equal to the given one.
     *
     * @param description the given description to compare the actual SPageImpl's description to.
     * @return this assertion object.
     * @throws AssertionError - if the actual SPageImpl's description is not equal to the given one.
     */
    public SPageImplAssert hasDescription(String description) {
        // check that actual SPageImpl we want to make assertions on is not null.
        isNotNull();

        // we overrides the default error message with a more explicit one
        String errorMessage = format("\nExpected <%s> description to be:\n  <%s>\n but was:\n  <%s>", actual,
                description, actual.getDescription());

        // check
        if (!actual.getDescription().equals(description)) {
            throw new AssertionError(errorMessage);
        }

        // return the current assertion for method chaining
        return this;
    }

    /**
     * Verifies that the actual SPageImpl's displayName is equal to the given one.
     *
     * @param displayName the given displayName to compare the actual SPageImpl's displayName to.
     * @return this assertion object.
     * @throws AssertionError - if the actual SPageImpl's displayName is not equal to the given one.
     */
    public SPageImplAssert hasDisplayName(String displayName) {
        // check that actual SPageImpl we want to make assertions on is not null.
        isNotNull();

        // we overrides the default error message with a more explicit one
        String errorMessage = format("\nExpected <%s> displayName to be:\n  <%s>\n but was:\n  <%s>", actual,
                displayName, actual.getDisplayName());

        // check
        if (!actual.getDisplayName().equals(displayName)) {
            throw new AssertionError(errorMessage);
        }

        // return the current assertion for method chaining
        return this;
    }

    /**
     * Verifies that the actual SPageImpl's id is equal to the given one.
     *
     * @param id the given id to compare the actual SPageImpl's id to.
     * @return this assertion object.
     * @throws AssertionError - if the actual SPageImpl's id is not equal to the given one.
     */
    public SPageImplAssert hasId(long id) {
        // check that actual SPageImpl we want to make assertions on is not null.
        isNotNull();

        // we overrides the default error message with a more explicit one
        String errorMessage = format("\nExpected <%s> id to be:\n  <%s>\n but was:\n  <%s>", actual, id,
                actual.getId());

        // check
        if (actual.getId() != id) {
            throw new AssertionError(errorMessage);
        }

        // return the current assertion for method chaining
        return this;
    }

    /**
     * Verifies that the actual SPageImpl's installationDate is equal to the given one.
     *
     * @param installationDate the given installationDate to compare the actual SPageImpl's installationDate to.
     * @return this assertion object.
     * @throws AssertionError - if the actual SPageImpl's installationDate is not equal to the given one.
     */
    public SPageImplAssert hasInstallationDate(long installationDate) {
        // check that actual SPageImpl we want to make assertions on is not null.
        isNotNull();

        // we overrides the default error message with a more explicit one
        String errorMessage = format("\nExpected <%s> installationDate to be:\n  <%s>\n but was:\n  <%s>", actual,
                installationDate, actual.getInstallationDate());

        // check
        if (actual.getInstallationDate() != installationDate) {
            throw new AssertionError(errorMessage);
        }

        // return the current assertion for method chaining
        return this;
    }

    /**
     * Verifies that the actual SPageImpl's installedBy is equal to the given one.
     *
     * @param installedBy the given installedBy to compare the actual SPageImpl's installedBy to.
     * @return this assertion object.
     * @throws AssertionError - if the actual SPageImpl's installedBy is not equal to the given one.
     */
    public SPageImplAssert hasInstalledBy(long installedBy) {
        // check that actual SPageImpl we want to make assertions on is not null.
        isNotNull();

        // we overrides the default error message with a more explicit one
        String errorMessage = format("\nExpected <%s> installedBy to be:\n  <%s>\n but was:\n  <%s>", actual,
                installedBy, actual.getInstalledBy());

        // check
        if (actual.getInstalledBy() != installedBy) {
            throw new AssertionError(errorMessage);
        }

        // return the current assertion for method chaining
        return this;
    }

    /**
     * Verifies that the actual SPageImpl's lastModificationDate is equal to the given one.
     *
     * @param lastModificationDate the given lastModificationDate to compare the actual SPageImpl's lastModificationDate
     *        to.
     * @return this assertion object.
     * @throws AssertionError - if the actual SPageImpl's lastModificationDate is not equal to the given one.
     */
    public SPageImplAssert hasLastModificationDate(long lastModificationDate) {
        // check that actual SPageImpl we want to make assertions on is not null.
        isNotNull();

        // we overrides the default error message with a more explicit one
        String errorMessage = format("\nExpected <%s> lastModificationDate to be:\n  <%s>\n but was:\n  <%s>", actual,
                lastModificationDate, actual.getLastModificationDate());

        // check
        if (actual.getLastModificationDate() != lastModificationDate) {
            throw new AssertionError(errorMessage);
        }

        // return the current assertion for method chaining
        return this;
    }

    /**
     * Verifies that the actual SPageImpl's lastUpdatedBy is equal to the given one.
     *
     * @param lastUpdatedBy the given lastUpdatedBy to compare the actual SPageImpl's lastUpdatedBy to.
     * @return this assertion object.
     * @throws AssertionError - if the actual SPageImpl's lastUpdatedBy is not equal to the given one.
     */
    public SPageImplAssert hasLastUpdatedBy(long lastUpdatedBy) {
        // check that actual SPageImpl we want to make assertions on is not null.
        isNotNull();

        // we overrides the default error message with a more explicit one
        String errorMessage = format("\nExpected <%s> lastUpdatedBy to be:\n  <%s>\n but was:\n  <%s>", actual,
                lastUpdatedBy, actual.getLastUpdatedBy());

        // check
        if (actual.getLastUpdatedBy() != lastUpdatedBy) {
            throw new AssertionError(errorMessage);
        }

        // return the current assertion for method chaining
        return this;
    }

    /**
     * Verifies that the actual SPageImpl's name is equal to the given one.
     *
     * @param name the given name to compare the actual SPageImpl's name to.
     * @return this assertion object.
     * @throws AssertionError - if the actual SPageImpl's name is not equal to the given one.
     */
    public SPageImplAssert hasName(String name) {
        // check that actual SPageImpl we want to make assertions on is not null.
        isNotNull();

        // we overrides the default error message with a more explicit one
        String errorMessage = format("\nExpected <%s> name to be:\n  <%s>\n but was:\n  <%s>", actual, name,
                actual.getName());

        // check
        if (!actual.getName().equals(name)) {
            throw new AssertionError(errorMessage);
        }

        // return the current assertion for method chaining
        return this;
    }

    /**
     * Verifies that the actual SPageImpl's processDefinitionId is equal to the given one.
     *
     * @param processDefinitionId the given processDefinitionId to compare the actual SPageImpl's processDefinitionId
     *        to.
     * @return this assertion object.
     * @throws AssertionError - if the actual SPageImpl's processDefinitionId is not equal to the given one.
     */
    public SPageImplAssert hasProcessDefinitionId(long processDefinitionId) {
        // check that actual SPageImpl we want to make assertions on is not null.
        isNotNull();

        // we overrides the default error message with a more explicit one
        String errorMessage = format("\nExpected <%s> processDefinitionId to be:\n  <%s>\n but was:\n  <%s>", actual,
                processDefinitionId, actual.getProcessDefinitionId());

        // check
        if (actual.getProcessDefinitionId() != processDefinitionId) {
            throw new AssertionError(errorMessage);
        }

        // return the current assertion for method chaining
        return this;
    }

    /**
     * Verifies that the actual SPageImpl is provided.
     *
     * @return this assertion object.
     * @throws AssertionError - if the actual SPageImpl is not provided.
     */
    public SPageImplAssert isProvided() {
        // check that actual SPageImpl we want to make assertions on is not null.
        isNotNull();

        // we overrides the default error message with a more explicit one
        String errorMessage = format("Expected actual SPageImpl to be provided but was not.", actual);

        // check
        if (!actual.isProvided())
            throw new AssertionError(errorMessage);

        // return the current assertion for method chaining
        return this;
    }

    /**
     * Verifies that the actual SPageImpl is not provided.
     *
     * @return this assertion object.
     * @throws AssertionError - if the actual SPageImpl is provided.
     */
    public SPageImplAssert isNotProvided() {
        // check that actual SPageImpl we want to make assertions on is not null.
        isNotNull();

        // we overrides the default error message with a more explicit one
        String errorMessage = format("Expected actual SPageImpl not to be provided but was.", actual);

        // check
        if (actual.isProvided())
            throw new AssertionError(errorMessage);

        // return the current assertion for method chaining
        return this;
    }

    /**
     * Verifies that the actual SPageImpl's tenantId is equal to the given one.
     *
     * @param tenantId the given tenantId to compare the actual SPageImpl's tenantId to.
     * @return this assertion object.
     * @throws AssertionError - if the actual SPageImpl's tenantId is not equal to the given one.
     */
    public SPageImplAssert hasTenantId(long tenantId) {
        // check that actual SPageImpl we want to make assertions on is not null.
        isNotNull();

        // we overrides the default error message with a more explicit one
        String errorMessage = format("\nExpected <%s> tenantId to be:\n  <%s>\n but was:\n  <%s>", actual, tenantId,
                actual.getTenantId());

        // check
        if (actual.getTenantId() != tenantId) {
            throw new AssertionError(errorMessage);
        }

        // return the current assertion for method chaining
        return this;
    }

}
