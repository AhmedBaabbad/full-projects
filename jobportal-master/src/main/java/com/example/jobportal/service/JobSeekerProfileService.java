package com.example.jobportal.service;

import com.example.jobportal.model.JobSeekerProfile;
import com.example.jobportal.model.RecruiterProfile;
import com.example.jobportal.model.Users;
import com.example.jobportal.repository.JobSeekerProfileRepository;
import com.example.jobportal.repository.UsersRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JobSeekerProfileService {

    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final UsersRepository usersRepository;

    public JobSeekerProfileService(JobSeekerProfileRepository jobSeekerProfileRepository,
                                   UsersRepository usersRepository) {
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.usersRepository = usersRepository;
    }
    public Optional<JobSeekerProfile> getJobSeekerProfile(Integer jobSeekerProfileId) {
       return jobSeekerProfileRepository.findById(jobSeekerProfileId);
    }

    public JobSeekerProfile addNew(JobSeekerProfile jobSeekerProfile) {
        return jobSeekerProfileRepository.save(jobSeekerProfile);
    }

    public JobSeekerProfile getCurrentSeekerProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)){
            String username = auth.getName();
            Users user = usersRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("user not found"));
            Optional<JobSeekerProfile> seekerProfile= getJobSeekerProfile(user.getUserId());
            return seekerProfile.orElse(null);
        }
        return null;
    }
}
