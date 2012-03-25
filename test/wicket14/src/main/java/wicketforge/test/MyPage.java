package wicketforge.test;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * --MyPage
 *
 * abstractComponent
 * referencedAbstractComponent
 * component
 * referencedComponent
 *
 * abstractContainer
 *      abstractContainerLabel
 * referencedAbstractContainer
 *      referencedAbstractContainerLabel
 *      referencedAbstractContainerLabelOuter
 * container
 * referencedContainer
 *      referencedContainerLabelOuter
 *
 * abstractMyContainer
 *      myMarkupContainer
 *      abstractMyContainerLabel
 * referencedAbstractMyContainer
 *      myMarkupContainer
 *      referencedAbstractMyContainerLabel
 *      referencedAbstractMyContainerLabelOuter
 * myContainer
 *      myMarkupContainer
 * referencedMyContainer
 *      myMarkupContainer
 *      referencedMyContainerLabelOuter
 *
 * abstractPanel
 * referencedAbstractPanel
 * panel
 * referencedPanel
 *
 * abstractMyPanel
 * referencedAbstractMyPanel
 * myPanel
 * referencedMypPanel
 *
 */
public class MyPage extends WebPage {
    public MyPage() {

        //** Component without childs

        { // add abstract component
            add(new WebComponent("abstractComponent") {
                @Override
                protected void onInitialize() {
                    super.onInitialize();
                }
            });
        }

        { // add referenced abstract component
            WebComponent component = new WebComponent("referencedAbstractComponent") {
                @Override
                protected void onInitialize() {
                    super.onInitialize();
                }
            };
            add(component);
        }

        { // add component
            add(new WebComponent("component"));
        }

        { // add referenced component
            WebComponent component = new WebComponent("referencedComponent");
            add(component);
        }

        //** Container

        { // add abstract container
            add(new WebMarkupContainer("abstractContainer") {
                @Override
                protected void onInitialize() {
                    super.onInitialize();
                    add(new Label("abstractContainerLabel"));                                                   // ***
                }
            });
        }

        { // add referenced abstract container
            MarkupContainer container = new WebMarkupContainer("referencedAbstractContainer") {
                @Override
                protected void onInitialize() {
                    super.onInitialize();
                    add(new Label("referencedAbstractContainerLabel"));                                      // ***
                }
            };
            container.add(new Label("referencedAbstractContainerLabelOuter"));
            add(container);
        }

        { // add container
            add(new WebMarkupContainer("container"));
        }

        { // add referenced container
            MarkupContainer container = new WebMarkupContainer("referencedContainer");            
            container.add(new Label("referencedContainerLabelOuter"));
            add(container);
        }

        //** Container (inner static class)

        { // add abstract myContainer
            add(new MyMarkupContainer("abstractMyContainer") {
                @Override
                protected void onInitialize() {
                    super.onInitialize();
                    add(new Label("abstractMyContainerLabel"));
                }
            });
        }

        { // add referenced abstract myContainer
            MarkupContainer myContainer = new MyMarkupContainer("referencedAbstractMyContainer") {
                @Override
                protected void onInitialize() {
                    super.onInitialize();
                    add(new Label("referencedAbstractMyContainerLabel"));
                }
            };
            myContainer.add(new Label("referencedAbstractMyContainerLabelOuter"));
            add(myContainer);
        }

        { // add myContainer
            add(new MyMarkupContainer("myContainer"));
        }

        { // add referenced myContainer
            MarkupContainer myContainer = new MyMarkupContainer("referencedMyContainer");
            myContainer.add(new Label("referencedMyContainerLabelOuter"));
            add(myContainer);
        }

        //** Panel

        { // add abstract panel
            add(new Panel("abstractPanel") {
                @Override
                protected void onInitialize() {
                    super.onInitialize();
                    add(new Label("abstractPanelLabel"));
                }
            });
        }

        { // add referenced abstract panel
            Panel panel = new Panel("referencedAbstractPanel") {
                @Override
                protected void onInitialize() {
                    super.onInitialize();
                    add(new Label("referencedAbstractPanelLabel"));
                }
            };
            panel.add(new Label("referencedAbstractPanelLabelOuter"));
            add(panel);
        }

        { // add panel
            add(new Panel("panel"));
        }

        { // add referenced panel
            Panel panel = new Panel("referencedPanel");
            panel.add(new Label("referencedPanelLabelOuter"));
            add(panel);
        }

        //** Panel (inner static class)

        { // add abstract myPanel
            add(new MyPanel("abstractMyPanel") {
                @Override
                protected void onInitialize() {
                    super.onInitialize();
                    add(new Label("abstractMyPanelLabel"));
                }
            });
        }

        { // add referenced abstract myPanel
            Panel myPanel = new MyPanel("referencedAbstractMyPanel") {
                @Override
                protected void onInitialize() {
                    super.onInitialize();
                    add(new Label("referencedAbstractMyPanelLabel"));
                }
            };
            myPanel.add(new Label("referencedAbstractMyPanelLabelOuter"));
            add(myPanel);
        }

        { // add myPanel
            add(new MyPanel("myPanel"));
        }

        { // add referenced myPanel
            Panel myPanel = new MyPanel("referencedMyPanel");
            myPanel.add(new Label("referencedMyPanelLabelOuter"));
            add(myPanel);
        }

    }

    /**
     *
     */
    public static class MyMarkupContainer extends WebMarkupContainer {
        public MyMarkupContainer(String id) {
            super(id);
            add(new Label("myMarkupContainer"));
        }
    }

    /**
     *
     */
    public static class MyPanel extends Panel {
        public MyPanel(String id) {
            super(id);
            add(new Label("myPanel"));
        }
    }
}
