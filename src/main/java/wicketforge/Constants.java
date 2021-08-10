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
import org.jetbrains.annotations.NotNull;
import wicketforge.templates.WicketTemplates;

import javax.swing.*;

/**
 * A collection of constants used by the plugin.
 */
public interface Constants {
    //
    public static final String WICKET_ID = "wicket:id";

    //
    public static final char HIERARCHYSEPARATOR = '#';

    // wicket classes
    public static final String WICKET_APPLICATION = "org.apache.wicket.Application";
    public static final String WICKET_COMPONENT = "org.apache.wicket.Component";
    public static final String WICKET_PAGE = "org.apache.wicket.Page";
    public static final String WICKET_PANEL = "org.apache.wicket.markup.html.panel.Panel";
    public static final String WICKET_FORMCOMPONENTPANEL = "org.apache.wicket.markup.html.form.FormComponentPanel";
    public static final String WICKET_BORDER = "org.apache.wicket.markup.html.border.Border";
    public static final String WICKET_RESOURCEMODEL = "org.apache.wicket.model.ResourceModel";
    public static final String WICKET_STRINGRESOURCEMODEL = "org.apache.wicket.model.StringResourceModel";

    // wicketforge annotations
    public static final String WICKETFORGE_COMPONENT_FACTORY = "com.googlecode.wicketforge.annotations.ComponentFactory";

    // intension/inspection
    public static final String INTENSION_INSPECTION_GROUPNAME = "Wicket";

    // fileTemplate properties
    public static final String PROP_WICKET_NS = "WICKET_NS";



    enum PropertiesType {
        PROPERTIES(WicketTemplates.WICKET_PROPERTIES),
        XML(WicketTemplates.WICKET_PROPERTIES_XML);

        private String templateName;

        private PropertiesType(String templateName) {
            this.templateName = templateName;
        }

        @NotNull
        public String getTemplateName() {
            return templateName;
        }
    }
}
