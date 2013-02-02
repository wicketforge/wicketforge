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
package wicketforge.psi.hierarchy;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.Constants;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ClassWicketIdItem implements ItemPresentation {
    private String wicketId;
    private List<NewComponentReference> references;
    private List<ClassWicketIdItem> children;

    ClassWicketIdItem(@NotNull String wicketId) {
        this.wicketId = wicketId;
        this.references = new SmartList<NewComponentReference>();
    }

    @Nullable
    ClassWicketIdItem findChild(@NotNull String wicketId) {
        if (children != null) {
            for (ClassWicketIdItem child : children) {
                if (wicketId.equals(child.wicketId)) {
                    return child;
                }
            }
        }
        return null;
    }

    void addChild(@NotNull ClassWicketIdItem child) {
        if (children == null) {
            children = new ArrayList<ClassWicketIdItem>();
        }
        children.add(child);
    }

    @NotNull
    public String getWicketId() {
        return wicketId;
    }

    @NotNull
    public List<NewComponentReference> getReferences() {
        return references;
    }

    @NotNull
    public List<ClassWicketIdItem> getChildren() {
        return children == null ? Collections.<ClassWicketIdItem>emptyList() : children;
    }

    /* ItemPresentation */

    public String getPresentableText() {
        return wicketId;
    }

    private String location;
    public String getLocationString() {
        if (location == null) {
            StringBuilder sb = new StringBuilder();
            for (NewComponentReference reference : references) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(reference.getLocationString());
            }
            location = sb.toString();
        }
        return location;
    }

    public Icon getIcon() {
        return getIcon(false);
    }

    public Icon getIcon(boolean open) {
        return Constants.WICKET_COMPONENT_ICON;
    }

    public TextAttributesKey getTextAttributesKey() {
        return null;
    }
}