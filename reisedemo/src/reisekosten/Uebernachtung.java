package reisekosten;


import com.sdm.quasar.persistence.*;
import com.sdm.quasar.persistence.implementation.*;
public class Uebernachtung extends reisekosten.Kostenpunkt {
//<< created Sat Feb 22 15:05:53 CET 2003 with JavaGenerator 1.6
	public static final String HOTEL = "hotel";
	static PropertyModel sHotelModel;
	public java.lang.String hotel;
	public java.lang.String getHotel() throws PersistenceException {
		if (sHotelModel == null)
			try {
				sHotelModel = getTypeModel().getPropertyModel(HOTEL);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		return (java.lang.String)sHotelModel.getValue(this);
	}
	public void setHotel(java.lang.String value) throws PersistenceException {
		if (sHotelModel == null)
			try {
				sHotelModel = getTypeModel().getPropertyModel(HOTEL);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		sHotelModel.setValue(this, value);
	}
	public static final String ANZAHLNAECHTE = "anzahlNaechte";
	static PropertyModel sAnzahlNaechteModel;
	public java.lang.Double anzahlNaechte;
	public double getAnzahlNaechte() throws PersistenceException {
		if (sAnzahlNaechteModel == null)
			try {
				sAnzahlNaechteModel = getTypeModel().getPropertyModel(ANZAHLNAECHTE);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		return ((java.lang.Double)sAnzahlNaechteModel.getValue(this)).doubleValue();
	}
	public java.lang.Double getAnzahlNaechteObject() throws PersistenceException {
		if (sAnzahlNaechteModel == null)
			try {
				sAnzahlNaechteModel = getTypeModel().getPropertyModel(ANZAHLNAECHTE);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		return (java.lang.Double)sAnzahlNaechteModel.getValue(this);
	}
	public void setAnzahlNaechte(double value) throws PersistenceException {
		if (sAnzahlNaechteModel == null)
			try {
				sAnzahlNaechteModel = getTypeModel().getPropertyModel(ANZAHLNAECHTE);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		sAnzahlNaechteModel.setValue(this, new java.lang.Double(value));
	}
	public void setAnzahlNaechte(java.lang.Double value) throws PersistenceException {
		if (sAnzahlNaechteModel == null)
			try {
				sAnzahlNaechteModel = getTypeModel().getPropertyModel(ANZAHLNAECHTE);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		sAnzahlNaechteModel.setValue(this, value);
	}

//>>
}
