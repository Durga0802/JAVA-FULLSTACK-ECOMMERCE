package com.piyush.ecommerce.service;

import com.piyush.ecommerce.dto.ProductRequestDTO;
import com.piyush.ecommerce.dto.ProductResponseDTO;
import com.piyush.ecommerce.entity.Category;
import com.piyush.ecommerce.entity.Product;
import com.piyush.ecommerce.exception.ResourceNotFoundException;
import com.piyush.ecommerce.repository.CategoryRepository;
import com.piyush.ecommerce.repository.ProductRepository;
import org.springframework.data.domain.Page; // Sahi import
import org.springframework.data.domain.PageRequest; // Sahi import
import org.springframework.data.domain.Pageable; // Sahi import
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.io.IOException;
import java.nio.file.StandardCopyOption;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // Pagination wala method
    public Page<ProductResponseDTO> getProducts(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Product> productPage = productRepository.findAll(pageable);

        // Entity to DTO mapping
        return productPage.map(this::mapToDTO);
    }

    // Purana method (Optional: agar abhi bhi list chahiye ho kahin)
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Product createProduct(ProductRequestDTO dto) {
        Category category = categoryRepository
                .findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCategory(category);

        return productRepository.save(product);
    }

    public List<ProductResponseDTO> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDTO> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    public ProductResponseDTO createProductWithImage(String name, String description, double price, int stock, Long categoryId, MultipartFile image, String imageUrl) throws IOException {

        // 1. Category check karo
        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        String finalImageValue = "";

        // 2. File handle karo (Sirf tab jab image null na ho)
        if (image != null && !image.isEmpty()) {
            String originalFileName = image.getOriginalFilename();
            // Null check for filename to prevent NullPointerException
            String safeName = (originalFileName != null) ? originalFileName.replaceAll("\\s+", "_") : "default.jpg";
            finalImageValue = System.currentTimeMillis() + "_" + safeName;

            Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.copy(image.getInputStream(),
                    uploadPath.resolve(finalImageValue),
                    StandardCopyOption.REPLACE_EXISTING);
        }
        // 3. Agar file nahi hai par URL hai, toh URL use karo
        else if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            finalImageValue = imageUrl;
        }

        // 4. Product object banao
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategory(category);
        product.setImageUrl(finalImageValue); // File name ya External URL save hoga

        Product saved = productRepository.save(product);

        return mapToDTO(saved);
    }

    public ProductResponseDTO mapToDTO(Product product) {
        String categoryName = (product.getCategory() != null)
                ? product.getCategory().getName()
                : "Unknown Category";

        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                categoryName,          // 6th Number: Ab yahan Category jayegi
                product.getImageUrl()  // 7th Number: Ab yahan Image jayegi
        );
    }

    // Product Update karne ka logic
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
        // 1. Pehle purana product dhoondo, nahi mile toh error do
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // 2. Category check karo (Kyuki category bhi badal sakti hai)
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // 3. Data update karo
        existingProduct.setName(dto.getName());
        existingProduct.setDescription(dto.getDescription());
        existingProduct.setPrice(dto.getPrice());
        existingProduct.setStock(dto.getStock());
        existingProduct.setCategory(category);

        // Note: Image update ka logic alag se upload API se handle karenge
        // ya fir yahan agar DTO mein image path hai toh set karein.

        // 4. Save karo
        Product updatedProduct = productRepository.save(existingProduct);

        // 5. DTO mein badal ke wapas bhej do
        return mapToDTO(updatedProduct);
    }
}