package com.example.mininetflix.service;

import com.example.mininetflix.model.Video;
import com.example.mininetflix.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class VideoService {

    private final Path storageLocation;
    private final VideoRepository repository;

    public VideoService(@Value("${file.storage.location:uploads}") String storageLocation, VideoRepository repository) {
        this.storageLocation = Paths.get(storageLocation).toAbsolutePath().normalize();
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(storageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create storage directory", e);
        }
    }

    public Video store(MultipartFile file, String title, String description) throws IOException {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String storedFilename = UUID.randomUUID().toString() + "-" + originalFilename;
        Path target = this.storageLocation.resolve(storedFilename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        Video video = new Video();
        video.setTitle((title == null || title.isBlank()) ? originalFilename : title);
        video.setFilename(storedFilename);
        video.setContentType(file.getContentType());
        video.setSize(file.getSize());
        video.setUploadDate(LocalDateTime.now());
        video.setDescription(description);

        return repository.save(video);
    }

    public List<Video> listAll() {
        return repository.findAll();
    }

    public Video findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Video não encontrado: " + id));
    }

    public Resource loadAsResource(Long id) throws FileNotFoundException {
        Video video = findById(id);
        Path file = storageLocation.resolve(video.getFilename()).normalize();
        Resource resource = new FileSystemResource(file.toFile());
        if (!resource.exists() || !resource.isReadable()) {
            throw new FileNotFoundException("Arquivo não encontrado: " + video.getFilename());
        }
        return resource;
    }
}
