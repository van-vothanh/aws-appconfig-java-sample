// depreciation example - sun.misc.BASE64Encoder;
package com.amazonaws.samples.appconfig.utils;

import java.util.Base64;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;


public class Encoder {

    Date defaultDate = Date.from(LocalDate.of(1999, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());

    byte[] bytes = new byte[57];
    String enc1 = Base64.getEncoder().encodeToString(bytes);


}