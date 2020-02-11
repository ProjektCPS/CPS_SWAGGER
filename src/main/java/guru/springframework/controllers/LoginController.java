package guru.springframework.controllers;

import dataAccessObjects.AdminDaoImplement;
import entities.UcetEntity;
import guru.springframework.services.ProductService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@Api(value="onlinestore", description="Http operations with products category", authorizations = {@Authorization(value = "basicAuth") })
public class LoginController {

    private ProductService productService;

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @ApiOperation(value = "View a list of categories",response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully operation"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
    }
    )
    @RequestMapping(value = "/auth", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity addSubcategory(@PathVariable String username, @PathVariable String password){
        if(username.isEmpty() || password.isEmpty())
        {
            return new ResponseEntity("Payload must contains property: login and password", HttpStatus.BAD_REQUEST);
        }
        AdminDaoImplement baseService = new AdminDaoImplement();
        UcetEntity user = baseService.login(username, password);
        if(user == null)
        {
            return new ResponseEntity("Error during insert DB", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity("Successfully created", HttpStatus.OK);
    }

}
