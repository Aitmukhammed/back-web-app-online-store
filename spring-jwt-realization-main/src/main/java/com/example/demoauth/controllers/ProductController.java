package com.example.demoauth.controllers;

import com.example.demoauth.details.CartDetails;
import com.example.demoauth.details.ProductDetails;
import com.example.demoauth.models.Category;
import com.example.demoauth.repository.CategoryRepository;
import com.example.demoauth.repository.UserRepository;
import com.example.demoauth.service.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.demoauth.models.Product;
import com.example.demoauth.models.User;
import com.example.demoauth.service.ProductService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping("/add/products")
    public ResponseEntity<?> createProduct(@RequestBody Product productRequest, Authentication authentication, @RequestHeader Map<String, String> headers) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

//        Category category = categoryRepository.findById(productRequest.getCategory().getId())
        Category category = categoryRepository.findByCategoryCode(productRequest.getCategory().getCategoryCode())
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        // Создаем новый продукт с переданными данными и текущим пользователем
        Product product = new Product(productRequest.getName(), productRequest.getPrice(),
                productRequest.getPictureUrl(), currentUser, category);

        productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/all/products")
    public ResponseEntity<List<ProductDetails>> getAllProducts() {
        try {
            List<ProductDetails> productDetailsList = productService.getAllProducts();
            return ResponseEntity.ok(productDetailsList);
        } catch (Exception e) {
            log.error("Error fetching products: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/delete/product/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/product/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody Product updatedProductRequest) {
        try {
            Optional<Product> optionalProduct = productService.getProductById(productId);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                // Обновляем данные продукта
                product.setName(updatedProductRequest.getName());
                product.setPrice(updatedProductRequest.getPrice());
                product.setPictureUrl(updatedProductRequest.getPictureUrl());

                // Получаем полную информацию о категории
                Category category = categoryRepository.findByCategoryCode(updatedProductRequest.getCategory().getCategoryCode())
                        .orElseThrow(() -> new RuntimeException("Категория не найдена"));
                product.setCategory(category);

                // Сохраняем обновленный продукт
                productService.updateProduct(product);

                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error updating product: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/search/products")
    public ResponseEntity<List<ProductDetails>> searchProductByName(@RequestParam String name) {
        try {
            List<ProductDetails> productDetailsList = productService.searchProductsByName(name);
            return ResponseEntity.ok(productDetailsList);
        } catch (Exception e) {
            log.error("Error searching products: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

// Подтягивает продуктов по id категории (Пока не надо = но функция работает)

//    @GetMapping("/products/category/{categoryId}")
//    public ResponseEntity<List<ProductDetails>> getProductsByCategory(@PathVariable Long categoryId) {
//        try {
//             List<ProductDetails> productDetailsList = productService.getProductsByCategory(categoryId);
//             return ResponseEntity.ok(productDetailsList);
//        } catch (Exception e) {
//            log.error("Error fetching products by category: ", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }

}