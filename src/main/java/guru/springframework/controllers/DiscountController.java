package guru.springframework.controllers;

import com.google.gson.Gson;
import entities.TypZlavyEntity;
import entities.UcetEntity;
import entities.customEntities.Discount;
import guru.springframework.domain.Product;
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
@RequestMapping("/discount")
@Api(value="onlinestore", description="Http operations with products category", authorizations = {@Authorization(value = "basicAuth") })
public class DiscountController {

    private ProductService authentificationService;

    @Autowired
    public void setProductService(ProductService productService) {
        this.authentificationService = productService;
    }

    @ApiOperation(value = "View a list of discount type",response = Iterable.class, authorizations = {@Authorization(value="basicAuth")})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully operation"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
    @RequestMapping(value = "/discountType/list", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity list(HttpServletRequest httpRequest){
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
        List<TypZlavyEntity> productItems = baseService.getMainDiscountTypes();
        String json = new Gson().toJson(productItems);
        return new ResponseEntity(json, HttpStatus.OK);
    }

    @ApiOperation(value = "Get discount by ID", authorizations = {@Authorization(value="basicAuth")})
    @RequestMapping(value = "/{id}", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity getDiscountById(@PathVariable Integer id, HttpServletRequest httpRequest){
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
        Discount discount = baseService.getDiscount(id);
        if(discount == null)
        {
            return new ResponseEntity("Not found discount id: " + id, HttpStatus.NOT_FOUND);
        }
        String json = new Gson().toJson(discount);
        return new ResponseEntity(json, HttpStatus.OK);
    }

    @ApiOperation(value = "Get discount type by ID",response = Product.class, authorizations = {@Authorization(value="basicAuth")})
    @RequestMapping(value = "discountType/{id}", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity getSubcateegory(@PathVariable Integer id, HttpServletRequest httpRequest){
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
        TypZlavyEntity productItems = baseService.getMainDiscountType(id);
        if(productItems == null)
        {
            return new ResponseEntity("Wrong request not found selected ID " +id, HttpStatus.NOT_FOUND);
        }
        String json = new Gson().toJson(productItems);
        return new ResponseEntity(json, HttpStatus.OK);
    }

    @ApiOperation(value = "Add discount", authorizations = {@Authorization(value="basicAuth")})
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity addDiscount(@RequestBody Map<String, String> data,  HttpServletRequest httpRequest){
        UcetEntity ucet = this.authentificationService.authentification(httpRequest);
        if(ucet == null)
        {
            return new ResponseEntity("Wrong user or password: ", HttpStatus.UNAUTHORIZED);
        }

        if(!data.containsKey("mainDiscountType") || !data.containsKey("discount-value")
                || !data.containsKey("discount-from") || !data.containsKey("discountType"))
        {
            return new ResponseEntity("Payload must contains mandatory property", HttpStatus.BAD_REQUEST);
        }
        if(ucet.getActive() == 0 || ucet.getTenantId() == null)
        {
            return new ResponseEntity("Dont have access or your tenant is not active", HttpStatus.UNAUTHORIZED);
        }
        String discountType = data.get("discountType");
        if(discountType.equals("quantity") || discountType.equals("percent") || discountType.equals("price"))
        {
            BaseService baseService = new BaseServiceImplement(ucet.getTenantId());
            Map<String, String> categoryItem = baseService.insertDiscount(data);
            if(categoryItem.containsKey("err"))
            {
                return new ResponseEntity("Error during insert DB", HttpStatus.CONFLICT);
            }

            return new ResponseEntity("Successfully created", HttpStatus.CREATED);
        }
        return new ResponseEntity("Discount type support only these value: quantity ,percent, price ", HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @ApiOperation(value = "Add discount type", authorizations = {@Authorization(value="basicAuth")})
    @RequestMapping(value = "discountType/add", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity addMainDiscountType(@RequestBody Map<String, String> data, HttpServletRequest httpRequest){
        UcetEntity ucet = this.authentificationService.authentification(httpRequest);
        if(ucet == null)
        {
            return new ResponseEntity("Wrong user or password: ", HttpStatus.UNAUTHORIZED);
        }
        if(!data.containsKey("discount-type-name"))
        {
            return new ResponseEntity("Payload must contains mandatory property", HttpStatus.BAD_REQUEST);
        }
        if(ucet.getActive() == 0 || ucet.getTenantId() == null)
        {
            return new ResponseEntity("Dont have access or your tenant is not active", HttpStatus.UNAUTHORIZED);
        }
        BaseService baseService = new BaseServiceImplement(ucet.getTenantId());
        Map<String, String> categoryItem = baseService.insertMainDiscountType(data);
        if(categoryItem.containsKey("err"))
        {
            return new ResponseEntity("Error during insert DB", HttpStatus.CONFLICT);
        }

        return new ResponseEntity("Successfully created", HttpStatus.CREATED);
    }



    @ApiOperation(value = "Update a discount type", authorizations = {@Authorization(value="basicAuth")})
    @RequestMapping(value = "/discountType/update/{id}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity updateDiscountType(@PathVariable String  id, @RequestBody Map<String, String> data, HttpServletRequest httpRequest){
        UcetEntity ucet = this.authentificationService.authentification(httpRequest);
        if(ucet == null)
        {
            return new ResponseEntity("Wrong user or password: ", HttpStatus.UNAUTHORIZED);
        }
        if(!data.containsKey("discount-type-name") || !data.containsKey("discount-type-id"))
        {
            return new ResponseEntity("Payload must contains mandatory property", HttpStatus.BAD_REQUEST);
        }
        if(ucet.getActive() == 0 || ucet.getTenantId() == null)
        {
            return new ResponseEntity("Dont have access or your tenant is not active", HttpStatus.UNAUTHORIZED);
        }
        BaseService baseService = new BaseServiceImplement(1);
        id = data.get("discount-type-id");
        Map<String, String> categoryItem = baseService.updateMainDiscountType(id, data);
        if(categoryItem.containsKey("err"))
        {
            return new ResponseEntity("Error during insert DB", HttpStatus.CONFLICT);
        }

        String json = new Gson().toJson(categoryItem);
        return new ResponseEntity("Discount Type updated successfully", HttpStatus.OK);
    }

    @ApiOperation(value = "Delete discountType" , authorizations = {@Authorization(value="basicAuth")})
    @RequestMapping(value="/delete/{mainDiscountId}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity delete(@PathVariable Integer mainDiscountId, HttpServletRequest httpRequest){
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
        Map<String, String> categoryItem = baseService.deleteMainDiscountType(mainDiscountId);
        if(categoryItem.containsKey("err"))
        {
            return new ResponseEntity("Error during remove from DB", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity("Discount Type deleted successfully", HttpStatus.OK);

    }

}
