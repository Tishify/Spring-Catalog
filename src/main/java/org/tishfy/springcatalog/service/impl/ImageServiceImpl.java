package org.tishfy.springcatalog.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tishfy.springcatalog.model.Image;
import org.tishfy.springcatalog.model.Item;
import org.tishfy.springcatalog.repository.ImageRepository;
import org.tishfy.springcatalog.repository.ItemRepository;
import org.tishfy.springcatalog.service.ImageService;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ItemRepository itemRepository;

    @Override
    public Image createImage(Long itemId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found: " + itemId));

        Image image = new Image();
        image.setItem(item);
        try {
            image.setImage(file.getBytes());
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read file", e);
        }
        Image saved = imageRepository.save(image);
        return saved;
    }

    @Override
    public byte[] getImageData(Long itemId, Long imageId) {
        Image img = imageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image not found: " + imageId));
        if (!img.getItem().getItemId().equals(itemId)) {
            throw new EntityNotFoundException("Image " + imageId + " does not belong to item " + itemId);
        }
        return img.getImage();
    }

    @Override
    public void delete(Long itemId, Long imageId) {
        // проверим принадлежность
//        if (!imageRepository.existsByIdAndItem_ItemId(imageId, itemId)) {
//            throw new EntityNotFoundException("Image not found: " + imageId + " for item " + itemId);
//        }
        imageRepository.deleteById(imageId);
    }
}

