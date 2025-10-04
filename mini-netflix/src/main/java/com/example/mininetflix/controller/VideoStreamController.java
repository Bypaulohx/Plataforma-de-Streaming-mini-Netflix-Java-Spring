package com.example.mininetflix.controller;

import com.example.mininetflix.service.VideoService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceRegion;
import org.springframework.http.*;
import org.springframework.http.converter.ResourceRegionHttpMessageConverter;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class VideoStreamController {

    private final VideoService service;

    public VideoStreamController(VideoService service) {
        this.service = service;
    }

    @GetMapping("/api/videos/{id}/stream")
    public ResponseEntity<ResourceRegion> stream(@PathVariable Long id,
                                                 @RequestHeader(value = "Range", required = false) String httpRangeHeader) throws IOException {
        Resource video = service.loadAsResource(id);
        long contentLength = video.contentLength();

        if (httpRangeHeader == null) {
            ResourceRegion region = new ResourceRegion(video, 0, contentLength);
            MediaType mediaType = MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(mediaType)
                    .body(region);
        }

        // Parse range (we only handle the first range)
        List<HttpRange> ranges = HttpRange.parseRanges(httpRangeHeader);
        HttpRange range = ranges.get(0);
        ResourceRegion region = range.toResourceRegion(video);

        MediaType mediaType = MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(mediaType)
                .body(region);
    }

    // download endpoint (optional)
    @GetMapping("/api/videos/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws IOException {
        Resource video = service.loadAsResource(id);
        String filename = video.getFilename();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentLength(video.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(video);
    }
}
