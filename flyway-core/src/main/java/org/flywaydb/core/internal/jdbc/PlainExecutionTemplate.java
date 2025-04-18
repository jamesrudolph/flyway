/*-
 * ========================LICENSE_START=================================
 * flyway-core
 * ========================================================================
 * Copyright (C) 2010 - 2025 Red Gate Software Ltd
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.flywaydb.core.internal.jdbc;

import lombok.CustomLog;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.exception.FlywaySqlException;

import java.sql.SQLException;
import java.util.concurrent.Callable;

@CustomLog
public class PlainExecutionTemplate implements ExecutionTemplate {
    private final boolean skipErrorLog;

    public PlainExecutionTemplate() {
        this.skipErrorLog = false;
    }

    public PlainExecutionTemplate(boolean skipErrorLog) {
        this.skipErrorLog = skipErrorLog;
    }

    @Override
    public <T> T execute(Callable<T> callback) {
        try {
            LOG.debug("Performing operation in non-transactional context.");
            return callback.call();
        } catch (Exception e) {
            if (!skipErrorLog) {
                LOG.error("Failed to execute operation in non-transactional context. Please restore backups and roll back database and code!");
            }

            if (e instanceof SQLException) {
                throw new FlywaySqlException("Failed to execute operation.", (SQLException) e);
            }

            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }

            throw new FlywayException(e);
        }
    }
}
