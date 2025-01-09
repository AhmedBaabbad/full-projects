package com.example.jobportal.service;

import com.example.jobportal.model.JobSeekerProfile;
import com.example.jobportal.model.RecruiterProfile;
import com.example.jobportal.model.Users;
import com.example.jobportal.repository.JobSeekerProfileRepository;
import com.example.jobportal.repository.RecruiterProfileRepository;
import com.example.jobportal.repository.UsersRepository;
import com.example.jobportal.util.ConstantsUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersService(UsersRepository usersRepository, RecruiterProfileRepository recruiterProfileRepository,
                        JobSeekerProfileRepository jobSeekerProfileRepository,
                        PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users createUser(Users users) {
        users.setActive(true);
        users.setRegistrationDate(new Date(System.currentTimeMillis()));
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        Users savedUsers = usersRepository.save(users);
        int userTypeId = users.getUserTypeId().getUserTypeId();
        if (userTypeId == 1) {
            recruiterProfileRepository.save(new RecruiterProfile(savedUsers));
        } else {
            jobSeekerProfileRepository.save(new JobSeekerProfile(savedUsers));
        }

        return savedUsers;
    }

    public Optional<Users> getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    public Object getCurrentUserProfile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            Users user = usersRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("username not found"));
            int userId = user.getUserId();
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(ConstantsUtil.ROLE_RECRUITER))) {
                return recruiterProfileRepository.findById(userId).orElse(new RecruiterProfile());
            } else {
                return jobSeekerProfileRepository.findById(userId).orElse(new JobSeekerProfile());
            }
        }
 return null;
    }

    public Users getCurrentUser() {

        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName();
            Users user=  usersRepository.findByEmail(username).orElseThrow(
                    () -> new UsernameNotFoundException("username not found"));
            return user;

        }
        return null;
    }

    public Users findByEmail(String username) {
        return usersRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("username not found"));
    }
}
