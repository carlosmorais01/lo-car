package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

/**
 * A classe `PasswordHasher` fornece funcionalidades para gerar hashes seguros de senhas
 * usando o algoritmo SHA-256.
 */
public class PasswordHasher {

    /**
     * Gera o hash SHA-256 de uma string de entrada.
     * Este método obtém uma instância de `MessageDigest` para "SHA-256",
     * calcula o hash dos bytes da senha (UTF-8) e converte o resultado em uma string hexadecimal.
     *
     * @param password A string de senha a ser hashed.
     * @return O hash SHA-256 da string de senha como uma string hexadecimal, ou {@code null} se o algoritmo "SHA-256" não for encontrado.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Algoritmo de hash não encontrado: " + e.getMessage());
            return null;
        }
    }

    /**
     * Converte um array de bytes em sua representação hexadecimal.
     * Cada byte é convertido em dois caracteres hexadecimais, preenchendo com '0' à esquerda
     * se necessário, para garantir o formato correto.
     *
     * @param hash O array de bytes a ser convertido.
     * @return Uma string representando o array de bytes em formato hexadecimal.
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}