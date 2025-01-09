package com.example.jobportal.controller;

import com.example.jobportal.model.JobSeekerProfile;
import com.example.jobportal.model.Skills;
import com.example.jobportal.model.Users;
import com.example.jobportal.repository.UsersRepository;
import com.example.jobportal.service.JobSeekerProfileService;
import com.example.jobportal.util.FileDownloadUtil;
import com.example.jobportal.util.FileUploadUtil;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/job-seeker-profile")
public class JobSeekerProfileController {

    private final JobSeekerProfileService jobSeekerProfileService ;
    private final UsersRepository usersRepository ;

    public JobSeekerProfileController(JobSeekerProfileService jobSeekerProfileService, UsersRepository usersRepository) {
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.usersRepository = usersRepository;
    }
    @GetMapping("/")
    public String jobSeekerProfile (Model model) {
        JobSeekerProfile jobSeekerProfile = new JobSeekerProfile();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<Skills> skillsList= new ArrayList<>();
        if (!(auth instanceof AnonymousAuthenticationToken)){
            String username = auth.getName();
            Users user= usersRepository.findByEmail(username).orElseThrow(
                    ()->new UsernameNotFoundException("user not found"));
            Optional<JobSeekerProfile> seekerProfile= jobSeekerProfileService.
                    getJobSeekerProfile(user.getUserId());
            if(seekerProfile.isPresent()){
                jobSeekerProfile = seekerProfile.get();
                if (jobSeekerProfile.getSkills().isEmpty()) {
                    skillsList.add(new Skills());
                    jobSeekerProfile.setSkills(skillsList);
                }

            }
            model.addAttribute("skills", skillsList);
            model.addAttribute("profile", jobSeekerProfile);
        }
        return "job-seeker-profile";
    }
    @PostMapping("/addNew")
    public String addNew (JobSeekerProfile jobSeekerProfile,
                          @RequestParam ("image") MultipartFile image,
                          @RequestParam ("pdf") MultipartFile pdf,
                          Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName();
            Users user = usersRepository.findByEmail(username).orElseThrow(
                    () -> new UsernameNotFoundException("user not found"));
            jobSeekerProfile.setUserId(user);
            jobSeekerProfile.setUserAccountId(user.getUserId());
        }
        List<Skills> skillsList= new ArrayList<>();
        model.addAttribute("profile", jobSeekerProfile);
        model.addAttribute("skills", skillsList);
        for (Skills skills : jobSeekerProfile.getSkills()) {
            skills.setJobSeekerProfile(jobSeekerProfile);
        }
        String imageName="";
        String resumeName="";
        if (!Objects.equals(image.getOriginalFilename(),"")){
            imageName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            jobSeekerProfile.setProfilePhoto(imageName);
        }
        if (!Objects.equals(pdf.getOriginalFilename(),"")){
            resumeName = StringUtils.cleanPath(Objects.requireNonNull(pdf.getOriginalFilename()));
            jobSeekerProfile.setResume(resumeName);
        }
        JobSeekerProfile seekerProfile = jobSeekerProfileService.addNew(jobSeekerProfile);
        try {

            String uploadDir= "photos/candidate/"+ seekerProfile.getUserAccountId();
            if (Objects.equals(image.getOriginalFilename(),"")) {
                FileUploadUtil.saveFile(uploadDir,imageName,image);
            }
            if (Objects.equals(pdf.getOriginalFilename(),"")) {
                FileUploadUtil.saveFile(uploadDir, resumeName, pdf);
            }
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }

        return "redirect:/dashboard/";
    }
    @GetMapping("/{id}")
    public String candidateProfile(@PathVariable("id") int id ,Model model) {
        Optional<JobSeekerProfile> seekerProfile= jobSeekerProfileService.getJobSeekerProfile(id);
        model.addAttribute("profile", seekerProfile.get());
        return "job-seeker-profile";

    }
    @GetMapping("/downloadResume")
    public ResponseEntity<?> downloadResume(@RequestParam(value="fileName") String fileName,
                                            @RequestParam(value="userID") String userId){
        FileDownloadUtil downloadUtil = new FileDownloadUtil();
        Resource resource = null;

        try {
            resource = downloadUtil.getFileAsResourse("photos/candidate/" + userId, fileName);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }

        if (resource == null) {
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);

    }
}