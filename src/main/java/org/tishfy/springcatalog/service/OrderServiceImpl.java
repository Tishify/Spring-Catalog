package org.tishfy.springcatalog.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tishfy.springcatalog.exeptions.OrderNotFoundException;
import org.tishfy.springcatalog.model.Item;
import org.tishfy.springcatalog.model.Order;
import org.tishfy.springcatalog.model.OrderItem;
import org.tishfy.springcatalog.model.User;
import org.tishfy.springcatalog.repository.ItemRepository;
import org.tishfy.springcatalog.repository.OrderRepository;
import org.tishfy.springcatalog.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id)));
    }

    @Override
    @Transactional
    public Order create(Order order) {
        order.setOrderId(null);
        User user = userRepository.findById(order.getUser().getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + order.getUser().getUserId()));
        order.setUser(user);
        for (OrderItem orderItem : order.getOrderItems()) {
            Item item = itemRepository.findById(orderItem.getItem().getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found: " + orderItem.getItem().getItemId()));
            orderItem.setOrder(order);
            orderItem.setItem(item);
        }
        Order orderDB = orderRepository.save(order);
        return orderDB;
    }

    @Override
    @Transactional
    public Optional<Order> update(Long id, Order patch) {
        return orderRepository.findById(id).map(u -> {
            u.setTotalCost(patch.getTotalCost());
            if (patch.getOrderItems() == null || patch.getOrderItems().isEmpty()) {
                u.getOrderItems().clear();
            } else {
                List<OrderItem> orderItemsForDelete = new ArrayList<>();
                for (OrderItem orderItem : u.getOrderItems()) {
                    Optional<OrderItem> patchItem = patch.getOrderItems().stream()
                            .filter(i -> i.getItem().getItemId().equals(orderItem.getItem().getItemId())).findFirst();
                    if (patchItem.isPresent()) {
                        //TODO set quantity
                    } else {
                        orderItemsForDelete.add(orderItem);
                    }
                }
                u.getOrderItems().removeAll(orderItemsForDelete);


                for (OrderItem orderItem : patch.getOrderItems()) {
                    Optional<OrderItem> patchItem = u.getOrderItems().stream()
                            .filter(i -> i.getItem().getItemId().equals(orderItem.getItem().getItemId())).findFirst();
                    if (patchItem.isEmpty()) {
                        Item item = itemRepository.findById(orderItem.getItem().getItemId())
                                .orElseThrow(() -> new RuntimeException("Item not found: " + orderItem.getItem().getItemId()));
                        u.getOrderItems().add(OrderItem.builder().order(u).item(item).build());
                    }
                }
            }
            return orderRepository.save(u);
        });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findById(id).map(order -> {
            orderRepository.delete(order);
            return null;
        });
    }
}
