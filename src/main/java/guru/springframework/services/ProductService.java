package guru.springframework.services;


import entities.UcetEntity;
import guru.springframework.domain.Product;

import javax.servlet.http.HttpServletRequest;

public interface ProductService {
    Iterable<Product> listAllProducts();

    Product getProductById(Integer id);

    Product saveProduct(Product product);

    void deleteProduct(Integer id);

    UcetEntity authentification(HttpServletRequest httpRequest);
}
