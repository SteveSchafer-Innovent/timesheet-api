package com.stephenschafer.timesheet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class UploadController {
	@Autowired
	private FileDao fileDao;
	@Autowired
	private UploadDao uploadDao;
	@Autowired
	private NetsuiteClientDao netsuiteClientDao;
	@Autowired
	private NetsuiteProjectDao netsuiteProjectDao;
	@Autowired
	private NetsuiteTaskDao netsuiteTaskDao;
	@Autowired
	private NetsuiteEventDao netsuiteEventDao;
	@PersistenceContext
	EntityManager entityManager;

	@GetMapping("/uploads")
	public ApiResponse<List<UploadReportRow>> getUploads() {
		log.info("GET /uploads");
		final Query namedQuery = entityManager.createNamedQuery("UploadEntity.report");
		@SuppressWarnings("unchecked")
		final List<Object[]> list = namedQuery.getResultList();
		final List<UploadReportRow> resultList = new ArrayList<>();
		list.forEach(objArray -> {
			int i = 0;
			final Integer uploadId = (Integer) objArray[i++];
			final Date uploadDate = (Date) objArray[i++];
			final String filename = (String) objArray[i++];
			final Date minTime = (Date) objArray[i++];
			final Date maxTime = (Date) objArray[i++];
			final UploadReportRow reportRow = new UploadReportRow(uploadId, uploadDate, filename,
					minTime, maxTime);
			resultList.add(reportRow);
		});
		return new ApiResponse<>(HttpStatus.OK.value(), "Success", resultList);
	}

	@GetMapping("/upload-detail/{uploadId}")
	public ApiResponse<List<UploadRow>> getUploadDetail(@PathVariable final Integer uploadId) {
		log.info(MessageFormat.format("GET /upload-detail/{0}", uploadId));
		final List<UploadRow> resultList = new ArrayList<>();
		final List<NetsuiteEventEntity> events = netsuiteEventDao.findByUploadId(uploadId);
		events.forEach(event -> {
			final Integer eventId = event.getId();
			final Date date = event.getDate();
			final Integer taskId = event.getTaskId();
			final Optional<NetsuiteTaskEntity> optionalTask = netsuiteTaskDao.findById(taskId);
			NetsuiteTaskEntity task = null;
			NetsuiteProjectEntity project = null;
			NetsuiteClientEntity client = null;
			if (optionalTask.isPresent()) {
				task = optionalTask.get();
				final Optional<NetsuiteProjectEntity> optionalProject = netsuiteProjectDao.findById(
					task.getProjectId());
				if (optionalProject.isPresent()) {
					project = optionalProject.get();
					final Optional<NetsuiteClientEntity> optionalClient = netsuiteClientDao.findById(
						project.getClientId());
					if (optionalClient.isPresent()) {
						client = optionalClient.get();
					}
				}
			}
			final String notes = event.getNotes();
			final Double hours = event.getHours();
			final UploadRow uploadRow = new UploadRow(eventId, date, client, project, task, notes,
					hours);
			resultList.add(uploadRow);
		});
		return new ApiResponse<>(HttpStatus.OK.value(), "Success", resultList);
	}

	@PostMapping("/upload")
	public ApiResponse<List<UploadRow>> upload(@RequestParam("file") final MultipartFile file) {
		log.info("POST /upload " + file);
		if (file.isEmpty()) {
			return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(),
					"Pleaes select a file to upload", null);
		}
		final List<UploadRow> resultList = new ArrayList<>();
		try {
			final byte[] bytes = file.getBytes();
			log.info("bytes.length = " + bytes.length);
			final String filename = file.getOriginalFilename();
			log.info("file originalFilename = " + filename);
			final String pathName = "./files/" + filename;
			final Path path = Paths.get(pathName);
			log.info("path = " + path);
			final String contentType = file.getContentType();
			log.info("contentType = " + contentType);
			// Files.createDirectories(path.getParent());
			// Files.write(path, bytes);
			FileEntity fileEntity = null;
			final Optional<FileEntity> optionalFileEntity = fileDao.findByName(filename);
			if (optionalFileEntity.isPresent()) {
				fileEntity = optionalFileEntity.get();
			}
			else {
				fileEntity = fileDao.save(new FileEntity(null, filename));
			}
			final UploadEntity uploadEntity = uploadDao.save(
				new UploadEntity(null, fileEntity.getId(), new Date()));
			final InputStreamReader reader = new InputStreamReader(file.getInputStream());
			try {
				final DateFormat df = new SimpleDateFormat("MM/dd/yy");
				final DecimalFormat nf = new DecimalFormat("#,##0.00");
				final CSVFormat format = CSVFormat.EXCEL.withHeader("Date", "Client", "Project",
					"Task", "Notes", "Hours").withAllowMissingColumnNames();
				boolean foundStart = false;
				Date lastDate = null;
				final CSVParser parser = format.parse(reader);
				try {
					for (final CSVRecord record : parser) {
						final Map<String, String> map = record.toMap();
						if ("Date".equals(map.get("Date"))) {
							foundStart = true;
							continue;
						}
						if (!foundStart) {
							continue;
						}
						final String dateString = map.get("Date");
						final String clientString = map.get("Client");
						final String projectString = map.get("Project");
						final String taskString = map.get("Task");
						final String notesString = map.get("Notes");
						final String hoursString = map.get("Hours");
						Date date;
						if (dateString == null || dateString.trim().length() == 0) {
							date = lastDate;
						}
						else {
							date = df.parse(dateString);
							lastDate = date;
						}
						if (clientString == null || clientString.trim().length() == 0) {
							continue;
						}
						final Optional<NetsuiteClientEntity> optionalNetsuiteClientEntity = netsuiteClientDao.findByName(
							clientString);
						final NetsuiteClientEntity netsuiteClientEntity;
						if (optionalNetsuiteClientEntity.isPresent()) {
							netsuiteClientEntity = optionalNetsuiteClientEntity.get();
						}
						else {
							netsuiteClientEntity = netsuiteClientDao.save(
								new NetsuiteClientEntity(null, clientString));
						}
						if (projectString == null || projectString.trim().length() == 0) {
							continue;
						}
						final Optional<NetsuiteProjectEntity> optionalNetsuiteProjectEntity = netsuiteProjectDao.findByClientIdAndName(
							netsuiteClientEntity.getId(), projectString);
						final NetsuiteProjectEntity netsuiteProjectEntity;
						if (optionalNetsuiteProjectEntity.isPresent()) {
							netsuiteProjectEntity = optionalNetsuiteProjectEntity.get();
						}
						else {
							netsuiteProjectEntity = netsuiteProjectDao.save(
								new NetsuiteProjectEntity(null, projectString,
										netsuiteClientEntity.getId()));
						}
						if (taskString == null || taskString.trim().length() == 0) {
							continue;
						}
						final Optional<NetsuiteTaskEntity> optionalNetsuiteTaskEntity = netsuiteTaskDao.findByProjectIdAndName(
							netsuiteProjectEntity.getId(), taskString);
						final NetsuiteTaskEntity netsuiteTaskEntity;
						if (optionalNetsuiteTaskEntity.isPresent()) {
							netsuiteTaskEntity = optionalNetsuiteTaskEntity.get();
						}
						else {
							netsuiteTaskEntity = netsuiteTaskDao.save(new NetsuiteTaskEntity(null,
									taskString, netsuiteProjectEntity.getId()));
						}
						if ("Day Total".equals(notesString)) {
							continue;
						}
						if ("Total".equals(notesString)) {
							continue;
						}
						if ("Day Total".equals(notesString)) {
							continue;
						}
						if ("Total hours - Billable".equals(notesString)) {
							continue;
						}
						if ("Total hours - Internal".equals(notesString)) {
							continue;
						}
						final String notes = notesString;
						if (hoursString == null || hoursString.trim().length() == 0) {
							continue;
						}
						double hours;
						try {
							hours = nf.parse(hoursString).doubleValue();
						}
						catch (final Exception e) {
							log.error("Failed to parse hours", e);
							continue;
						}
						final NetsuiteEventEntity event = new NetsuiteEventEntity(null, date,
								netsuiteTaskEntity.getId(), notes, Double.valueOf(hours),
								uploadEntity.getId());
						try {
							netsuiteEventDao.save(event);
						}
						catch (final Exception e) {
							log.error("Failed to save event: " + event, e);
						}
						final UploadRow uploadRow = new UploadRow(event.getId(), date,
								netsuiteClientEntity, netsuiteProjectEntity, netsuiteTaskEntity,
								notes, hours);
						resultList.add(uploadRow);
					}
				}
				finally {
					parser.close();
				}
			}
			catch (final Exception e) {
				return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Unexpected error: " + e,
						null);
			}
			finally {
				reader.close();
			}
			return new ApiResponse<>(HttpStatus.OK.value(),
					file.getOriginalFilename() + " successfully uploaded.", resultList);
		}
		catch (final IOException e) {
			log.error("failed to upload", e);
			return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(),
					null);
		}
	}

	@Getter
	@Setter
	@ToString
	@AllArgsConstructor
	private static class FileInfo {
		private String filename;
		private String mimeType;
	}
}
