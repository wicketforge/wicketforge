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
import com.intellij.facet.impl.ui.FacetEditorsFactoryImpl;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.framework.library.DownloadableLibraryService;
import com.intellij.util.messages.Topic;
import org.jdom.Element;
import wicketforge.library.WicketLibraryType;

import java.util.ArrayList;
import java.util.List;

/**
 * WicketForgeFacetConfiguration
 */
public class WicketForgeFacetConfiguration implements FacetConfiguration {
    private static final String RESOURCEURLS_TAG = "resourceUrls";
    private static final String RESOURCEURL_TAG = "resourceUrl";
    private static final String URL = "url";

    public static final Topic<Runnable> ADDITIONAL_PATHS_CHANGED = new Topic<Runnable>("additional resource paths changed", Runnable.class);

    List<String> resourceUrls = new ArrayList<String>();

    @Override
    public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
        validatorsManager.registerValidator(FacetEditorsFactoryImpl.getInstanceImpl().createLibraryValidator(
                DownloadableLibraryService.getInstance().createDescriptionForType(WicketLibraryType.class),
                editorContext,
                validatorsManager,
                "wicket"
        ));
        return new FacetEditorTab[]{new WicketFacetEditorTab(editorContext)};
    }

    @Override
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

    @Override
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
