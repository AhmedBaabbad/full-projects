package com.example.jobportal.controller;

import com.example.jobportal.model.JobPostActivity;
import com.example.jobportal.model.JobSeekerProfile;
import com.example.jobportal.model.JobSeekerSave;
import com.example.jobportal.model.Users;
import com.example.jobportal.service.JobPostActivityService;
import com.example.jobportal.service.JobSeekerProfileService;
import com.example.jobportal.service.JobSeekerSaveService;
import com.example.jobportal.service.UsersService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class JobSeekerSaveController {

    private final UsersService usersService;
    private final JobSeekerSaveService jobSeekerSaveService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerProfileService jobSeekerProfileService;

    public JobSeekerSaveController(UsersService usersService, JobSeekerSaveService jobSeekerSaveService,
                                   JobPostActivityService jobPostActivityService, JobSeekerProfileService jobSeekerProfileService) {
        this.usersService = usersService;
        this.jobSeekerSaveService = jobSeekerSaveService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerProfileService = jobSeekerProfileService;
    }
    @PostMapping("job-details/save/{id}")
    public String save(@PathVariable("id") int id, JobSeekerSave jobSeekerSave) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!(auth instanceof AnonymousAuthenticationToken)){
            String username = auth.getName();
            Users user= usersService.findByEmail(username);
            Optional<JobSeekerProfile> jobSeekerProfile=jobSeekerProfileService.getJobSeekerProfile(user.getUserId());
            JobPostActivity JobPostActivity= jobPostActivityService.getOne(id);

            if(jobSeekerProfile.isPresent() && JobPostActivity!=null){
                jobSeekerSave.setJob(JobPostActivity);
                jobSeekerSave.setUserId(jobSeekerProfile.get());
            }
            else {
                throw new RuntimeException(" user not found");
            }
            jobSeekerSaveService.addNew(jobSeekerSave);
        }
        return "redirect:/dashboard/";
    }

    @GetMapping("saved-jobs/")
    public String savedJobs (Model model) {
        List<JobPostActivity> jobPost= new ArrayList<>();
        Object currentUserProfile = usersService.getCurrentUserProfile();
        List<JobSeekerSave> jobSeekerSaveList= jobSeekerSaveService.getCandidateJobs((JobSeekerProfile) currentUserProfile);
        for (JobSeekerSave jobSeekerSave: jobSeekerSaveList) {
            jobPost.add(jobSeekerSave.getJob());
        }
        model.addAttribute("jobPost", jobPost);
        model.addAttribute("User", currentUserProfile);
        return "saved-jobs";
    }
}
