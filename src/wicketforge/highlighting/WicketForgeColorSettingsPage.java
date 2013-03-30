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
package wicketforge.highlighting;

import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.PlainSyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.psi.PsiElement;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;
import wicketforge.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class WicketForgeColorSettingsPage implements ColorSettingsPage {
    private static final TextAttributes DEFAULTWICKETID = new TextAttributes(new JBColor(new Color(0xe8590a), new Color(0xc8530a)), null, null, null, Font.BOLD);
    private static final TextAttributes DEFAULTWICKETID_NOTRESOLVABLE = new TextAttributes(null, null, null, null, Font.PLAIN);

    private static final TextAttributesKey JAVAWICKETID = TextAttributesKey.createTextAttributesKey("JAVAWICKETID", DEFAULTWICKETID);
    private static final TextAttributesKey JAVAWICKETID_NOTRESOLVABLE = TextAttributesKey.createTextAttributesKey("JAVAWICKETID_NOTRESOLVABLE", DEFAULTWICKETID_NOTRESOLVABLE);
    private static final TextAttributesKey MARKUPWICKETID = TextAttributesKey.createTextAttributesKey("MARKUPWICKETID", DEFAULTWICKETID);

    static final HighlightInfoType HIGHLIGHT_JAVAWICKETID = new WicketHighlightInfoType(JAVAWICKETID);
    static final HighlightInfoType HIGHLIGHT_JAVAWICKETID_NOTRESOLVABLE = new WicketHighlightInfoType(JAVAWICKETID_NOTRESOLVABLE);
    static final HighlightInfoType HIGHLIGHT_MARKUPWICKETID = new WicketHighlightInfoType(MARKUPWICKETID);

    private static final AttributesDescriptor[] ATTRIBUTESDESC = {
            new AttributesDescriptor("java wicketId", JAVAWICKETID),
            new AttributesDescriptor("java wicketId (not resolvable)", JAVAWICKETID_NOTRESOLVABLE),
            new AttributesDescriptor("markup wicketId", MARKUPWICKETID)
    };

    @Override
    @NotNull
    public String getDisplayName() {
        return "Wicket";
    }

    @Override
    public Icon getIcon() {
        return Constants.WICKET_ICON;
    }

    @Override
    @NotNull
    public AttributesDescriptor[] getAttributeDescriptors() {
        return ATTRIBUTESDESC;
    }

    @Override
    @NotNull
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @Override
    @NotNull
    public SyntaxHighlighter getHighlighter() {
        return new PlainSyntaxHighlighter();
    }

    @Override
    @NotNull
    public String getDemoText() {
        return "-- java\n" +
                "new Label(<javaWicketId>\"someWicketId\"</javaWicketId>, \"Hello World!\")\n" +
                "new Label(<javaWicketIdNotResolvable>id</javaWicketIdNotResolvable>, \"Hello World!\")\n" +
                "\n" +
                "-- markup\n" +
                "span wicket:id=<markupWicketId>\"someWicketId\"</markupWicketId>\n";
    }

    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        Map<String, TextAttributesKey> map = new HashMap<String, TextAttributesKey>();
        map.put("javaWicketId", JAVAWICKETID);
        map.put("javaWicketIdNotResolvable", JAVAWICKETID_NOTRESOLVABLE);
        map.put("markupWicketId", MARKUPWICKETID);
        return map;
    }

    private static class WicketHighlightInfoType implements HighlightInfoType {
        private TextAttributesKey textAttributesKey;

        private WicketHighlightInfoType(TextAttributesKey textAttributesKey) {
            this.textAttributesKey = textAttributesKey;
        }

        @Override
        @NotNull
        public HighlightSeverity getSeverity(PsiElement psiElement) {
            return HighlightSeverity.INFORMATION;
        }

        @Override
        public TextAttributesKey getAttributesKey() {
            return textAttributesKey;
        }
    }
}
