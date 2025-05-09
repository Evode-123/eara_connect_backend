package com.earacg.earaConnect.security;

import com.earacg.earaConnect.repository.CommissionerGeneralRepository;
import com.earacg.earaConnect.repository.CommitteeMembersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CommitteeMembersRepository committeeMembersRepository;
    private final CommissionerGeneralRepository commissionerGeneralRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Try to find user in committee members
        var committeeMember = committeeMembersRepository.findByEmail(email);
        if (committeeMember.isPresent()) {
            return committeeMember.get();
        }
        
        // Try to find user in commissioner generals
        var commissioner = commissionerGeneralRepository.findByEmail(email);
        if (commissioner.isPresent()) {
            return commissioner.get();
        }
        
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}