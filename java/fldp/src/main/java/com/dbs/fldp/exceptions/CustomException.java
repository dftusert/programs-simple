package com.dbs.fldp.exceptions;

/***
 * Собственный тип исключения
 ***/
public class CustomException extends Exception {
    /* Конструкторы */
    public CustomException() {}
    public CustomException(String message) { super(message); }
}