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
package wicketforge.templates;

import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import wicketforge.Constants;

/**
 * WicketFileTemplateGroupFactory
 */
public class WicketFileTemplateGroupFactory implements FileTemplateGroupDescriptorFactory, WicketTemplates {
    
    @Override
    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        final FileTemplateGroupDescriptor groupDescriptor = new FileTemplateGroupDescriptor("Wicket", Constants.WICKET_ICON);
        groupDescriptor.addTemplate(new FileTemplateDescriptor(WICKET_PAGE_HTML));
        groupDescriptor.addTemplate(new FileTemplateDescriptor(WICKET_PANEL_HTML));
        groupDescriptor.addTemplate(new FileTemplateDescriptor(WICKET_BORDER_HTML));
        groupDescriptor.addTemplate(new FileTemplateDescriptor(WICKET_PROPERTIES));
        groupDescriptor.addTemplate(new FileTemplateDescriptor(WICKET_PROPERTIES_XML));
        return groupDescriptor;
    }
    
}
