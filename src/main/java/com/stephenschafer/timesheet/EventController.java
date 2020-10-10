package com.stephenschafer.timesheet;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
public class EventController {
	@Autowired
	EventService eventService;
	@Autowired
	UserService userService;

	@GetMapping("/dup/{id}")
	@ResponseBody
	public ApiResponse<EditedEvent> dupEvent(@PathVariable(required = true) final Integer id,
			final HttpServletRequest request) {
		log.info("GET /dup " + id);
		final EditedEvent editedEvent = eventService.findById(id.intValue());
		editedEvent.setDatetime(new Date());
		final PostedEvent postedEvent = editedEvent.getPostedEvent();
		final String username = (String) request.getAttribute("username");
		final UserEntity user = userService.findByUsername(username);
		EditedEvent result;
		try {
			result = eventService.insert(postedEvent, user.getId());
		}
		catch (final Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return new ApiResponse<>(HttpStatus.OK.value(), "Event saved successfully.", result);
	}

	@PostMapping("/events")
	@ResponseBody
	public ApiResponse<EditedEvent> postEvent(@RequestBody final PostedEvent postedEvent,
			final HttpServletRequest request) {
		log.info("POST /events " + postedEvent);
		final String username = (String) request.getAttribute("username");
		final UserEntity user = userService.findByUsername(username);
		EditedEvent result;
		try {
			result = eventService.insert(postedEvent, user.getId());
		}
		catch (final Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return new ApiResponse<>(HttpStatus.OK.value(), "Event saved successfully.", result);
	}

	@PutMapping("/events")
	@ResponseBody
	public ApiResponse<EditedEvent> putEvent(@RequestBody final EditedEvent editedEvent,
			final HttpServletRequest request) {
		log.info("PUT /events " + editedEvent);
		final String username = (String) request.getAttribute("username");
		final UserEntity user = userService.findByUsername(username);
		EditedEvent result;
		try {
			result = eventService.update(editedEvent, user.getId());
		}
		catch (final Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return new ApiResponse<>(HttpStatus.OK.value(), "Event updated successfully.", result);
	}

	@GetMapping("/events/{dateString}")
	@ResponseBody
	public ApiResponse<List<ReportEvent>> getEvents(
			@PathVariable(required = true) final String dateString,
			final HttpServletRequest request) {
		log.info("GET /events dateString=" + dateString);
		final String username = (String) request.getAttribute("username");
		final UserEntity user = userService.findByUsername(username);
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		final Date day;
		try {
			day = df.parse(dateString);
		}
		catch (final ParseException e) {
			throw new BadRequestException("Bad date format", e);
		}
		return new ApiResponse<>(HttpStatus.OK.value(), "Event list fetched successfully.",
				eventService.findByDay(day, user.getId()));
	}

	@GetMapping("/event/{id}")
	@ResponseBody
	public ApiResponse<EditedEventWithAncestry> getEvent(
			@PathVariable(required = true) final Integer id) {
		log.info("GET /event id=" + id);
		final EditedEvent editedEvent = eventService.findById(id.intValue());
		final List<List<Integer>> projects = eventService.getProjectAncestry(
			editedEvent.getProjects());
		return new ApiResponse<>(HttpStatus.OK.value(), "Event fetched successfully.",
				editedEvent.getEventWithAncestry(projects));
	}

	@DeleteMapping("/event/{id}")
	@ResponseBody
	public ApiResponse<Integer> deleteEvent(@PathVariable(required = true) final Integer id) {
		log.info("DELETE /event id=" + id);
		final int count = eventService.delete(id);
		return new ApiResponse<>(HttpStatus.OK.value(), "Event successfully deleted.",
				Integer.valueOf(count));
	}
}
