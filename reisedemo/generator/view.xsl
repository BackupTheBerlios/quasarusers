<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.xslt.extensions.Redirect"
    extension-element-prefixes="redirect">

    <xsl:template match="simpleview/visualizer">
      <xsl:variable name="package-path"><xsl:value-of select="$pkg-prefix"/><xsl:value-of select="../@package"/>.<xsl:value-of select="translate(@type,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/></xsl:variable>
      <xsl:variable name="file"><xsl:value-of select="translate($package-path, '.', '/')"/>/<xsl:value-of select="../@name"/>Visualizer.java</xsl:variable>
      <redirect:open file="{$file}"/>
      <redirect:write file="{$file}">package <xsl:value-of select="$package-path"/>;
import com.sdm.quasar.view.implementation.wings.WingsViewVisualizer;
import com.sdm.quasar.lang.Keywords;
import de.sdm.sia.remis.portal.TemplateViewPanel;

public class <xsl:value-of select="../@name"/>Visualizer extends WingsViewVisualizer {
  public <xsl:value-of select="../@name"/>Visualizer(Keywords arguments) {
    super(arguments);
  }

  public Object makeVisualRepresentation() {
    <xsl:if test="@panel!=''">return new <xsl:value-of select="@panel"/>();</xsl:if><xsl:if test="@template!=''">
        return new TemplateViewPanel("<xsl:value-of select="@template"/>");</xsl:if>
  }
}
      </redirect:write>
      <redirect:close file="{$file}"/>
    </xsl:template>

    <xsl:template match="simpleview">
        <xsl:variable name="package-path"><xsl:value-of select="$pkg-prefix"/><xsl:value-of select="@package"/></xsl:variable><xsl:variable name="file"><xsl:value-of select="translate($package-path, '.', '/')"/>/<xsl:value-of select="@name"/>.java</xsl:variable>
        <redirect:open file="{$file}"/><redirect:write file="{$file}">package <xsl:value-of select="$package-path"/>;

import com.sdm.quasar.view.AbstractView;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.lang.Symbol;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.view.ViewVisualizer;
import com.sdm.quasar.view.UserInterfaceType;
import com.sdm.quasar.view.ViewManager;
import com.sdm.quasar.util.Assertion;
import com.sdm.quasar.component.Command;
import com.sdm.quasar.component.NoSuchCommandException;

public class <xsl:value-of select="@name"/> extends AbstractView {
    <xsl:call-template name="commandSymbols"/>

    public <xsl:value-of select="@name"/>(Keywords arguments) throws ComponentException {
        super(arguments);
    }

    <xsl:call-template name="buildCommands"/>

    <xsl:call-template name="makeViewVisualizer"><xsl:with-param name="package-path" select="$package-path"/></xsl:call-template>
}
       </redirect:write>
       <redirect:close file="{$file}"/>
      <xsl:apply-templates/>

      <xsl:if test="count(remotecommand)>0">
        <xsl:variable name="package-path"><xsl:value-of select="$pkg-prefix"/><xsl:value-of select="@package"/>.server</xsl:variable><xsl:variable name="file"><xsl:value-of select="translate($package-path, '.', '/')"/>/<xsl:value-of select="@name"/>Server.java</xsl:variable>
        <redirect:open file="{$file}"/><redirect:write file="{$file}">package <xsl:value-of select="$package-path"/>;

import com.sdm.quasar.view.server.AbstractViewServer;
import com.sdm.quasar.lang.Keywords;
import com.sdm.quasar.lang.Symbol;
import com.sdm.quasar.component.ComponentException;
import com.sdm.quasar.view.ViewVisualizer;
import com.sdm.quasar.view.UserInterfaceType;
import com.sdm.quasar.view.ViewManager;
import com.sdm.quasar.util.Assertion;
import com.sdm.quasar.component.Command;

public class <xsl:value-of select="@name"/>Server extends AbstractViewServer {
    <call-template name="commandSymbols"/>

    public <xsl:value-of select="@name"/>Server(Keywords arguments) throws ComponentException {
        super(arguments);
    }
    <call-template name="buildRemoteCommands"/>
}
       </redirect:write>
       <redirect:close file="{$file}"/>
    </xsl:if>

    <xsl:variable name="package-path"><xsl:value-of select="$pkg-prefix"/><xsl:value-of select="@package"/>.server</xsl:variable><xsl:variable name="file"><xsl:value-of select="translate($package-path, '.', '/')"/>/<xsl:value-of select="@name"/>Description.java</xsl:variable>
    <redirect:open file="{$file}"/><redirect:write file="{$file}">package <xsl:value-of select="$package-path"/>;
