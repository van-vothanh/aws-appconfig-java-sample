//javax.security.cert to java.security.cert classes migration
package com.amazonaws.samples.appconfig.utils;

import java.security.cert.*;
import java.security.cert.CertificateFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class Security {
    public Certificate getCertificate(File certFile) throws CertificateExpiredException, CertificateNotYetValidException {
        X509Certificate cert = null;
        try (InputStream inStream = new FileInputStream(certFile)) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate) cf.generateCertificate(inStream);
            cert.checkValidity(new Date());
        } catch (CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
        return cert;
    }

}