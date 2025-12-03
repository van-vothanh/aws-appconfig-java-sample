// depreciation example - sun.misc.BASE64Encoder;
package com.amazonaws.samples.appconfig.utils;

import java.util.Base64;
import java.util.Calendar;
import java.util.Date;


public class Encoder {

    Date defaultDate;
    
    {
        Calendar cal = Calendar.getInstance();
        cal.set(1999, Calendar.JANUARY, 1);
        defaultDate = cal.getTime();
    }

    byte[] bytes = new byte[57];
    String enc1 = Base64.getEncoder().encodeToString(bytes);


}