package com.mrthinkj.videoservice.repository;

import com.mrthinkj.videoservice.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByTitleLike(String title);
    Optional<Video> findByVideoUUID(String videoUUID);
}
