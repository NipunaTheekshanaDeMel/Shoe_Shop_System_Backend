package software.nipunatheekshana.shoe_shop_management_system.exception;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String message){
        super(message);
    }
}