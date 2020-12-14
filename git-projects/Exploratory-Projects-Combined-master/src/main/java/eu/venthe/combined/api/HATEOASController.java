package eu.venthe.combined.api;

import lombok.Value;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RequestMapping("/hateoas-manager")
@RestController
public class HATEOASController {

    private static final String EMPLOYEES = "employees";

    @GetMapping("/employees")
    public ResponseEntity<CollectionModel<EntityModel<TestDto>>> findAll() {

        List<EntityModel<TestDto>> employeeResources = Stream.of(new TestDto(1, "2"))
                .map(employee -> EntityModel.of(employee,
                        linkTo(methodOn(HATEOASController.class).findOne(employee.getValue())).withSelfRel()
                                .andAffordance(afford(methodOn(HATEOASController.class).updateEmployee(null, employee.getValue())))
                                .andAffordance(afford(methodOn(HATEOASController.class).deleteEmployee(employee.getValue()))),
                        linkTo(methodOn(HATEOASController.class).findAll()).withRel(EMPLOYEES)))
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(
                employeeResources,
                linkTo(methodOn(HATEOASController.class).findAll()).withSelfRel()
                        .andAffordance(afford(methodOn(HATEOASController.class).newEmployee(null)))));
    }

    @PostMapping("/" + EMPLOYEES)
    public ResponseEntity<Object> newEmployee(@RequestBody TestDto employee) {
        return EntityModel.of(employee,
                linkTo(methodOn(HATEOASController.class).findOne(employee.getValue())).withSelfRel()
                        .andAffordance(afford(methodOn(HATEOASController.class).updateEmployee(null, employee.getValue())))
                        .andAffordance(afford(methodOn(HATEOASController.class).deleteEmployee(employee.getValue()))),
                linkTo(methodOn(HATEOASController.class).findAll()).withRel(EMPLOYEES)).getLink(IanaLinkRelations.SELF)
                .map(Link::getHref)
                .map(href -> {
                    try {
                        return new URI(href);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(uri -> ResponseEntity.noContent().location(uri).build())
                .orElse(ResponseEntity.badRequest().body("Unable to create " + employee));
    }

    @GetMapping("/" + EMPLOYEES +"/{value}")
    public ResponseEntity<EntityModel<TestDto>> findOne(@PathVariable int value) {
        return java.util.Optional.of(new TestDto(value, "2"))
                .map(employee -> EntityModel.of(employee,
                        linkTo(methodOn(HATEOASController.class).findOne(employee.getValue())).withSelfRel()
                                .andAffordance(afford(methodOn(HATEOASController.class).updateEmployee(null, employee.getValue())))
                                .andAffordance(afford(methodOn(HATEOASController.class).deleteEmployee(employee.getValue()))),
                        linkTo(methodOn(HATEOASController.class).findAll()).withRel(EMPLOYEES)))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/" + EMPLOYEES +"/{id}")
    public ResponseEntity<Object> updateEmployee(@RequestBody TestDto employee, @PathVariable int value) {

        final TestDto updatedEmployee = new TestDto(value, "aad");

        return EntityModel.of(updatedEmployee,
                linkTo(methodOn(HATEOASController.class).findOne(updatedEmployee.getValue())).withSelfRel()
                        .andAffordance(afford(methodOn(HATEOASController.class).updateEmployee(null, updatedEmployee.getValue())))
                        .andAffordance(afford(methodOn(HATEOASController.class).deleteEmployee(updatedEmployee.getValue()))),
                linkTo(methodOn(HATEOASController.class).findAll()).withRel(EMPLOYEES)).getLink(IanaLinkRelations.SELF)
                .map(Link::getHref).map(href -> {
                    try {
                        return new URI(href);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(uri -> ResponseEntity.noContent().location(uri).build())
                .orElse(ResponseEntity.badRequest().body("Unable to update " + employee));
    }

    @DeleteMapping("/" + EMPLOYEES +"/{id}")
    public ResponseEntity<Object> deleteEmployee(@PathVariable int value) {
        return ResponseEntity.noContent().build();
    }

    @Value(staticConstructor = "of")
    public static class TestDto {
        int value;
        String text;
    }
}
