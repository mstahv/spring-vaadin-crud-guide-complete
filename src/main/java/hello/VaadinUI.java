package hello;

import com.vaadin.annotations.Theme;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

@SpringUI
@Theme("valo")
public class VaadinUI extends UI {

    @Autowired
    CustomerRepository repo;

    @Autowired
    CustomerEditor editor;

    MTable<Customer> grid = new MTable(Customer.class).withProperties(
            "id", "firstName", "lastName").withHeight("300px");
    TextField filter = new TextField();
    Button addNewBtn = new Button("New customer", FontAwesome.PLUS);

    @Override
    protected void init(VaadinRequest request) {
        // build layout
        setContent(new MVerticalLayout(
                new MHorizontalLayout(filter, addNewBtn),
                grid,
                editor
        ));

        filter.setInputPrompt("Filter by last name");

        // Hook logic to components
        // Replace listing with filtered content when user changes filter
        filter.addTextChangeListener(e -> listCustomers(e.getText()));

        // Connect selected Customer to editor or hide if none is selected
        grid.addMValueChangeListener(e -> {
            if (e.getValue() == null) {
                editor.setVisible(false);
            } else {
                editor.setEntity(e.getValue());
            }
        });

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener(e -> editor.setEntity(new Customer("", "")));

        // Listen changes made by the editor, refresh data from backend
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listCustomers(filter.getValue());
        });

        // Initialize listing
        listCustomers(null);
    }

    private void listCustomers(String text) {
        if (StringUtils.isEmpty(text)) {
            grid.setBeans(repo.findAll());
        } else {
            grid.setBeans(repo.findByLastNameStartsWithIgnoreCase(text));
        }
    }

}
