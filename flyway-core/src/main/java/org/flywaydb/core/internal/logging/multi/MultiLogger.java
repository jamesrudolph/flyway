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
package org.flywaydb.core.internal.logging.multi;

import lombok.RequiredArgsConstructor;
import org.flywaydb.core.api.logging.Log;

import java.util.List;

/**
 * Log implementation that forwards method calls to multiple implementations
 */
@RequiredArgsConstructor
public class MultiLogger implements Log {

    private final List<Log> logs;

    @Override
    public boolean isDebugEnabled() {
        for (Log log : logs) {
            if (!log.isDebugEnabled()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void debug(String message) {
        for (Log log : logs) {
            log.debug(message);
        }
    }

    @Override
    public void info(String message) {
        for (Log log : logs) {
            log.info(message);
        }
    }

    @Override
    public void warn(String message) {
        for (Log log : logs) {
            log.warn(message);
        }
    }

    @Override
    public void error(String message) {
        for (Log log : logs) {
            log.error(message);
        }
    }

    @Override
    public void error(String message, Exception e) {
        for (Log log : logs) {
            log.error(message, e);
        }
    }

    @Override
    public void notice(String message) {
        for (Log log : logs) {
            log.notice(message);
        }
    }
}
