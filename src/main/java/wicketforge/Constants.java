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

    // icons
    public static final Icon WICKET_ICON = IconLoader.getIcon("/resources/icons/wicket.png", Constants.class);
    public static final Icon WICKET_COMPONENT_ICON = IconLoader.getIcon("/resources/icons/wicket_component.png", Constants.class);
    public static final Icon TOJAVAREF = IconLoader.getIcon("/resources/icons/wicket_form.png", Constants.class);
    public static final Icon TOMARKUPREF = IconLoader.getIcon("/resources/icons/wicket_form.png", Constants.class);

    // icon markup references
    public static final Icon ICON_MARKUP_ = IconLoader.getIcon("/resources/icons/wicket_component.png", Constants.class);
    public static final Icon ICON_MARKUP_DIV = IconLoader.getIcon("/resources/icons/components/div.png", Constants.class);
    public static final Icon ICON_MARKUP_SPAN = IconLoader.getIcon("/resources/icons/components/div.png", Constants.class);
    public static final Icon ICON_MARKUP_LINK = IconLoader.getIcon("/resources/icons/components/link.png", Constants.class);
    public static final Icon ICON_MARKUP_TABLE = IconLoader.getIcon("/resources/icons/components/table.png", Constants.class);
    public static final Icon ICON_MARKUP_TR = IconLoader.getIcon("/resources/icons/components/table_tr.png", Constants.class);
    public static final Icon ICON_MARKUP_TD = IconLoader.getIcon("/resources/icons/components/table_td.png", Constants.class);
    public static final Icon ICON_MARKUP_UL = IconLoader.getIcon("/resources/icons/components/ul.png", Constants.class);
    public static final Icon ICON_MARKUP_LI = IconLoader.getIcon("/resources/icons/components/bullet.png", Constants.class);
    public static final Icon ICON_MARKUP_LABEL = IconLoader.getIcon("/resources/icons/components/label.png", Constants.class);
    public static final Icon ICON_MARKUP_INPUT = IconLoader.getIcon("/resources/icons/components/textField.png", Constants.class);
    public static final Icon ICON_MARKUP_INPUT_CHECKBOX = IconLoader.getIcon("/resources/icons/components/checkBox.png", Constants.class);
    public static final Icon ICON_MARKUP_INPUT_RADIO = IconLoader.getIcon("/resources/icons/components/radioButton.png", Constants.class);
    public static final Icon ICON_MARKUP_TEXTAREA = IconLoader.getIcon("/resources/icons/components/textArea.png", Constants.class);
    public static final Icon ICON_MARKUP_SELECT = IconLoader.getIcon("/resources/icons/components/select.png", Constants.class);
    public static final Icon ICON_MARKUP_OPTION = IconLoader.getIcon("/resources/icons/components/bullet.png", Constants.class);
    public static final Icon ICON_MARKUP_FORM = IconLoader.getIcon("/resources/icons/components/form.png", Constants.class);
    public static final Icon ICON_MARKUP_IMG = IconLoader.getIcon("/resources/icons/components/image.png", Constants.class);
    public static final Icon ICON_MARKUP_BUTTON = IconLoader.getIcon("/resources/icons/components/button.png", Constants.class);

    // icon class references
    public static final Icon ICON_CLASS_ = IconLoader.getIcon("/resources/icons/wicket_component.png", Constants.class);
    public static final Icon ICON_CLASS_FORM = IconLoader.getIcon("/resources/icons/components/form.png", Constants.class);
    public static final Icon ICON_CLASS_SELECT = IconLoader.getIcon("/resources/icons/components/select.png", Constants.class);
    public static final Icon ICON_CLASS_CHECKBOX = IconLoader.getIcon("/resources/icons/components/checkBox.png", Constants.class);
    public static final Icon ICON_CLASS_RADIO = IconLoader.getIcon("/resources/icons/components/radioButton.png", Constants.class);
    public static final Icon ICON_CLASS_LABEL = IconLoader.getIcon("/resources/icons/components/label.png", Constants.class);
    public static final Icon ICON_CLASS_LINK = IconLoader.getIcon("/resources/icons/components/link.png", Constants.class);
    public static final Icon ICON_CLASS_BUTTON = IconLoader.getIcon("/resources/icons/components/button.png", Constants.class);
    public static final Icon ICON_CLASS_TEXTFIELD = IconLoader.getIcon("/resources/icons/components/textField.png", Constants.class);
    public static final Icon ICON_CLASS_TEXTAREA = IconLoader.getIcon("/resources/icons/components/textArea.png", Constants.class);
    public static final Icon ICON_CLASS_REPEATER = IconLoader.getIcon("/resources/icons/components/repeater.png", Constants.class);
    public static final Icon ICON_CLASS_PANEL = IconLoader.getIcon("/resources/icons/components/panel.png", Constants.class);
    public static final Icon ICON_CLASS_BORDER = IconLoader.getIcon("/resources/icons/components/border.png", Constants.class);
    public static final Icon ICON_CLASS_FORMCOMPONENTPANEL = IconLoader.getIcon("/resources/icons/components/formComponent.png", Constants.class);
    public static final Icon ICON_CLASS_FORMCOMPONENT = IconLoader.getIcon("/resources/icons/components/formComponent.png", Constants.class);
    public static final Icon ICON_CLASS_WEBMARKUPCONTAINER = IconLoader.getIcon("/resources/icons/components/div.png", Constants.class);

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
