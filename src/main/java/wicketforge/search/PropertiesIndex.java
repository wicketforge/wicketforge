/*
 * Copyright 2013 The WicketForge-Team
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
package wicketforge.search;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.lang.properties.PropertiesFileType;
import com.intellij.lang.properties.PropertiesImplUtil;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.util.indexing.FileContent;
import com.intellij.util.indexing.ID;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.xml.NanoXmlUtil;
import com.intellij.util.xml.XmlFileHeader;

public class PropertiesIndex extends WicketResourceIndexExtension {
    private static final ID<String, Void> NAME = ID.create("WicketPropertiesIndex");


    @SuppressWarnings("unused")
    private PropertiesIndex() throws NotImplementedException {
    }

    @SuppressWarnings("unused")
    public PropertiesIndex(@NotNull MessageBus messageBus) {
        super(messageBus);
    }

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return NAME;
    }

    @Override
    public boolean acceptInput(@NotNull VirtualFile file) {
        FileType fileType = file.getFileType();
        return PropertiesFileType.INSTANCE.equals(fileType) || XmlFileType.INSTANCE.equals(fileType);
    }

    @NotNull
    @Override
    public Map<String, Void> map(FileContent inputData) {
        if (XmlFileType.INSTANCE.equals(inputData.getFileType())) {
            boolean isPropertiesXml = false;
            try {
                XmlFileHeader fileHeader = NanoXmlUtil.parseHeaderWithException(inputData.getFile());
                isPropertiesXml = "properties".equals(fileHeader.getRootTagLocalName());
            } catch (IOException ignore) {
            }
            if (!isPropertiesXml) {
                return Collections.emptyMap(); // if not, nothing to map here
            }
        }
        return super.map(inputData);
    }

    /**
     * Returns all properties files for the passed PsiClass.
     *
     * @param psiClass PsiClass
     * @return all properties or empty array if no such file exists.
     */
    @NotNull
    public static PsiFile[] getAllFiles(@NotNull final PsiClass psiClass) {
        return getFilesByClass(NAME, psiClass, true);
    }

    /**
     * Returns the base properties file for the passed PsiClass.
     *
     * @param psiClass PsiClass
     * @return the base properties or null if no such file exists.
     */
    @Nullable
    public static PropertiesFile getBaseFile(@NotNull final PsiClass psiClass) {
        try {
            PsiFile[] files = getFilesByClass(NAME, psiClass, false);
            return files.length > 0 ? PropertiesImplUtil.getPropertiesFile(files[0]) : null;
        } catch (NoClassDefFoundError error){
            return null;
        }
    }
}
