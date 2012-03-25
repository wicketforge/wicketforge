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

import javax.swing.*;

/**
 */
@Deprecated
public class WicketForgeConfigurationPanel {
    JPanel contentRoot;
    private JCheckBox javaToMarkupCheckBox;
    private JCheckBox markupToJavaCheckBox;
    private JCheckBox javaToComponentsCheckBox;
    private JCheckBox markupToComponentsCheckBox;

    public boolean isModified(WicketForgeApplicationComponent configuration) {
        return false;
/*
        return configuration.showJavaToMarkup != javaToMarkupCheckBox.isSelected() ||
                configuration.showJavaToComponents != javaToComponentsCheckBox.isSelected() ||
                configuration.showMarkupToJava != markupToJavaCheckBox.isSelected() ||
                configuration.showMarkupToComponents != markupToComponentsCheckBox.isSelected();
*/
    }

    public void pushDataTo(WicketForgeApplicationComponent configuration) {
/*
        configuration.showJavaToMarkup = javaToMarkupCheckBox.isSelected();
        configuration.showJavaToComponents = javaToComponentsCheckBox.isSelected();
        configuration.showMarkupToJava = markupToJavaCheckBox.isSelected();
        configuration.showMarkupToComponents = markupToComponentsCheckBox.isSelected();
*/
    }

    public void pullDataFrom(WicketForgeApplicationComponent configuration) {
/*
        javaToMarkupCheckBox.setSelected(configuration.showJavaToMarkup);
        javaToComponentsCheckBox.setSelected(configuration.showJavaToComponents);
        markupToJavaCheckBox.setSelected(configuration.showMarkupToJava);
        markupToComponentsCheckBox.setSelected(configuration.showMarkupToComponents);
*/
    }
}
