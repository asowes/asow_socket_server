package com.young.asow.util;

import java.math.BigInteger;

public class GenerateUtil {

    public static String generateMessageId(Long originalString, String additionalString) {
        if (additionalString.equals("")) {
            return originalString + "000001";
        }

        BigInteger additionalNum = new BigInteger(additionalString);
        additionalNum = additionalNum.add(BigInteger.ONE);

        return String.format("%06d", additionalNum);
    }


}