package com.example.jobportal.config;

import com.example.jobportal.util.ConstantsUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CusomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        UserDetails userDetails= (UserDetails) authentication.getPrincipal();
        String username=userDetails.getUsername();
        System.out.println("The user name "+username +"is logged in ");
        boolean hasJobSeekerRole= authentication.getAuthorities().stream().anyMatch(
                authority -> authority.getAuthority().equals(ConstantsUtil.ROLE_JOB_SEEKER));
        boolean hasJobRecruiterRole= authentication.getAuthorities().stream().anyMatch(
                authority -> authority.getAuthority().equals(ConstantsUtil.ROLE_RECRUITER));
        if (hasJobSeekerRole || hasJobRecruiterRole) {
            response.sendRedirect("/dashboard/");
        }
    }
}
