package guru.springframework.controllers;

import com.google.gson.Gson;
import entities.KategorieEntity;
import entities.TypPredmetuEntity;
import guru.springframework.domain.Product;
import guru.springframework.services.ProductService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import services.BaseService;
import services.BaseServiceImplement;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/category")
@Api(value="onlinestore", description="Http operations with products category", authorizations = @Authorization(value = "basicAuth"))
public class CategoryController {

    private ProductService productService;

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @ApiOperation(value = "View a list of categories",response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully operation"),
            @ApiResponse(code = 201, message = "Object created"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 400, message = "Wrong input parameter"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @RequestMapping(value = "/maincategory/list", method= RequestMethod.GET, produces = "application/json")
    public String list(Model model){
        BaseService baseService = new BaseServiceImplement(1);
        List<TypPredmetuEntity> productItems = baseService.getProductTypes();
        String json = new Gson().toJson(productItems);
        return json;
    }

    @ApiOperation(value = "Get main category by ID",response = Product.class)
    @RequestMapping(value = "maincategory/{id}", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity getMainCategory(@PathVariable Integer id){
        BaseService baseService = new BaseServiceImplement(1);
        TypPredmetuEntity typPredmetu = baseService.getMainCategory(id);
        if(typPredmetu == null)
        {
            return new ResponseEntity("Wrong request", HttpStatus.NOT_FOUND);
        }
        String json = new Gson().toJson(typPredmetu);
        return new ResponseEntity(json, HttpStatus.OK);
    }

    @ApiOperation(value = "Get subcategory by ID",response = Product.class)
    @RequestMapping(value = "subcategory/{id}", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSubcateegory(@PathVariable Integer id){
        BaseService baseService = new BaseServiceImplement(1);
        KategorieEntity productItems = baseService.getProductCategory(id);
        if(productItems == null)
        {
            return new ResponseEntity("Wrong request", HttpStatus.NOT_FOUND);
        }
        String json = new Gson().toJson(productItems);
        return new ResponseEntity(json, HttpStatus.OK);
    }

    @ApiOperation(value = "Add subcategory")
    @RequestMapping(value = "/subcategory/add", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity addSubcategory(@RequestBody Map<String, String> data){
        if(!data.containsKey("category-name") || !data.containsKey("mainCategoryId"))
        {
            return new ResponseEntity("Payload must contains property: category-name and mainCategoryId", HttpStatus.BAD_REQUEST);
        }
        BaseService baseService = new BaseServiceImplement(1);
        Map<String, String> categoryItem = baseService.insertProductCategory(data);
        if(categoryItem.containsKey("err"))
        {
            return new ResponseEntity("Error during insert DB", HttpStatus.CONFLICT);
        }

        return new ResponseEntity("Successfully created", HttpStatus.CREATED);
    }

    @ApiOperation(value = "Add maincategory")
    @RequestMapping(value = "/maincategory/add", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity addMainCategory(@RequestBody Map<String, String> data){
        if(!data.containsKey("main-category-name"))
        {
            return new ResponseEntity("Payload must contains property: main-category-name", HttpStatus.BAD_REQUEST);
        }
        BaseService baseService = new BaseServiceImplement(1);
        Map<String, String> categoryItem = baseService.insertMainCategory(data);
        if(categoryItem.containsKey("err"))
        {
            return new ResponseEntity("Error during insert DB", HttpStatus.CONFLICT);
        }

        String json = new Gson().toJson(categoryItem);
        return new ResponseEntity("Successfully created", HttpStatus.CREATED);
    }

    @ApiOperation(value = "Update a main category")
    @RequestMapping(value = "/maincategory/update/{id}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity updateProduct(@PathVariable String  mainCategoryId, @RequestBody Map<String, String> data){
        if(!data.containsKey("main-category-name") || mainCategoryId.isEmpty())
        {
            return new ResponseEntity("Payload must contains property: main-category-name", HttpStatus.BAD_REQUEST);
        }
        BaseService baseService = new BaseServiceImplement(1);
        Map<String, String> categoryItem = baseService.updateMainCategory(mainCategoryId, data);
        if(categoryItem.containsKey("err"))
        {
            return new ResponseEntity("Error during insert DB", HttpStatus.CONFLICT);
        }

        String json = new Gson().toJson(categoryItem);
        return new ResponseEntity("Main category updated successfully", HttpStatus.OK);
    }

    @ApiOperation(value = "Delete a product")
    @RequestMapping(value="/delete/{id}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity delete(@PathVariable Integer id){
        productService.deleteProduct(id);
        return new ResponseEntity("Product deleted successfully", HttpStatus.OK);

    }

}
