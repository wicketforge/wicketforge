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
package wicketforge.model;

import wicketforge.model.tags.WicketCompletionType;
import wicketforge.model.tags.WicketTag;
import wicketforge.model.tags.WicketTagAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class WicketTagRegistry {

    private static List<WicketTag> tags = new ArrayList<WicketTag>();

    static {
        tags.add(new WicketTag("wicket:container").addAttribute(new WicketTagAttribute("wicket:id", WicketCompletionType.JAVA)));
        tags.add(new WicketTag("wicket:enclosure").addAttribute(new WicketTagAttribute("child", WicketCompletionType.JAVA)));
        tags.add(new WicketTag("wicket:extend"));
        tags.add(new WicketTag("wicket:head"));
        tags.add(new WicketTag("wicket:message").addAttribute(new WicketTagAttribute("key", WicketCompletionType.PROPERTIES)));
        tags.add(new WicketTag("wicket:panel"));
        tags.add(new WicketTag("wicket:child"));
    }

    public static WicketTag getTag(String elementName) {
        for (WicketTag tag : tags) {
            if (tag.getName().equals(elementName)) {
                return tag;
            }
        }
        return null;
    }

}
