package jp.demo.nfccard;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class DemoHceService extends HostApduService {
    private static final byte[] STATUS_SUCCESS = HexUtils.hexToBytes("9000");
    private static final byte[] STATUS_FAILED = HexUtils.hexToBytes("6F00");
    private static final String SELECT_PREFIX = "00A4040007F0010203040506";
    private static final byte[] GET_DATA = HexUtils.hexToBytes("00CA000000");

    // Demo only. In production, the app should generate a short-lived signed token
    // using a private key that never leaves Android Keystore / Secure Element.
    private static final String EMPLOYEE_PROOF_JSON = "{" +
            "\"credential_type\":\"employee_attribute_proof\"," +
            "\"subject_id\":\"anon-demo-001\"," +
            "\"attributes\":{\"employee\":true}," +
            "\"issuer\":\"Japan Communications Inc.\"," +
            "\"certificate_id\":\"cert-demo-employee-001\"," +
            "\"issued_at\":\"2026-05-28T10:00:00+09:00\"," +
            "\"expires_at\":\"2026-12-31T23:59:59+09:00\"," +
            "\"nonce\":\"demo-reader-nonce\"," +
            "\"signature_status\":\"demo_valid\"," +
            "\"demo_signature\":\"not_for_production\"" +
            "}";

    @Override public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        String commandHex = HexUtils.bytesToHex(commandApdu);

        if (commandHex.startsWith(SELECT_PREFIX)) {
            return concat("EMPLOYEE_PROOF_READY".getBytes(StandardCharsets.UTF_8), STATUS_SUCCESS);
        }

        if (Arrays.equals(commandApdu, GET_DATA)) {
            return concat(EMPLOYEE_PROOF_JSON.getBytes(StandardCharsets.UTF_8), STATUS_SUCCESS);
        }

        return STATUS_FAILED;
    }

    @Override public void onDeactivated(int reason) { }

    private static byte[] concat(byte[] a, byte[] b) {
        byte[] out = new byte[a.length + b.length];
        System.arraycopy(a, 0, out, 0, a.length);
        System.arraycopy(b, 0, out, a.length, b.length);
        return out;
    }
}
