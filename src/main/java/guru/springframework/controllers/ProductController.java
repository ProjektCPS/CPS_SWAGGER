package guru.springframework.controllers;

import com.google.gson.Gson;
import entities.PredmetPredajaEntity;
import guru.springframework.domain.Product;
import guru.springframework.services.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.BaseService;
import services.BaseServiceImplement;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
@Api(value="onlinestore", description="All operations for product managment")
public class ProductController {

    private ProductService productService;

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @ApiOperation(value = "View a list of available products",response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @RequestMapping(value = "/list", method= RequestMethod.GET, produces = "application/json")
    public String listOfProduct(){
        BaseService baseService = new BaseServiceImplement(1);
        List<entities.customEntities.Product> productItems = baseService.getProductAllProduct();
        String json = new Gson().toJson(productItems);
        return json;
    }

    @ApiOperation(value = "View a list of available products in selected category",response = Iterable.class)
    @RequestMapping(value = "/list/{categoryName}", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity listOfProduct(String categoryName){
        if(categoryName.isEmpty())
        {
            return new ResponseEntity("Missing category name", HttpStatus.BAD_REQUEST);
        }

        BaseService baseService = new BaseServiceImplement(1);
        List<entities.customEntities.Product> productItems = baseService.getProducts(categoryName);

        if(productItems == null)
        {
            return new ResponseEntity("Category name " +categoryName +" not found", HttpStatus.NOT_FOUND);
        }

        String json = new Gson().toJson(productItems);
        return new ResponseEntity(json, HttpStatus.OK);
    }

    @ApiOperation(value = "Get product by ID",response = Product.class)
    @RequestMapping(value = "/{id}", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity GetProductBy(@PathVariable int id){
        if(id != (int) id)
        {
            return new ResponseEntity("Missing category name", HttpStatus.BAD_REQUEST);
        }

        BaseService baseService = new BaseServiceImplement(1);
        PredmetPredajaEntity product = baseService.getProductById(id);
        if(product == null)
        {
            return new ResponseEntity("Product: " +id +" not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity("Missing category name", HttpStatus.OK);
    }

    @ApiOperation(value = "Add a product")
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity saveProduct(@RequestBody Map<String, String> data){
        if(!data.containsKey("categoryId") || !data.containsKey("name")
                || !data.containsKey("price") || !data.containsKey("unit"))
        {
            return new ResponseEntity("Payload must contains mandatory data", HttpStatus.BAD_REQUEST);
        }
        BaseService baseService = new BaseServiceImplement(1);
        Map<String, String> newProduct = baseService.insertProduct(data);
        if(newProduct.containsKey("err"))
        {
            return new ResponseEntity("Error during insert DB", HttpStatus.CONFLICT);
        }

        String json = new Gson().toJson(newProduct);
        return new ResponseEntity("Successfully created", HttpStatus.CREATED);
    }

    @ApiOperation(value = "Update a product")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity updateProduct(@PathVariable Integer id, @RequestBody Map<String, String> data){
        if(id != (int) id ||!data.containsKey("categoryId") || !data.containsKey("name")
                || !data.containsKey("price") || !data.containsKey("unit"))
        {
            return new ResponseEntity("Payload must contains mandatory data", HttpStatus.BAD_REQUEST);
        }
        BaseService baseService = new BaseServiceImplement(1);
        Map<String, String> newProduct = baseService.updateProduct(id, data);
        if(newProduct.containsKey("err"))
        {
            return new ResponseEntity("Error during insert DB", HttpStatus.CONFLICT);
        }

        String json = new Gson().toJson(newProduct);
        return new ResponseEntity("Successfully updated", HttpStatus.OK);
    }

    @ApiOperation(value = "Delete a product")
    @RequestMapping(value="/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity delete(@PathVariable Integer id){
        if(id != (int) id)
        {
            return new ResponseEntity("ID must be number", HttpStatus.BAD_REQUEST);
        }

        BaseService baseService = new BaseServiceImplement(1);
        Map<String, String> removeProduct = baseService.deleteProduct(id);
        if(removeProduct.containsKey("conflict")){
            return new ResponseEntity("Unsuccessfull operation because this product has references in other tables", HttpStatus.CONFLICT);
        }

        if(removeProduct.containsKey("err")){
            return new ResponseEntity("Not found id: "+ id, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity("Product was deleted", HttpStatus.OK);
    }

}
