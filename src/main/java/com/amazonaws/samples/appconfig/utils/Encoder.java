// depreciation example - sun.misc.BASE64Encoder;
package com.amazonaws.samples.appconfig.utils;

import java.util.Base64;
import java.util.Date;


public class Encoder {

    Date defaultDate = new Date(1999, 0, 1);

    byte[] bytes = new byte[57];
    String enc1 = Base64.getEncoder().encodeToString(bytes);


}