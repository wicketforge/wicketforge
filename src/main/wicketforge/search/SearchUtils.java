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

import org.jetbrains.annotations.Nullable;

final class SearchUtils {
    private SearchUtils() {
    }

    private static final String BIND_BEGIN = "<!--wicketforge-bind:";
    private static final String BIND_END = "-->";

    @Nullable
    public static String getBoundClassName(@Nullable String content) {
        if (content == null) {
            return null;
        }
        int begin = content.indexOf(BIND_BEGIN);
        if (begin < 0) {
            return null;
        }
        int end = content.indexOf(BIND_END, begin);
        return end > 0 ? content.substring(begin + BIND_BEGIN.length(), end).trim() : null;
    }
}
