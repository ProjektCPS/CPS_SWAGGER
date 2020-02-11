package guru.springframework.controllers;

import com.google.gson.Gson;
import entities.PredmetPredajaEntity;
import entities.UcetEntity;
import guru.springframework.services.ProductService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.BaseService;
import services.BaseServiceImplement;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
@Api(value="onlinestore", description="All operations for product managment", authorizations = @Authorization(value = "basicAuth"))
public class ProductController {

    private ProductService authentificationService;

    @Autowired
    public void setProductService(ProductService productService) {
        this.authentificationService = productService;
    }

    @ApiOperation(value = "View a list of available products",response = Iterable.class, authorizations = {@Authorization(value="basicAuth")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @RequestMapping(value = "/list", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity listOfProduct(HttpServletRequest httpRequest){
        UcetEntity ucet = this.authentificationService.authentification(httpRequest);
        if(ucet == null)
        {
            return new ResponseEntity("Wrong user or password: ", HttpStatus.UNAUTHORIZED);
        }
        if(ucet.getActive() == 0 || ucet.getTenantId() == null)
        {
            return new ResponseEntity("Dont have access or your tenant is not active", HttpStatus.UNAUTHORIZED);
        }
        BaseService baseService = new BaseServiceImplement(ucet.getTenantId());
        List<entities.customEntities.Product> productItems = baseService.getProductAllProduct();
        String json = new Gson().toJson(productItems);
        return new ResponseEntity(json, HttpStatus.OK);
    }

    @ApiOperation(value = "View a list of available products in selected category",response = Iterable.class, authorizations = {@Authorization(value="basicAuth")})
    @RequestMapping(value = "/list/{categoryName}", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity listOfProduct(String categoryName, HttpServletRequest httpRequest){
        UcetEntity ucet = this.authentificationService.authentification(httpRequest);
        if(ucet == null)
        {
            return new ResponseEntity("Wrong user or password: ", HttpStatus.UNAUTHORIZED);
        }
        if(categoryName.isEmpty())
        {
            return new ResponseEntity("Missing category name", HttpStatus.BAD_REQUEST);
        }
        if(ucet.getActive() == 0 || ucet.getTenantId() == null)
        {
            return new ResponseEntity("Dont have access or your tenant is not active", HttpStatus.UNAUTHORIZED);
        }
        BaseService baseService = new BaseServiceImplement(ucet.getTenantId());
        List<entities.customEntities.Product> productItems = baseService.getProducts(categoryName);

        if(productItems == null)
        {
            return new ResponseEntity("Category name " +categoryName +" not found", HttpStatus.NOT_FOUND);
        }

        String json = new Gson().toJson(productItems);
        return new ResponseEntity(json, HttpStatus.OK);
    }

    @ApiOperation(value = "Get product by ID", authorizations = {@Authorization(value="basicAuth")})
    @RequestMapping(value = "/{id}", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity GetProductBy(@PathVariable int id, HttpServletRequest httpRequest){
        UcetEntity ucet = this.authentificationService.authentification(httpRequest);
        if(ucet == null)
        {
            return new ResponseEntity("Wrong user or password: ", HttpStatus.UNAUTHORIZED);
        }
        if(id != (int) id)
        {
            return new ResponseEntity("Wrong id", HttpStatus.BAD_REQUEST);
        }
        if(ucet.getActive() == 0 || ucet.getTenantId() == null)
        {
            return new ResponseEntity("Dont have access or your tenant is not active", HttpStatus.UNAUTHORIZED);
        }
        BaseService baseService = new BaseServiceImplement(ucet.getTenantId());
        PredmetPredajaEntity product = baseService.getProductById(id);
        if(product == null)
        {
            return new ResponseEntity("Product: " +id +" not found", HttpStatus.NOT_FOUND);
        }

        String json = new Gson().toJson(product);
        return new ResponseEntity(json, HttpStatus.OK);
    }

    @ApiOperation(value = "Add a product", authorizations = {@Authorization(value="basicAuth")})
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity saveProduct(@RequestBody Map<String, String> data, HttpServletRequest httpRequest){
        UcetEntity ucet = this.authentificationService.authentification(httpRequest);
        if(ucet == null)
        {
            return new ResponseEntity("Wrong user or password: ", HttpStatus.UNAUTHORIZED);
        }
        if(!data.containsKey("categoryId") || !data.containsKey("name")
                || !data.containsKey("price") || !data.containsKey("unit"))
        {
            return new ResponseEntity("Payload must contains mandatory data", HttpStatus.BAD_REQUEST);
        }
        if(ucet.getActive() == 0 || ucet.getTenantId() == null)
        {
            return new ResponseEntity("Dont have access or your tenant is not active", HttpStatus.UNAUTHORIZED);
        }
        BaseService baseService = new BaseServiceImplement(ucet.getTenantId());
        Map<String, String> newProduct = baseService.insertProduct(data);
        if(newProduct.containsKey("err"))
        {
            return new ResponseEntity("Error during insert DB", HttpStatus.CONFLICT);
        }

        String json = new Gson().toJson(newProduct);
        return new ResponseEntity("Successfully created", HttpStatus.CREATED);
    }

    @ApiOperation(value = "Update a product", authorizations = {@Authorization(value="basicAuth")})
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity updateProduct(@PathVariable Integer id, @RequestBody Map<String, String> data, HttpServletRequest httpRequest){
        UcetEntity ucet = this.authentificationService.authentification(httpRequest);
        if(ucet == null)
        {
            return new ResponseEntity("Wrong user or password: ", HttpStatus.UNAUTHORIZED);
        }
        if(id != (int) id ||!data.containsKey("categoryId") || !data.containsKey("name")
                || !data.containsKey("price") || !data.containsKey("unit"))
        {
            return new ResponseEntity("Payload must contains mandatory data", HttpStatus.BAD_REQUEST);
        }
        if(ucet.getActive() == 0 || ucet.getTenantId() == null)
        {
            return new ResponseEntity("Dont have access or your tenant is not active", HttpStatus.UNAUTHORIZED);
        }
        BaseService baseService = new BaseServiceImplement(ucet.getTenantId());
        Map<String, String> newProduct = baseService.updateProduct(id, data);
        if(newProduct.containsKey("err"))
        {
            return new ResponseEntity("Error during insert DB", HttpStatus.CONFLICT);
        }

        String json = new Gson().toJson(newProduct);
        return new ResponseEntity("Successfully updated", HttpStatus.OK);
    }

    @ApiOperation(value = "Delete a product", authorizations = {@Authorization(value="basicAuth")})
    @RequestMapping(value="/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity delete(@PathVariable Integer id, HttpServletRequest httpRequest){
        UcetEntity ucet = this.authentificationService.authentification(httpRequest);
        if(ucet == null)
        {
            return new ResponseEntity("Wrong user or password: ", HttpStatus.UNAUTHORIZED);
        }

        if(id != (int) id)
        {
            return new ResponseEntity("ID must be number", HttpStatus.BAD_REQUEST);
        }

        if(ucet.getActive() == 0 || ucet.getTenantId() == null)
        {
            return new ResponseEntity("Dont have access or your tenant is not active", HttpStatus.UNAUTHORIZED);
        }
        BaseService baseService = new BaseServiceImplement(ucet.getTenantId());
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
