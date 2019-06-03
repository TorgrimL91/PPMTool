package no.kristiania.springreact.ppmtool.web;

import no.kristiania.springreact.ppmtool.domain.ProjectTask;
import no.kristiania.springreact.ppmtool.services.MapValidationErrorService;
import no.kristiania.springreact.ppmtool.services.ProjectTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/api/backlog")
@CrossOrigin
public class BacklogController {

    @Autowired
    private ProjectTaskService projectTaskService;

    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @PostMapping("/{backlogId}")
    public ResponseEntity<?> addProjectTaskToBacklog(@Valid @RequestBody ProjectTask projectTask,
                                                     BindingResult result, @PathVariable String backlogId, Principal principal){

        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationService(result);
        if(errorMap != null){
            return errorMap;
        }

        ProjectTask projectTask1 = projectTaskService.addProjectTask(backlogId, projectTask, principal.getName());

        return new ResponseEntity<ProjectTask>(projectTask1, HttpStatus.CREATED);

    }

    @GetMapping("/{backlogId}")
    public Iterable<ProjectTask> getProjectBacklog(@PathVariable String backlogId, Principal principal){

        return projectTaskService.findBacklogById(backlogId, principal.getName());
    }

    @GetMapping("/{backlogId}/{ptid}")
    public ResponseEntity<?> getProjectTask(@PathVariable String backlogId, @PathVariable String ptid, Principal principal){
        ProjectTask projectTask = projectTaskService.findPTByProjectSequence(backlogId, ptid, principal.getName());

        return new ResponseEntity<ProjectTask>(projectTask, HttpStatus.OK);
    }

    @PatchMapping("/{backlogId}/{ptid}")
    public ResponseEntity<?> updateProjectTask(@Valid @RequestBody ProjectTask projectTask, BindingResult result,
                                               @PathVariable String backlogId, @PathVariable String ptid, Principal principal){
        ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationService(result);
        if(errorMap != null){
            return errorMap;
        }
        ProjectTask updatedTask = projectTaskService.updateByProjectSequence(projectTask, backlogId, ptid, principal.getName());

        return new ResponseEntity<ProjectTask>(updatedTask, HttpStatus.OK);
    }


    @DeleteMapping("/{backlogId}/{ptid}")
    public ResponseEntity<?> deleteProjectTask(@PathVariable String backlogId, @PathVariable String ptid, Principal principal){
        projectTaskService.deletePTByProjectSequence(backlogId, ptid, principal.getName());

        return new ResponseEntity<String>("Project Task " + ptid + " was deleted successfully", HttpStatus.OK);
    }

}
