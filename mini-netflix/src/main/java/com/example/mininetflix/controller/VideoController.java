package com.example.mininetflix.controller;

import com.example.mininetflix.model.Video;
import com.example.mininetflix.service.VideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "*")
public class VideoController {

    private final VideoService service;

    public VideoController(VideoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Video> upload(@RequestPart("file") MultipartFile file,
                                        @RequestParam(value = "title", required = false) String title,
                                        @RequestParam(value = "description", required = false) String description) throws IOException {
        Video saved = service.store(file, title, description);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Video> list() {
        return service.listAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Video> get(@PathVariable Long id) {
        Video video = service.findById(id);
        return ResponseEntity.ok(video);
    }
}
