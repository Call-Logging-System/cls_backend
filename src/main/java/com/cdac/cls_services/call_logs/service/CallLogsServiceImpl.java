package com.cdac.cls_services.call_logs.service;

import com.cdac.cls_services.call_logs.dto.*;
import com.cdac.cls_services.call_logs.models.CallLogModel;
import com.cdac.cls_services.call_logs.models.OfficeModel;
import com.cdac.cls_services.call_logs.repositories.CallLogRepository;
import com.cdac.cls_services.call_logs.repositories.OfficeRepository;
import com.cdac.cls_services.call_logs.repositories.UserRepository;
import com.cdac.cls_services.phone_book.dto.DeleteOfficeDto;
import lombok.AllArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@AllArgsConstructor
public class CallLogsServiceImpl implements CallLogsService{

    private CallLogRepository callLogRepo;
    private OfficeRepository officeRepo;
    private UserRepository userRepo;

    @Override
    public List<CallLogResponseDto> getCallLogsList() {
        return callLogRepo.getCallLogs();
    }

    @Override
    public void saveOffice(AddOfficeReqDto dto) {

        boolean exists = officeRepo.existsByOfficeUserName(dto.getUserName());
        if(!exists){
            OfficeModel office = new OfficeModel();
            office.setOfficeUserName(dto.getUserName());
            office.setOfficeLevel(dto.getOfficeLevel());

            officeRepo.save(office);
        }
    }

    @Override
    public List<DropdownDto> getUsersDropdown() {
        return userRepo.findByIsActiveTrueAndRole(2L)
                .stream()
                .map(user -> {
                    DropdownDto dto = new DropdownDto();
                    dto.setKey(user.getId().intValue());
                    dto.setValue(user.getName());
                    return dto;
                })
                .toList();
    }

    @Override
    public void saveCallLog(AddCallLogDto dto) {
        OfficeModel office = officeRepo.findByOfficeUserName(dto.getOfficeUserName());

        if(office == null){
            OfficeModel officeModel = new OfficeModel();
            officeModel.setOfficeUserName(dto.getOfficeUserName());
            officeModel.setOfficeLevel(dto.getOfficeLevel());

            office = officeRepo.save(officeModel);
        }

        CallLogModel model = new CallLogModel();
        model.setCallDate(LocalDate.parse(dto.getCallDate()));
        model.setIssueReported(dto.getIssueReported());
        model.setIssueType(dto.getIssueType().charAt(0));
        model.setDescription(dto.getDescription());
        model.setOfficeId(Integer.valueOf(office.getId().toString()));
        model.setReportedTo(dto.getReportedTo());
        model.setSolvedBy(dto.getSolvedBy());
        model.setPriority(dto.getPriority().charAt(0));
        model.setStatus(dto.getStatus().charAt(0));
        model.setIsReleased(dto.getIsReleased().equals("Y") ? true : false);
        model.setCallStartTime(dto.getCallStartTime());
        model.setCallEndTime(dto.getCallEndTime());
        model.setCreatedBy(dto.getReportedTo());

        if (dto.getCallStartTime() != null && dto.getCallEndTime() != null) {
            long totalSeconds = ChronoUnit.SECONDS.between(dto.getCallStartTime(), dto.getCallEndTime());
            long timeTaken = (long) Math.ceil(totalSeconds / 60.0);
            model.setTimeTakenMinutes((int) timeTaken);
        }

        callLogRepo.save(model);
    }

    @Override
    public void delete(DeleteCallLogDto dto) {
        boolean exists = callLogRepo.existsById(dto.getId());

        if(exists){
            callLogRepo.deleteById(dto.getId());
        }
    }

    @Override
    public CallLogResponseDto get(GetCallLogDto dto) {

        return callLogRepo.getCallLogById(dto.getId()).orElseThrow(()-> new RuntimeException("No Call Log Found"));
    }

