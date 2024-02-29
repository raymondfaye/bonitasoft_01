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
package org.bonitasoft.engine.business.application.importer;

import static org.assertj.core.api.Assertions.assertThat;

import org.bonitasoft.engine.business.application.ApplicationImportPolicy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StrategySelectorTest {

    @InjectMocks
    private StrategySelector selector;

    @Test
    public void selectStrategy_should_return_instance_of_FailOnDuplicate_when_policy_is_FailOnDuplicate() {
        //when
        ApplicationImportStrategy strategy = selector.selectStrategy(ApplicationImportPolicy.FAIL_ON_DUPLICATES);

        //then
        assertThat(strategy).isInstanceOf(FailOnDuplicateApplicationImportStrategy.class);
    }

    @Test
    public void selectStrategy_should_return_instance_of_ReplaceDuplicates_when_policy_is_ReplaceDuplicates() {
        //when
        ApplicationImportStrategy strategy = selector.selectStrategy(ApplicationImportPolicy.REPLACE_DUPLICATES);

        //then
        assertThat(strategy).isInstanceOf(ReplaceDuplicateApplicationImportStrategy.class);
    }

}
