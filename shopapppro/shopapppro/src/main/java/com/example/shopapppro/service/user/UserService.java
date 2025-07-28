package com.example.shopapppro.service.user;

import com.example.shopapppro.components.JwtTokenUtils;
import com.example.shopapppro.dtos.UserDTO;
import com.example.shopapppro.exception.DataNotFoundException;
import com.example.shopapppro.exception.PermissionDenyException;
import com.example.shopapppro.models.Role;
import com.example.shopapppro.models.User;
import com.example.shopapppro.repository.RoleRepository;
import com.example.shopapppro.repository.UserRepository;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    private  final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private  final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private  final AuthenticationManager authenticationManager;

    @Override
    public User createUser(UserDTO userDTO) throws Exception {
        String phoneNumber = userDTO.getPhoneNumber();
//        kiểm tra sdt đã có hay chưa
        if(userRepository.existsByPhoneNumber(phoneNumber)){
            throw new DataIntegrityViolationException("phone number already exists");
        }
        // KIỂM TRA ROLL NẾU LÀ ADMIN THÌ DUYỆT KHÔNG THÌ BỎ
        Role role=roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(()-> new DataNotFoundException("role not found"));
        if(role.getName().toUpperCase().equals(Role.ADMIN)){
            throw new PermissionDenyException("you can create an admin account");
        }
        //        convert từ userdto qua user
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .build();

        newUser.setRole(role);
        // kiểm tra xem đăngb nhập bằng fb hay gg ko nếu có thì miễn ko phải thêm mật khẩu
        if(userDTO.getFacebookAccountId()==0 && userDTO.getGoogleAccountId()==0){
            String  password = userDTO.getPassword();
//            Mã hoóa mật khẩu
            String encodedPassword =passwordEncoder .encode(password);
            newUser.setPassword(encodedPassword);

        }
        return userRepository.save(newUser);
    }

    @Override
    public String login(String phoneNumber, String password) throws Exception {
       Optional<User>optionalUser= userRepository.findByPhoneNumber(phoneNumber);
        if (optionalUser.isEmpty()){
            throw  new DataNotFoundException("Invalid  name phoneNumber/ pass ");
        }
        // trả về token
        User existingUser = optionalUser.get();
//        kiểm tra pass
        if(existingUser.getFacebookAccountId()==0
                && existingUser.getGoogleAccountId()==0){
            // không phải sigin fb và gg nếu có thì thôi
            if(!passwordEncoder.matches(password,existingUser.getPassword())){
                throw new BadCredentialsException("wrong pass word");
            }

        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(phoneNumber,password,existingUser.getAuthorities());
        // bắt buộc phải sác thực với pring security
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtils.generateToken(existingUser);


    }
}