    @Override
    public void update(AddCallLogDto dto) {
        OfficeModel office = officeRepo.findByOfficeUserName(dto.getOfficeUserName());

        if(office == null){
            OfficeModel officeModel = new OfficeModel();
            officeModel.setOfficeUserName(dto.getOfficeUserName());
            officeModel.setOfficeLevel(dto.getOfficeLevel());

            office = officeRepo.save(officeModel);
        }

        CallLogModel model = callLogRepo.findById(dto.getId()).orElseThrow(()-> new RuntimeException("No Record Found"));

        model.setCallDate(LocalDate.parse(dto.getCallDate()));
        model.setIssueReported(dto.getIssueReported());
        model.setIssueType(dto.getIssueType().charAt(0));
        model.setDescription(dto.getDescription());
        model.setOfficeId(Integer.valueOf(office.getId().toString()));
        model.setReportedTo(dto.getReportedTo());
        model.setSolvedBy(dto.getSolvedBy());
        model.setPriority(dto.getPriority().charAt(0));
        model.setStatus(dto.getStatus().charAt(0));
        model.setIsReleased(dto.getIsReleased().equals("Y") ? true : false);
        model.setCallStartTime(dto.getCallStartTime());
        model.setCallEndTime(dto.getCallEndTime());

        if (dto.getTimeTakenMinutes() != null) {
            model.setTimeTakenMinutes(dto.getTimeTakenMinutes());
        } else if (dto.getCallStartTime() != null && dto.getCallEndTime() != null) {
            // fallback: recalculate only if not provided
            long totalSeconds = ChronoUnit.SECONDS.between(dto.getCallStartTime(), dto.getCallEndTime());
            long timeTaken = (long) Math.ceil(totalSeconds / 60.0);
            model.setTimeTakenMinutes((int) timeTaken);
        }

        callLogRepo.save(model);
    }

    @Override
    public List<OfficeModel> getOffices() {
        return officeRepo.findAll();
    }

