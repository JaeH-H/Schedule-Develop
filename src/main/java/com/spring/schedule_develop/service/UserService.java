package com.spring.schedule_develop.service;

import com.spring.schedule_develop.dto.LoginRequestDto;
import com.spring.schedule_develop.dto.UserResponseDto;
import com.spring.schedule_develop.entity.User;
import com.spring.schedule_develop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import jakarta.servlet.http.HttpSession;


import java.util.List;



@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final HttpSession session;


    @Transactional
    public UserResponseDto createUser(String userName, String email, String password) {

        User user = new User(userName, email, password);
        User savedUser = userRepository.save(user);

        return new UserResponseDto(savedUser.getUserId(),savedUser.getUserName(), savedUser.getEmail(), savedUser.getCreateAt());

    }


    @Transactional
    public List<UserResponseDto> findAllUsers(){

        return userRepository.findAll().stream().map(UserResponseDto::toDto).toList();
    }


    @Transactional
    public void updatePassword(Long userId, String oldPassword, String newPassword){
        User user = userRepository.findByIdOrElseThrow(userId);

        if(!user.getPassword().equals(oldPassword)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Password does not match");
        }

        user.updatePassword(newPassword);
    }

    @Transactional
    public UserResponseDto deleteUser(Long userId){
        User user = userRepository.findByIdOrElseThrow(userId);

        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");
        }

        userRepository.delete(user);


       session.invalidate();

        return UserResponseDto.toDto(user);
    }

    public User login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail());
        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");
        }
        return user;
    }


    public User findById(Long userId){
        return userRepository.findByIdOrElseThrow(userId);


    }

}
