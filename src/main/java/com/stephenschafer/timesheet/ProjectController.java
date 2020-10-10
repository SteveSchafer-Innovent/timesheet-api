package com.stephenschafer.timesheet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/projects")
public class ProjectController {
	@Autowired
	ProjectService projectService;

	@PostMapping
	@ResponseBody
	public ApiResponse<ProjectEntity> postProject(@RequestBody final ProjectEntity project) {
		log.info("POST /projects " + project);
		return new ApiResponse<>(HttpStatus.OK.value(), "Project saved successfully.",
				projectService.insert(project));
	}

	@GetMapping("/{parentId}")
	@ResponseBody
	public ApiResponse<List<FindProjectResult>> getProjects(
			@PathVariable(required = true) final Integer parentId,
			final HttpServletRequest request) {
		log.info("GET /projects parentId=" + parentId);
		return new ApiResponse<>(HttpStatus.OK.value(), "Project list fetched successfully.",
				projectService.findByParent(parentId));
	}

	@GetMapping("/root")
	@ResponseBody
	public ApiResponse<List<FindProjectResult>> getRootProjects(final HttpServletRequest request) {
		log.info("GET /projects");
		return new ApiResponse<>(HttpStatus.OK.value(), "Project list fetched successfully.",
				projectService.findByRoot());
	}
}
