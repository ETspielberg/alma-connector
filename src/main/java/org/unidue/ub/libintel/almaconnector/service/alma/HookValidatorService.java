package org.unidue.ub.libintel.almaconnector.service.alma;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * validates an alma webhook message by its hash signature
 *
 * @author Eike Spielberg
 * @author eike.spielberg@uni-due.de
 * @version 1.0
 */
@Service
@Slf4j
public class HookValidatorService {

    @Value("${libintel.alma.hook.secret}")
    private String hookSecret;

    /**
     * validates an alma webhook content by the header taken from the request header.
     * @param content the string content of the webhook request
     * @param signature the signature provided in the request header
     * @return true, if the message content validates
     * @throws NoSuchAlgorithmException thrown if the hashing mechanism is invalid
     * @throws InvalidKeyException thrown if the hashing key generation fails
     */
    public boolean isValid(String content, String signature) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(hookSecret.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(content.getBytes()));
        log.debug(String.format("signature of web hook %s equals calculated hash %s : &b", signature, hash, hash.equals(signature)));
        return hash.equals(signature);
    }
}
