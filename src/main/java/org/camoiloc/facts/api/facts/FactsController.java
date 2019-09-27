package org.camoiloc.facts.api.facts;

import io.swagger.annotations.*;
import org.camoiloc.facts.dto.Fact;
import org.camoiloc.facts.dto.Status;
import org.camoiloc.facts.exception.UnsupportedDirectionException;
import org.camoiloc.facts.service.FactsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Api(value = "Facts Endpoint", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class FactsController {

    private static final Logger log = LoggerFactory.getLogger(FactsController.class);

    private final FactsService service;

    public FactsController(FactsService service) {
        this.service = service;
    }

    @ApiOperation(value = "Retrieve all stored fact IDs", response = Set.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved list"),
                           @ApiResponse(code = 401, message = "You are not authorized"),
                           @ApiResponse(code = 503, message = "Service is not ready")})
    @RequestMapping(value = "/facts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<String>> getFacts() {
        log.debug("All facts requested");
        if (service.getStatus() == Status.COMPLETED) {
            Set<String> factIds = service.getFactIDs();
            log.debug("Returning fact IDs [{}]", factIds);
            return ResponseEntity.ok(factIds);
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @ApiOperation(value = "Retrieve specific fact by ID", response = Fact.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved fact"),
                           @ApiResponse(code = 401, message = "You are not authorized"),
                           @ApiResponse(code = 404, message = "The fact is not found"),
                           @ApiResponse(code = 501, message = "Unsupported translation direction"),
                           @ApiResponse(code = 503, message = "Service is not ready")})
    @RequestMapping(value = "/facts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getFact(@ApiParam(value = "Fact ID", required = true) @PathVariable String id,
                                  @ApiParam(value = "Desired language", required = false)
                                  @RequestParam(value = "lang", required = false) String language) {
        log.debug("Fact {} requested", id);
        if (service.getStatus() == Status.COMPLETED) {
            Fact fact = null;
            try {
                fact = service.getFact(id, language);
            } catch (UnsupportedDirectionException e) {
                log.warn("Fact [{}] was requested with an unsupported language", id, e);
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(e.getLocalizedMessage());
            }
            if (fact == null) {
                log.debug("Fact {} was not found!", id);
                return ResponseEntity.notFound().build();
            }
            log.debug("Returning fact [{}]", fact);
            return ResponseEntity.ok(fact);
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

}
