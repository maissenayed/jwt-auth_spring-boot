package com.example.jwtauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CustomException extends Exception{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Object[] paras;

    public CustomException(){
        super();
    }

    public CustomException(String message){
        super(message);
    }

    public CustomException(String message,Object ... paras){
        super(message);
    }

    public Object[] getParas() {
        return paras;
    }

    public void setParas(Object[] paras) {
        this.paras = paras;
    }
}