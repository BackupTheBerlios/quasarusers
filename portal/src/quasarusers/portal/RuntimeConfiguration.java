/*
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: 10.03.2002
 * Time: 09:20:10
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package quasarusers.portal;

/**
 * @author Marco Schmickler
 */

//todo dpk 28.01.03 -> MC kommentieren

public interface RuntimeConfiguration {
    /**
	 * Installs and initializes the authorization manager.
	 */
    public abstract void initialize(ConfigurableRuntime runtime);

    public abstract void registerManagers();

    public abstract void startup();
}
