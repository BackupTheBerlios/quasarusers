package reisekosten;


import com.sdm.quasar.persistence.*;
import com.sdm.quasar.persistence.implementation.*;
public class Kostenpunkt extends AbstractPersistent {
//<< created Sat Feb 22 15:05:53 CET 2003 with JavaGenerator 1.6
	public static final String OID = "oid";
	static PropertyModel sOidModel;
	public java.lang.Long oid;
	public long getOid() throws PersistenceException {
		if (sOidModel == null)
			try {
				sOidModel = getTypeModel().getPropertyModel(OID);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		return ((java.lang.Long)sOidModel.getValue(this)).longValue();
	}
	public java.lang.Long getOidObject() throws PersistenceException {
		if (sOidModel == null)
			try {
				sOidModel = getTypeModel().getPropertyModel(OID);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		return (java.lang.Long)sOidModel.getValue(this);
	}
	public static final String BETRAG = "betrag";
	static PropertyModel sBetragModel;
	public reisekosten.Money betrag;
	public reisekosten.Money getBetrag() throws PersistenceException {
		if (sBetragModel == null)
			try {
				sBetragModel = getTypeModel().getPropertyModel(BETRAG);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		return (reisekosten.Money)sBetragModel.getValue(this);
	}
	public void setBetrag(reisekosten.Money value) throws PersistenceException {
		if (sBetragModel == null)
			try {
				sBetragModel = getTypeModel().getPropertyModel(BETRAG);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		sBetragModel.setValue(this, value);
	}
	public static final String REISE = "reise";
	static PropertyModel sReiseModel;
	public Object reise;
	public reisekosten.Reise getReise() throws PersistenceException {
		if (sReiseModel == null)
			try {
				sReiseModel = getTypeModel().getPropertyModel(REISE);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		return (reisekosten.Reise)sReiseModel.getValue(this);
	}
	public void setReise(reisekosten.Reise value) throws PersistenceException {
		if (sReiseModel == null)
			try {
				sReiseModel = getTypeModel().getPropertyModel(REISE);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		sReiseModel.setValue(this, value);
	}

//>>
}
