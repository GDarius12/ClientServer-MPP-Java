package Darius.utils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;

public class Calculeaza_Varsta {

    public static Integer Varsta_CNP(String cnp){
        int secol;
        switch (cnp.charAt(0)){
            case '1': case '2':
                secol = 1900;
                break;
            case '5': case '6':
                secol = 2000;
                break;
            default:
                throw new IllegalArgumentException("CNP invalid!");
        }

        int an = secol + Integer.parseInt(cnp.substring(1, 3));
        int luna = Integer.parseInt(cnp.substring(3, 5));
        int zi = Integer.parseInt(cnp.substring(5, 7));
        LocalDate Data_Nasterii;
        try {
            Data_Nasterii = LocalDate.of(an, luna, zi);
        }catch (DateTimeException e){
            throw new IllegalArgumentException("CNP invalid!");
        }
        LocalDate currentDate = LocalDate.now();
        return Period.between(Data_Nasterii, currentDate).getYears();
    }
}
