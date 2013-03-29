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
import org.jetbrains.annotations.Nullable;
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
        return getResourceFilename(clazz) + FilenameConstants.EXT_HTML;
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
                return getResourceFilename(clazz) + FilenameConstants.EXT_PROPERTIES;
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

    /**
     * @return filename with removed extension (first match from fileExtensions)
     */
    @NotNull
    public static String removeExtension(@NotNull String filename, @NotNull String[] fileExtensions) {
        for (String fileExtension : fileExtensions) {
            if (filename.endsWith(fileExtension)) {
                return filename.substring(0, filename.length() - fileExtension.length());
            }
        }
        return filename;
    }

    /**
     * @return extension from filename (first match from fileExtensions)
     */
    @Nullable
    public static String extractExtension(@NotNull String filename, @NotNull String[] fileExtensions) {
        for (String fileExtension : fileExtensions) {
            if (filename.endsWith(fileExtension)) {
                return fileExtension;
            }
        }
        return null;
    }

    /**
     * <pre>
     *     HomePage      ->  [null]
     *     HomePage_en   ->  en
     * </pre>
     */
    @Nullable
    public static String extractLocale(@NotNull String filenameWithoutExtension) {
        int indexOfLocale = indexOfLocale(filenameWithoutExtension);
        return indexOfLocale > 0 ? filenameWithoutExtension.substring(indexOfLocale + 1) : null;
    }

    /**
     * <pre>
     *     HomePage      ->  HomePage
     *     HomePage_en   ->  HomePage
     * </pre>
     */
    @NotNull
    public static String extractBasename(@NotNull String filenameWithoutExtension) {
        int indexOfLocale = indexOfLocale(filenameWithoutExtension);
        return indexOfLocale > 0 ? filenameWithoutExtension.substring(0, indexOfLocale) : filenameWithoutExtension;
    }

    private static int indexOfLocale(@NotNull String filenameWithoutExtension) {
        return filenameWithoutExtension.indexOf('_'); // find '_' should be enought for the moment, we want a filename without extension for future improvement (if needed)
    }
}
