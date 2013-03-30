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
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wicketforge.Constants;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MarkupWicketIdItem implements ItemPresentation {
    private String wicketId;
    private XmlAttribute attribute;
    private XmlAttributeValue attributeValue;
    private List<MarkupWicketIdItem> children;

    // for root
    MarkupWicketIdItem() {
        this.wicketId = "";
    }

    MarkupWicketIdItem(@NotNull String wicketId, @NotNull XmlAttribute attribute, @NotNull XmlAttributeValue attributeValue) {
        this.wicketId = wicketId;
        this.attribute = attribute;
        this.attributeValue = attributeValue;
    }

    void addChild(@NotNull MarkupWicketIdItem child) {
        if (children == null) {
            children = new ArrayList<MarkupWicketIdItem>();
        }
        children.add(child);
    }

    @NotNull
    public String getWicketId() {
        return wicketId;
    }

    @Nullable
    public XmlTag getTag() {
        return attribute != null ? attribute.getParent() : null;
    }

    @Nullable
    public XmlAttributeValue getAttributeValue() {
        return attributeValue;
    }

    @NotNull
    public List<MarkupWicketIdItem> getChildren() {
        return children == null ? Collections.<MarkupWicketIdItem>emptyList() : children;
    }

    /* ItemPresentation*/

    @Override
    public String getPresentableText() {
        return wicketId;
    }

    @Override
    public String getLocationString() {
        XmlTag tag = getTag();
        return tag != null ? tag.getName() : null;
    }

    @Override
    @Nullable
    public Icon getIcon(boolean unused) {
        return getIcon(getTag());
    }

    private static Icon getIcon(@Nullable XmlTag tag) {
        if (tag != null) {
            String name = tag.getName();
            if ("div".equals(name)) {
                return Constants.ICON_MARKUP_DIV;
            }
            if ("span".equals(name)) {
                return Constants.ICON_MARKUP_SPAN;
            }
            if ("a".equals(name)) {
                return Constants.ICON_MARKUP_LINK;
            }
            if ("table".equals(name)) {
                return Constants.ICON_MARKUP_TABLE;
            }
            if ("tr".equals(name)) {
                return Constants.ICON_MARKUP_TR;
            }
            if ("td".equals(name)) {
                return Constants.ICON_MARKUP_TD;
            }
            if ("ul".equals(name)) {
                return Constants.ICON_MARKUP_UL;
            }
            if ("li".equals(name)) {
                return Constants.ICON_MARKUP_LI;
            }
            if ("label".equals(name)) {
                return Constants.ICON_MARKUP_LABEL;
            }
            if ("input".equals(name)) {
                String type = tag.getAttributeValue("type");
                if ("submit".equals(type) || "reset".equals(type) || "image".equals(type) || "button".equals(type)) {
                    return Constants.ICON_MARKUP_BUTTON;
                }
                if ("checkbox".equals(type)) {
                    return Constants.ICON_MARKUP_INPUT_CHECKBOX;
                }
                if ("radio".equals(type)) {
                    return Constants.ICON_MARKUP_INPUT_RADIO;
                }
                return Constants.ICON_MARKUP_INPUT;
            }
            if ("textarea".equals(name)) {
                return Constants.ICON_MARKUP_TEXTAREA;
            }
            if ("select".equals(name)) {
                // generic icon for combobox and list because user dont have to set size attribute...
                return Constants.ICON_MARKUP_SELECT;
            }
            if ("option".equals(name)) {
                return Constants.ICON_MARKUP_OPTION;
            }
            if ("form".equals(name)) {
                return Constants.ICON_MARKUP_FORM;
            }
            if ("img".equals(name)) {
                return Constants.ICON_MARKUP_IMG;
            }
            if ("button".equals(name)) {
                return Constants.ICON_MARKUP_BUTTON;
            }
        }
        return Constants.ICON_MARKUP_;
    }
}