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
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.Constants;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AttributeItem implements ItemPresentation {
    private String wicketId;
    private XmlAttribute attribute;
    private XmlAttributeValue attributeValue;
    private List<AttributeItem> children;

    // for root
    AttributeItem() {
        this.wicketId = "";
    }

    AttributeItem(@NotNull String wicketId, @NotNull XmlAttribute attribute, @NotNull XmlAttributeValue attributeValue) {
        this.wicketId = wicketId;
        this.attribute = attribute;
        this.attributeValue = attributeValue;
    }

    void addChild(@NotNull AttributeItem child) {
        if (children == null) {
            children = new ArrayList<AttributeItem>();
        }
        children.add(child);
    }

    @NotNull
    public String getWicketId() {
        return wicketId;
    }

    @Nullable
    public XmlAttribute getAttribute() {
        return attribute;
    }

    @Nullable
    public XmlAttributeValue getAttributeValue() {
        return attributeValue;
    }

    @NotNull
    public List<AttributeItem> getChildren() {
        return children == null ? Collections.<AttributeItem>emptyList() : children;
    }

    /* ItemPresentation*/

    public String getPresentableText() {
        return wicketId;
    }

    private String location;
    public String getLocationString() {
        if (attribute != null && location == null) {
            location = new StringBuilder().append('<').append(attribute.getParent().getName()).append('>').toString();
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