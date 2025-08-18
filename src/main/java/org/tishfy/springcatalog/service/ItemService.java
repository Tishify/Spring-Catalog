package org.tishfy.springcatalog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tishfy.springcatalog.model.Item;
import org.tishfy.springcatalog.repository.ItemRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;

    public List<Item> getAllItems() {
        log.debug("Getting all items");
        return itemRepository.findAllByOrderByItemName();
    }

    public Optional<Item> getItemById(Long itemId) {
        log.debug("Getting item by id: {}", itemId);
        return itemRepository.findById(itemId);
    }

    public Item createItem(Item item) {
        log.debug("Creating new item: {}", item.getItemName());
        if (item.getItemId() != null) {
            throw new IllegalArgumentException("Item ID should be null for new items");
        }
        return itemRepository.save(item);
    }

    public Item updateItem(Item item) {
        log.debug("Updating item: {}", item.getItemId());
        if (item.getItemId() == null) {
            throw new IllegalArgumentException("Item ID is required for updates");
        }

        Item existingItem = itemRepository.findById(item.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        // Update fields
        existingItem.setItemName(item.getItemName());
        existingItem.setItemPrice(item.getItemPrice());
        existingItem.setItemDescription(item.getItemDescription());
        existingItem.setItemPhoto(item.getItemPhoto());
        existingItem.setCategory(item.getCategory());
        existingItem.setQuantity(item.getQuantity());
        existingItem.setActive(item.isActive());

        return itemRepository.save(existingItem);
    }

    public void deleteItem(Long itemId) {
        log.debug("Deleting item: {}", itemId);
        if (!itemRepository.existsById(itemId)) {
            throw new IllegalArgumentException("Item not found");
        }
        itemRepository.deleteById(itemId);
    }

    public List<Item> searchItems(String query) {
        log.debug("Searching items with query: {}", query);
        if (query == null || query.trim().isEmpty()) {
            return getAllItems();
        }

        // Search by name (case insensitive)
        List<Item> items = itemRepository.findByItemNameContainingIgnoreCase(query.trim());

        // If no results by name, try searching by category
        if (items.isEmpty()) {
            items = itemRepository.findByCategoryContainingIgnoreCase(query.trim());
        }

        return items;
    }

    public Item toggleItemStatus(Long itemId) {
        log.debug("Toggling status for item: {}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        item.setActive(!item.isActive());
        return itemRepository.save(item);
    }
}