import de.sdm.sia.remis.util.SimpleView;

public class <xsl:value-of select="@name"/>Description extends ViewDescription  {
  public <xsl:value-of select="@name"/>Description() {
    super("<xsl:value-of select="@name"/>",
          "<xsl:value-of select="@label"/>",
          "<xsl:value-of select="@documentation"/>",
          <xsl:value-of select="$pkg-prefix"/><xsl:value-of select="@package"/>.<xsl:value-of select="@name"/>.class.getName()<xsl:if test="count(remotecommand)>0">,
          <xsl:value-of select="$pkg-prefix"/><xsl:value-of select="@package"/>.server.<xsl:value-of select="@name"/>Server.class.getName()</xsl:if>);
  }
}
    </redirect:write>
    <redirect:close file="{$file}"/>
</xsl:template>

<xsl:template name="buildCommands">
    <xsl:if test="(count(command)+count(remotecommand))>0">
    public void buildCommands() {
        super.buildCommands();
        <xsl:for-each select="command">
        addCommand(new Command(C_<xsl:value-of select="translate(@name,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>) {
          protected Object perform(Keywords arguments) throws ComponentException {
            return <xsl:value-of select="@name"/>(arguments);
          }
        });</xsl:for-each>
        <xsl:for-each select="remotecommand">
        useViewServerCommand(C_<xsl:value-of select="translate(@name,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>);</xsl:for-each>
    }

    public void updateCommandSetup() {
      super.updateCommandSetup();
      try {<xsl:for-each select="remotecommand|command">
        setCommandEnabled(C_<xsl:value-of select="translate(@name,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>, true);</xsl:for-each>
      } catch (NoSuchCommandException e) {
        // Cannot happen...

        Assertion.fail("Internal error detected");
      }
    }

    <xsl:for-each select="command">
    public Object <xsl:value-of select="@name"/>(Keywords arguments) {
        return null;
    }</xsl:for-each>

    </xsl:if>
</xsl:template>

<xsl:template name="buildRemoteCommands">
    public void buildCommands() {
        super.buildCommands();
        <xsl:for-each select="remotecommand">
        addCommand(new Command(C_<xsl:value-of select="translate(@name,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>) {
          protected Object perform(Keywords arguments) throws ComponentException {
          return <xsl:value-of select="@name"/>(arguments);
          }
        });</xsl:for-each>
    }

    public void updateCommandSetup() {
      super.updateCommandSetup();

      try {<xsl:for-each select="remotecommand">
        setCommandEnabled(C_<xsl:value-of select="translate(@name,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>, true);</xsl:for-each>
      } catch (NoSuchCommandException e) {
        // Cannot happen...

        Assertion.fail("Internal error detected");
      }
    }

    <xsl:for-each select="remotecommand">
    public Object <xsl:value-of select="@name"/>(Keywords arguments) {
        return null;
    }</xsl:for-each>
</xsl:template>

<xsl:template name="makeViewVisualizer">
    <xsl:param name="package-path"/>
    public ViewVisualizer makeViewVisualizer(Keywords arguments) {
        UserInterfaceType userInterfaceType = getViewManager().getUserInterfaceType();

        <xsl:for-each select="visualizer">
        if (userInterfaceType == UserInterfaceType.<xsl:value-of select="translate(@type,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>)
            return new <xsl:value-of select="$package-path"/>.<xsl:value-of select="translate(@type,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/>.<xsl:value-of select="../@view"/>Visualizer(arguments);
        </xsl:for-each>

        Assertion.fail("User interface type " + userInterfaceType + " not supported by " + this);

        return null;
    }
</xsl:template>
<xsl:template name="commandImports">
    <xsl:if test="(count(command)+count(remotecommand))>0">import com.sdm.quasar.component.Command;
    import com.sdm.quasar.component.NoSuchCommandException;
    import com.sdm.quasar.lang.Symbol;</xsl:if>
</xsl:template>

<xsl:template name="remoteCommandSymbols">
    <xsl:for-each select="remotecommand">
    public static final Symbol C_<xsl:value-of select="translate(@name,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/> = Symbol.forName("<xsl:value-of select="@name"/>");</xsl:for-each>
</xsl:template>

<xsl:template name="commandSymbols">
    <xsl:for-each select="command">
    public static final Symbol C_<xsl:value-of select="translate(@name,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/> = Symbol.forName("<xsl:value-of select="@name"/>");</xsl:for-each>
    <xsl:for-each select="remotecommand">
    public static final Symbol C_<xsl:value-of select="translate(@name,'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/> = Symbol.forName("<xsl:value-of select="@name"/>");</xsl:for-each>
</xsl:template>

</xsl:stylesheet>
