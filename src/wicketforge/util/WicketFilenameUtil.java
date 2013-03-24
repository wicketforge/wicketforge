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
package wicketforge.util;

import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;
import wicketforge.Constants;

public final class WicketFilenameUtil {
    private WicketFilenameUtil() {
    }

    /**
     * Returns the markup file name that is associated with the class
     *
     * @param clazz the PsiClass
     * @return the markup file name
     */
    @NotNull
    public static String getMarkupFilename(@NotNull PsiClass clazz) {
        return getResourceFilename(clazz) + ".html";
    }

    /**
     * Returns the markup file name that is associated with the class
     *
     * @param clazz the PsiClass
     * @return the markup file name
     */
    @NotNull
    public static String getPropertiesFilename(@NotNull PsiClass clazz, @NotNull Constants.PropertiesType propertiesType) {
        switch (propertiesType) {
            case PROPERTIES:
                return getResourceFilename(clazz) + ".properties";
            case XML:
                return getResourceFilename(clazz) + WicketVersion.getVersion(clazz).getXmlPropertiesFileExtension();
            default:
                throw new IllegalArgumentException("Unsupported PropertiesType " + propertiesType);
        }
    }

    /**
     * Return the (resources) name of a PsiClass (works for inner classes too).
     *
     * @param clazz The PsiClass
     * @return      ResourceFileName ex 'MyClass' or 'MyClass$MyInnerClass'
     */
    @NotNull
    private static String getResourceFilename(@NotNull PsiClass clazz) {
        StringBuilder sb = new StringBuilder(clazz.getName());

        PsiClass workPsiClass = clazz;
        while ((workPsiClass = workPsiClass.getContainingClass()) != null) {
            sb.insert(0, '$').insert(0, workPsiClass.getName());
        }

        return sb.toString();
    }
}
