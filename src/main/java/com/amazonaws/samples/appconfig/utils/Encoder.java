// Updated to use java.time API instead of deprecated Date constructor
package com.amazonaws.samples.appconfig.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

public class Encoder {

    // Using LocalDate and converting to Date when needed
    LocalDate localDate = LocalDate.of(1999, 1, 1);
    Date defaultDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

    byte[] bytes = new byte[57];
    String enc1 = Base64.getEncoder().encodeToString(bytes);
}