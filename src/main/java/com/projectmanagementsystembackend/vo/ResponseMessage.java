package com.projectmanagementsystembackend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMessage {

    private String message;
    private int status;
    private Object data;

    public ResponseMessage(String message, int status) {
        this.message = message;
        this.status = status;
    }
}
