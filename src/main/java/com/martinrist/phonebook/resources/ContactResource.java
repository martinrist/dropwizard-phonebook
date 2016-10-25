package com.martinrist.phonebook.resources;

import com.martinrist.phonebook.dao.ContactDao;
import com.martinrist.phonebook.representations.Contact;
import org.skife.jdbi.v2.DBI;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Path("/contact")
@Produces(MediaType.APPLICATION_JSON)
public class ContactResource {

    private final ContactDao contactDao;
    private final Validator validator;

    public ContactResource(DBI jdbi, Validator validator)
    {
        this.contactDao = jdbi.onDemand(ContactDao.class);
        this.validator = validator;
    }

    @GET
    @Path("/{id}")
    public Response getContact(@PathParam("id") int id)
    {
        Contact contact = contactDao.getContactById(id);
        return Response.ok(contact).build();
    }

    @POST
    public Response createContact(Contact contact) throws URISyntaxException {

        return validateAndPerform(contact,
                c -> {
                    int newContactId = contactDao.createContact(contact.getFirstName(), contact.getLastName(), contact.getPhone());
                    try {
                        URI location = new URI(String.valueOf(newContactId));
                        return Response.created(location).build();
                    } catch (URISyntaxException e) {
                        return Response.created(null).build();
                    }
                });

    }

    @DELETE
    @Path("/{id}")
    public Response deleteContact(@PathParam("id") int id)
    {
        contactDao.deleteContact(id);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}")
    public Response updateContact(@PathParam("id") int id,
                                  Contact contact)
    {
        return validateAndPerform(contact,
                c -> {
                    contactDao.updateContact(id, contact.getFirstName(), contact.getLastName(), contact.getPhone());
                    return Response.ok(new Contact(id, contact.getFirstName(), contact.getLastName(), contact.getPhone())).build();
                });
    }

    private Response validateAndPerform(Contact contact, Function<Contact, Response> func) {

        List<String> validationMessages = validateContact(contact);
        if (validationMessages.isEmpty()) {
            return func.apply(contact);
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(validationMessages)
                    .build();
        }
    }

    private List<String> validateContact(final Contact contact) {

        Set<ConstraintViolation<Contact>> violations = validator.validate(contact);
        return violations.stream()
                .map(v -> v.getPropertyPath().toString() + ": " + v.getMessage())
                .collect(Collectors.toList());

    }
}
