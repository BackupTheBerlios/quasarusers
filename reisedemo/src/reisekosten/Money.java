/*
 * Copyright (c) 2001, 2002. software design & management AG
 * All rights reserved.
 * This file is made available under the terms of the license
 * agreement that accompanies this distribution.
 *
 * $Revision: 1.1 $, last modified $Date: 2003/03/26 19:59:52 $ by $Author: schmickler $
 */
package reisekosten;

import java.io.Serializable;

/**
 * Klasse, die einen Geldbetrag inkl. Währung repräsentiert.
 *
 * @author Martin Fritz
 */
public final class Money implements Serializable {
    /**
     * Betrag
     */
    private final double value;

    /**
     * Währung
     */
    private final String currency;

    /**
     * Konstruktor
     *
     * @param value Betrag
     * @param currency Währung
     */
    public Money(double value, String currency) {
        this.value = value;
        this.currency = currency;
    }

    public final double getValue() {
        return value;
    }

    public final String getCurrency() {
        return currency;
    }

    public final boolean equals(Object object) {
        if (object == this)
            return true;
        else if ((object != null) && (object.getClass() == getClass())) {
            Money money = (Money) object;

            return (value == money.value) && currency.equals(money.currency);
        } else
            return false;
    }

    public final int hashCode() {
        return (int) Double.doubleToLongBits(value);
    }

    public final String toString() {
        return currency + Double.toString(value);
    }
}
