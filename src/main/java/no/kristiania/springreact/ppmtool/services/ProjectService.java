package no.kristiania.springreact.ppmtool.services;

import no.kristiania.springreact.ppmtool.domain.Backlog;
import no.kristiania.springreact.ppmtool.domain.Project;
import no.kristiania.springreact.ppmtool.exceptions.ProjectIdException;
import no.kristiania.springreact.ppmtool.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public Project saveOrUpdateProject(Project project){

        try{
            project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());

            if(project.getId()==null){
                Backlog backlog = new Backlog();
                project.setBacklog(backlog);
                backlog.setProject(project);
                backlog.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
            }

            return projectRepository.save(project);
        }catch (Exception e){
            throw new ProjectIdException("Project ID '" + project.getProjectIdentifier().toUpperCase() + "' already exists");
        }
    }

    public Project findProjectByIdentifier(String projectId){

        Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());

        if(project == null){
            throw new ProjectIdException("Project ID '" + projectId + "' does not exists");

        }

        return project;
    }

    public Iterable<Project> findAllProjects(){
        return  projectRepository.findAll();
    }

    public void deleteProjectByIdentifier(String projectId){
        Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());

        if(project == null){
            throw new ProjectIdException("Cannot delete project with ID '" + projectId + "'. This project does not exist");
        }

        projectRepository.delete(project);
    }


}
