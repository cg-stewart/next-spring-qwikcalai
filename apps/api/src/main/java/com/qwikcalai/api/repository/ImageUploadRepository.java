package com.qwikcalai.api.repository;

import com.qwikcalai.api.model.ImageUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageUploadRepository extends JpaRepository<ImageUpload, String> {
    List<ImageUpload> findByUserId(String userId);
}
