package reisekosten;


import com.sdm.quasar.persistence.*;
import com.sdm.quasar.persistence.implementation.*;
public class Fahrtkosten extends reisekosten.Kostenpunkt {
//<< created Sat Feb 22 15:05:53 CET 2003 with JavaGenerator 1.6
	public static final String WEG = "weg";
	static PropertyModel sWegModel;
	public java.lang.String weg;
	public java.lang.String getWeg() throws PersistenceException {
		if (sWegModel == null)
			try {
				sWegModel = getTypeModel().getPropertyModel(WEG);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		return (java.lang.String)sWegModel.getValue(this);
	}
	public void setWeg(java.lang.String value) throws PersistenceException {
		if (sWegModel == null)
			try {
				sWegModel = getTypeModel().getPropertyModel(WEG);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		sWegModel.setValue(this, value);
	}
	public static final String ENTFERNUNG = "entfernung";
	static PropertyModel sEntfernungModel;
	public java.lang.Double entfernung;
	public double getEntfernung() throws PersistenceException {
		if (sEntfernungModel == null)
			try {
				sEntfernungModel = getTypeModel().getPropertyModel(ENTFERNUNG);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		return ((java.lang.Double)sEntfernungModel.getValue(this)).doubleValue();
	}
	public java.lang.Double getEntfernungObject() throws PersistenceException {
		if (sEntfernungModel == null)
			try {
				sEntfernungModel = getTypeModel().getPropertyModel(ENTFERNUNG);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		return (java.lang.Double)sEntfernungModel.getValue(this);
	}
	public void setEntfernung(double value) throws PersistenceException {
		if (sEntfernungModel == null)
			try {
				sEntfernungModel = getTypeModel().getPropertyModel(ENTFERNUNG);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		sEntfernungModel.setValue(this, new java.lang.Double(value));
	}
	public void setEntfernung(java.lang.Double value) throws PersistenceException {
		if (sEntfernungModel == null)
			try {
				sEntfernungModel = getTypeModel().getPropertyModel(ENTFERNUNG);
			}
			catch(Exception exception) {
				throw new RuntimeException("panic");
			}

		sEntfernungModel.setValue(this, value);
	}

//>>
}
