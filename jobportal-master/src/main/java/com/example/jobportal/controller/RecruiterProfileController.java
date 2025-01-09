package com.example.jobportal.controller;

import com.example.jobportal.model.RecruiterProfile;
import com.example.jobportal.model.Users;
import com.example.jobportal.repository.UsersRepository;
import com.example.jobportal.service.RecruiterProfileService;
import com.example.jobportal.util.FileUploadUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;


@Controller
@RequestMapping("/recruiter-profile")
public class RecruiterProfileController {

    private final UsersRepository usersRepository;
    private final RecruiterProfileService recruiterProfileService;

    public RecruiterProfileController(UsersRepository usersRepository,
                                      RecruiterProfileService recruiterProfileServic) {
        this.usersRepository = usersRepository;
        this.recruiterProfileService = recruiterProfileServic;
    }

    @GetMapping("/")
    public String recruiterProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName();
            Users users = usersRepository.findByEmail(username).orElseThrow(
                    () -> new UsernameNotFoundException("User"));
            Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getOne(users.getUserId());
            if (!recruiterProfile.isEmpty()) {
                model.addAttribute("profile", recruiterProfile.get());
            }
        }
        return "recruiter_profile";
    }
    @PostMapping("/addNew")
    public String addNew (RecruiterProfile recruiterProfile, @RequestParam ("image")
    MultipartFile multipartFile, Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (! (auth instanceof AnonymousAuthenticationToken)) {
            String currentUsername  = auth.getName();
            Users users = usersRepository.findByEmail(currentUsername ).orElseThrow(
                    () -> new UsernameNotFoundException("User"));
            recruiterProfile.setUserId(users);
            recruiterProfile.setUserAccountId(users.getUserId());
        }
        model.addAttribute("profile", recruiterProfile);
        String fileName = "";
        if (!multipartFile.getOriginalFilename().equals("")) {
            fileName= StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            recruiterProfile.setProfilePhoto(fileName);
        }
        RecruiterProfile saveUser= recruiterProfileService.addNew(recruiterProfile);
        String uploadDir= "photos/recruiter/"+saveUser.getUserAccountId();
        try {
            FileUploadUtil.saveFile(uploadDir,fileName,multipartFile);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "redirect:/dashboard/";
    }
}
