package com.example.mininetflix.repository;

import com.example.mininetflix.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
