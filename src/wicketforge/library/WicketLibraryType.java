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
package wicketforge.library;

import com.intellij.framework.library.DownloadableLibraryTypeBase;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import wicketforge.Constants;

import java.net.MalformedURLException;
import java.net.URL;

public class WicketLibraryType extends DownloadableLibraryTypeBase {
//    private static final Logger LOG = Logger.getInstance("#wicketforge.library.WicketLibraryType");
    private static final String[] DETECTIONCLASSNAMES = new String[]{Constants.WICKET_APPLICATION};

    public WicketLibraryType() {
        super("Wicket", "wicket", "wicket", Constants.WICKET_ICON, getUrl("wicket"));
    }

    @Override
    protected String[] getDetectionClassNames() {
        return DETECTIONCLASSNAMES;
    }

    @NotNull
    private static URL getUrl(@NotNull String lib) {
        return WicketLibraryType.class.getResource("/resources/" + lib + ".xml");
//        try {
////            return new URL("http", "wicketforge.googlecode.com", "/files/" + lib + ".xml");
//            return new URL("http", "resources.wicketforge.googlecode.com", "/git/" + lib + ".xml");
//        } catch (MalformedURLException e) {
//            LOG.error(e);
//            return null;
//        }
    }
}
