package com.sparta.springcore2.repository;


import com.sparta.springcore2.model.Folder;
import com.sparta.springcore2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findAllByUser(User user);

    Folder findByName(String folderName);
}
