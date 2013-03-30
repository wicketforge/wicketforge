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

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.framework.detection.FacetBasedFrameworkDetector;
import com.intellij.framework.detection.FileContentPattern;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PatternCondition;
import com.intellij.util.ProcessingContext;
import com.intellij.util.indexing.FileContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.Constants;

import javax.swing.*;

/**
 * WicketForgeFacetType
 */
public class WicketForgeFacetType extends FacetType<WicketForgeFacet, WicketForgeFacetConfiguration> {

    private static final String STRING_ID = "wicketforge";
    private static final String PRESENTABLE_NAME = "Wicket";
    public static final FacetTypeId<WicketForgeFacet> ID = new FacetTypeId<WicketForgeFacet>(STRING_ID);
    public static final WicketForgeFacetType INSTANCE = new WicketForgeFacetType();

    public WicketForgeFacetType() {
        super(ID, STRING_ID, PRESENTABLE_NAME);
    }

    @Override
    public WicketForgeFacetConfiguration createDefaultConfiguration() {
        return new WicketForgeFacetConfiguration();
    }

    @Override
    public WicketForgeFacet createFacet(@NotNull Module module, String name, @NotNull WicketForgeFacetConfiguration configuration, @Nullable Facet underlyingFacet) {
        return new WicketForgeFacet(this, module, name, configuration, underlyingFacet);
    }

    @Override
    public boolean isSuitableModuleType(ModuleType moduleType) {
        return (moduleType instanceof JavaModuleType);
    }

    @Override
    public boolean isOnlyOneFacetAllowed() {
        return true;
    }

    @Override
    public Icon getIcon() {
        return Constants.WICKET_ICON;
    }

    public static class WicketForgeFacetDetector extends FacetBasedFrameworkDetector<WicketForgeFacet, WicketForgeFacetConfiguration> {
        public WicketForgeFacetDetector() {
            super(STRING_ID);
        }

        @Override
        public FacetType<WicketForgeFacet, WicketForgeFacetConfiguration> getFacetType() {
            //noinspection unchecked
            return FacetTypeRegistry.getInstance().findFacetType(STRING_ID);
        }

        @NotNull
        @Override
        public FileType getFileType() {
            return StdFileTypes.HTML;
        }

        @NotNull
        @Override
        public ElementPattern<FileContent> createSuitableFilePattern() {
            // review: improve "xmlns:wicket" detection with patterns
            return FileContentPattern.fileContent().with(new PatternCondition<FileContent>("wicketNamespace") {
                @Override
                public boolean accepts(@NotNull final FileContent fileContent, final ProcessingContext context) {
                    return fileContent.getContentAsText().toString().contains("xmlns:wicket");
                }
            });
        }
    }
}
