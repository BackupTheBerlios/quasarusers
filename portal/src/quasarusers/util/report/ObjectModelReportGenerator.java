package quasarusers.util.report;

import com.sdm.quasar.dataview.model.FilterPart;
import com.sdm.quasar.dataview.model.SimpleSearchModel;
import com.sdm.quasar.dataview.server.DataIterator;
import com.sdm.quasar.dataview.server.model.QueryModel;
import com.sdm.quasar.dataview.server.model.ColumnModel;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.modelview.server.model.AttributeModel;
import com.sdm.quasar.modelview.server.model.ObjectModel;
import com.sdm.quasar.modelview.server.model.RelationshipModel;
import com.sdm.quasar.util.Strings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.text.DateFormat;
import java.util.Locale;

/**
 * @author Marco Schmickler
 */

//todo dpk 23/01/2003 -> MC kommentieren

public class ObjectModelReportGenerator {
  public Object generateReport(Object object,
                               ObjectModel objectModel,
                               Keywords arguments) throws Exception {
    Document document = buildDocument(object, objectModel);

    String styleName = (String) arguments.getValue("style");
    String fileName = (String) arguments.getValue("file");

    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    StreamResult result = new StreamResult(outStream);

    transform(document, styleName, result);

    final byte[] bytes = outStream.toByteArray();

    outStream.close();

    if (fileName != null) {
      OutputStream fileStream = new FileOutputStream(fileName);

      fileStream.write(bytes);
      fileStream.close();
    }

    return bytes;

  }

  private void transform(Document document,
                         String styleName,
                         Result result) throws TransformerException {
    Transformer transformer;

    if (styleName != null) {
      InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(styleName);

      transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(resourceStream));
    } else
      transformer = TransformerFactory.newInstance().newTransformer();

