


import java.io.IOException;
import java.util.Arrays;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import org.bouncycastle.jcajce.provider.digest.SHA256;

public final class Base58Check {
    public static String bytesToBase58(byte[] data) {
        return rawBytesToBase58(addCheckHash(data));
    }

    static String rawBytesToBase58(byte[] data) {
        StringBuilder sb = new StringBuilder();
        BigInteger num = new BigInteger(1, data);
        while (num.signum() != 0) {
            BigInteger[] quotrem = num.divideAndRemainder(ALPHABET_SIZE);
            sb.append(ALPHABET.charAt(quotrem[1].intValue()));
            num = quotrem[0];
        }

        for (int i = 0; i < data.length && data[i] == 0; i++) sb.append(ALPHABET.charAt(0));
        return sb.reverse().toString();
    }

    static byte[] addCheckHash(byte[] data) {
        try {
            SHA256.Digest digest = new SHA256.Digest();
            digest.update(data);
            byte[] hash0 = digest.digest();
            digest.reset();
            digest.update(hash0);
            byte[] hash = Arrays.copyOf(digest.digest(), 4);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            buf.write(data);
            buf.write(hash);
            return buf.toByteArray();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public static byte[] base58ToBytes(String s) {
        byte[] concat = base58ToRawBytes(s);
        byte[] data = Arrays.copyOf(concat, concat.length - 4);
        byte[] hash = Arrays.copyOfRange(concat, concat.length - 4, concat.length);

        SHA256.Digest digest = new SHA256.Digest();
        digest.update(data);
        byte[] hash0 = digest.digest();
        digest.reset();
        digest.update(hash0);

        byte[] rehash = Arrays.copyOf(digest.digest(), 4);
        if (!Arrays.equals(rehash, hash))
            throw new IllegalArgumentException("Checksum mismatch");
        return data;
    }

    static byte[] base58ToRawBytes(String s) {
        BigInteger num = BigInteger.ZERO;
        for (int i = 0; i < s.length(); i++) {
            num = num.multiply(ALPHABET_SIZE);
            int digit = ALPHABET.indexOf(s.charAt(i));
            if (digit == -1)
                throw new IllegalArgumentException("Invalid character for Base58Check");
            num = num.add(BigInteger.valueOf(digit));
        }

        byte[] b = num.toByteArray();
        if (b[0] == 0)
            b = Arrays.copyOfRange(b, 1, b.length);

        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            for (int i = 0; i < s.length() && s.charAt(i) == ALPHABET.charAt(0); i++) buf.write(0);
            buf.write(b);
            return buf.toByteArray();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }


    public static final String ALPHABET =
            "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"; // Everything except 0OIl
    private static final BigInteger ALPHABET_SIZE = BigInteger.valueOf(ALPHABET.length());


    private Base58Check() {} 
}