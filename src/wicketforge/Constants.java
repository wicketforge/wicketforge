/*
 * Copyright 2010 The WicketForge-Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package wicketforge;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * A collection of constants used by the plugin.
 */
public interface Constants {
    // common
    public static final String HTML = "html";
    public static final String JAVA = "java";
    public static final String PROPERTIES = "properties";

    //
    public static final String WICKET_ID = "wicket:id";

    //
    public static final char HIERARCHYSEPARATOR = '#';

    // wicket classes
    public static final String WICKET_COMPONENT = "org.apache.wicket.Component";
    public static final String WICKET_PAGE = "org.apache.wicket.Page";
    public static final String WICKET_PANEL = "org.apache.wicket.markup.html.panel.Panel";
    public static final String WICKET_FORMCOMPONENTPANEL = "org.apache.wicket.markup.html.form.FormComponentPanel";
    public static final String WICKET_IMODEL = "org.apache.wicket.model.IModel";
    public static final String WICKET_PROPERTYMODEL = "org.apache.wicket.model.PropertyModel";
    public static final String WICKET_RESOURCEMODEL = "org.apache.wicket.model.ResourceModel";
    public static final String WICKET_STRINGRESOURCEMODEL = "org.apache.wicket.model.StringResourceModel";

    // intension/inspection
    public static final String INTENSION_INSPECTION_GROUPNAME = "Wicket";

    // fileTemplate vars
    public static final String PROP_WICKET_DTD = "WICKET_DTD";
    public static final String PROP_WICKET_DTD_UNDEFINED = "http://wicket.apache.org/";

    // icons
    public static final Icon WICKET_ICON = IconLoader.findIcon("/icons/wicket.png");
    public static final Icon HTML_ICON = IconLoader.findIcon("/icons/icon_html.png");
    public static final Icon PROPERTIES_ICON = IconLoader.findIcon("/icons/icon_properties.png");
    public static final Icon WICKET_COMPONENT_ICON = IconLoader.findIcon("/icons/wicket_component.png");
    public static final Icon TOJAVAREF = IconLoader.findIcon("/icons/form.png");
    public static final Icon TOMARKUPREF = IconLoader.findIcon("/icons/form.png");
}
