/*******************************************************************************
 * Copyright (C) 2014 BonitaSoft S.A.
 * BonitaSoft is a trademark of BonitaSoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * BonitaSoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or BonitaSoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 ******************************************************************************/

package com.bonitasoft.engine.business.application.model.builder;

import org.bonitasoft.engine.recorder.model.EntityUpdateDescriptor;

/**
 * @author Elias Ricken de Medeiros
 */
public interface SApplicationMenuUpdateBuilder {

    EntityUpdateDescriptor done();

    SApplicationMenuUpdateBuilder updateDisplayName(String displayName);

    SApplicationMenuUpdateBuilder updateApplicationPageId(Long applicationPageId);

    SApplicationMenuUpdateBuilder updateIndex(int index);

    SApplicationMenuUpdateBuilder updateParentId(Long parentId);



}
