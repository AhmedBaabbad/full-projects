package com.example.jobportal.service;

import com.example.jobportal.model.JobPostActivity;
import com.example.jobportal.model.JobSeekerProfile;
import com.example.jobportal.model.JobSeekerSave;
import com.example.jobportal.repository.JobSeekerSaveRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobSeekerSaveService {
    private final JobSeekerSaveRepository jobSeekerSaveRepository;

    public JobSeekerSaveService(JobSeekerSaveRepository jobSeekerSaveRepository) {
        this.jobSeekerSaveRepository = jobSeekerSaveRepository;
    }

    public List<JobSeekerSave> getCandidateJobs (JobSeekerProfile userAccoountId){
        return jobSeekerSaveRepository.findByUserId(userAccoountId);
    }

    public List<JobSeekerSave> getJobsCandidates (JobPostActivity job){
        return jobSeekerSaveRepository.findByJob(job);
    }


    public void addNew(JobSeekerSave jobSeekerSave) {

        jobSeekerSaveRepository.save(jobSeekerSave);
    }
}
