package org.tishfy.springcatalog.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tishfy.springcatalog.exeptions.ItemNotFoundException;
import org.tishfy.springcatalog.model.Item;
import org.tishfy.springcatalog.repository.ItemRepository;
import org.tishfy.springcatalog.service.ItemService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id)));
    }

    @Override
    @Transactional
    public Item create(Item item) {
        item.setItemId(null);
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Optional<Item> update(Long id, Item patch) {
        return itemRepository.findById(id).map(u -> {
            u.setItemDescription(patch.getItemDescription());
            u.setItemName(patch.getItemName());
            u.setItemPrice(patch.getItemPrice());
            return itemRepository.save(u);
        });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findById(id).map(item -> {
            itemRepository.delete(item);
            return null;
        });

    }
}
