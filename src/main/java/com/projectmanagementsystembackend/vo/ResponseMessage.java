package com.projectmanagementsystembackend.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseMessage {

    private String message;
    private int status;
    private Object data;

    public ResponseMessage(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public static ResponseMessage getServerError(Exception e){
        return new ResponseMessage("Internal Server Error: " + e.getMessage(), 500);
    }

    public interface Generic {
        ResponseMessage OTP_SENT = new ResponseMessage("OTP sent successfully", 200);
        ResponseMessage ERROR_IN_SENDING_OTP = new ResponseMessage("Error in sending OTP", 500);
        ResponseMessage DATA_SAVE_SUCCESS = new ResponseMessage("Data saved successfully", 200);
        ResponseMessage DATA_UPDATE_SUCCESS = new ResponseMessage("Data updated successfully", 200);
        ResponseMessage DATA_DELETE_SUCCESS = new ResponseMessage("Data deleted successfully", 200);
        ResponseMessage DATA_NOT_FOUND = new ResponseMessage("Data not found", 404);
        ResponseMessage INTERNAL_ERROR = new ResponseMessage("Something went wrong", 500);
    }
}
