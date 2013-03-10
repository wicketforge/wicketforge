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
package wicketforge.facet.ui;

import com.sun.istack.internal.NotNull;

/**
 *
 */
@Deprecated // TODO remove WicketVersion and WicketForgeSupportModel and implement in other way...
public enum WicketVersion {
    WICKET_1_3("1.3", "http://wicket.apache.org/dtds.data/wicket-xhtml1.3-strict.dtd", "xml"),
    WICKET_1_4("1.4", "http://wicket.apache.org/dtds.data/wicket-xhtml1.4-strict.dtd", "xml"),
    WICKET_1_5("1.5", "http://wicket.apache.org/dtds.data/wicket-xhtml1.4-strict.dtd", "properties.xml"); // at the moment there is no 1.5 dtd...

    public static WicketVersion HIGHEST_STABLE = WICKET_1_5;

    private final String name;
    private String dtd;
    private String xmlPropertiesFileExtension;

    private WicketVersion(@NotNull String name, @NotNull String dtd, @NotNull String xmlPropertiesFileExtension) {
        this.name = name;
        this.dtd = dtd;
        this.xmlPropertiesFileExtension = xmlPropertiesFileExtension;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getXmlPropertiesFileExtension() {
        return xmlPropertiesFileExtension;
    }

    @NotNull
    public String getDtd() {
        return dtd;
    }

    public String toString() {
        return name;
    }
}
