package reisekosten;


import com.sdm.quasar.persistence.*;
import com.sdm.quasar.persistence.implementation.*;
public class Reise extends AbstractPersistent {
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
	public static final String BESCHREIBUNG = "beschreibung";
	static PropertyModel sBeschreibungModel;
	public java.lang.String beschreibung;
	public java.lang.String getBeschreibung() throws PersistenceException {
		if (sBeschreibungModel == null)
			try {
				sBeschreibungModel = getTypeModel().getPropertyModel(BESCHREIBUNG);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		return (java.lang.String)sBeschreibungModel.getValue(this);
	}
	public void setBeschreibung(java.lang.String value) throws PersistenceException {
		if (sBeschreibungModel == null)
			try {
				sBeschreibungModel = getTypeModel().getPropertyModel(BESCHREIBUNG);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		sBeschreibungModel.setValue(this, value);
	}
	public static final String KOSTENPUNKTE = "kostenpunkte";
	static PropertyModel sKostenpunkteModel;
	public Object kostenpunkte;
	public Relation getKostenpunkte() throws PersistenceException {
		if (sKostenpunkteModel == null)
			try {
				sKostenpunkteModel = getTypeModel().getPropertyModel(KOSTENPUNKTE);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		return (Relation)sKostenpunkteModel.getValue(this);
	}
	public Relation getKostenpunkte(boolean eager) throws PersistenceException {
		if (sKostenpunkteModel == null)
			try {
				sKostenpunkteModel = getTypeModel().getPropertyModel(KOSTENPUNKTE);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		return (Relation)((RelationshipModel)sKostenpunkteModel).getValue(this, eager);
	}
	public static final String STARTDATUM = "startDatum";
	static PropertyModel sStartDatumModel;
	public java.sql.Date startDatum;
	public java.sql.Date getStartDatum() throws PersistenceException {
		if (sStartDatumModel == null)
			try {
				sStartDatumModel = getTypeModel().getPropertyModel(STARTDATUM);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		return (java.sql.Date)sStartDatumModel.getValue(this);
	}
	public void setStartDatum(java.sql.Date value) throws PersistenceException {
		if (sStartDatumModel == null)
			try {
				sStartDatumModel = getTypeModel().getPropertyModel(STARTDATUM);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		sStartDatumModel.setValue(this, value);
	}
	public static final String ENDDATUM = "endDatum";
	static PropertyModel sEndDatumModel;
	public java.sql.Date endDatum;
	public java.sql.Date getEndDatum() throws PersistenceException {
		if (sEndDatumModel == null)
			try {
				sEndDatumModel = getTypeModel().getPropertyModel(ENDDATUM);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		return (java.sql.Date)sEndDatumModel.getValue(this);
	}
	public void setEndDatum(java.sql.Date value) throws PersistenceException {
		if (sEndDatumModel == null)
			try {
				sEndDatumModel = getTypeModel().getPropertyModel(ENDDATUM);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		sEndDatumModel.setValue(this, value);
	}
	public static final String MITARBEITER = "mitarbeiter";
	static PropertyModel sMitarbeiterModel;
	public java.lang.String mitarbeiter;
	public java.lang.String getMitarbeiter() throws PersistenceException {
		if (sMitarbeiterModel == null)
			try {
				sMitarbeiterModel = getTypeModel().getPropertyModel(MITARBEITER);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		return (java.lang.String)sMitarbeiterModel.getValue(this);
	}
	public void setMitarbeiter(java.lang.String value) throws PersistenceException {
		if (sMitarbeiterModel == null)
			try {
				sMitarbeiterModel = getTypeModel().getPropertyModel(MITARBEITER);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		sMitarbeiterModel.setValue(this, value);
	}
	public static final String KOSTENSTELLE = "kostenstelle";
	static PropertyModel sKostenstelleModel;
	public Object kostenstelle;
	public reisekosten.Kostenstelle getKostenstelle() throws PersistenceException {
		if (sKostenstelleModel == null)
			try {
				sKostenstelleModel = getTypeModel().getPropertyModel(KOSTENSTELLE);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		return (reisekosten.Kostenstelle)sKostenstelleModel.getValue(this);
	}
	public void setKostenstelle(reisekosten.Kostenstelle value) throws PersistenceException {
		if (sKostenstelleModel == null)
			try {
				sKostenstelleModel = getTypeModel().getPropertyModel(KOSTENSTELLE);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		sKostenstelleModel.setValue(this, value);
	}

//>>
}
