package com.frontlinehomes.save2buy.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
public class ResponseDTO<T> {
    private ResponseStatus status;
    private Integer statusCode;
    private String message;

    private T body;


    public ResponseDTO(ResponseStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseDTO(ResponseStatus status, Integer statusCode, String message) {
        this.status = status;
        this.statusCode = statusCode;
        this.message = message;
    }

    public ResponseDTO() {
    }
}
