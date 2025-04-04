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
package org.flywaydb.core.api;

import org.flywaydb.core.extensibility.MigrationType;

import java.util.Date;
import org.flywaydb.core.internal.schemahistory.SchemaHistory;

/**
 * Info about a migration.
 */
public interface MigrationInfo extends Comparable<MigrationInfo> {
    /**
     * @return The type of migration (BASELINE, SQL, JDBC, ...)
     */
    MigrationType getType();

    /**
     * @return The target version of this migration.
     */
    Integer getChecksum();

    default boolean isVersioned() {
        return getVersion() != null;
    }

    /**
     * @return The schema version after the migration is complete.
     */
    MigrationVersion getVersion();

    /**
     * @return The description of the migration.
     */
    String getDescription();

    /**
     * @return The name of the script to execute for this migration, relative to its classpath or filesystem location.
     */
    String getScript();

    /**
     * @return The state of the migration (PENDING, SUCCESS, ...)
     */
    MigrationState getState();

    /**
     * @return The timestamp when this migration was installed. (Only for applied migrations)
     */
    Date getInstalledOn();

    /**
     * @return The user that installed this migration. (Only for applied migrations)
     */
    String getInstalledBy();

    /**
     * @return The rank of this installed migration. This is the most precise way to sort applied migrations by installation order.
     * Migrations that were applied later have a higher rank. (Only for applied migrations)
     */
    Integer getInstalledRank();

    /**
     * @return The execution time (in millis) of this migration. (Only for applied migrations)
     */
    Integer getExecutionTime();

    /**
     * @return The physical location of the migration on disk.
     */
    String getPhysicalLocation();

    /**
     * @return The result between a comparison of these MigrationInfo's versions.
     */
    int compareVersion(MigrationInfo o);

    /**
     * @return The shouldExecute expression if present and supported by the migration type. Otherwise {@code null}.
     */
    default String getShouldExecuteExpression() { return null; }

    default boolean isShouldExecute() {return true; }
    
    default boolean isRepeatable() { return getVersion() == null; }
    
    default boolean isChecksumMatching() {
        return getResolvedChecksum() == null || getAppliedChecksum() == null || getResolvedChecksum().equals(getAppliedChecksum());
    }
    
    default boolean isDescriptionMatching() {
        return getResolvedDescription() == null || getAppliedDescription() == null || getResolvedDescription().equals(getAppliedDescription())
            || SchemaHistory.NO_DESCRIPTION_MARKER.equals(getAppliedDescription()) && getResolvedDescription().isEmpty();
    }

    default boolean isTypeMatching() {
        return getResolvedType() == null || getAppliedType() == null || getResolvedType().equals(getAppliedType());
    }

    default Boolean isPlaceholderReplacement() {
        return true;
    }

    default Integer getResolvedChecksum() { return null; }
    default Integer getAppliedChecksum() { return null; }
    
    default String getResolvedDescription() { return null; }
    default String getAppliedDescription() { return null; }

    default MigrationType getResolvedType() { return null; }
    default MigrationType getAppliedType() { return null; }

    default boolean isApplied() {
        return getInstalledRank() != null;
    }
}
