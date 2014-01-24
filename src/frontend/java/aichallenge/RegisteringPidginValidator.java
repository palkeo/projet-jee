package aichallenge;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class RegisteringPidginValidator implements Validator
{
    private PidginRepository repo;

    public RegisteringPidginValidator(PidginRepository repo)
    {
        this.repo = repo;
    }

    @Override
    public boolean supports(Class clazz)
    {
        return RegisteringPidgin.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors)
    {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "login", "required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmation", "required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "required");

        RegisteringPidgin user = (RegisteringPidgin)target;
        System.out.println(user.getConfirmation() == null);

        if(!user.getPassword().equals(user.getConfirmation()))
        {
            errors.rejectValue("password", "notmatch.password");
        }

        if(repo.findByLogin(user.getLogin()) != null)
        {
            errors.rejectValue("login", "alreadyused.login");
        }
    }
}
