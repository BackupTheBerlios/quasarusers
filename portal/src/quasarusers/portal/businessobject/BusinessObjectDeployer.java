package quasarusers.portal.businessobject;

import org.jboss.system.ServiceMBeanSupport;
import com.sdm.quasar.util.Visitor;
import com.sdm.quasar.util.LocalizedString;
import com.sdm.quasar.util.registry.Registry;
import com.sdm.quasar.util.registry.RegistryObject;
import com.sdm.quasar.xml.XMLParser;
import com.sdm.quasar.xml.Converter;
import com.sdm.quasar.xml.SimpleNode;
import com.sdm.quasar.xml.XMLException;
import com.sdm.quasar.businessobject.BusinessObjectManager;
import com.sdm.quasar.businessobject.AbstractBusinessSystem;
import com.sdm.quasar.lang.Keywords;

import java.net.URL;

import quasarusers.util.businessobject.ViewProperty;

/**
 * Created by IntelliJ IDEA.
 * User: schmickl
 * Date: Feb 15, 2003
 * Time: 6:12:09 PM
 * To change this template use Options | File Templates.
 */
public class BusinessObjectDeployer extends ServiceMBeanSupport implements BusinessObjectDeployerMBean {
    private String resource;

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    protected void startService() throws Exception {
        System.out.println("parsing " + resource);
        new BusinessObjectVisitor().setup(getClass().getClassLoader().getResource(getResource()));
    }

    public static class BusinessObjectVisitor extends Visitor {
        public BusinessObjectVisitor() {
            super("register");
        }

        // local class

        public static class Model extends XMLParser {
            // static

            static IntegerConverter sIntegerConverter = new IntegerConverter();

            // helper classes

            static class IntegerConverter implements Converter {
                public String write(Object o) {
                    return o.toString(); // default
                }

                public Object read(Object v) throws Exception {
                    if (v instanceof Integer)
                        return v;
                    else {
                        String value = (String) v;

                        if (value.length() == 0)
                            return new Integer(0); // avoid exception
                        else
                            return new Integer(Integer.parseInt((String) v));
                    } // else
                }
            }

            // node classes

            public static class BusinessRegistryNode extends SimpleNode {
            }

            public static class NamedNode extends SimpleNode {
                public String name;
                public String label;
                public String documentation;
            }

            public static class ViewPropertyNode extends RegistryNode {
                public Keywords arguments;
            }

            public static class KeyNode extends SimpleNode {
                public String key;
                public Object value;
            }

            public static class RegistryNode extends NamedNode {
                public RegistryObject registryObject;
            }

            public static class BusinessSystemNode extends RegistryNode {
            }

            public static class BusinessComponentNode extends RegistryNode {
                public String parent;
                public String clazz;
            }

            // constructor

            Model() throws XMLException {
                super(false); // not validating

                // classes

                registerClass("business-registry", BusinessRegistryNode.class);

                registerClass("business-system", BusinessSystemNode.class);

                registerField(NamedNode.class, "name" , "name", null, null, true);
                registerField(NamedNode.class, "label" , "label", null, "", true);
                registerField(NamedNode.class, "documentation" , "documentation", null, "", true);


                registerClass("business-component", BusinessComponentNode.class);
                registerField(BusinessComponentNode.class, "parent" , "parent", null, null, true);
                registerField(BusinessComponentNode.class, "class" , "clazz", null, null, true);

                registerClass("view-property", ViewPropertyNode.class);

                registerClass("key", KeyNode.class);
                registerField(KeyNode.class, "key" , "key", null, null, true);
                registerField(KeyNode.class, "value" , "value", null, null, true);
            }


            public static BusinessRegistryNode getConfiguration(URL url) {
                try {
                    Model parser = new Model();

                    parser.parse(url.toString());

                    return (BusinessRegistryNode) parser.getRoot();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.exit(0);
                    return null;
                }
            }
        }

        public static class StandardBusinessSystem extends AbstractBusinessSystem {
            public StandardBusinessSystem(String name, LocalizedString label, LocalizedString documentation) {
                super(name, label, documentation);
            }

            public StandardBusinessSystem(String name, String label, String documentation) {
                super(name, label, documentation);
            }

            public void registerComponents(Registry registry) {
            }
        }


        /**
         * Parses the specified configuration file
         * @param url URL of the configuration file
         */
        public void setup(URL url) {
            try {
                Model parser = new Model();

                parser.parse(url.toString());

                visit(parser.getRoot());
            }
            catch (Throwable throwable) {
                throwable.printStackTrace();
                System.exit(0);
            }
        }

        private Registry getRegistry() {
            return BusinessObjectManager.getBusinessObjectManager().getRegistry();
        }

        // configure

        public void register(SimpleNode node) throws Throwable {
            node.visitChildren(this);
        }

        public void register(Model.BusinessSystemNode node) throws Throwable {
            System.out.println("register " + node.getName());

            node.registryObject = new StandardBusinessSystem(node.name, node.label, node.documentation);

            getRegistry().registerObject(node.registryObject);

            node.visitChildren(this);
        }

        public void register(Model.BusinessComponentNode node) throws Throwable {
            System.out.println("register " + node.getName());

            if (node.clazz != null) {
                node.registryObject = (RegistryObject)Class.forName(node.clazz).newInstance();

                getRegistry().registerComponent(((Model.RegistryNode)node.getParent()).registryObject, node.registryObject);
            }
            node.visitChildren(this);
        }

        public void register(Model.ViewPropertyNode node) throws Throwable {
            System.out.println("register " + node.getName());

            node.arguments = new Keywords();

            node.visitChildren(this);

            ViewProperty viewProperty = new ViewProperty(node.name, node.label, node.documentation, node.arguments, true);

            getRegistry().registerProperty (((Model.RegistryNode)node.getParent()).registryObject, viewProperty);
        }

        public void register(Model.KeyNode node) throws Throwable {
            System.out.println("register " + node.getName());

            ((Model.ViewPropertyNode)node.getParent()).arguments.addValue(node.key, node.value);
        }
    }
}
