package com.piyush.ecommerce.controller;

import com.piyush.ecommerce.dto.ProductRequestDTO;
import com.piyush.ecommerce.dto.ProductResponseDTO;
import com.piyush.ecommerce.entity.Product;
import com.piyush.ecommerce.repository.ProductRepository;
import com.piyush.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page; // Isse Page return hoga
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;


    public ProductController(ProductService productService,ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository=productRepository;
    }

    // GET products - Fixed for Frontend compatibility
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        // Yahan Product ki jagah ProductResponseDTO aayega
        List<ProductResponseDTO> productList = productService.getAllProducts();
        return ResponseEntity.ok(productList);
    }

    @PostMapping
    public ProductResponseDTO createProduct(
            @Valid @RequestBody ProductRequestDTO dto) {
        Product product = productService.createProduct(dto);
        return productService.mapToDTO(product);
    }

    @GetMapping("/category/{id}")
    public List<ProductResponseDTO> getProductsByCategory(@PathVariable Long id) {
        return productService.getProductsByCategoryId(id);
    }

    @GetMapping("/search")
    public List<ProductResponseDTO> searchProducts(@RequestParam String name) {
        return productService.searchProductsByName(name);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO dto) {

        ProductResponseDTO updated = productService.updateProduct(id, dto);
        return ResponseEntity.ok(updated);
    }
    @GetMapping("favicon.ico")
    @ResponseBody
    void returnNoFavicon() {
        // Ye method kuch nahi karega, bas browser ko 200 OK bhej dega
    }
    @GetMapping("/count")
    public long getCount() {
        return productRepository.count(); // Ye JpaRepository ka inbuilt method hai
    }
    // CREATE PRODUCT WITH IMAGE
    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createProductWithImage(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("stock") int stock,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "imageUrl", required = false) String imageUrl) { // Naya Param
        try {
            // Ab hum imageUrl bhi bhej rahe hain service ko
            ProductResponseDTO result = productService.createProductWithImage(name, description, price, stock, categoryId, image, imageUrl);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Asli Error: " + e.getMessage());
        }
    }
}