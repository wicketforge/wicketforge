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
    public static final String WICKET_RESOURCEMODEL = "org.apache.wicket.model.ResourceModel";
    public static final String WICKET_STRINGRESOURCEMODEL = "org.apache.wicket.model.StringResourceModel";

    // intension/inspection
    public static final String INTENSION_INSPECTION_GROUPNAME = "Wicket";

    // fileTemplate vars
    public static final String PROP_WICKET_DTD = "WICKET_DTD";

    // icons
    public static final Icon WICKET_ICON = IconLoader.findIcon("/icons/wicket.png");
    public static final Icon WICKET_COMPONENT_ICON = IconLoader.findIcon("/icons/wicket_component.png");
    public static final Icon TOJAVAREF = IconLoader.findIcon("/icons/wicket_form.png");
    public static final Icon TOMARKUPREF = IconLoader.findIcon("/icons/wicket_form.png");

    // icon markup references
    public static final Icon ICON_MARKUP_ = IconLoader.findIcon("/icons/wicket_component.png");
    public static final Icon ICON_MARKUP_DIV = IconLoader.findIcon("/icons/components/div.png");
    public static final Icon ICON_MARKUP_SPAN = IconLoader.findIcon("/icons/components/div.png");
    public static final Icon ICON_MARKUP_LINK = IconLoader.findIcon("/icons/components/link.png");
    public static final Icon ICON_MARKUP_TABLE = IconLoader.findIcon("/icons/components/table.png");
    public static final Icon ICON_MARKUP_TR = IconLoader.findIcon("/icons/components/table_tr.png");
    public static final Icon ICON_MARKUP_TD = IconLoader.findIcon("/icons/components/table_td.png");
    public static final Icon ICON_MARKUP_UL = IconLoader.findIcon("/icons/components/ul.png");
    public static final Icon ICON_MARKUP_LI = IconLoader.findIcon("/icons/components/bullet.png");
    public static final Icon ICON_MARKUP_LABEL = IconLoader.findIcon("/icons/components/label.png");
    public static final Icon ICON_MARKUP_INPUT = IconLoader.findIcon("/icons/components/textField.png");
    public static final Icon ICON_MARKUP_INPUT_CHECKBOX = IconLoader.findIcon("/icons/components/checkBox.png");
    public static final Icon ICON_MARKUP_INPUT_RADIO = IconLoader.findIcon("/icons/components/radioButton.png");
    public static final Icon ICON_MARKUP_TEXTAREA = IconLoader.findIcon("/icons/components/textArea.png");
    public static final Icon ICON_MARKUP_SELECT = IconLoader.findIcon("/icons/components/select.png");
    public static final Icon ICON_MARKUP_OPTION = IconLoader.findIcon("/icons/components/bullet.png");
    public static final Icon ICON_MARKUP_FORM = IconLoader.findIcon("/icons/components/form.png");
    public static final Icon ICON_MARKUP_IMG = IconLoader.findIcon("/icons/components/image.png");
    public static final Icon ICON_MARKUP_BUTTON = IconLoader.findIcon("/icons/components/button.png");

    // icon class references
    public static final Icon ICON_CLASS_ = IconLoader.findIcon("/icons/wicket_component.png");
    public static final Icon ICON_CLASS_FORM = IconLoader.findIcon("/icons/components/form.png");
    public static final Icon ICON_CLASS_SELECT = IconLoader.findIcon("/icons/components/select.png");
    public static final Icon ICON_CLASS_CHECKBOX = IconLoader.findIcon("/icons/components/checkBox.png");
    public static final Icon ICON_CLASS_RADIO = IconLoader.findIcon("/icons/components/radioButton.png");
    public static final Icon ICON_CLASS_LABEL = IconLoader.findIcon("/icons/components/label.png");
    public static final Icon ICON_CLASS_LINK = IconLoader.findIcon("/icons/components/link.png");
    public static final Icon ICON_CLASS_BUTTON = IconLoader.findIcon("/icons/components/button.png");
    public static final Icon ICON_CLASS_TEXTFIELD = IconLoader.findIcon("/icons/components/textField.png");
    public static final Icon ICON_CLASS_TEXTAREA = IconLoader.findIcon("/icons/components/textArea.png");
    public static final Icon ICON_CLASS_REPEATER = IconLoader.findIcon("/icons/components/repeater.png");
    public static final Icon ICON_CLASS_PANEL = IconLoader.findIcon("/icons/components/panel.png");
    public static final Icon ICON_CLASS_BORDER = IconLoader.findIcon("/icons/components/border.png");
    public static final Icon ICON_CLASS_FORMCOMPONENTPANEL = IconLoader.findIcon("/icons/components/formComponent.png");
    public static final Icon ICON_CLASS_FORMCOMPONENT = IconLoader.findIcon("/icons/components/formComponent.png");
    public static final Icon ICON_CLASS_WEBMARKUPCONTAINER = IconLoader.findIcon("/icons/components/div.png");

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
