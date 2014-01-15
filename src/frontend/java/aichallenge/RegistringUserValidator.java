package aichallenge;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class RegistringUserValidator implements Validator {
    @Override
    public boolean supports(Class clazz) {
        return RegistringUser.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "login", "required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "required");

        //Fixme: for some reason, that one does not work, we always get null
        //ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmation", "required");

        RegistringUser user = (RegistringUser)target;
        System.out.println(user.getConfirmation() == null);

        if(!user.getPassword().equals(user.getConfirmation())){
            //errors.rejectValue("password", "notmatch.password");
        }
    }
}

