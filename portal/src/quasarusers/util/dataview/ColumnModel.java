package quasarusers.util.dataview;

/*
 * Copyright (c) 2001, 2002
 * software design & management AG / DEUTSCHER INVESTMENT-TRUST Gesellschaft fuer Wertpapieranlagen mbH.
 * All rights reserved.
 * This file is made available under the terms of the license
 * agreement that accompanies this distribution.
 * 
 * $Revision: 1.1 $, last modified $Date: 2003/03/26 19:58:37 $ by $Author: schmickler $
 */

import com.sdm.quasar.util.Assertion;
import com.sdm.quasar.util.LocalizedString;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.modelview.server.model.AttributeModel;

import java.util.Locale;
import java.io.Serializable;

/**
  * The <code>ColumnModel</code> describes the properties of a column of the query.
  * It provides a method to read out the column's value from an Object array
  * that is produced by the query result by using a column index.
  *
  * <p> A column can be marked as belonging to the primary key of a persistent class.
  * This is useful for operations on the lines of the query result to establish
  * a connection to a persistent object.
  *
  * <p> It is based on an <code>AttributeModel</code>, to which all other methods
  * are delegated.
  *
  * @see AttributeModel
 * @version 1.2
  */
public class ColumnModel implements com.sdm.quasar.dataview.server.model.ColumnModel {
    public ColumnModel(String name, AttributeModel attributeModel, int columnIndex, boolean primaryKey) {
        this(name, attributeModel.getLabel(), attributeModel.getDocumentation(), attributeModel.getType(), attributeModel.getTypeArguments(), columnIndex, primaryKey);
    }

    public ColumnModel(AttributeModel attributeModel, int columnIndex, boolean primaryKey) {
        this(attributeModel.getName(), attributeModel.getLabel(), attributeModel.getDocumentation(), attributeModel.getType(), attributeModel.getTypeArguments(), columnIndex, primaryKey);
    }

    public ColumnModel(AttributeModel attributeModel, int columnIndex) {
        this(attributeModel.getName(), attributeModel.getLabel(), attributeModel.getDocumentation(), attributeModel.getType(), attributeModel.getTypeArguments(), columnIndex, false);
    }
    private final String name;
    private final LocalizedString label;
    private final LocalizedString documentation;
    private final int columnIndex;
    private final boolean primaryKey;
    private final Keywords columnArguments;
    private Class type;

    /**
     * Constructs a new ColumnModel.
     *
     * @param   name              the name of the column
     * @param   columnIndex       the position of the column's value in the query result lines
     */
    public ColumnModel(String name, LocalizedString label, LocalizedString documentation, Class type, Keywords columnArguments, int columnIndex, boolean primaryKey) {
        if (Assertion.CHECK) {
            Assertion.checkNotNull(name, "name");
            Assertion.checkNotNull(label, "label");
            Assertion.checkNotNull(documentation, "documentation");
        }

        this.name = name;
        this.label = label;
        this.documentation = documentation;
        this.columnIndex = columnIndex;
        this.primaryKey = primaryKey;
        this.type = type;
        this.columnArguments = columnArguments;
    }

    /**
     * Constructs a new ColumnModel.
     *
     * @param   name              the name of the column
     * @param   columnIndex       the position of the column's value in the query result lines
     */
    public ColumnModel(String name, String label, String documentation, Class type, Keywords columnArguments, int columnIndex, boolean primaryKey) {
        if (Assertion.CHECK) {
            Assertion.checkNotNull(name, "name");
            Assertion.checkNotNull(label, "label");
            Assertion.checkNotNull(documentation, "documentation");
        }

        this.name = name;
        this.label = new LocalizedString(label);
        this.documentation = new LocalizedString(documentation);

        this.columnIndex = columnIndex;
        this.primaryKey = primaryKey;
        this.type = type;
        this.columnArguments = columnArguments;
    }

    public String getName() {
        return name;
    }

    public LocalizedString getLabel() {
        return label;
    }

    public LocalizedString getDocumentation() {
        return documentation;
    }

    public String getLabel(Locale locale) {
        return label.getString(locale);
    }

    public String getDocumentation(Locale locale) {
        return documentation.getString(locale);
    }

    public Class getType() {
        return type;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public boolean isVisible() {
        return !primaryKey;
    }

    public final boolean belongsToPrimaryKey() {
        return primaryKey;
    }

    public Object getValue(Object object) throws Exception {
        if (object instanceof Object[])
            return ((Object[])object)[columnIndex];

        return null;
    }

    public Keywords getTypeArguments() {
        return columnArguments;
    }
}
