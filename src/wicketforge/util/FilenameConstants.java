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

public interface FilenameConstants {
    String EXT_HTML = ".html";
    String EXT_PROPERTIES_XML = ".properties.xml";
    String EXT_PROPERTIES = ".properties";
    String EXT_XML = ".xml";

    String[] MARKUP_EXTENSIONS = new String[] {EXT_HTML};
    String[] PROPERTIES_EXTENSIONS = new String[] {EXT_PROPERTIES_XML, EXT_PROPERTIES, EXT_XML}; // care about order because we have to iterate and find first match
}
