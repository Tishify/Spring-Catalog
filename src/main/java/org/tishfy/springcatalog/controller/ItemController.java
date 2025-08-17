package org.tishfy.springcatalog.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.tishfy.springcatalog.model.Image;
import org.tishfy.springcatalog.model.Item;
import org.tishfy.springcatalog.service.ImageService;
import org.tishfy.springcatalog.service.ItemService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ImageService imageService;

    @GetMapping
    public List<Item> getItems() {
        return itemService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Item> get(@PathVariable @Positive Long id) {
        return itemService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item create(@RequestBody @Valid Item item) {

        return itemService.create(item);
    }

    @PutMapping("/{id}")
    public Optional<Item> update(@PathVariable @Positive Long id, @RequestBody @Valid Item item) throws ChangeSetPersister.NotFoundException {
        return itemService.update(id, item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id) throws ChangeSetPersister.NotFoundException {
        itemService.delete(id);
    }

    @GetMapping(value = "/{id}/images/{imageId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<ByteArrayResource> getImage(@PathVariable @Positive Long id,
                                                      @PathVariable @Positive Long imageId) {
        byte[] data = imageService.getImageData(id, imageId);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(data.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"image-" + imageId + ".bin\"")
                .body(resource);
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<Image> createImage(@PathVariable @Positive Long id,
                             @RequestPart("file") MultipartFile file) {

        Image image = imageService.createImage(id, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(image);
    }

    @DeleteMapping("/{id}/images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable @Positive Long id,
                            @PathVariable @Positive Long imageId) {
        imageService.delete(id, imageId);
    }

}