    transformer.transform(new DOMSource(document), result);
  }

  private void dump(Document document, String fileName) throws Exception {
    OutputStream outStream = new FileOutputStream(fileName);
    Transformer transformer = TransformerFactory.newInstance().newTransformer();

    transformer.transform(new DOMSource(document), new StreamResult(outStream));
  }

  protected Document buildDocument(Object object,
                                   ObjectModel objectModel) throws Exception {
    Document document
            = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

    Element reportNode = document.createElement("report");
    String today = DateFormat.getDateInstance().format(
            new Date(System.currentTimeMillis()));

    reportNode.setAttribute("date", today);

    buildObjectModelNode(document, reportNode, objectModel);
    buildObjectNode(document, reportNode, object, objectModel);

    document.appendChild(reportNode);

    return document;
  }

  protected void buildObjectModelNode(Document document,
                                      Element parent,
                                      ObjectModel objectModel) throws Exception {
    Locale locale = Locale.getDefault();
    Element objectModelNode = document.createElement("ObjectModel");

    objectModelNode.setAttribute("documentation", objectModel.getDocumentation(locale));
    objectModelNode.setAttribute("label", objectModel.getLabel(locale));
    objectModelNode.setAttribute("name", objectModel.getName());

    AttributeModel[] attributeModels = objectModel.getAttributeModels(true);

    for (int i = 0; i < attributeModels.length; i++) {
      AttributeModel attributeModel = attributeModels[i];

      if (attributeModel.isVisible()) {
        Element element = document.createElement("AttributeModel");

        element.setAttribute("label", attributeModel.getLabel(locale));
        element.setAttribute("name", attributeModel.getName());

        objectModelNode.appendChild(element);
      }
    }

    RelationshipModel[] relationshipModels = objectModel.getRelationshipModels(true);

    for (int i = 0; i < relationshipModels.length; i++) {
      RelationshipModel relationshipModel = relationshipModels[i];

      Element element = document.createElement("RelationshipModel");

      element.setAttribute("name", relationshipModel.getName());
      buildObjectModelNode(document, element, relationshipModel.getRelatedObjectModel());

      objectModelNode.appendChild(element);
    }

    parent.appendChild(objectModelNode);
  }

  protected void buildObjectNode(Document document,
                                 Element parent,
                                 Object object,
                                 ObjectModel objectModel) throws Exception {
    if (object == null)
      return;

    String name = objectModel.getName();
    name = Strings.split(name, ' ')[0];

    Element objectNode = document.createElement(name);
    AttributeModel[] attributeModels = objectModel.getAttributeModels(true);

    for (int i = 0; i < attributeModels.length; i++) {
      if (attributeModels[i].isVisible()) {
        Object value = attributeModels[i].getValue(object);

        if (value instanceof Date)
          value = DateFormat.getDateInstance().format((Date) value);
        else if (value != null)
          value = value.toString();

        objectNode.setAttribute(attributeModels[i].getName(), (String) value);
      }
    }

    RelationshipModel[] relationshipModels = objectModel.getRelationshipModels(true);

    for (int i = 0; i < relationshipModels.length; i++) {
      RelationshipModel relationshipModel = relationshipModels[i];
      Element relationNode = document.createElement(relationshipModel.getName());

      objectNode.appendChild(relationNode);

      ObjectModel relatedModel = relationshipModel.getRelatedObjectModel();

      if (relationshipModel.isSingleValued())
        buildObjectNode(document,
                        relationNode,
                        relationshipModel.getValue(object),
                        relatedModel);
    }

    parent.appendChild(objectNode);
  }

  protected void buildDataModelNode(Document document,
                                      Element parent,
                                      QueryModel objectModel) throws Exception {
    Locale locale = Locale.getDefault();
    Element objectModelNode = document.createElement("ObjectModel");

    objectModelNode.setAttribute("documentation", objectModel.getDocumentation(locale));
    objectModelNode.setAttribute("label", objectModel.getLabel(locale));
    objectModelNode.setAttribute("name", objectModel.getName());

    ColumnModel[] columnModels = objectModel.getColumnModels();

    for (int i = 0; i < columnModels.length; i++) {
      ColumnModel columnModel = columnModels[i];

      if (columnModel.isVisible()) {
        Element element = document.createElement("AttributeModel");

        element.setAttribute("label", columnModel.getLabel(locale));
        element.setAttribute("name", columnModel.getName());

        objectModelNode.appendChild(element);
      }
    }

    parent.appendChild(objectModelNode);
  }

  protected void buildDataNode(Document document,
                                 Element parent,
                                 Object object,
                                 QueryModel objectModel) throws Exception {
    if (object == null)
      return;

    String name = objectModel.getName();
    name = Strings.split(name, ' ')[0];

    Element objectNode = document.createElement(name);
    ColumnModel[] columnModels = objectModel.getColumnModels();

    for (int i = 0; i < columnModels.length; i++) {
      if (columnModels[i].isVisible()) {
        Object value = columnModels[i].getValue(object);

        if (value instanceof Date)
          value = DateFormat.getDateInstance().format((Date) value);
        else if (value != null)
          value = value.toString();

        objectNode.setAttribute(columnModels[i].getName(), (String) value);
      }
    }

    parent.appendChild(objectNode);
  }

  protected DataIterator makeDataIterator(QueryModel queryModel,
                                          Keywords arguments) {
    String className = queryModel.getDataIteratorClassName();

    try {
      Class iteratorClass = Class.forName(className);
      Constructor constructor
              = iteratorClass.getConstructor(new Class[]{QueryModel.class,
                                                         Keywords.class});
      DataIterator dataIterator
              = (DataIterator) constructor.newInstance(new Object[]{queryModel,
                                                                    arguments});

      return dataIterator;
    } catch (InvocationTargetException e) {
      throw new RuntimeException("Cannot instantiate class " + className);
    } catch (InstantiationException e) {
      throw new RuntimeException("Cannot instantiate class " + className);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Cannot instantiate class " + className);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Cannot instantiate class " + className);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Class " + className + " not found");
    }
  }

  protected void buildEmbeddedDataView(Document document,
                                       String name,
                                       QueryModel model,
                                       String filter,
                                       Object[] parameters) throws Exception {
    Node reportNode = document.getFirstChild();
    Element dataViewNode = document.createElement("EmbeddedDataView");

    dataViewNode.setAttribute("name", name);
    reportNode.appendChild(dataViewNode);
    buildDataModelNode(document, dataViewNode, model);

    Element dataNode = document.createElement("data");

    dataViewNode.appendChild(dataNode);

    final FilterPart part = new FilterPart(filter, parameters);

    final SimpleSearchModel searchModel = new SimpleSearchModel("",
                                                                "",
                                                                "",
                                                                part);
    DataIterator iterator = makeDataIterator(model,
                                             new Keywords("search", searchModel));

    iterator.start();

    while (iterator.hasNext()) {
        buildDataNode(document, dataNode, iterator.next(), model);
    }
  }
}
