package pl.lg.charity.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pl.lg.charity.domain.entities.User;
import pl.lg.charity.domain.repositories.UserRepository;
import pl.lg.charity.dtos.RegistrationDataDTO;
import pl.lg.charity.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.security.Principal;

@Service
@Transactional
@Slf4j
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DefaultUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegistrationDataDTO prepareEditDataUser(Principal principal) {
        User showDataLoggedUser = userRepository.findUserByEmail(principal.getName());
        ModelMapper model = new ModelMapper();
        return model.map(showDataLoggedUser, RegistrationDataDTO.class);
    }

    @Override
    public void processEditDataUser(RegistrationDataDTO dataDTO, Principal principal, HttpServletRequest req)
            throws ServletException {
        User editDataLoggedUser = userRepository.findUserByEmail(principal.getName());
        ModelMapper model = new ModelMapper();
        userRepository.editUserData(model.map(dataDTO, User.class).getEmail(), model.map(dataDTO, User.class).getUsername(),
                model.map(dataDTO, User.class).getLastName(), editDataLoggedUser.getId());
        req.logout();
    }

    @Override
    public void processEditPasswordUser(String password, Principal principal, HttpServletRequest req)
            throws ServletException {
        String email = principal.getName();
        userRepository.editUserPassword(email, passwordEncoder.encode(password));
        req.logout();
    }


}
