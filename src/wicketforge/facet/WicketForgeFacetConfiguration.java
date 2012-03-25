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
package wicketforge.facet;

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetEditorsFactory;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.facet.ui.libraries.FacetLibrariesValidator;
import com.intellij.facet.ui.libraries.LibraryInfo;
import org.jdom.Element;
import wicketforge.facet.ui.WicketFeaturesEditor;

import java.util.ArrayList;
import java.util.List;

/**
 * WicketForgeFacetConfiguration
 */
public class WicketForgeFacetConfiguration implements FacetConfiguration {
    private static final String RESOURCEURLS_TAG = "resourceUrls";
    private static final String RESOURCEURL_TAG = "resourceUrl";
    private static final String URL = "url";

    List<String> resourceUrls = new ArrayList<String>();

    public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
        FacetLibrariesValidator validator = FacetEditorsFactory.getInstance().createLibrariesValidator(LibraryInfo.EMPTY_ARRAY,
                        new WicketLibrariesValidatorDescription(), editorContext, validatorsManager);

        validatorsManager.registerValidator(validator);

        return new FacetEditorTab[] {new WicketFeaturesEditor(editorContext, validator)};
    }

    @Deprecated
    public void readExternal(Element element) {
        Element resourceUrlsElement = element.getChild(RESOURCEURLS_TAG);
        if (resourceUrlsElement != null) {
            List resourceUrlsChildren = resourceUrlsElement.getChildren(RESOURCEURL_TAG);
            if (resourceUrlsChildren != null) {
                for (Object child : resourceUrlsChildren) {
                    if (child instanceof Element) {
                        resourceUrls.add(((Element) child).getAttributeValue(URL));
                    }
                }
            }
        }
    }

    @Deprecated
    public void writeExternal(Element element) {
        Element resourceUrlsElement = new Element(RESOURCEURLS_TAG);
        element.addContent(resourceUrlsElement);
        for (String resourceUrl : resourceUrls) {
            Element child = new Element(RESOURCEURL_TAG);
            child.setAttribute(URL, resourceUrl);
            resourceUrlsElement.addContent(child);
        }
    }
}
