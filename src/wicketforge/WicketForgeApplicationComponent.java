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

import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.ServiceManager;
import org.jetbrains.annotations.NotNull;
import wicketforge.inspection.EmptySrcAttributeInspection;
import wicketforge.inspection.HtmlIdInspection;
import wicketforge.inspection.JavaIdInspection;

/**
 *
 */
/*@State(name = "WicketForgeApplicationComponent", storages = {@Storage(id = "other", file = "$APP_CONFIG$/other.xml")})*/
public class WicketForgeApplicationComponent implements ApplicationComponent, InspectionToolProvider/*, Configurable, PersistentStateComponent<WicketForgeApplicationComponent> */{
    /*private transient WicketForgeConfigurationPanel panel;*/

/*
    // gutter Icons
    public boolean showJavaToMarkup = true;
    public boolean showJavaToComponents = true;
    public boolean showMarkupToJava = true;
    public boolean showMarkupToComponents = true;
*/

    public static WicketForgeApplicationComponent get() {
      return ServiceManager.getService(WicketForgeApplicationComponent.class);
    }

    public WicketForgeApplicationComponent() {
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }

    @NotNull 
    public String getComponentName() {
        return "WicketForgeApplicationComponent";
    }

    public Class[] getInspectionClasses() {
        return new Class[]{HtmlIdInspection.class, JavaIdInspection.class, EmptySrcAttributeInspection.class};
    }
/*
    @Nls
    public String getDisplayName() {
        return "WicketForge";
    }

    public Icon getIcon() {
        return Constants.WICKET_ICON;
    }

    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        if (panel == null) {
            panel = new WicketForgeConfigurationPanel();
        }
        return panel.contentRoot;
    }

    public boolean isModified() {
        return panel != null && panel.isModified(this);
    }

    public void apply() throws ConfigurationException {
        if (panel != null) {
            panel.pushDataTo(this);
        }
    }

    public void reset() {
        if (panel != null) {
            panel.pullDataFrom(this);
        }
    }

    public void disposeUIResources() {
        panel = null;
    }

    public WicketForgeApplicationComponent getState() {
        return this;
    }

    public void loadState(WicketForgeApplicationComponent state) {
        XmlSerializerUtil.copyBean(state, this);
    }
*/
}
