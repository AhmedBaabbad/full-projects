package com.example.jobportal.repository;

import com.example.jobportal.model.JobPostActivity;
import com.example.jobportal.model.JobSeekerProfile;
import com.example.jobportal.model.JobSeekerSave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave,Integer> {

    List<JobSeekerSave> findByUserId(JobSeekerProfile userAccountid);

    List<JobSeekerSave> findByJob(JobPostActivity job);

}
