package com.example.jobportal.repository;

import com.example.jobportal.model.JobPostActivity;
import com.example.jobportal.model.JobSeekerApply;
import com.example.jobportal.model.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply,Long> {

    List<JobSeekerApply> findByUserId(JobSeekerProfile userId);

    List<JobSeekerApply> findByJob(JobPostActivity job);

}
