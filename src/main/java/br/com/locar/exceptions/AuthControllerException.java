package br.com.locar.exceptions;

public class AuthControllerException extends RuntimeException {
  public AuthControllerException(String message) {
    super(message);
  }
}
