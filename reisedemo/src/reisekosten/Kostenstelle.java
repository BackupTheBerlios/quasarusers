package reisekosten;


import com.sdm.quasar.persistence.*;
import com.sdm.quasar.persistence.implementation.*;
public class Kostenstelle extends AbstractPersistent {
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
	public static final String NUMMER = "nummer";
	static PropertyModel sNummerModel;
	public java.lang.Integer nummer;
	public int getNummer() throws PersistenceException {
		if (sNummerModel == null)
			try {
				sNummerModel = getTypeModel().getPropertyModel(NUMMER);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		return ((java.lang.Integer)sNummerModel.getValue(this)).intValue();
	}
	public java.lang.Integer getNummerObject() throws PersistenceException {
		if (sNummerModel == null)
			try {
				sNummerModel = getTypeModel().getPropertyModel(NUMMER);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		return (java.lang.Integer)sNummerModel.getValue(this);
	}
	public void setNummer(int value) throws PersistenceException {
		if (sNummerModel == null)
			try {
				sNummerModel = getTypeModel().getPropertyModel(NUMMER);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		sNummerModel.setValue(this, new java.lang.Integer(value));
	}
	public void setNummer(java.lang.Integer value) throws PersistenceException {
		if (sNummerModel == null)
			try {
				sNummerModel = getTypeModel().getPropertyModel(NUMMER);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		sNummerModel.setValue(this, value);
	}
	public static final String NAME = "name";
	static PropertyModel sNameModel;
	public java.lang.String name;
	public java.lang.String getName() throws PersistenceException {
		if (sNameModel == null)
			try {
				sNameModel = getTypeModel().getPropertyModel(NAME);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		return (java.lang.String)sNameModel.getValue(this);
	}
	public void setName(java.lang.String value) throws PersistenceException {
		if (sNameModel == null)
			try {
				sNameModel = getTypeModel().getPropertyModel(NAME);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		sNameModel.setValue(this, value);
	}
	public static final String REISEN = "reisen";
	static PropertyModel sReisenModel;
	public Object reisen;
	public Relation getReisen() throws PersistenceException {
		if (sReisenModel == null)
			try {
				sReisenModel = getTypeModel().getPropertyModel(REISEN);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		return (Relation)sReisenModel.getValue(this);
	}
	public Relation getReisen(boolean eager) throws PersistenceException {
		if (sReisenModel == null)
			try {
				sReisenModel = getTypeModel().getPropertyModel(REISEN);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		return (Relation)((RelationshipModel)sReisenModel).getValue(this, eager);
	}

//>>
}
