package com.dietetic.backend.controller;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dietetic.backend.entity.Image;
import com.dietetic.backend.entity.dto.AddFileRequestDTO;
import com.dietetic.backend.entity.dto.ImageResponseDTO;
import com.dietetic.backend.service.ImageService;

@RestController
@RequestMapping("images")
@CrossOrigin
public class ImagesController {
    @Autowired
    private ImageService imageService;

    @GetMapping
    public ResponseEntity<ImageResponseDTO> displayImage(@RequestParam("id") Long id) throws SQLException {
        Image image = imageService.viewById(id);
        String encodedString = Base64.getEncoder()
                .encodeToString(image.getImage().getBytes(1, (int) image.getImage().length()));
        return ResponseEntity.ok().body(ImageResponseDTO.builder().file(encodedString).id(id).build());
    }

    @PostMapping
    public String addImagePost(AddFileRequestDTO request) throws IOException, SQLException {
        byte[] bytes = request.getFile().getBytes();
        Blob blob = new SerialBlob(bytes);
        imageService.create(Image.builder().image(blob).build());
        return "created";
    }
} 