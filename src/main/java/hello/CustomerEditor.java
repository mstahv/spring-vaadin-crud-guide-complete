package hello;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.MBeanFieldGroup;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * A simple very simple form built form bolts and nuts. As your real application
 * is probably much more complicated than this example, you could re-use this
 * form from multiple places. Now, in this example this component is only used
 * in VaadinUI.
 * <p>
 * In a real world application you'll most likely using a super class for your
 * forms - less code, better UX. See e.g. AbstractForm in Viritin
 * (https://vaadin.com/addon/viritin).
 */
@SpringComponent
@UIScope
public class CustomerEditor extends AbstractForm<Customer> {

    @Autowired
    CustomerRepository repository;

    /* Fields to edit customer properties */
    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");

    public CustomerEditor() {
        // wire action buttons to save, delete reset
        setSavedHandler(entity->repository.save(entity));
        setResetHandler(entity->setEntity(entity));
        setVisible(false);
    }

    @Override
    protected Component createContent() {
        return new MVerticalLayout(
                firstName,
                lastName,
                getToolbar()
        ).withMargin(false);
    }

    public interface ChangeHandler {

        void onChange();
    }

    @Override
    public MBeanFieldGroup<Customer> setEntity(Customer entity) {
        // Edit fresh entity
        final boolean persisted = entity.getId() != null;
        if(persisted) {
            entity = repository.findOne(entity.getId());
            setDeleteHandler(customer->repository.delete(customer));
        } else {
            setDeleteHandler(null);
        }
        setVisible(true);
        focusFirst();
        return super.setEntity(entity);
    }

    public void setChangeHandler(ChangeHandler h) {
        // ChangeHandler is notified when either save or delete
        // is clicked
        getSaveButton().addClickListener(e -> h.onChange());
        getDeleteButton().addClickListener(e -> h.onChange());
    }

}
