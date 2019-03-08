/**
 * Copyright (C) 2015 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
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
package org.bonitasoft.engine.api.impl.transaction.platform;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bonitasoft.engine.api.impl.TenantConfiguration;
import org.bonitasoft.engine.builder.BuilderFactory;
import org.bonitasoft.engine.commons.exceptions.SBonitaException;
import org.bonitasoft.engine.commons.transaction.TransactionContent;
import org.bonitasoft.engine.connector.ConnectorExecutor;
import org.bonitasoft.engine.log.technical.TechnicalLogSeverity;
import org.bonitasoft.engine.log.technical.TechnicalLoggerService;
import org.bonitasoft.engine.platform.PlatformService;
import org.bonitasoft.engine.scheduler.JobRegister;
import org.bonitasoft.engine.scheduler.SchedulerService;
import org.bonitasoft.engine.scheduler.builder.SJobDescriptorBuilderFactory;
import org.bonitasoft.engine.scheduler.builder.SJobParameterBuilderFactory;
import org.bonitasoft.engine.scheduler.exception.SSchedulerException;
import org.bonitasoft.engine.scheduler.model.SJobDescriptor;
import org.bonitasoft.engine.scheduler.model.SJobParameter;
import org.bonitasoft.engine.scheduler.trigger.Trigger;
import org.bonitasoft.engine.work.WorkService;

/**
 * @author Baptiste Mesta
 * @author Elias Ricken de Medeiros
 */
public final class ActivateTenant implements TransactionContent {

    private final long tenantId;

    private final PlatformService platformService;

    private final SchedulerService schedulerService;

    private final TechnicalLoggerService logger;

    private final WorkService workService;

    private final ConnectorExecutor connectorExecutor;

    private final TenantConfiguration tenantConfiguration;

    public ActivateTenant(final long tenantId, final PlatformService platformService, final SchedulerService schedulerService,
                          final TechnicalLoggerService logger, final WorkService workService, final ConnectorExecutor connectorExecutor,
                          final TenantConfiguration tenantConfiguration) {
        this.tenantId = tenantId;
        this.platformService = platformService;
        this.schedulerService = schedulerService;
        this.logger = logger;
        this.workService = workService;
        this.connectorExecutor = connectorExecutor;
        this.tenantConfiguration = tenantConfiguration;
    }

    @Override
    public void execute() throws SBonitaException {
        final boolean tenantWasActivated = platformService.activateTenant(tenantId);
        // we execute that only if the tenant was not already activated
        if (tenantWasActivated) {
            workService.start();
            connectorExecutor.start();
            final List<JobRegister> jobsToRegister = tenantConfiguration.getJobsToRegister();
            for (final JobRegister jobRegister : jobsToRegister) {
                registerJob(jobRegister);
            }
            schedulerService.resumeJobs(tenantId);
        }
    }

    private void registerJob(final JobRegister jobRegister) {
        try {
            final List<String> jobs = schedulerService.getAllJobs();
            if (!jobs.contains(jobRegister.getJobName())) {
                if (logger.isLoggable(this.getClass(), TechnicalLogSeverity.INFO)) {
                    logger.log(this.getClass(), TechnicalLogSeverity.INFO, "Register " + jobRegister.getJobDescription());
                }
                final SJobDescriptor jobDescriptor = BuilderFactory.get(SJobDescriptorBuilderFactory.class)
                        .createNewInstance(jobRegister.getJobClass().getName(), jobRegister.getJobName(), true).done();
                final ArrayList<SJobParameter> jobParameters = new ArrayList<>();
                for (final Entry<String, Serializable> entry : jobRegister.getJobParameters().entrySet()) {
                    jobParameters.add(BuilderFactory.get(SJobParameterBuilderFactory.class).createNewInstance(entry.getKey(), entry.getValue()).done());
                }
                final Trigger trigger = jobRegister.getTrigger();
                schedulerService.schedule(jobDescriptor, jobParameters, trigger);
            } else {
                logger.log(this.getClass(), TechnicalLogSeverity.INFO, "The " + jobRegister.getJobDescription() + " was already started");
            }
        } catch (final SSchedulerException e) {
            logger.log(this.getClass(), TechnicalLogSeverity.ERROR,
                    "Unable to register job " + jobRegister.getJobDescription() + " because " + e.getMessage());
            if (logger.isLoggable(this.getClass(), TechnicalLogSeverity.DEBUG)) {
                logger.log(this.getClass(), TechnicalLogSeverity.DEBUG, e);
            }
        }
    }

}
