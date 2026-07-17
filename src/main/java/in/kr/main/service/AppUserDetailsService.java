package in.kr.main.service;

import java.util.ArrayList;
import java.util.Collections;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import in.kr.main.entity.UserEntity;
import in.kr.main.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found with this email " + email));
		return new User(userEntity.getEmail(), userEntity.getPassword(),new ArrayList<>());
	}
}
