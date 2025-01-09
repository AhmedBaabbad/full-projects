package com.example.jobportal.service;

import com.example.jobportal.model.*;
import com.example.jobportal.repository.JobPostActivityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class JobPostActivityService {

    private final JobPostActivityRepository jobPostActivityRepository;
    public JobPostActivityService(JobPostActivityRepository jobPostActivityRepository) {
        this.jobPostActivityRepository = jobPostActivityRepository;
    }
    public JobPostActivity addNew(JobPostActivity jobPostActivity) {
        return jobPostActivityRepository.save(jobPostActivity);
    }

    public List<RecruiterJobDto> getRecruiterJob (int recruiter){
        List<IRecruiterJobs> recruiterJobDtos = jobPostActivityRepository.getRecruiterJobs(recruiter);
        List<RecruiterJobDto> recruiterJobDtoList = new ArrayList<>();
        for (IRecruiterJobs rec : recruiterJobDtos) {
            JobLocation loc = new JobLocation(rec.getLocationId(),rec.getCity(),rec.getState(),rec.getCountry());
            JobCompany comp= new JobCompany(rec.getCompanyId(),rec.getName(),"");
            recruiterJobDtoList.add(new RecruiterJobDto(rec.getTotalCandidates(),rec.getJob_post_id(),rec.getJob_title(),loc,comp));
        }
        return recruiterJobDtoList;
    }

    public JobPostActivity getOne(int id) {
        return jobPostActivityRepository.findById(id).orElseThrow(()-> new RuntimeException("Job not found"));
    }

    public List<JobPostActivity> getAll() {
        return jobPostActivityRepository.findAll();
    }

    public List<JobPostActivity> search(String job, String location, List<String> type, List<String> remote, LocalDate searchDate) {
        if (searchDate == null) {
            return jobPostActivityRepository.searchWithoutDate(job, location, remote, type);
        }
        return jobPostActivityRepository.search(job, location, remote, type, searchDate);
    }
}
