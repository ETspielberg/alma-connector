package org.unidue.ub.libintel.almaconnector.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HookSignatureFilter implements Filter {

    private final Logger log = LoggerFactory.getLogger(HookSignatureFilter.class);

    private String secret;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (request.getMethod().equals(HttpMethod.POST.name())) {
            String signature = request.getHeader("X-Exl-Signature");
            char[] tmp = new char[ 4096 ];

            InputStream payloadStream = request.getInputStream();
            StringBuilder sb = new StringBuilder( Math.max( 16, payloadStream.available() ) );
            try {
                InputStreamReader reader = new InputStreamReader( payloadStream, StandardCharsets.UTF_8 );
                for( int cnt; ( cnt = reader.read( tmp ) ) > 0; )
                    sb.append( tmp, 0, cnt );
            } finally {
                payloadStream.close();
            }
            String payload = payloadStream.toString();
            log.info(payload);
            byte[] hmacSha256 = new byte[0];
            try {
                log.info("hashing payload with HmacSha256 and key " + this.secret);
                hmacSha256 = calcHmacSha256(secret.getBytes(StandardCharsets.UTF_8), payload.getBytes(StandardCharsets.UTF_8));
            } catch (NoSuchAlgorithmException e) {
                log.warn("wrong algorithm defined. Please select a proper hashing algorithm");
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                log.warn("bad key defined. Please check the provided encryption key");
                e.printStackTrace();
            }
            String base64HmacSha256 = Base64.getEncoder().encodeToString(hmacSha256);
            log.info(String.format("calculated signature: %s, provided signature: %s",base64HmacSha256, signature));
            if (!signature.equals(base64HmacSha256)) {
                HttpServletResponse response = (HttpServletResponse) servletResponse;
                log.warn(String.format("received bad signature on web hook call for endpoint %s: %s",request.getRequestURI(), signature));
                response.sendError(403, "signatures do not match");
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private byte[] calcHmacSha256(byte[] secretKey, byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
        mac.init(secretKeySpec);
        return mac.doFinal(message);
    }

    public HookSignatureFilter withSecret(String secret) {
        this.secret = secret;
        return this;
    }
}
