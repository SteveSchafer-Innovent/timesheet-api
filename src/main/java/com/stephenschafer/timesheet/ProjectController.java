package com.stephenschafer.timesheet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class ProjectController {
	@Autowired
	ProjectService projectService;

	@PostMapping("/projects")
	@ResponseBody
	public ApiResponse<ProjectEntity> postProject(@RequestBody final ProjectEntity project) {
		log.info("POST /projects " + project);
		return new ApiResponse<>(HttpStatus.OK.value(), "Project inserted successfully.",
				projectService.insert(project));
	}

	@PutMapping("/projects")
	@ResponseBody
	public ApiResponse<ProjectEntity> putProject(@RequestBody final ProjectEntity project) {
		log.info("PUT /projects " + project);
		return new ApiResponse<>(HttpStatus.OK.value(), "Project updated successfully.",
				projectService.update(project));
	}

	@DeleteMapping("/project/{id}")
	@ResponseBody
	public ApiResponse<Void> deleteProject(@PathVariable(required = true) final Integer id) {
		log.info("DELETE /project/" + id);
		projectService.delete(id);
		return new ApiResponse<>(HttpStatus.OK.value(), "Project deleted successfully", null);
	}

	@GetMapping("/project/canDelete/{id}")
	@ResponseBody
	public ApiResponse<Boolean> canDeleteProject(@PathVariable(required = true) final Integer id,
			final HttpServletRequest request) {
		log.info("GET /project/canDelete/" + id);
		return new ApiResponse<>(HttpStatus.OK.value(), "Can delete project",
				projectService.canDelete(id));
	}

	@GetMapping("/projects/{parentId}")
	@ResponseBody
	public ApiResponse<List<FindProjectResult>> getProjects(
			@PathVariable(required = true) final Integer parentId,
			final HttpServletRequest request) {
		log.info("GET /projects/" + parentId);
		return new ApiResponse<>(HttpStatus.OK.value(), "Project list fetched successfully.",
				projectService.findByParent(parentId));
	}

	@GetMapping("/projects/root")
	@ResponseBody
	public ApiResponse<List<FindProjectResult>> getRootProjects(final HttpServletRequest request) {
		log.info("GET /projects/root");
		return new ApiResponse<>(HttpStatus.OK.value(), "Project list fetched successfully.",
				projectService.findByRoot());
	}

	@GetMapping("/project/{id}")
	@ResponseBody
	public ApiResponse<ProjectEntity> getProject(@PathVariable(required = true) final Integer id,
			final HttpServletRequest request) {
		log.info("GET /project/" + id);
		return new ApiResponse<>(HttpStatus.OK.value(), "Project fetched successfully.",
				projectService.findById(id));
	}
}
