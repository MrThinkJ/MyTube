package com.mrthinkj.videoservice.repository;

import com.mrthinkj.videoservice.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    Optional<Video> findByVideoUUID(String videoUUID);
}
