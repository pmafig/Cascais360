package nsop.neds.mycascais.Manager.ControlsManager;

import android.text.TextUtils;
import android.util.Patterns;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidatorManager {

    public boolean isValidEmail(String string){
        return Patterns.EMAIL_ADDRESS.matcher(string).matches();

        /*final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();*/
    }

    public boolean isValidPassword(String input){
        final String pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

        return isValid(input, pattern);
    }

    public boolean isValidPassword_digit(String input){
        final String pattern = ".*[0-9]+.*";

        return isValid(input, pattern);
    }

    public boolean isValidPassword_lowerCase(String input){
        final String pattern = ".*[a-z]+.*";

        return isValid(input, pattern);
    }

    public boolean isValidPassword_upperCase(String input){
        final String pattern = ".*[A-Z]+.*";

        return isValid(input, pattern);
    }

    public boolean isValidPassword_specialCharacter(String input){
        final String pattern = ".*[^a-zA-Z 0-9]+.*";

        return isValid(input, pattern);
    }

    public boolean isValidPassword_size(String input){
        return input.length() >= 9 && input.length() <= 32;
    }



    public boolean isNullOrEmpty(String string){
        return TextUtils.isEmpty(string);
    }

    public boolean isNumeric(String string){
        return TextUtils.isDigitsOnly(string);
    }

    public boolean isValidPhone(String string){

        if(isNullOrEmpty(string)){
            return false;
        }

        char first = string.charAt(0);

        return first == '9' && string.length() == 9;
    }

    public boolean isValidAge(String accountBirthday){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date = formatter.parse(accountBirthday);
            Date currentDate = new Date();

            long diff = currentDate.getTime() - date.getTime();

            long diffYears = diff / 31536000000l;

            boolean result = diffYears >= 16;

            return result;

        }catch (Exception ex){
            return false;
        }
    }

    public boolean isValidNif(String nif) {
        final int max=9;

        if (!nif.matches("[0-9]+") || nif.length()!=max) return false;
        int checkSum=0;

        for (int i=0; i<max-1; i++){
            checkSum+=(nif.charAt(i)-'0')*(max-i);
        }
        int checkDigit=11-(checkSum % 11);

        if (checkDigit>=10) checkDigit=0;

        boolean result = checkDigit==nif.charAt(max-1)-'0';

        return result;
    }

    private boolean isValid(String string, String pattern){
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(string);

        boolean result = matcher.matches();
        return result;
    }
}
