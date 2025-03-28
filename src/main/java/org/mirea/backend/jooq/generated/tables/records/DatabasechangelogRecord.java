/*
 * This file is generated by jOOQ.
 */
package org.mirea.backend.jooq.generated.tables.records;


import java.time.LocalDateTime;

import org.jooq.impl.TableRecordImpl;
import org.mirea.backend.jooq.generated.tables.Databasechangelog;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class DatabasechangelogRecord extends TableRecordImpl<DatabasechangelogRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.databasechangelog.id</code>.
     */
    public void setId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.databasechangelog.id</code>.
     */
    public String getId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.databasechangelog.author</code>.
     */
    public void setAuthor(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.databasechangelog.author</code>.
     */
    public String getAuthor() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.databasechangelog.filename</code>.
     */
    public void setFilename(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.databasechangelog.filename</code>.
     */
    public String getFilename() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.databasechangelog.dateexecuted</code>.
     */
    public void setDateexecuted(LocalDateTime value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.databasechangelog.dateexecuted</code>.
     */
    public LocalDateTime getDateexecuted() {
        return (LocalDateTime) get(3);
    }

    /**
     * Setter for <code>public.databasechangelog.orderexecuted</code>.
     */
    public void setOrderexecuted(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.databasechangelog.orderexecuted</code>.
     */
    public Integer getOrderexecuted() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>public.databasechangelog.exectype</code>.
     */
    public void setExectype(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.databasechangelog.exectype</code>.
     */
    public String getExectype() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.databasechangelog.md5sum</code>.
     */
    public void setMd5sum(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.databasechangelog.md5sum</code>.
     */
    public String getMd5sum() {
        return (String) get(6);
    }

    /**
     * Setter for <code>public.databasechangelog.description</code>.
     */
    public void setDescription(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.databasechangelog.description</code>.
     */
    public String getDescription() {
        return (String) get(7);
    }

    /**
     * Setter for <code>public.databasechangelog.comments</code>.
     */
    public void setComments(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.databasechangelog.comments</code>.
     */
    public String getComments() {
        return (String) get(8);
    }

    /**
     * Setter for <code>public.databasechangelog.tag</code>.
     */
    public void setTag(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>public.databasechangelog.tag</code>.
     */
    public String getTag() {
        return (String) get(9);
    }

    /**
     * Setter for <code>public.databasechangelog.liquibase</code>.
     */
    public void setLiquibase(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>public.databasechangelog.liquibase</code>.
     */
    public String getLiquibase() {
        return (String) get(10);
    }

    /**
     * Setter for <code>public.databasechangelog.contexts</code>.
     */
    public void setContexts(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>public.databasechangelog.contexts</code>.
     */
    public String getContexts() {
        return (String) get(11);
    }

    /**
     * Setter for <code>public.databasechangelog.labels</code>.
     */
    public void setLabels(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>public.databasechangelog.labels</code>.
     */
    public String getLabels() {
        return (String) get(12);
    }

    /**
     * Setter for <code>public.databasechangelog.deployment_id</code>.
     */
    public void setDeploymentId(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>public.databasechangelog.deployment_id</code>.
     */
    public String getDeploymentId() {
        return (String) get(13);
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached DatabasechangelogRecord
     */
    public DatabasechangelogRecord() {
        super(Databasechangelog.DATABASECHANGELOG);
    }

    /**
     * Create a detached, initialised DatabasechangelogRecord
     */
    public DatabasechangelogRecord(String id, String author, String filename, LocalDateTime dateexecuted, Integer orderexecuted, String exectype, String md5sum, String description, String comments, String tag, String liquibase, String contexts, String labels, String deploymentId) {
        super(Databasechangelog.DATABASECHANGELOG);

        setId(id);
        setAuthor(author);
        setFilename(filename);
        setDateexecuted(dateexecuted);
        setOrderexecuted(orderexecuted);
        setExectype(exectype);
        setMd5sum(md5sum);
        setDescription(description);
        setComments(comments);
        setTag(tag);
        setLiquibase(liquibase);
        setContexts(contexts);
        setLabels(labels);
        setDeploymentId(deploymentId);
        resetChangedOnNotNull();
    }
}
