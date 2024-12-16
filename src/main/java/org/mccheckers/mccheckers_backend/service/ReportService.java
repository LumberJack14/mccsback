package org.mccheckers.mccheckers_backend.service;

import org.mccheckers.mccheckers_backend.db.ReportDAO;
import org.mccheckers.mccheckers_backend.dto.ReportRequestDTO;
import org.mccheckers.mccheckers_backend.model.Report;

import java.util.List;

public class ReportService {
    public Report createReport(ReportRequestDTO dto) throws Exception {
        Report report = ReportDAO.createReport(dto.getModeratorId(), dto.getReason(), dto.getUserId());
        if (report == null) {
            throw new Exception("Server Error while creating new report");
        }
        return report;
    }

    public List<Report> getReportsUser(int userId) {
        return ReportDAO.getReportsUser(userId);
    }

    public List<Report> getReportsByModerator(int moderatorId) {
        return ReportDAO.getReportsByModerator(moderatorId);
    }

    public Report getReportById(int id) {
        Report report = ReportDAO.getById(id);
        if (report == null) {
            throw new IllegalArgumentException("No report found with id " + id);
        }
        return report;
    }

    public List<Report> getAllReports() {
        return ReportDAO.getAllReports();
    }
}
