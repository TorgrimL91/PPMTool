package no.kristiania.springreact.ppmtool.services;

import no.kristiania.springreact.ppmtool.domain.Backlog;
import no.kristiania.springreact.ppmtool.domain.Project;
import no.kristiania.springreact.ppmtool.domain.ProjectTask;
import no.kristiania.springreact.ppmtool.exceptions.ProjectNotFoundException;
import no.kristiania.springreact.ppmtool.repositories.BacklogRepository;
import no.kristiania.springreact.ppmtool.repositories.ProjectRepository;
import no.kristiania.springreact.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import sun.print.BackgroundLookupListener;

import java.util.List;

@Service
public class ProjectTaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username){


            Backlog backlog = projectService.findProjectByIdentifier(projectIdentifier, username).getBacklog(); //backlogRepository.findByProjectIdentifier(projectIdentifier);
            projectTask.setBacklog(backlog);

            Integer BacklogSequence = backlog.getPTSequence();
            BacklogSequence++;

            backlog.setPTSequence(BacklogSequence);

            projectTask.setProjectSequence(projectIdentifier + "-" + BacklogSequence);
            projectTask.setProjectIdentifier(projectIdentifier);

            if (projectTask.getStatus() == "" || projectTask.getStatus() == null){
                projectTask.setStatus("TO_DO");
            }

            if(projectTask.getPriority() == null || projectTask.getPriority() == 0){
                projectTask.setPriority(3);
            }

            return projectTaskRepository.save(projectTask);



    }

    public Iterable<ProjectTask> findBacklogById(String id, String username){

       projectService.findProjectByIdentifier(id, username);

        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

    public ProjectTask findPTByProjectSequence(String backlogId, String ptid, String username){

       projectService.findProjectByIdentifier(backlogId, username);

        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(ptid);

        if(projectTask == null){
            throw new ProjectNotFoundException("Project task '" + ptid + "' not found");
        }
        if(!projectTask.getProjectIdentifier().equals(backlogId)){
            throw new ProjectNotFoundException("Project task '" + ptid + "' does not exist in project '"  +backlogId + "'");
        }

        return projectTask;
    }

    public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlogId, String ptid, String username){
        ProjectTask projectTask = findPTByProjectSequence(backlogId, ptid, username);

        projectTask = updatedTask;

        return projectTaskRepository.save(projectTask);

    }

    public void deletePTByProjectSequence(String backlogId, String ptid, String username){
        ProjectTask projectTask = findPTByProjectSequence(backlogId, ptid, username);

        projectTaskRepository.delete(projectTask);
    }

}
