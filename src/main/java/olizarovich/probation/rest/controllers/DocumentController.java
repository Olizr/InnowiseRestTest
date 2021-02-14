package olizarovich.probation.rest.controllers;

import io.swagger.annotations.*;
import olizarovich.probation.rest.exceptions.DocumentNotFoundException;
import olizarovich.probation.rest.exceptions.IllegalSortTypeException;
import olizarovich.probation.rest.models.Document;
import olizarovich.probation.rest.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/documents")
@Api(value="Documentrest", description="Operations with Document entity")
public class DocumentController {
    @Autowired
    private DocumentService service;

    @ApiOperation(value = "View a list of documents", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 400, message = "Incorrect parameter or parameters"),
    })
    @GetMapping(value = "/",  produces = "application/json")
    List<Document> all(@RequestParam(defaultValue = "") String title,
                       @RequestParam(defaultValue = "") String status,

                       @RequestParam() @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                       @ApiParam(value = "Date of document creation") Optional<LocalDate> creationDate,

                       @RequestParam(defaultValue = "") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                       @ApiParam(value = "Document created before giving date") Optional<LocalDate> creationDateBefore,

                       @RequestParam(defaultValue = "") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                       @ApiParam(value = "Document created after giving date") Optional<LocalDate> creationDateAfter,

                       @RequestParam() @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                       @ApiParam(value = "before giving date") Optional<LocalDate> executionPeriod,

                       @RequestParam(defaultValue = "") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                       @ApiParam(value = "Document execution before giving date") Optional<LocalDate> executionPeriodBefore,

                       @RequestParam(defaultValue = "") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                       @ApiParam(value = "Document execution after giving date") Optional<LocalDate> executionPeriodAfter,

                       @RequestParam(defaultValue = "-1") int customerId,
                       @RequestParam(defaultValue = "") String customerLastName,

                       @RequestParam(defaultValue = "-1") int executorId,
                       @RequestParam(defaultValue = "") String executorLastName,

                       @RequestParam(defaultValue = "") @ApiParam(value = "Setting sort order") String sort,
                       @RequestParam(defaultValue = "-1") int page,
                       @RequestParam(defaultValue = "10") int count)
    {
        List<Document> persons = new ArrayList<>();

        service.filterByTitle(title).filterByStatus(status);

        creationDate.ifPresent(d -> service.filterByCreationDate(d));
        creationDateBefore.ifPresent(d -> service.filterByCreationDateLessThan(d));
        creationDateAfter.ifPresent(d -> service.filterByCreationDateMoreThan(d));

        executionPeriod.ifPresent(d -> service.filterByExecutionDate(d));
        executionPeriodBefore.ifPresent(d -> service.filterByExecutionDateLessThan(d));
        executionPeriodAfter.ifPresent(d -> service.filterByExecutionDateMoreThan(d));

        service.filterByCustomerId(customerId).filterByCustomerLastName(customerLastName);
        service.filterByExecutorId(executorId).filterByExecutorLastName(executorLastName);

        try {
            DocumentService.DocumentSort personSort = DocumentService.DocumentSort.valueOf(sort);
            service.setSort(personSort);
        }
        catch (IllegalArgumentException ex) {
            throw new IllegalSortTypeException(sort);
        }

        if (page < 0) {
            service.findAll().forEach(persons::add);
        }
        else {
            persons = service.toPage(page, count).toList();
        }

        service.clearFilter();

        return persons;
    }

    @ApiOperation(value = "Add new document to database", response = Document.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully add new document"),
            @ApiResponse(code = 400, message = "Incorrect document entity"),
            @ApiResponse(code = 401, message = "You are not authorized"),
    })
    @PostMapping("/")
    @ResponseStatus(HttpStatus.OK)
    Document newPerson(@RequestBody Document document) {
        return service.save(document);
    }

    @ApiOperation(value = "Searching for Document with giving ID", response = Document.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved document"),
            @ApiResponse(code = 401, message = "You are not authorized"),
            @ApiResponse(code = 404, message = "The document with id you were trying to search is not found")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    Document findById(@PathVariable Integer id) {

        Optional<Document> person = service.findById(id);

        if(!person.isPresent()) {
            throw new DocumentNotFoundException(id);
        }

        return person.get();
    }

    @ApiOperation(value = "Update document with giving ID", response = Document.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated document"),
            @ApiResponse(code = 400, message = "Incorrect parameter or parameters"),
            @ApiResponse(code = 401, message = "You are not authorized"),
            @ApiResponse(code = 404, message = "The document with id you were trying to update is not found")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    Document updatePerson(@RequestBody Document newDocument, @PathVariable Integer id) {
        return service.update(newDocument, id);
    }

    @ApiOperation(value = "Deleted document with giving ID", response = Document.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deleted document"),
            @ApiResponse(code = 401, message = "You are not authorized"),
            @ApiResponse(code = 404, message = "The document with id you were trying to deleted is not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    void deletePerson(@PathVariable Integer id) {
        service.deleteById(id);
    }
}
