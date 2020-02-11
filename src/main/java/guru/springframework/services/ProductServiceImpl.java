package guru.springframework.services;

import dataAccessObjects.AdminDaoImplement;
import entities.UcetEntity;
import guru.springframework.domain.Product;
import guru.springframework.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class ProductServiceImpl implements ProductService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ProductRepository productRepository;

    @Autowired
    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Iterable<Product> listAllProducts() {
        logger.debug("listAllProducts called");
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Integer id) {
        logger.debug("getProductById called");
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Product saveProduct(Product product) {
        logger.debug("saveProduct called");
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Integer id) {
        logger.debug("deleteProduct called");
        productRepository.deleteById(id);
    }

    @Override
    public UcetEntity authentification(HttpServletRequest httpRequest) {
        final String authorization = httpRequest.getHeader("Authorization");
        String[] values = new String[2];
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            values = credentials.split(":", 2);
        }
        else {
            return null;
        }

        if(values.length == 0)
        {
            return null;
        }

        AdminDaoImplement baseService = new AdminDaoImplement();
        UcetEntity user = baseService.login(values[0], values[1]);
        if(user == null)
        {
            return null;
        }
        return user;
    }
}

