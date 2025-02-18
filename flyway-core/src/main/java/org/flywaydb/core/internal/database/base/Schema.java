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
package org.flywaydb.core.internal.database.base;

import lombok.CustomLog;
import org.flywaydb.core.internal.exception.FlywaySqlException;
import org.flywaydb.core.internal.jdbc.JdbcTemplate;
import org.flywaydb.core.internal.jdbc.JdbcUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@CustomLog
public abstract class Schema<D extends Database, T extends Table> {
    protected final JdbcTemplate jdbcTemplate;
    protected final D database;
    protected final String name;

    /**
     * @param jdbcTemplate The Jdbc Template for communicating with the DB.
     * @param database The database-specific support.
     * @param name The name of the schema.
     */
    public Schema(JdbcTemplate jdbcTemplate, D database, String name) {
        this.jdbcTemplate = jdbcTemplate;
        this.database = database;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean exists() {
        try {
            return doExists();
        } catch (SQLException e) {
            throw new FlywaySqlException("Unable to check whether schema " + this + " exists", e);
        }
    }

    /**
     * Checks whether this schema exists.
     *
     * @throws SQLException when the check failed.
     */
    protected abstract boolean doExists() throws SQLException;

    public boolean empty() {
        try {
            return doEmpty();
        } catch (SQLException e) {
            throw new FlywaySqlException("Unable to check whether schema " + this + " is empty", e);
        }
    }

    /**
     * Checks whether this schema is empty.
     *
     * @throws SQLException when the check failed.
     */
    protected abstract boolean doEmpty() throws SQLException;

    /**
     * Creates this schema in the database.
     */
    public void create() {
        try {
            LOG.info("Creating schema " + this + " ...");
            doCreate();
        } catch (SQLException e) {
            throw new FlywaySqlException("Unable to create schema " + this, e);
        }
    }

    /**
     * Creates this schema in the database.
     *
     * @throws SQLException when the creation failed.
     */
    protected abstract void doCreate() throws SQLException;

    /**
     * Drops this schema from the database.
     */
    public void drop() {
        try {
            doDrop();
        } catch (SQLException e) {
            throw new FlywaySqlException("Unable to drop schema " + this, e);
        }
    }

    /**
     * Drops this schema from the database.
     *
     * @throws SQLException when the drop failed.
     */
    protected abstract void doDrop() throws SQLException;

    /**
     * Cleans all the objects in this schema.
     */
    public void clean() {
        try {
            doClean();
        } catch (SQLException e) {
            throw new FlywaySqlException("Unable to clean schema " + this, e);
        }
    }

    /**
     * Cleans all the objects in this schema.
     *
     * @throws SQLException when the clean failed.
     */
    protected abstract void doClean() throws SQLException;

    /**
     * Retrieves all the tables in this schema.
     */
    public T[] allTables() {
        try {
            return doAllTables();
        } catch (SQLException e) {
            throw new FlywaySqlException("Unable to retrieve all tables in schema " + this, e);
        }
    }

    /**
     * Retrieves all the tables in this schema.
     *
     * @throws SQLException when the retrieval failed.
     */
    protected abstract T[] doAllTables() throws SQLException;

    /**
     * Retrieves all the types in this schema.
     */
    protected final Type[] allTypes() {
        ResultSet resultSet = null;
        try {
            resultSet = database.jdbcMetaData.getUDTs(null, name, null, null);

            List<Type> types = new ArrayList<>();
            while (resultSet.next()) {
                types.add(getType(resultSet.getString("TYPE_NAME")));
            }

            return types.toArray(new Type[0]);
        } catch (SQLException e) {
            throw new FlywaySqlException("Unable to retrieve all types in schema " + this, e);
        } finally {
            JdbcUtils.closeResultSet(resultSet);
        }
    }

    /**
     * Retrieves the type with this name in this schema.
     */
    protected Type getType(String typeName) {
        return null;
    }

    /**
     * Retrieves the table with this name in this schema.
     */
    public abstract Table getTable(String tableName);

    /**
     * Retrieves the function with this name in this schema.
     */
    public Function getFunction(String functionName, String... args) {
        throw new UnsupportedOperationException("getFunction()");
    }

    /**
     * Retrieves all the functions in this schema.
     */
    protected final Function[] allFunctions() {
        try {
            return doAllFunctions();
        } catch (SQLException e) {
            throw new FlywaySqlException("Unable to retrieve all functions in schema " + this, e);
        }
    }

    /**
     * Retrieves all the functions in this schema.
     *
     * @throws SQLException when the retrieval failed.
     */
    protected Function[] doAllFunctions() throws SQLException {
        return new Function[0];
    }

    /**
     * @return The quoted name of this schema.
     */
    @Override
    public String toString() {
        return database.quote(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Schema schema = (Schema) o;
        return name.equals(schema.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
