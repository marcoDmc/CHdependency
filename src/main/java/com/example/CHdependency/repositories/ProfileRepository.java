package com.example.CHdependency.repositories;

import com.example.CHdependency.entities.Profile;
import com.example.CHdependency.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

}
