package olizarovich.probation.rest.controllers;

import io.swagger.annotations.*;
import olizarovich.probation.rest.exceptions.IllegalSortTypeException;
import olizarovich.probation.rest.exceptions.PersonNotFoundException;
import olizarovich.probation.rest.models.Person;
import olizarovich.probation.rest.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/persons")
@Api(value = "Person rest", description = "Operations with Person entity")
public class PersonController {
    @Autowired
    private PersonService service;

    @ApiOperation(value = "View a list of persons", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 400, message = "Incorrect parameter or parameters"),
    })

    @GetMapping(value = "", produces = "application/json")
    List<Person> all(@RequestParam(defaultValue = "") String firstName,
                     @RequestParam(defaultValue = "") String lastName,

                     @RequestParam() @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                     @ApiParam(value = "Date of person birthday") Optional<LocalDate> birthDate,

                     @RequestParam(defaultValue = "") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                     @ApiParam(value = "Search for person with birthday before giving date")
                             Optional<LocalDate> birthDateBefore,

                     @RequestParam(defaultValue = "") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                     @ApiParam(value = "Search for person with birthday after giving date")
                             Optional<LocalDate> birthDateAfter,

                     @RequestParam(defaultValue = "") @ApiParam(value = "Setting sort order") String sort,
                     @RequestParam(defaultValue = "-1") int page,
                     @RequestParam(defaultValue = "10") int count,
                     Authentication authentication) {
        List<Person> persons = new ArrayList<>();

        service.filterByFirstName(firstName).filterByLastName(lastName);
        birthDate.ifPresent(d -> service.filterByBirthDate(d));
        birthDateBefore.ifPresent(d -> service.filterByBirthDateLessThan(d));
        birthDateAfter.ifPresent(d -> service.filterByBirthDateMoreThan(d));

        if (!sort.isEmpty()) {
            try {
                PersonService.PersonSort personSort = PersonService.PersonSort.valueOf(sort);
                service.setSort(personSort);
            } catch (IllegalArgumentException ex) {
                throw new IllegalSortTypeException(sort);
            }
        }

        /*
         * If user is admin, then load all users
         */
        if (isUserHasRole(authentication, "ADMIN_ROLE")) {
            service.includeDeleted();
        }

        if (page < 0) {
            service.findAll().forEach(persons::add);
        } else {
            persons = service.toPage(page, count).toList();
        }

        service.clearFilter();

        return persons;
    }

    @ApiOperation(value = "Add new person to database", response = Person.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully add new person"),
            @ApiResponse(code = 400, message = "Incorrect person entity"),
            @ApiResponse(code = 401, message = "You are not authorized"),
    })
    @PostMapping("")
    Person newPerson(@RequestBody Person person) {
        return service.save(person);
    }

    @ApiOperation(value = "Searching for Person with giving ID", response = Person.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved person"),
            @ApiResponse(code = 400, message = "Incorrect parameter or parameters"),
            @ApiResponse(code = 401, message = "You are not authorized"),
            @ApiResponse(code = 404, message = "The person with id you were trying to search is not found")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    Person findById(@PathVariable Integer id) {

        Optional<Person> person = service.findById(id);

        if (!person.isPresent()) {
            throw new PersonNotFoundException(id);
        }

        return person.get();
    }

    @ApiOperation(value = "Update person with giving ID", response = Person.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated person"),
            @ApiResponse(code = 400, message = "Incorrect parameter or parameters"),
            @ApiResponse(code = 401, message = "You are not authorized"),
            @ApiResponse(code = 404, message = "The person with id you were trying to update is not found")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    Person updatePerson(@RequestBody Person newPerson, @PathVariable Integer id) {
        return service.update(newPerson, id);
    }

    @ApiOperation(value = "Deleted person with giving ID", response = Person.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted person"),
            @ApiResponse(code = 401, message = "You are not authorized"),
            @ApiResponse(code = 404, message = "The person with id you were trying to deleted is not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    void deletePerson(@PathVariable Integer id) {
        service.deleteById(id);
    }

    private boolean isUserHasRole(Authentication authentication, String role) {
        boolean hasRole = false;
        try {
            for (GrantedAuthority i : authentication.getAuthorities()) {
                hasRole = i.getAuthority().equals(role);
                if (hasRole) {
                    return true;
                }
            }
        } catch (NullPointerException ex) {
            hasRole = false;
        }

        return hasRole;
    }
}