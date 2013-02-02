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
package wicketforge.completion;

import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class WicketTag {

    public static final WicketTag[] TAGS = {
            new WicketTag("wicket:container", "wicket:id"),
            new WicketTag("wicket:enclosure", "child"),
            new WicketTag("wicket:extend"),
            new WicketTag("wicket:head"),
            new WicketTag("wicket:message", "key"),
            new WicketTag("wicket:label", "key"),
            new WicketTag("wicket:panel"),
            new WicketTag("wicket:child")
    };

    WicketTag(@NotNull String name, @Nullable String... attributes) {
        this.name = name;
        this.attributes = attributes;
    }

    private final String name;
    private final String[] attributes;

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String[] getAttributes() {
        return attributes == null ? ArrayUtil.EMPTY_STRING_ARRAY : attributes;
    }

    @Nullable
    public static WicketTag of(@Nullable String tagName) {
        for (WicketTag tag : TAGS) {
            if (tag.getName().equals(tagName)) {
                return tag;
            }
        }
        return null;
    }
}
