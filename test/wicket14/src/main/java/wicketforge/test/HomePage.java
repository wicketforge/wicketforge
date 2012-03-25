package wicketforge.test;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.FormComponentFeedbackBorder;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.ArrayList;
import java.util.List;

/**
 * Homepage
 */
public class HomePage extends WebPage {

	private static final long serialVersionUID = 1L;

    /**
	 * Constructor that is invoked when page is invoked without a session.
	 *
	 * @param parameters
	 *            Page parameters
	 */
    public HomePage(final PageParameters parameters) {
        setOutputMarkupId(true);

        //--------------

        // issue 87 -> Missing references on MarkupContainer.replace(...) and Component#replaceWith(...)
        add(new Label("messageReplace", "click below to change label with replace"));
        AjaxLink linkReplace = new AjaxLink("linkReplace") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                HomePage.this.replace(new Label("messageReplace", "replaced"));
                target.addComponent(HomePage.this);
            }
        };
        add(linkReplace);

        //--------------

        // issue 87 -> Missing references on MarkupContainer.replace(...) and Component#replaceWith(...)
        final Label label = new Label("messageReplaceWith", "click below to change label with replaceWith");
        add(label);
        AjaxLink linkReplaceWith = new AjaxLink("linkReplaceWith") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                label.replaceWith(new Label("messageReplaceWith", "replaced"));
                target.addComponent(HomePage.this);
            }
        };
        add(linkReplaceWith);

        //--------------

        // issue 48 ->
        // NPE when creating anonymous class with base of WebMarkupContainerWithAssociatedMarkup 
        add(new InnerPanel("innerPanel") {
            @Override
            protected String getText() {
                return "Inner Panel";
            }
        });

        //--------------

/*
        new PropertyModel<String>(new Person(), "firstName");

        Person p = new Person();
        p.setLastName("Smith");
        new PropertyModel<String>(p, "lastName");

        IModel<Person> personModel = new LoadableDetachableModel<Person>() {
            @Override
            protected Person load() {
                Person p = new Person();
                p.setLastName("Smith");
                return p;
            }
        };
        new PropertyModel<String>(personModel, "homeAddress.city");
*/

        add(new FullScanAlertListView("allDetails"));

        //--------------

        // issue 80 -> add() instead item.add() inside populateItem
        List<String> labels = new ArrayList<String>();
        add(new ListView<String>("allDetails2", labels) {
            @Override
            protected void populateItem(ListItem<String> item) {
                item.add(new Label("detailsLabel", item.getModelObject()));
                add(new Label("detailsLabel", item.getModelObject())); // should get warning
            }
        });

        ListView<String> lv = new ListView<String>("allDetails2", labels) {
            @Override
            protected void populateItem(ListItem<String> item) {
                item.add(new Label("detailsLabel", item.getModelObject()));
                add(new Label("detailsLabel", item.getModelObject())); // should get warning
            }
        };
        add(lv);

        //--------------

        // issue 68 -> WicketForge does not recognize FormComponentPanel as a Panel
        TextField firstName = new TextField("firstName");
        FormComponentFeedbackBorder firstNameBorder = new FormComponentFeedbackBorder("firstNameBorder");
        firstNameBorder.add(firstName);
        add(firstNameBorder);
    }

    private class FullScanAlertListView extends ListView {
        public FullScanAlertListView(String wicketId) {
            super(wicketId);
        }

        @Override protected void populateItem(ListItem item) {
            Form detailsForm = new Form("detailsForm");
            item.add(detailsForm);
        }
    }

    public static abstract class InnerPanel extends Panel {
        public InnerPanel(String id) {
            super(id);
            add(new Label("innerPanelText", getText()));
        }

        protected abstract String getText();
    }
}
