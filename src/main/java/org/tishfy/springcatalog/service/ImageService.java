package org.tishfy.springcatalog.service;

import org.springframework.web.multipart.MultipartFile;
import org.tishfy.springcatalog.model.Image;
import org.tishfy.springcatalog.model.Item;

import java.util.Optional;

public interface ImageService {

    byte[] getImageData(Long itemId, Long imageId);

    Image createImage(Long itemId, MultipartFile file);

    void delete(Long itemId, Long imageId);
}