    @Override
    public byte[] exportAllCallLogs() {
        List<CallLogResponseDto> logs = callLogRepo.getCallLogs();

        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Call Logs");

            // --- Fonts & Styles ---
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setFontName("Cambria");

            Font dataFont = wb.createFont();
            dataFont.setFontHeightInPoints((short) 11);
            dataFont.setFontName("Cambria");

            CellStyle headerStyle = wb.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setWrapText(true);
            headerStyle.setVerticalAlignment(VerticalAlignment.TOP);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle dataStyle = wb.createCellStyle();
            dataStyle.setFont(dataFont);
            dataStyle.setWrapText(true);
            dataStyle.setVerticalAlignment(VerticalAlignment.TOP);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            CellStyle dateStyle = wb.createCellStyle();
            dateStyle.setFont(dataFont);
            dateStyle.setWrapText(false);
            dateStyle.setVerticalAlignment(VerticalAlignment.TOP);
            dateStyle.setBorderBottom(BorderStyle.THIN);
            dateStyle.setBorderTop(BorderStyle.THIN);
            dateStyle.setBorderLeft(BorderStyle.THIN);
            dateStyle.setBorderRight(BorderStyle.THIN);
            CreationHelper ch = wb.getCreationHelper();
            dateStyle.setDataFormat(ch.createDataFormat().getFormat("dd/mm/yyyy"));

            CellStyle timeStyle = wb.createCellStyle();
            timeStyle.setFont(dataFont);
            timeStyle.setVerticalAlignment(VerticalAlignment.TOP);
            timeStyle.setBorderBottom(BorderStyle.THIN);
            timeStyle.setBorderTop(BorderStyle.THIN);
            timeStyle.setBorderLeft(BorderStyle.THIN);
            timeStyle.setBorderRight(BorderStyle.THIN);
            timeStyle.setDataFormat(ch.createDataFormat().getFormat("hh:mm AM/PM"));

            // --- Header row ---
            String[] headers = {
                    "Date",
                    "Issue Reported",
                    "Type(Bug,Support,Change,Backend)",
                    "Description",
                    "Issue Reported To",
                    "Reported BY (Division/name of Person)",
                    "Is Released",
                    "Release Date",
                    "Call Start Time",
                    "Call END Time",
                    "Issue Actually Solved By",
                    "Time Taken for Closing Issue(in Hrs)"
            };

            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(110);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // --- Data rows ---
            int rowNum = 1;
            for (CallLogResponseDto log : logs) {
                Row row = sheet.createRow(rowNum++);
                row.setHeightInPoints(60);

                // Date
                Cell dateCell = row.createCell(0);
                if (log.getCallDate() != null) {
                    dateCell.setCellValue(log.getCallDate());
                    dateCell.setCellStyle(dateStyle);
                } else {
                    dateCell.setCellValue("");
                    dateCell.setCellStyle(dataStyle);
                }

                // Issue Reported
                Cell issueCell = row.createCell(1);
                issueCell.setCellValue(log.getIssueReported() != null ? log.getIssueReported() : "");
                issueCell.setCellStyle(dataStyle);

                // Type
                Cell typeCell = row.createCell(2);
                typeCell.setCellValue(resolveIssueType(String.valueOf(log.getIssueType())));
                typeCell.setCellStyle(dataStyle);

                // Description
                Cell descCell = row.createCell(3);
                descCell.setCellValue(log.getDescription() != null ? log.getDescription() : "");
                descCell.setCellStyle(dataStyle);

                // Issue Reported To
                Cell reportedToCell = row.createCell(4);
                reportedToCell.setCellValue(log.getReportedTo() != null ? String.valueOf(log.getReportedTo()) : "");
                reportedToCell.setCellStyle(dataStyle);

                // Reported By
                Cell reportedByCell = row.createCell(5);
                reportedByCell.setCellValue(log.getReportedBy() != null ? log.getReportedBy() : "");
                reportedByCell.setCellStyle(dataStyle);

                // Is Released
                Cell releasedCell = row.createCell(6);
                releasedCell.setCellValue(Boolean.TRUE.equals(log.getIsReleased()) ? "Yes" : "No");
                releasedCell.setCellStyle(dataStyle);

                // Release Date
                Cell releaseDateCell = row.createCell(7);
                if (log.getReleaseDate() != null) {
                    releaseDateCell.setCellValue(log.getReleaseDate());
                    releaseDateCell.setCellStyle(dateStyle);
                } else {
                    releaseDateCell.setCellValue("");
                    releaseDateCell.setCellStyle(dataStyle);
                }

                // Call Start Time
                Cell startCell = row.createCell(8);
                if (log.getCallStartTime() != null) {
                    startCell.setCellValue(log.getCallStartTime().toString());
                } else {
                    startCell.setCellValue("");
                }
                startCell.setCellStyle(dataStyle);

                // Call End Time
                Cell endCell = row.createCell(9);
                if (log.getCallEndTime() != null) {
                    endCell.setCellValue(log.getCallEndTime().toString());
                } else {
                    endCell.setCellValue("");
                }
                endCell.setCellStyle(dataStyle);

                // Solved By
                Cell solvedByCell = row.createCell(10);
                solvedByCell.setCellValue(log.getSolvedBy() != null ? String.valueOf(log.getSolvedBy()) : "");
                solvedByCell.setCellStyle(dataStyle);

                // Time Taken
                Cell timeTakenCell = row.createCell(11);
                if (log.getTimeTakenMinutes() != null && log.getTimeTakenMinutes() > 0) {
                    int hrs = log.getTimeTakenMinutes() / 60;
                    int mins = log.getTimeTakenMinutes() % 60;
                    String timeTaken = hrs > 0
                            ? (mins > 0 ? hrs + "hr " + mins + "min" : hrs + "hr")
                            : mins + "min";
                    timeTakenCell.setCellValue(timeTaken);
                } else {
                    timeTakenCell.setCellValue("");
                }
                timeTakenCell.setCellStyle(dataStyle);
            }

            // --- Column widths (matching original) ---
            sheet.setColumnWidth(0,  13 * 256);   // Date
            sheet.setColumnWidth(1,  35 * 256);   // Issue Reported
            sheet.setColumnWidth(2,  14 * 256);   // Type
            sheet.setColumnWidth(3,  30 * 256);   // Description
            sheet.setColumnWidth(4,  18 * 256);   // Reported To
            sheet.setColumnWidth(5,  25 * 256);   // Reported By
            sheet.setColumnWidth(6,  12 * 256);   // Is Released
            sheet.setColumnWidth(7,  14 * 256);   // Release Date
            sheet.setColumnWidth(8,  14 * 256);   // Start Time
            sheet.setColumnWidth(9,  14 * 256);   // End Time
            sheet.setColumnWidth(10, 20 * 256);   // Solved By
            sheet.setColumnWidth(11, 20 * 256);   // Time Taken

            // Freeze header row
            sheet.createFreezePane(0, 1);

            wb.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String resolveIssueType(String c) {
        if (c == null) return "";
        return switch (c) {
            case "B" -> "Bug";
            case "S" -> "Support";
            case "C" -> "Change";
            case "K" -> "Backend";
            default  -> c;
        };
    }

    private String resolvePriority(String c) {
        if (c == null) return "";
        return switch (c) {
            case "L" -> "Low";
            case "M" -> "Medium";
            case "H" -> "High";
            case "C" -> "Critical";
            default  -> c;
        };
    }

    private String resolveStatus(String c) {
        if (c == null) return "";
        return switch (c) {
            case "O" -> "Open";
            case "P" -> "In Progress";
            case "D" -> "Done";
            case "C" -> "Closed";
            default  -> c;
        };
    }

    private CellStyle createHeaderStyle(Workbook wb) {
        XSSFWorkbook xwb = (XSSFWorkbook) wb;
        XSSFCellStyle style = xwb.createCellStyle();

        Font font = xwb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);

        // #1C3F6E — your CLS primary navy
        XSSFColor navy = new XSSFColor(new byte[]{(byte) 28, (byte) 63, (byte) 110}, null);
        style.setFillForegroundColor(navy);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.MEDIUM);
        return style;
    }

    private CellStyle createWrapStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setWrapText(true);
        style.setVerticalAlignment(VerticalAlignment.TOP);
        return style;
    }


}
