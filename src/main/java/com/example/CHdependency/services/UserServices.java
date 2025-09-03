package com.example.CHdependency.services;

import com.example.CHdependency.configuration.ConfigAuthentication;
import com.example.CHdependency.dto.user.UserDeleteDTO;
import com.example.CHdependency.dto.user.UserPasswordDTO;
import com.example.CHdependency.dto.user.UserRequestDTO;
import com.example.CHdependency.dto.user.UserResponseDTO;
import com.example.CHdependency.entities.User;
import com.example.CHdependency.mappers.UserMapper;
import com.example.CHdependency.repositories.RefreshTokenRepository;
import com.example.CHdependency.repositories.UserRepository;
import com.example.CHdependency.utils.Utils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServices {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Utils utils;
    private final ConfigAuthentication config;
    private final RefreshTokenRepository refreshTokenRepository;

    private final S3Services s3Service;
    private final ProfileRepository profileRepository;
    private final ProfileServices profileServices;


    public UserServices(UserRepository userRepository,
                        UserMapper userMapper,
                        Utils utils,
                        ConfigAuthentication config,
                        ProfileRepository profileRepository,
                        RefreshTokenRepository refreshTokenRepository,
                        ProfileServices profileServices,
                        S3Services s3Service) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.utils = utils;
        this.config = config;
        this.refreshTokenRepository = refreshTokenRepository;
        this.s3Service = s3Service;
        this.profileRepository = profileRepository;
        this.profileServices = profileServices;
    }

    @Transactional
    public boolean updatePassword(UserPasswordDTO userDto) {
        if (userDto.getEmail().isEmpty()) return false;
        var user = userRepository.findByEmail(userDto.getEmail());
        if (user == null) return false;
        boolean isPwd = config.passwordEncoder().matches(userDto.getPassword(), user.getPassword());
        if (!isPwd) return false;
        boolean verifyPasswordIsEqual = config.passwordEncoder().matches(userDto.getNewPassword(), user.getPassword());
        if (verifyPasswordIsEqual) return false;
        user.setPassword(config.passwordEncoder().encode(userDto.getNewPassword()));
        userRepository.save(user);
        return true;
    }

    @Transactional
    public boolean delete(UserDeleteDTO userDto) {
        var user = userRepository.findByEmail(userDto.getEmail());
        if (user == null) return false;

        boolean isValid = config.passwordEncoder().matches(userDto.getPassword(), user.getPassword());
        if (!isValid) return false;

        refreshTokenRepository.deleteByUserId(user.getId());
        userRepository.delete(user);
        return true;
    }


    @Transactional
    public UserResponseDTO create(UserRequestDTO userDto) {
        var user = userMapper.forUserEntity(userDto);

        if (!utils.validateEmail(user.getEmail())) return null;
        if (!utils.validatePassword(user.getPassword())) return null;
        if (!utils.validateName(user.getName())) return null;

        user.setPassword(config.passwordEncoder().encode(user.getPassword()));

        userRepository.save(user);

        return userMapper.forResponse(user);
    }

    @Transactional
    public User getUser(String email) {
        if (email.isEmpty()) return null;
        return userRepository.findByEmail(email);

    }


    @Transactional
    public String saveImage(MultipartFile file, User user) throws Exception {
        var binaryData = file.getInputStream();
        BufferedImage buffer = ImageIO.read(binaryData);

        BufferedImage newBuffer = new BufferedImage(220, 220, buffer.TYPE_INT_RGB);
        Graphics2D graphic = newBuffer.createGraphics();
        graphic.drawImage(buffer, 0, 0, 220, 220, null);
        graphic.dispose();


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(newBuffer, "WebP", baos);
        byte[] optimizedImageBytes = baos.toByteArray();

        String filename = UUID.randomUUID().toString() + ".webp";

        s3Service.awsBucketManager(optimizedImageBytes, filename);

        Profile profile = profileServices.findProfile(user.getId()).orElse(new Profile());

        profile.setUser(user);
        profile.setImage("images/" + filename);
        profileRepository.save(profile);


        return filename;
    }

    @Transactional
    public Optional<User> getUserId(Long id) {
        if (id == null) return null;
        return userRepository.findById(id);
    }

    @Transactional
    public Optional<User> getName(String name) {
        if (name.isEmpty()) return null;
        return userRepository.findByName(name);
    }
}


