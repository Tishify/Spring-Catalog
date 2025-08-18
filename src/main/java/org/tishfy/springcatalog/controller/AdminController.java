package org.tishfy.springcatalog.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.tishfy.springcatalog.model.Item;
import org.tishfy.springcatalog.service.ItemService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final ItemService itemService;

    @GetMapping("/panel")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminPanel(Model model) {
        log.debug("Admin panel accessed");

        List<Item> items = itemService.getAllItems();
        model.addAttribute("items", items);

        return "admin-panel";
    }

    @GetMapping("/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editItem(@RequestParam(required = false) Long itemId, Model model) {
        log.debug("Edit item page accessed with itemId: {}", itemId);

        Item item;
        if (itemId != null) {
            item = itemService.getItemById(itemId)
                    .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        } else {
            item = new Item();
        }

        model.addAttribute("item", item);
        model.addAttribute("isEdit", itemId != null);

        return "admin-edit-item";
    }

    @PostMapping("/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String saveItem(@ModelAttribute Item item,
                           @RequestParam(required = false) Long itemId,
                           RedirectAttributes redirectAttributes) {
        try {
            log.debug("Saving item: {}", item.getItemName());

            if (itemId != null) {
                item.setItemId(itemId);
                itemService.updateItem(item);
                redirectAttributes.addFlashAttribute("successMessage", "Item updated successfully!");
            } else {
                itemService.createItem(item);
                redirectAttributes.addFlashAttribute("successMessage", "Item created successfully!");
            }

        } catch (Exception e) {
            log.error("Error saving item", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error saving item: " + e.getMessage());
        }

        return "redirect:/admin/panel";
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteItem(@RequestParam Long itemId, RedirectAttributes redirectAttributes) {
        try {
            log.debug("Deleting item with id: {}", itemId);
            itemService.deleteItem(itemId);
            redirectAttributes.addFlashAttribute("successMessage", "Item deleted successfully!");
        } catch (Exception e) {
            log.error("Error deleting item", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting item: " + e.getMessage());
        }

        return "redirect:/admin/panel";
    }

    @PostMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public List<Item> searchItems(@RequestParam String query) {
        log.debug("Searching items with query: {}", query);
        return itemService.searchItems(query);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public String searchPage(@RequestParam(required = false) String query, Model model) {
        log.debug("Search page accessed with query: {}", query);

        List<Item> items;
        if (query != null && !query.trim().isEmpty()) {
            items = itemService.searchItems(query.trim());
            model.addAttribute("searchQuery", query);
        } else {
            items = itemService.getAllItems();
        }

        model.addAttribute("items", items);
        model.addAttribute("isSearchResults", query != null && !query.trim().isEmpty());

        return "admin-panel";
    }

    @PostMapping("/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Map<String, Object> toggleItemStatus(@RequestParam Long itemId) {
        Map<String, Object> response = new HashMap<>();
        try {
            log.debug("Toggling status for item: {}", itemId);
            Item item = itemService.toggleItemStatus(itemId);
            response.put("success", true);
            response.put("newStatus", item.isActive());
            response.put("message", "Status updated successfully");
        } catch (Exception e) {
            log.error("Error toggling item status", e);
            response.put("success", false);
            response.put("message", "Error updating status: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        log.debug("Admin logout");

        Cookie cookie = new Cookie("JWT", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        SecurityContextHolder.clearContext();

        redirectAttributes.addFlashAttribute("message", "You have been logged out successfully.");
        return "redirect:/authentication";
    }
}
