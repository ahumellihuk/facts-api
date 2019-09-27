package org.camoiloc.facts.api.status;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.camoiloc.facts.dto.Stats;
import org.camoiloc.facts.dto.Status;
import org.camoiloc.facts.dto.StatusResponse;
import org.camoiloc.facts.service.FactsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Status Endpoint", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class StatusController {

    private static final Logger log = LoggerFactory.getLogger(StatusController.class);

    private final FactsService service;

    public StatusController(FactsService service) {
        this.service = service;
    }

    @ApiOperation(value = "Retrieve current status of the service", response = StatusResponse.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved fact"),
                           @ApiResponse(code = 401, message = "You are not authorized")})
    @RequestMapping(value = "/status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public StatusResponse getStatus() {
        StatusResponse response = new StatusResponse();
        response.setStatus(service.getStatus());
        if (response.getStatus() == Status.COMPLETED) {
            response.setStats(new Stats(service.getTotalFacts(), service.getUniqueFacts()));
        }
        log.debug("Returning status {}", response);
        return response;
    }

}
