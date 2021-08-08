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

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.util.indexing.ID;
import com.intellij.util.messages.MessageBus;

import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MarkupIndex extends WicketResourceIndexExtension {
    private static final ID<String, Void> NAME = ID.create("WicketMarkupIndex");

    public MarkupIndex() throws NotImplementedException {
    }

    public MarkupIndex(@NotNull MessageBus messageBus) {
        super(messageBus);
    }

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return NAME;
    }

    @Override
    public boolean acceptInput(@NotNull VirtualFile file) {
        return StdFileTypes.HTML.equals(file.getFileType());
    }

    /**
     * Returns all markup files for the passed PsiClass.
     *
     * @param psiClass PsiClass
     * @return all markups or empty array if no such file exists.
     */
    @NotNull
    public static PsiFile[] getAllFiles(@NotNull final PsiClass psiClass) {
        return getFilesByClass(NAME, psiClass, true);
    }

    /**
     * Returns the base markup file for the passed PsiClass.
     *
     * @param psiClass PsiClass
     * @return the base markup or null if no such file exists.
     */
    @Nullable
    public static PsiFile getBaseFile(@NotNull final PsiClass psiClass) {
        PsiFile[] files = getFilesByClass(NAME, psiClass, false);
        return files.length > 0 ? files[0] : null;
    }
}
