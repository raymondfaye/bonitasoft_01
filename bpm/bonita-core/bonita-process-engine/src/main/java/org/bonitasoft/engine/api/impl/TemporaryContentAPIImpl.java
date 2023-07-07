/**
 * Copyright (C) 2023 Bonitasoft S.A.
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

import lombok.extern.slf4j.Slf4j;
import org.bonitasoft.engine.api.TemporaryContentAPI;
import org.bonitasoft.engine.commons.exceptions.SObjectNotFoundException;
import org.bonitasoft.engine.exception.BonitaRuntimeException;
import org.bonitasoft.engine.io.FileContent;
import org.bonitasoft.engine.io.TemporaryFileNotFoundException;
import org.bonitasoft.engine.service.ServiceAccessor;
import org.bonitasoft.engine.service.impl.ServiceAccessorFactory;
import org.bonitasoft.engine.temporary.content.STemporaryContent;
import org.bonitasoft.engine.temporary.content.TemporaryContentService;
import org.bonitasoft.engine.transaction.TransactionService;

@Slf4j
public class TemporaryContentAPIImpl implements TemporaryContentAPI {

    private final TemporaryContentService temporaryContentService;
    private final TransactionService transactionService;

    public TemporaryContentAPIImpl() {
        this.temporaryContentService = getServiceAccessor().getTemporaryContentService();
        this.transactionService = getServiceAccessor().getTransactionService();
    }

    protected ServiceAccessor getServiceAccessor() {
        try {
            return ServiceAccessorFactory.getInstance().createServiceAccessor();
        } catch (final Exception e) {
            throw new BonitaRuntimeException(e);
        }
    }

    @Override
    public String storeTempFile(FileContent fileContent) {
        try {
            return transactionService
                    .executeInTransaction(
                            () -> temporaryContentService.add(fileContent.getFileName(), fileContent.getInputStream(),
                                    fileContent.getMimeType()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileContent retrieveTempFile(String tempFileKey) throws TemporaryFileNotFoundException {
        try {
            STemporaryContent temporaryContent = transactionService
                    .executeInTransaction(() -> temporaryContentService.get(tempFileKey));
            return new FileContent(temporaryContent.getFileName(), temporaryContent.getContent().getBinaryStream(),
                    temporaryContent.getMimeType(), temporaryContent.getContent().length());
        } catch (SObjectNotFoundException e) {
            throw new TemporaryFileNotFoundException(e);
        } catch (Exception e) {
            throw new BonitaRuntimeException(e);
        }
    }

    @Override
    public void removeTempFile(String tempFileKey) throws TemporaryFileNotFoundException {
        try {
            transactionService
                    .executeInTransaction(() -> {
                        temporaryContentService.remove(temporaryContentService.get(tempFileKey));
                        return null;
                    });
        } catch (SObjectNotFoundException e) {
            throw new TemporaryFileNotFoundException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
