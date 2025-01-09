package com.example.jobportal.controller;

import com.example.jobportal.model.*;
import com.example.jobportal.service.*;
import com.example.jobportal.util.ConstantsUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class JobSeekerApplyController {

    private final JobPostActivityService jobPostActivityService;
    private final UsersService usersService;
    private final JobSeekerSaveService jobSeekerSaveService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final JobSeekerProfileService jobSeekerProfileService;
    private final RecruiterProfileService recruiterProfileService;

    public JobSeekerApplyController(JobPostActivityService jobPostActivityService, UsersService usersService,
                                    JobSeekerSaveService jobSeekerSaveService, JobSeekerApplyService jobSeekerApplyService,
                                    JobSeekerProfileService jobSeekerProfileService, RecruiterProfileService recruiterProfileService) {
        this.jobPostActivityService = jobPostActivityService;
        this.usersService = usersService;
        this.jobSeekerSaveService = jobSeekerSaveService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.recruiterProfileService = recruiterProfileService;
    }

    @GetMapping("job-details-apply/{id}")
    public String display (@PathVariable ("id") int id, Model model) {

        JobPostActivity jobPostActivityDetails= jobPostActivityService.getOne(id);
        List<JobSeekerApply> jobSeekerApplyList= jobSeekerApplyService.getJobCandidates(jobPostActivityDetails);
        List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getJobsCandidates(jobPostActivityDetails);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)){
            if(auth.getAuthorities().contains(new SimpleGrantedAuthority(ConstantsUtil.ROLE_RECRUITER))) {
                RecruiterProfile user = recruiterProfileService.getCurrentRecruiterProfile();
                if (user != null) {
                    model.addAttribute("applyList", jobSeekerApplyList);
                }
            }
                else {
                    JobSeekerProfile user = jobSeekerProfileService.getCurrentSeekerProfile();
                    if (user != null) {
                        boolean exists = false;
                        boolean saved = false;
                        for (JobSeekerApply jobSeekerApply : jobSeekerApplyList) {
                            if (jobSeekerApply.getUserId().getUserAccountId() == user.getUserAccountId()) {
                                exists = true;
                                break;
                            }
                        }
                        for (JobSeekerSave jobSeekerSave : jobSeekerSaveList) {
                            if (jobSeekerSave.getUserId().getUserAccountId() == user.getUserAccountId()) {
                                saved = true;
                                break;
                            }
                        }
                        model.addAttribute("alreadyApplied", exists);
                        model.addAttribute("alreadySaved", saved);

                    }
                }

        }
        JobSeekerApply jobSeekerApply = new JobSeekerApply();
        model.addAttribute("applyJob",jobSeekerApply);

        model.addAttribute("jobDetails", jobPostActivityDetails);
        model.addAttribute("user",usersService.getCurrentUserProfile());
        return "job-details";

    }

    @PostMapping("dashboard/edit/{id}")
    public String editJob (@PathVariable ("id") int id, Model model) {
        JobPostActivity jobPostActivityDetails= jobPostActivityService.getOne(id);
        model.addAttribute("jobPostActivity", jobPostActivityDetails);
        model.addAttribute("user",usersService.getCurrentUserProfile());
        return "add-jobs";
    }
    @PostMapping("job-details/apply/{id}")
    public String apply (@PathVariable("id") int id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof AnonymousAuthenticationToken) {
            throw new RuntimeException("User is not authenticated");
        }

        String currentUsername = auth.getName();
        Users user = usersService.findByEmail(currentUsername);
        Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getJobSeekerProfile(user.getUserId());
        JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);

        if (seekerProfile.isEmpty() || jobPostActivity == null) {
            throw new RuntimeException("Invalid user profile or job post activity");
        }

        JobSeekerApply jobSeekerApply = new JobSeekerApply();
        jobSeekerApply.setUserId(seekerProfile.get());
        jobSeekerApply.setJob(jobPostActivity);
        jobSeekerApply.setApplyDate(new Date());

        jobSeekerApplyService.addNew(jobSeekerApply);

        return "redirect:/dashboard/";
    }

}
